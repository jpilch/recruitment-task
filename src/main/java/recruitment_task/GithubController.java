package recruitment_task;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import reactor.core.publisher.Flux;
import recruitment_task.dto.GithubRepositoryResponse;

@RestController
public class GithubController {

    private final GithubFacade githubFacade;

    public GithubController(GithubFacade githubFacade) {
        this.githubFacade = githubFacade;
    }

    @GetMapping("/{username}")
    public Flux<GithubRepositoryResponse> findRepositoriesForUser(@PathVariable String username) {
        return githubFacade.findRepositoriesForUser(username);
    }

}