package io.izzel.mesmerize.api.cause;

import io.izzel.mesmerize.api.service.StatsService;

import java.io.Closeable;

public interface CauseManager {

    EventContext currentContext();

    StackFrame pushStackFrame();

    <T> void pushContext(ContextKey<T> key, T value);

    static CauseManager instance() {
        return StatsService.instance().getCauseManager();
    }

    interface StackFrame extends Closeable {

        @SuppressWarnings("UnusedReturnValue")
        <T> StackFrame pushContext(ContextKey<T> key, T value);

        EventContext getFrameContext();

        @Override
        void close();
    }
}
