package ru.shtanko.logginstarter.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@ConfigurationProperties(prefix = "logging.mask")
public class MaskConfigurationProperties {

    private Set<String> headers = new HashSet<>();

    public Set<String> getHeaders() {
        return headers;
    }

    public void setHeaders(List<String> headers) {
        this.headers = new HashSet<>(headers);
    }
}
