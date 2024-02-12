package pl.dudi.repolistapp.util;

import github.api.responses.branches.BranchesSchema;
import github.api.responses.branches.Commit;
import github.api.responses.repos.Owner;
import github.api.responses.repos.ReposSchema;

import java.net.URI;
import java.util.List;
import java.util.UUID;

public class GithubResponse {

    public static List<ReposSchema> someGithubRepos(String username) {
        return List.of(
            someGithubRepo1(username),
            someGithubRepo2(username),
            someGithubRepo3(username)
        );
    }

    private static ReposSchema someGithubRepo1(String username) {
        return new ReposSchema()
            .withFork(false)
            .withName("someRepoName1")
            .withOwner(new Owner()
                .withName(username)
                .withEmail("somemail1@mail.com"));
    }

    private static ReposSchema someGithubRepo2(String username) {
        return new ReposSchema()
            .withFork(false)
            .withName("someRepoName2")
            .withOwner(new Owner()
                .withName(username)
                .withEmail("somemail2@mail.com"));
    }

    private static ReposSchema someGithubRepo3(String username) {
        return new ReposSchema()
            .withFork(true)
            .withName("someRepoName3")
            .withOwner(new Owner()
                .withName(username)
                .withEmail("somemail3@mail.com"));
    }

    public static List<BranchesSchema> someBranches1() {
        return List.of(
            someBranch1()
        );
    }

    public static List<BranchesSchema> someBranches2() {
        return List.of(
            someBranch2(),
            someBranch3()
        );
    }

    private static BranchesSchema someBranch1() {
        return new BranchesSchema()
            .withName("master")
            .withProtected(false)
            .withCommit(new Commit()
                .withSha(UUID.randomUUID().toString())
                .withUrl(URI.create("http://api.service.com/some/path1/for/commit"))
            );
    }

    private static BranchesSchema someBranch2() {
        return new BranchesSchema()
            .withName("master")
            .withProtected(false)
            .withCommit(new Commit()
                .withSha(UUID.randomUUID().toString())
                .withUrl(URI.create("http://api.service.com/some/path2/for/commit"))
            );
    }

    private static BranchesSchema someBranch3() {
        return new BranchesSchema()
            .withName("feature")
            .withProtected(true)
            .withCommit(new Commit()
                .withSha(UUID.randomUUID().toString())
                .withUrl(URI.create("http://api.service.com/some/path3/for/commit"))
            );
    }
}
