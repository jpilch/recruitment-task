package recruitment_task.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import recruitment_task.dto.GithubApiBranchResponse;
import recruitment_task.dto.GithubBranchResponse;

@Mapper(componentModel = "spring")
public interface GithubBranchMapper {
 
    @Mapping(target = "lastCommitSha", source = "commit.sha")
    GithubBranchResponse toResponse(GithubApiBranchResponse repository);
}
