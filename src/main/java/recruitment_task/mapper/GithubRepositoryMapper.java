package recruitment_task.mapper;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import recruitment_task.dto.GithubApiBranchResponse;
import recruitment_task.dto.GithubApiRepositoryResponse;
import recruitment_task.dto.GithubRepositoryResponse;

@Mapper(componentModel = "spring", uses = GithubBranchMapper.class)
public interface GithubRepositoryMapper {
    
    @Mapping(target = "ownerLogin", source = "repo.owner.login")
    @Mapping(target = "repositoryName", source = "repo.name")
    GithubRepositoryResponse toResponse(GithubApiRepositoryResponse repo, List<GithubApiBranchResponse> branches);
}
