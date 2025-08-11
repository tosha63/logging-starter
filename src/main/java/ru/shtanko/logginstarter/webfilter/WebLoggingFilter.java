package ru.shtanko.logginstarter.webfilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.PathMatcher;
import org.springframework.web.util.ContentCachingResponseWrapper;
import ru.shtanko.logginstarter.properties.LoggingConfigurationProperties;
import ru.shtanko.logginstarter.util.LoggingUtil;

import java.io.IOException;
import java.nio.charset.StandardCharsets;


@Component
public class WebLoggingFilter extends HttpFilter {

    private static final Logger log = LoggerFactory.getLogger(WebLoggingFilter.class);

    private final PathMatcher pathMatcher = new AntPathMatcher();

    @Autowired
    private LoggingUtil loggingUtil;

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

        String method = request.getMethod();
        String requestURI = request.getRequestURI() + loggingUtil.formatQueryString(request);
        String requestHeaders = loggingUtil.inlineRequestHeaders(request);

        log.info("Запрос: {} {} {}", method, requestURI, requestHeaders);

        ContentCachingResponseWrapper responseWrapper = new ContentCachingResponseWrapper(response);

        try {
            super.doFilter(request, responseWrapper, chain);

            String responseBody = "body=" + new String(responseWrapper.getContentAsByteArray(), StandardCharsets.UTF_8);
            String responseHeaders = loggingUtil.inlineResponseHeaders(response);

            log.info("Ответ: {} {} {} {} {}", method, requestURI, response.getStatus(), responseHeaders, responseBody);
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
