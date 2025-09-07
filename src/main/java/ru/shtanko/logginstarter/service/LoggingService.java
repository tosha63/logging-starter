package ru.shtanko.logginstarter.service;

import feign.Request;
import feign.Response;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.logging.log4j.util.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.shtanko.logginstarter.dto.RequestDirection;
import ru.shtanko.logginstarter.properties.MaskConfigurationProperties;

import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class LoggingService {

    private static final Logger log = LoggerFactory.getLogger(LoggingService.class);

    private static final String MASK = "***";

    @Autowired
    private MaskConfigurationProperties properties;

    public void logRequest(HttpServletRequest request) {
        String method = request.getMethod();
        String requestURI = request.getRequestURI() + formatQueryString(request);
        String requestHeaders = inlineRequestHeaders(request);

        log.info("Запрос: {} {} {} {}", RequestDirection.IN, method, requestURI, requestHeaders);
    }

    public void logFeignRequest(Request request) {
        String method = request.httpMethod().name();
        String requestURI = request.url();
        String requestHeaders = formatHeaders(request.headers());
        String body = new String(request.body(), StandardCharsets.UTF_8);

        log.info("Запрос: {} {} {} {} body={}", RequestDirection.OUT, method, requestURI, requestHeaders, body);
    }

    public void logResponse(HttpServletRequest request, HttpServletResponse response, String responseBody) {
        String method = request.getMethod();
        String requestURI = request.getRequestURI() + formatQueryString(request);
        String responseHeaders = inlineResponseHeaders(response);

        log.info("Ответ: {} {} {} {} {} {}", RequestDirection.IN, method, requestURI, response.getStatus(), responseHeaders, responseBody);
    }

    public void logResponse(HttpServletRequest request, Object body) {
        String method = request.getMethod();
        String requestURI = request.getRequestURI() + formatQueryString(request);

        log.info("Тело запроса: {} {} {}", method, requestURI, body);
    }

    public void logFeignResponse(Response response, String responseBody) {
        String url = response.request().url();
        String method = response.request().httpMethod().name();
        String responseHeaders = formatHeaders(response.headers());
        int status = response.status();

        log.info("Ответ: {} {} {} {} {} body={}", RequestDirection.OUT, method, url, status, responseHeaders, responseBody);
    }

    private String inlineRequestHeaders(HttpServletRequest request) {
        Map<String, Collection<String>> headersMap = Collections.list(request.getHeaderNames()).stream()
                .collect(Collectors.toMap(it -> it, headerName -> Collections.list(request.getHeaders(headerName))));

        return formatHeaders(headersMap);
    }


    private String inlineResponseHeaders(HttpServletResponse response) {
        Map<String, Collection<String>> headersMap = response.getHeaderNames().stream()
                .collect(Collectors.toMap(it -> it, headerName -> response.getHeaderNames()));

        return formatHeaders(headersMap);
    }

    private String formatHeaders(Map<String, Collection<String>> headersMap) {
        String inlineHeaders = headersMap.entrySet().stream()
                .map(entry -> {
                    String headerName = entry.getKey();
                    String headerValue = String.join(", ", entry.getValue());
                    return shouldMask(headerName) ? headerName + "=" + MASK :
                            headerName + "=" + headerValue;
                })
                .collect(Collectors.joining(","));
        return "headers={" + inlineHeaders + "}";
    }

    public String formatQueryString(HttpServletRequest request) {
        return Optional.ofNullable(request.getQueryString())
                .map(qs -> "?" + qs)
                .orElse(Strings.EMPTY);
    }

    private boolean shouldMask(String headerName) {
        return properties.getHeaders().stream()
                .anyMatch(headerName::equalsIgnoreCase);
    }
}
