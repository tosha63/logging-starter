package ru.shtanko.logginstarter;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import ru.shtanko.logginstarter.aspect.LogExecutionAspect;
import ru.shtanko.logginstarter.mask.MaskerHeader;
import ru.shtanko.logginstarter.util.LoggingUtil;
import ru.shtanko.logginstarter.webfilter.WebLoggingFilter;
import ru.shtanko.logginstarter.webfilter.WebLoggingRequestBodyAdvice;

import java.util.HashSet;
import java.util.List;

@AutoConfiguration
@ConditionalOnProperty(prefix = "logging", value = "enabled", havingValue = "true", matchIfMissing = true)
public class LoggingStarterAutoConfiguration {

    @Bean
    @ConditionalOnProperty(prefix = "logging", value = "log-exec-time", havingValue = "true")
    public LogExecutionAspect logExecutionAspect() {
        return new LogExecutionAspect();
    }

    @Bean
    @ConditionalOnProperty(prefix = "logging.web-logging", value = "enabled", havingValue = "true", matchIfMissing = true)
    public WebLoggingFilter webLoggingFilter() {
        return new WebLoggingFilter();
    }

    @Bean
    @ConditionalOnProperty(prefix = "logging.web-logging", value = {"enabled", "log-body"}, havingValue = "true")
    public WebLoggingRequestBodyAdvice webLoggingRequestBodyAdvice() {
        return new WebLoggingRequestBodyAdvice();
    }

    @Bean
    @ConditionalOnProperty(prefix = "logging.mask", value = "enabled", havingValue = "true")
    public MaskerHeader masker(@Value("#{'${logging.mask.headers:}'.split(',')}") List<String> headersToMask) {
        return new MaskerHeader(new HashSet<>(headersToMask));
    }

    @Bean
    @ConditionalOnBean(MaskerHeader.class)
    public MaskerInitializer maskerInitializer(MaskerHeader maskerHeader) {
        LoggingUtil.setMasker(maskerHeader);
        return new MaskerInitializer();
    }

    private static class MaskerInitializer {}
}
