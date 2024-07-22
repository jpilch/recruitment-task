package recruitment_task;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;
import recruitment_task.dto.GithubRepositoryResponse;
import recruitment_task.mapper.GithubRepositoryMapper;
import recruitment_task.service.GithubService;



@Service
@RequiredArgsConstructor
public class GithubFacade {

    private final GithubService githubService;
    private final GithubRepositoryMapper mapper;

    public Flux<GithubRepositoryResponse> findRepositoriesForUser(String username) {
        return githubService.findAllRepositoriesByUsername(username)
            .filter(repo -> !repo.fork())
            .flatMap(repo -> githubService.findAllBranchesForRepository(username, repo.name())
                .collectList()
                .map(branches -> mapper.toResponse(repo, branches))
            );
    }
}
