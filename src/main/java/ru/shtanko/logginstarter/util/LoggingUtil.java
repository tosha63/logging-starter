package ru.shtanko.logginstarter.util;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.logging.log4j.util.Strings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.shtanko.logginstarter.properties.MaskConfigurationProperties;

import java.util.Collections;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
public class LoggingUtil {

    private static final String MASK = "***";

    @Autowired
    private MaskConfigurationProperties properties;

    public String inlineRequestHeaders(HttpServletRequest request) {
        Map<String, String> headersMap = Collections.list(request.getHeaderNames()).stream()
             .collect(Collectors.toMap(it -> it, request::getHeader));

        return formatHeaders(headersMap);
    }

    public String inlineResponseHeaders(HttpServletResponse response) {
        Map<String, String> headersMap = response.getHeaderNames().stream()
              .collect(Collectors.toMap(it -> it, response::getHeader));

        return formatHeaders(headersMap);
    }

    private String formatHeaders(Map<String, String> headersMap) {
        String inlineHeaders = headersMap.entrySet().stream()
              .map(this::formatHeaderWithMask)
              .collect(Collectors.joining(","));
        return "headers={" + inlineHeaders + "}";
    }

    public String formatQueryString(HttpServletRequest request) {
        return Optional.ofNullable(request.getQueryString())
               .map(qs -> "?" + qs)
               .orElse(Strings.EMPTY);
    }

    private String formatHeaderWithMask(Map.Entry<String, String> entry) {
        return shouldMask(entry.getKey()) ? entry.getKey() + "=" + MASK :
            entry.getKey() + "=" + entry.getValue();
    }

    private boolean shouldMask(String headerName) {
        return properties.getHeaders().stream()
                .anyMatch(headerName::equalsIgnoreCase);
    }
}
