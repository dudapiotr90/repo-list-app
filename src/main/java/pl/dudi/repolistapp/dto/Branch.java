package pl.dudi.repolistapp.dto;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Branch {
    private String name;
    private String lastCommitSha;
}
