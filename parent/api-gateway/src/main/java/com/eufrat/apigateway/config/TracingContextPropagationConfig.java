package com.eufrat.apigateway.config;

import jakarta.annotation.PostConstruct;
import org.springframework.context.annotation.Configuration;
import reactor.core.publisher.Hooks;

@Configuration
public class TracingContextPropagationConfig {

    @PostConstruct
    public void enableAutomaticContextPropagation() {
        Hooks.enableAutomaticContextPropagation();
    }
}