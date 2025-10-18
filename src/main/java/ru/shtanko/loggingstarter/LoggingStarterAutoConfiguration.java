package ru.shtanko.loggingstarter;

import feign.Logger;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import ru.shtanko.loggingstarter.aspect.LogExecutionAspect;
import ru.shtanko.loggingstarter.feign.FeignRequestLogger;
import ru.shtanko.loggingstarter.feign.config.TracingFeignRequestInterceptor;
import ru.shtanko.loggingstarter.properties.LoggingConfigurationProperties;
import ru.shtanko.loggingstarter.properties.MaskConfigurationProperties;
import ru.shtanko.loggingstarter.service.LoggingService;
import ru.shtanko.loggingstarter.webfilter.WebLoggingFilter;
import ru.shtanko.loggingstarter.webfilter.WebLoggingRequestBodyAdvice;


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
    public LoggingService loggingService() {
        return new LoggingService();
    }

    @Bean
    @ConditionalOnProperty(prefix = "logging.web-logging", value = "log-feign-requests", havingValue = "true")
    public FeignRequestLogger feignRequestLogger() {
        return new FeignRequestLogger();
    }

    @Bean
    @ConditionalOnProperty(prefix = "logging.web-logging", value = "log-feign-requests", havingValue = "true")
    public Logger.Level feignLogLevel() {
        return Logger.Level.BASIC;
    }

    @Bean
    @ConditionalOnProperty(prefix = "logging.web-logging", value = "log-feign-requests", havingValue = "true")
    public TracingFeignRequestInterceptor tracingFeignRequestInterceptor() {
        return new TracingFeignRequestInterceptor();
    }
}
