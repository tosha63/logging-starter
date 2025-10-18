package ru.shtanko.loggingstarter.feign.config;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import io.micrometer.tracing.Span;
import io.micrometer.tracing.Tracer;
import io.micrometer.tracing.propagation.Propagator;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Конфигурация для автоматического добавления заголовков OpenTelemetry трассировки в Feign запросы
 */
public class TracingFeignRequestInterceptor implements RequestInterceptor {

    @Autowired
    private Tracer tracer;

    @Autowired
    private Propagator propagator;

    @Override
    public void apply(RequestTemplate template) {
        Span currentSpan = tracer.currentSpan();

        if (currentSpan == null) {
            return;
        }

        propagator.inject(currentSpan.context(), template, (requestTemplate, name, values) -> {
            if (requestTemplate != null) {
                requestTemplate.header(name, values);
            }
        });
    }
}
