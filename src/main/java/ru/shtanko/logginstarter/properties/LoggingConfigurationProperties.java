
package ru.shtanko.logginstarter.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.ArrayList;
import java.util.List;

@ConfigurationProperties(prefix = "logging.web-logging")
public class LoggingConfigurationProperties {

    private List<String> excludeEndpoints = new ArrayList<>();

    public List<String> getExcludeEndpoints() {
        return excludeEndpoints;
    }

    public void setExcludeEndpoints(List<String> excludeEndpoints) {
        this.excludeEndpoints = excludeEndpoints;
    }
}
