package pl.dudi.repolistapp.integration.configuration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.core.WireMockConfiguration;
import io.restassured.RestAssured;
import io.restassured.config.ObjectMapperConfig;
import io.restassured.config.RestAssuredConfig;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.ActiveProfiles;
import pl.dudi.repolistapp.RepoListApiApplication;
import pl.dudi.repolistapp.integration.support.IntegrationTestSupport;

@ActiveProfiles("test")
@SpringBootTest(
    classes = RepoListApiApplication.class,
    webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT
)
public abstract class RestAssuredITBase implements IntegrationTestSupport {

    protected static WireMockServer wireMockServer;
    @LocalServerPort
    protected int port;
    @Value("${server.servlet.context-path}")
    protected String basePath;
    @Autowired
    protected ObjectMapper objectMapper;
    @BeforeAll
    static void beforeAll() {
        wireMockServer = new WireMockServer(
            WireMockConfiguration.wireMockConfig()
                .port(9999)
        );
        wireMockServer.start();
    }

    @AfterAll
    static void afterAll() {
        wireMockServer.stop();
    }

    public RequestSpecification requestSpecification() {
        return RestAssured
            .given()
            .config(getConfig())
            .basePath(basePath)
            .port(port)
            .accept(ContentType.JSON);
    }

    private RestAssuredConfig getConfig() {
        return RestAssuredConfig.config()
            .objectMapperConfig(new ObjectMapperConfig()
                .jackson2ObjectMapperFactory((type, s) -> objectMapper));
    }

    @AfterEach
    void afterEach() {
        wireMockServer.resetAll();
    }

}
