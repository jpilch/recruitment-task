package recruitment_task.dto;

public record GithubApiRepositoryResponse(Long id, String name, Owner owner, boolean fork) {

    public record Owner(String login) {}
}