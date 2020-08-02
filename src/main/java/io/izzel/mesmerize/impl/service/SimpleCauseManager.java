package io.izzel.mesmerize.impl.service;

import io.izzel.mesmerize.api.cause.CauseManager;
import io.izzel.mesmerize.api.cause.ContextKey;
import io.izzel.mesmerize.api.cause.EventContext;

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

        private final EventContext last;

        public SimpleFrame() {
            last = context.get();
            context.set(EventContext.create(last));
        }

        @Override
        public void close() throws Exception {
            context.set(last);
        }

        @Override
        public <T> StackFrame pushContext(ContextKey<T> key, T value) {
            SimpleCauseManager.this.pushContext(key, value);
            return this;
        }

        @Override
        public EventContext getFrameContext() {
            return context.get();
        }
    }
}
