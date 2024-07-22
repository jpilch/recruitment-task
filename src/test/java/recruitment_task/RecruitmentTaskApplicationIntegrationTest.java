package recruitment_task;

import static org.instancio.Select.field;

import java.util.List;

import org.instancio.Instancio;
import org.instancio.Model;
import org.instancio.Select;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.cloud.contract.wiremock.WireMockSpring;
import org.springframework.test.web.reactive.server.WebTestClient;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.ResponseDefinitionBuilder;

import recruitment_task.dto.GithubApiBranchResponse;
import recruitment_task.dto.GithubApiRepositoryResponse;

import static com.github.tomakehurst.wiremock.client.WireMock.*;


@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT, properties = {
    "github-api.url=http://localhost:8081",
    "github-api.key=fake-key"
})
public class RecruitmentTaskApplicationIntegrationTest {
    
    private static WireMockServer wiremock = new WireMockServer(WireMockSpring.options().port(8081));
    private static String mockNextPageLink = "rel=\"next\"";

    @Autowired
    private WebTestClient webClient;

    @BeforeAll
    static void beforeAll() {
        wiremock.start();
    }

    @AfterEach
    void afterEach() {
        wiremock.resetAll();
    }

    @AfterAll
    static void afterAll() {
        wiremock.shutdown();
    }

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void returnsRepositoryForExistingUser() throws JsonProcessingException {
        GithubApiRepositoryResponse repo = Instancio
            .of(GithubApiRepositoryResponse.class)
            .set(field("fork"), false)
            .create();

        GithubApiBranchResponse branch = Instancio
            .of(GithubApiBranchResponse.class)
            .create();

        wiremock.stubFor(get("/users/jpilch/repos?per_page=100&page=1")
            .willReturn(githubApiResponse()
                .withStatus(200)
                .withBody(objectMapper.writeValueAsString(List.of(repo)))));

        wiremock.stubFor(get("/repos/jpilch/%s/branches?per_page=100&page=1".formatted(repo.name()))
            .willReturn(githubApiResponse()
                .withStatus(200)
                .withBody(objectMapper.writeValueAsString(List.of(branch)))));

        webClient.get().uri("/jpilch")
            .exchange()
            .expectStatus()
            .isOk()
            .expectBody()
            .jsonPath("$.length()").isEqualTo(1)
            .jsonPath("$[0].repositoryName").isEqualTo(repo.name())
            .jsonPath("$[0].ownerLogin").isEqualTo(repo.owner().login())
            .jsonPath("$[0].branches.length()").isEqualTo(1)
            .jsonPath("$[0].branches[0].name").isEqualTo(branch.name())
            .jsonPath("$[0].branches[0].lastCommitSha").isEqualTo(branch.commit().sha());
    }

    @Test
    void findsRepositoriesFromAllNonEmptyPages() throws JsonProcessingException {
        Model<GithubApiRepositoryResponse> nonForkRepoModel = Instancio.of(GithubApiRepositoryResponse.class)
            .set(Select.field("fork"), false)
            .toModel();

        List<GithubApiRepositoryResponse> page1Repos = Instancio.ofList(nonForkRepoModel).size(2).create();
        List<GithubApiRepositoryResponse> page2Repos = Instancio.ofList(nonForkRepoModel).size(2).create();

        wiremock.stubFor(get("/users/jpilch/repos?per_page=100&page=1")
            .willReturn(githubApiResponse()
                .withStatus(200)
                .withHeader("link", mockNextPageLink)
                .withBody(objectMapper.writeValueAsString(page1Repos))));

        wiremock.stubFor(get("/users/jpilch/repos?per_page=100&page=2")
            .willReturn(githubApiResponse()
                .withStatus(200)
                .withBody(objectMapper.writeValueAsString(page2Repos))));

        wiremock.stubFor(get(urlPathMatching("/repos/.*/.*/branches"))
            .willReturn(githubApiResponse()
                .withStatus(200)
                .withBody("[]")));

        webClient.get().uri("/jpilch")
            .exchange()
            .expectStatus()
            .isOk()
            .expectBody()
            .jsonPath("$.length()").isEqualTo(4);
    }

    @Test
    void findRepositoryBranchesFromAllNonEmptyPages() throws JsonProcessingException {
        GithubApiRepositoryResponse repo = Instancio
            .of(GithubApiRepositoryResponse.class)
            .set(field("fork"), false)
            .create();

        List<GithubApiBranchResponse> page1Branches = Instancio.ofList(GithubApiBranchResponse.class).size(2).create();
        List<GithubApiBranchResponse> page2Branches = Instancio.ofList(GithubApiBranchResponse.class).size(2).create();

        wiremock.stubFor(get("/users/%s/repos?per_page=100&page=1".formatted(repo.owner().login()))
            .willReturn(githubApiResponse()
                .withStatus(200)
                .withBody(objectMapper.writeValueAsString(List.of(repo)))));

        wiremock.stubFor(get("/repos/%s/%s/branches?per_page=100&page=1".formatted(repo.owner().login(), repo.name()))
            .willReturn(githubApiResponse()
                .withStatus(200)
                .withHeader("link", mockNextPageLink)
                .withBody(objectMapper.writeValueAsString(page1Branches))));

        wiremock.stubFor(get("/repos/%s/%s/branches?per_page=100&page=2".formatted(repo.owner().login(), repo.name()))
            .willReturn(githubApiResponse()
                .withStatus(200)
                .withBody(objectMapper.writeValueAsString(page2Branches))));

        webClient.get().uri("/{username}", repo.owner().login())
            .exchange()
            .expectStatus()
            .isOk()
            .expectBody()
            .jsonPath("$.length()").isEqualTo(1)
            .jsonPath("$[0].branches.length()").isEqualTo(4);
    }

    @Test
    void returnsErrorResponseIfGithubUserNotFound() {
        wiremock.stubFor(get("/users/jpilch/repos?per_page=100&page=1")
            .willReturn(githubApiResponse().withStatus(404)));

        webClient.get().uri("/jpilch")
            .exchange()
            .expectStatus()
            .isNotFound()
            .expectBody()
            .jsonPath("$.message").isEqualTo("User does not exist")
            .jsonPath("$.status").isEqualTo(404);
    }

    @Test
    void filtersForkRepositories() throws JsonProcessingException {
        GithubApiRepositoryResponse forkRepo = Instancio.of(GithubApiRepositoryResponse.class)
            .set(field("fork"), true)
            .create();
        GithubApiRepositoryResponse nonForkRepo = Instancio.of(GithubApiRepositoryResponse.class)
            .set(field("fork"), false)
            .create();

        wiremock.stubFor(get("/users/jpilch/repos?per_page=100&page=1")
            .willReturn(githubApiResponse()
                .withStatus(200)
                .withBody(objectMapper.writeValueAsString(List.of(forkRepo, nonForkRepo)))));

        wiremock.stubFor(get(urlPathMatching("/repos/.*/.*/branches"))
            .willReturn(githubApiResponse()
                .withStatus(200)
                .withBody("[]")));

        webClient.get().uri("/jpilch")
            .exchange()
            .expectStatus()
            .isOk()
            .expectBody()
            .jsonPath("$.length()").isEqualTo(1)
            .jsonPath("$[0].repositoryName").isEqualTo(nonForkRepo.name())
            .jsonPath("$[0].ownerLogin").isEqualTo(nonForkRepo.owner().login());
    }

    private ResponseDefinitionBuilder githubApiResponse() {
        return aResponse()
            .withHeader("Content-Type", "application/vnd.github+json");
    }

}
