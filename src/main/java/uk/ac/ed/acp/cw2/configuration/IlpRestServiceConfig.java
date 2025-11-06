package uk.ac.ed.acp.cw2.configuration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import uk.ac.ed.acp.cw2.utility.Utility;

import java.net.URL;

/**
 * Configuration class that is used for dependency injection
 */
@Configuration
@EnableScheduling
public class IlpRestServiceConfig
{
    private final String DEFAULT_ILP_ENDPOINT = "https://ilp-rest-2025-bvh6e9hschfagrgy.ukwest-01.azurewebsites.net/";

    // Dependency inject the method to get url
    @Bean
    public String getEndpoint()
    {
        String endpoint = System.getenv("ILP_ENDPOINT");

        if (endpoint == null || endpoint.isEmpty())
        {
            endpoint = DEFAULT_ILP_ENDPOINT;
        }

        return endpoint;
    }

    // Dependency inject Utility class into RestServiceImplementation class
    @Bean
    public Utility  utility()
    {
        return new Utility();
    }




}
