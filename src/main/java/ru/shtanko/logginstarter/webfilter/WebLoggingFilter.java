package ru.shtanko.logginstarter.webfilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.util.ContentCachingResponseWrapper;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import static ru.shtanko.logginstarter.util.LoggingUtil.formatQueryString;
import static ru.shtanko.logginstarter.util.LoggingUtil.inlineRequestHeaders;
import static ru.shtanko.logginstarter.util.LoggingUtil.inlineResponseHeaders;


@Component
public class WebLoggingFilter extends HttpFilter {

    private static final Logger log = LoggerFactory.getLogger(WebLoggingFilter.class);

    @Override
    protected void doFilter(HttpServletRequest request,
                            HttpServletResponse response,
                            FilterChain chain) throws IOException, ServletException {
        String method = request.getMethod();
        String requestURI = request.getRequestURI() + formatQueryString(request);
        String requestHeaders = inlineRequestHeaders(request);

        log.info("Запрос: {} {} {}", method, requestURI, requestHeaders);

        ContentCachingResponseWrapper responseWrapper = new ContentCachingResponseWrapper(response);

        try {
            super.doFilter(request, responseWrapper, chain);

            String responseBody = "body=" + new String(responseWrapper.getContentAsByteArray(), StandardCharsets.UTF_8);

            String responseHeaders = inlineResponseHeaders(response);

            log.info("Ответ: {} {} {} {} {}", method, requestURI, response.getStatus(), responseHeaders, responseBody);
        } finally {
            responseWrapper.copyBodyToResponse();
        }
    }
}
