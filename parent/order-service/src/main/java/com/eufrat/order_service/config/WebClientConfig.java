package com.eufrat.order_service.config;

import io.micrometer.tracing.Span;
import io.micrometer.tracing.Tracer;
import io.micrometer.tracing.propagation.Propagator;
import jakarta.annotation.PostConstruct;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.ClientRequest;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Hooks;
import reactor.core.publisher.Mono;

@Configuration
public class WebClientConfig {

    @Bean
    @LoadBalanced
    public WebClient.Builder builder() {
        return WebClient.builder();
    }

    @Bean
    public WebClient webClient(Tracer tracer, Propagator propagator) {
        return builder()
                .filter(propagateTraceContextFilter(tracer, propagator))
                .build();
    }

    private ExchangeFilterFunction propagateTraceContextFilter(Tracer tracer, Propagator propagator) {
        return ExchangeFilterFunction.ofRequestProcessor(request -> {
            var builder = ClientRequest.from(request);

            // Propagate the current trace context into the HTTP headers
            Propagator.Setter<ClientRequest.Builder> setter = (carrier, key, value) -> {
                if (carrier != null) {
                    carrier.header(key, value);
                }
            };

            Span span = tracer.currentSpan();
            if (span != null) {
                propagator.inject(span.context(), builder, setter);
            }

            return Mono.just(builder.build());
        });
    }
}
