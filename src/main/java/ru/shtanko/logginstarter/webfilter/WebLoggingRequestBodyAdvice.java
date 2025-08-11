package ru.shtanko.logginstarter.webfilter;

import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.PathMatcher;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.RequestBodyAdviceAdapter;
import ru.shtanko.logginstarter.properties.LoggingConfigurationProperties;
import ru.shtanko.logginstarter.util.LoggingUtil;

import java.lang.reflect.Type;


@ControllerAdvice
public class WebLoggingRequestBodyAdvice extends RequestBodyAdviceAdapter {

    private static final Logger log = LoggerFactory.getLogger(WebLoggingRequestBodyAdvice.class);

    private final PathMatcher pathMatcher = new AntPathMatcher();

    @Autowired
    private LoggingUtil loggingUtil;

    @Autowired
    private LoggingConfigurationProperties properties;


    @Autowired
    private HttpServletRequest request;

    @Override
    public Object afterBodyRead(Object body, HttpInputMessage inputMessage, MethodParameter parameter, Type targetType, Class<? extends HttpMessageConverter<?>> converterType) {

        if (shouldSkipLogging()) {
            return super.afterBodyRead(body, inputMessage, parameter, targetType, converterType);
        }

        String method = request.getMethod();
        String requestURI = request.getRequestURI() + loggingUtil.formatQueryString(request);

        log.info("Тело запроса: {} {} {}", method, requestURI, body);
        return super.afterBodyRead(body, inputMessage, parameter, targetType, converterType);
    }

    @Override
    public boolean supports(MethodParameter methodParameter, Type targetType, Class<? extends HttpMessageConverter<?>> converterType) {
        return !shouldSkipLogging();
    }

    private boolean shouldSkipLogging() {
        if (properties.getExcludeEndpoints().isEmpty()) {
            return false;
        }

        String path = request.getRequestURI();
        return properties.getExcludeEndpoints().stream()
             .anyMatch(pattern -> pathMatcher.match(pattern, path));
    }
}
