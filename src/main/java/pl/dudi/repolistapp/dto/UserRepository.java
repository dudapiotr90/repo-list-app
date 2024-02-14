package pl.dudi.repolistapp.dto;

import java.util.List;
public record UserRepository(
    String repositoryName,
    String ownerLogin,
    List<Branch> branches) {

    public UserRepository withRepositoryName(String repositoryName) {
        return new UserRepository(repositoryName, this.ownerLogin, this.branches);
    }
    public UserRepository withBranches(List<Branch> branches) {
        return new UserRepository(this.repositoryName, this.ownerLogin, branches);
    }
}
