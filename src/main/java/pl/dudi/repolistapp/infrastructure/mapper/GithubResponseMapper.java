package pl.dudi.repolistapp.infrastructure.mapper;

import github.api.responses.branches.BranchesSchema;
import github.api.responses.repos.ReposSchema;
import org.springframework.stereotype.Component;
import pl.dudi.repolistapp.dto.Branch;
import pl.dudi.repolistapp.dto.UserRepository;

import java.util.List;

@Component
public abstract class GithubResponseMapper {

    public UserRepository map(ReposSchema repository) {
        return new UserRepository(
            repository.getName(),
            repository.getOwner().getLogin(),
            null
        );
    }

    public Branch map(BranchesSchema branch) {
        return new Branch(branch.getName(), branch.getCommit().getSha());
    }
    protected List<UserRepository> filterAndMap(List<ReposSchema> repoResponse) {
        return repoResponse.stream()
            .filter(repo -> !repo.getFork())
            .map(this::map)
            .toList();
    }
}
