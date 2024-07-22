package recruitment_task.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;
import recruitment_task.dto.GithubApiBranchResponse;
import recruitment_task.dto.GithubApiRepositoryResponse;
import recruitment_task.exception.GithubUserNotFoundException;

@Service
@RequiredArgsConstructor
public class GithubService {

    private final WebClient webClient;

    public Flux<GithubApiRepositoryResponse> findAllRepositoriesByUsername(String username) {
        return findAllRepositoriesByUsername(username, 1);
    }

    public Flux<GithubApiBranchResponse> findAllBranchesForRepository(String username, String repoName) {
        return findAllBranchesForRepository(username, repoName, 1);
    }

    private Flux<GithubApiRepositoryResponse> findAllRepositoriesByUsername(String username, int page) {
        return findRepositoriesByUsername(username, page)
            .expand(response -> hasNextPage(response) ? findRepositoriesByUsername(username, page + 1) : Flux.empty())
            .flatMap(response -> response.bodyToFlux(GithubApiRepositoryResponse.class));
    }

    private Flux<GithubApiBranchResponse> findAllBranchesForRepository(String username, String repoName, int page) {
        return findBranchesForRepository(username, repoName, page)
            .expand(response -> hasNextPage(response) ? findBranchesForRepository(username, repoName, page + 1) : Flux.empty())
            .flatMap(response -> response.bodyToFlux(GithubApiBranchResponse.class));
    }

    private Flux<ClientResponse> findRepositoriesByUsername(String username, int page) {
        return webClient.get()
            .uri(uriBuilder -> uriBuilder
                .path("/users/{username}/repos")
                .queryParam("per_page", 100)
                .queryParam("page", page)
                .build(username))
            .exchangeToFlux(this::handleApiResponse);
    }

    private Flux<ClientResponse> findBranchesForRepository(String username, String repoName, int page) {
        return webClient.get()
            .uri(uriBuilder -> uriBuilder
                .path("/repos/{username}/{repo}/branches")
                .queryParam("per_page", 100)
                .queryParam("page", page)
                .build(username, repoName))
            .exchangeToFlux(this::handleApiResponse);
    }

    private Flux<ClientResponse> handleApiResponse(ClientResponse response) {
        return switch (response.statusCode().value()) {
            case 404 -> Flux.error(GithubUserNotFoundException::new);
            case 200 -> Flux.just(response);
            default -> Flux.empty();
        };
    }

    private boolean hasNextPage(ClientResponse response) {
        List<String> linkHeader = response.headers().header("link");

        return !linkHeader.isEmpty() && linkHeader.get(0).contains("rel=\"next\"");
    }
}