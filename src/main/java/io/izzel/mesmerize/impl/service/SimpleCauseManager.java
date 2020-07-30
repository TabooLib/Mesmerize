package io.izzel.mesmerize.impl.service;

import com.google.common.collect.Multimap;
import com.google.common.collect.MultimapBuilder;
import io.izzel.mesmerize.api.cause.CauseManager;
import io.izzel.mesmerize.api.cause.ContextKey;
import io.izzel.mesmerize.api.cause.EventContext;

import java.util.Map;

public class SimpleCauseManager implements CauseManager {

    private final ThreadLocal<EventContext> context = ThreadLocal.withInitial(EventContext::create);

    @Override
    public EventContext currentContext() {
        return context.get();
    }

    @Override
    public StackFrame pushStackFrame() {
        return new SimpleFrame();
    }

    @Override
    public <T> void pushContext(ContextKey<T> key, T value) {
        currentContext().add(key, value);
    }

    private class SimpleFrame implements StackFrame {

        private final Multimap<ContextKey<?>, Object> contextKey = MultimapBuilder.hashKeys().linkedListValues().build();

        @SuppressWarnings({"unchecked", "rawtypes"})
        @Override
        public void close() throws Exception {
            for (Map.Entry<ContextKey<?>, Object> entry : contextKey.entries()) {
                currentContext().remove((ContextKey) entry.getKey(), entry.getValue());
            }
        }

        @Override
        public <T> StackFrame pushContext(ContextKey<T> key, T value) {
            if (currentContext().add(key, value)) {
                contextKey.put(key, value);
            }
            return this;
        }
    }
}
