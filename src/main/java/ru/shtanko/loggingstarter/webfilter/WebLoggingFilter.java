package ru.shtanko.loggingstarter.webfilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.PathMatcher;
import org.springframework.web.util.ContentCachingResponseWrapper;
import ru.shtanko.loggingstarter.properties.LoggingConfigurationProperties;
import ru.shtanko.loggingstarter.service.LoggingService;

import java.io.IOException;
import java.nio.charset.StandardCharsets;


@Component
public class WebLoggingFilter extends HttpFilter {

    private final PathMatcher pathMatcher = new AntPathMatcher();

    @Autowired
    private LoggingService loggingService;

    @Autowired
    private LoggingConfigurationProperties properties;


    @Override
    protected void doFilter(HttpServletRequest request,
                            HttpServletResponse response,
                            FilterChain chain) throws IOException, ServletException {

        if (shouldSkipLogging(request)) {
            chain.doFilter(request, response);
            return;
        }

        loggingService.logRequest(request);

        ContentCachingResponseWrapper responseWrapper = new ContentCachingResponseWrapper(response);

        try {
            super.doFilter(request, responseWrapper, chain);

            String responseBody = "body=" + new String(responseWrapper.getContentAsByteArray(), StandardCharsets.UTF_8);
            loggingService.logResponse(request, response, responseBody);
        } finally {
            responseWrapper.copyBodyToResponse();
        }
    }

    private boolean shouldSkipLogging(HttpServletRequest request) {
        if (properties.getExcludeEndpoints().isEmpty()) {
            return false;
        }

        String path = request.getRequestURI();
        return properties.getExcludeEndpoints().stream()
             .anyMatch(pattern -> pathMatcher.match(pattern, path));
    }
}
