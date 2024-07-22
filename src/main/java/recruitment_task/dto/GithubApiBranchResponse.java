package recruitment_task.dto;

public record GithubApiBranchResponse(String name, Commit commit) {

    public record Commit(String sha) {}
}
