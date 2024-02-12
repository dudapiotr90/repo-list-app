package pl.dudi.repolistapp.infrastructure.mapper;

import github.api.responses.branches.BranchesSchema;
import github.api.responses.repos.ReposSchema;
import org.springframework.stereotype.Component;
import pl.dudi.repolistapp.dto.Branch;
import pl.dudi.repolistapp.dto.UserRepository;

@Component
public class GithubResponseMapper {

    public UserRepository map(ReposSchema repository) {
        return UserRepository.builder()
            .repositoryName(repository.getName())
            .ownerLogin(repository.getOwner().getLogin())
            .build();
    }

    public Branch map(BranchesSchema branch) {
        return Branch.builder()
            .name(branch.getName())
            .lastCommitSha(branch.getCommit().getSha())
            .build();
    }
}
