package com.eufrat.order_service.helper;

import brave.Tracer;
import brave.Tracing;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;

@Component
public class TracingUtils {

    @Autowired
    Tracing tracing;
    @Autowired
    Tracer tracer;

    public <T> CompletableFuture<T> completableFuture(Supplier<T> supplier) {
        var scopeManager = tracing.currentTraceContext();
        var context = tracer.currentSpan().context();
        return CompletableFuture.supplyAsync(() -> {
            try(var x = scopeManager.newScope(context)) {
                return supplier.get();
            }
        });
    }

}
