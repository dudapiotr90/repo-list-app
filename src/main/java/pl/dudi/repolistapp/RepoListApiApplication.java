package pl.dudi.repolistapp;

import io.swagger.v3.oas.annotations.ExternalDocumentation;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.info.License;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@OpenAPIDefinition(
	info = @Info(
		title = "List user repositories",
		description = "Application to communicate with GitHub Api",
		version = "v1.0",
		contact = @Contact(
			name = "Piotr",
			email = "dudapiotr90@gmail.com",
			url = "https://github.com/dudapiotr90"
		),
		license = @License(
			name = "Apache 2.0",
			url = "https://github.com/dudapiotr90"
		)
	),
	externalDocs = @ExternalDocumentation(
		description = "Repo list api documentation",
		url = "https://github.com/dudapiotr90/repo-list-app"
	)
)
@SpringBootApplication
public class RepoListApiApplication {

	public static void main(String[] args) {
		SpringApplication.run(RepoListApiApplication.class, args);
	}

}
