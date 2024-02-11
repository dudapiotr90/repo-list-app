package pl.dudi.repolistapp.dto;

import lombok.*;

import java.util.List;
@With
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserRepository {
    private String repositoryName;
    private String ownerLogin;
    private List<Branch> branches;
}
