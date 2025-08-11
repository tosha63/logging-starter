package ru.shtanko.logginstarter;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import ru.shtanko.logginstarter.aspect.LogExecutionAspect;
import ru.shtanko.logginstarter.properties.LoggingConfigurationProperties;
import ru.shtanko.logginstarter.util.LoggingUtil;
import ru.shtanko.logginstarter.properties.MaskConfigurationProperties;
import ru.shtanko.logginstarter.webfilter.WebLoggingFilter;
import ru.shtanko.logginstarter.webfilter.WebLoggingRequestBodyAdvice;


@AutoConfiguration
@ConditionalOnProperty(prefix = "logging", value = "enabled", havingValue = "true", matchIfMissing = true)
@EnableConfigurationProperties({MaskConfigurationProperties.class, LoggingConfigurationProperties.class})
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
    @ConditionalOnProperty(prefix = "logging.web-logging", value = "enabled", havingValue = "true", matchIfMissing = true)
    public LoggingUtil loggingUtil() {
        return new LoggingUtil();
    }
}
