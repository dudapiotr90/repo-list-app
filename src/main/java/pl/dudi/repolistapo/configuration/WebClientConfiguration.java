package pl.dudi.repolistapo.configuration;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.http.codec.json.Jackson2JsonDecoder;
import org.springframework.http.codec.json.Jackson2JsonEncoder;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfiguration {

    public static final String BASE_URL = "https://api.github.com";
    @Bean
    public WebClient webClient(final ObjectMapper objectMapper) {
        return WebClient.builder()
            .baseUrl(BASE_URL)
            .exchangeStrategies(exchangeStrategies(objectMapper))
            .build();
    }

    private ExchangeStrategies exchangeStrategies(ObjectMapper objectMapper) {
        return ExchangeStrategies.builder()
            .codecs(config -> {
                config
                    .defaultCodecs()
                    .jackson2JsonEncoder(new Jackson2JsonEncoder(objectMapper, MediaType.APPLICATION_JSON));
                config.defaultCodecs()
                    .jackson2JsonDecoder(new Jackson2JsonDecoder(objectMapper, MediaType.APPLICATION_JSON));
            })
            .build();
    }
}
