package uk.ac.ed.acp.cw2.configuration;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.WebClient;
import uk.ac.ed.acp.cw2.utility.Utility;

import java.net.URL;

/**
 * Configuration class that is used for dependency injection
 */
@Configuration
@EnableScheduling
public class IlpRestServiceConfig
{
    // Dependency inject url
    @Bean
    public String serviceUrl()
    {
        String url = System.getenv("ILP_ENDPOINT");

        if (url == null || url.isEmpty())
        {
            url = "https://ilp-rest-2025-bvh6e9hschfagrgy.ukwest-01.azurewebsites.net";
        }

        return url;
    }

    @Bean
    public RestTemplate restTemplate(RestTemplateBuilder builder)
    {
        return builder.build();
    }

    // Dependency inject Utility class into RestServiceImplementation class
    @Bean
    public Utility utility(ObjectMapper objectMapper)
    {
        return new Utility(objectMapper);
    }
}
