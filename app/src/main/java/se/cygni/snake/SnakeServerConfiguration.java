package se.cygni.snake;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

/**
 * @author Alan Tibbetts
 * @since 15/04/16
 */
@Configuration
public class SnakeServerConfiguration {

    //TODO: Move some of the hard coded values scattered around the system to application.properties and read into this class.
    
    @Value("${info.build.version}")
    private String serverVersion;

    public String getServerVersion() {
        return serverVersion;
    }
}
