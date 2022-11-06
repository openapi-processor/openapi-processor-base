/*
 * Copyright 2022 https://github.com/openapi-processor/openapi-processor-base
 * PDX-License-Identifier: Apache-2.0
 */

package io.openapiprocessor.slf4j.messager;

import org.slf4j.Marker;
import org.slf4j.event.Level;
import org.slf4j.helpers.*;

import javax.annotation.processing.Messager;
import javax.tools.Diagnostic;

public class MessagerLogger extends LegacyAbstractLogger {

    static final MessagerLoggerConfiguration config = new MessagerLoggerConfiguration ();

    private final CurrentMessager messager;

    static void init() {
        config.init ();
    }

    MessagerLogger(String name, CurrentMessager messager) {
        this.name = name;
        this.messager = messager;
    }

    @Override
    protected String getFullyQualifiedCallerName () {
        return null;
    }

    @Override
    protected void handleNormalizedLoggingCall (
        Level level, Marker marker, String messagePattern, Object[] arguments, Throwable throwable) {

        FormattingTuple formattingTuple = MessageFormatter.arrayFormat (messagePattern, arguments, throwable);
        String message = formattingTuple.getMessage ();
        Messager log = messager.get ();
        log.printMessage (mapToKind (level), message);
    }

    @Override
    public boolean isErrorEnabled () {
        return isLevelEnabled (Level.ERROR);
    }

    @Override
    public boolean isWarnEnabled () {
        return isLevelEnabled (Level.WARN);
    }

    @Override
    public boolean isInfoEnabled () {
        return isLevelEnabled (Level.INFO);
    }

    @Override
    public boolean isDebugEnabled () {
        return isLevelEnabled (Level.DEBUG);
    }

    @Override
    public boolean isTraceEnabled () {
        return isLevelEnabled (Level.TRACE);
    }

    private boolean isLevelEnabled(Level logLevel) {
        return (logLevel.toInt () >= config.getLogLevel ().toInt ());
    }

    private Diagnostic.Kind mapToKind(Level level) {
        switch (level) {
            case ERROR:
                return Diagnostic.Kind.ERROR;
            case WARN:
                return Diagnostic.Kind.WARNING;
            case INFO:
                return Diagnostic.Kind.NOTE;
            case DEBUG:
            case TRACE:
                return Diagnostic.Kind.OTHER;
            default:
                return Diagnostic.Kind.NOTE;
        }
    }
}
