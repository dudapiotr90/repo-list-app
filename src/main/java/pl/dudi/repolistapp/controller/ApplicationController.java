package pl.dudi.repolistapp.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.dudi.repolistapp.dto.ErrorMessage;
import pl.dudi.repolistapp.dto.UserRepository;
import pl.dudi.repolistapp.service.ApiService;

import java.util.List;

@Tag(
    name = "Repository Service - ApplicationController",
    description = "ApplicationController exposes REST API for RepoListApplication"
)
@RestController
@RequiredArgsConstructor
@RequestMapping(ApplicationController.REPOS)
public class ApplicationController {

    public static final String REPOS = "/repos";
    public static final String USER = "/{username}";

    private final ApiService apiService;



    @Operation(
        summary = "Get repositories",
        description = "List all user github repositories, which are not forks"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(
                    implementation = UserRepository.class
                )
            )
        ),
        @ApiResponse(
            responseCode = "404",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(
                    implementation = ErrorMessage.class
                )
            )
        ),
        @ApiResponse(
            responseCode = "400",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(
                    implementation = ErrorMessage.class
                )
            ),
            description = "BAD REQUEST"
        )
    })
    @GetMapping(value = USER,headers = "Accept=application/json")
    public ResponseEntity<List<UserRepository>> getNonForkRepositories(
        @RequestHeader(HttpHeaders.ACCEPT) String accept,
        @Parameter(description = "owner of github account")
        @PathVariable(name = "username") String username
    ) {
        List<UserRepository> response = apiService.getNonForkRepositories(username);
        return ResponseEntity.ok(response);
    }

}
