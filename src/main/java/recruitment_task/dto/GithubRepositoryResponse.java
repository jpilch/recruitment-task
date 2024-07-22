package recruitment_task.dto;

import java.util.List;

public record GithubRepositoryResponse(String repositoryName, String ownerLogin, List<GithubBranchResponse> branches) {}
