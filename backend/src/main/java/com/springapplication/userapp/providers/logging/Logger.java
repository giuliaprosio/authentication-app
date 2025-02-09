package com.springapplication.userapp.providers.logging;

public sealed interface Logger permits MDCLogger {

    void debug(String message, Object... args);

    void info(String message, Object... args);

    void warn(String message, Object... args);

    void warn(String message, Throwable throwable, Object... args);

    void error(String message, Object... args);

    void error(String message, Throwable throwable, Object... args);

}

record MDCLogger(org.slf4j.Logger logger) implements Logger {

    @Override
    public void debug(String message, Object... args) {
        logger.debug(message, args);
    }

    @Override
    public void info(String message, Object... args) {
        logger.info(message, args);
    }

    @Override
    public void warn(String message, Object... args) {
        logger.warn(message, args);
    }

    @Override
    public void warn(String message, Throwable throwable, Object... args) {
        logger.warn(message, throwable, args);
    }

    @Override
    public void error(String message, Object... args) {
        logger.error(message, args);
    }

    @Override
    public void error(String message, Throwable throwable, Object... args) {
        logger.error(message, throwable, args);
    }
}
