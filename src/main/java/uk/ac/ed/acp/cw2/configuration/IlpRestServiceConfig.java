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
    // Dependency inject Utility class into RestServiceImplementation class
    @Bean
    public Utility  utility()
    {
        return new Utility();
    }


}
