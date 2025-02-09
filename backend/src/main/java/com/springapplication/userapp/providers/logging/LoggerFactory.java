package com.springapplication.userapp.providers.logging;

public class LoggerFactory {
    public static Logger getLogger(Class<?> clazz) { return new MDCLogger(org.slf4j.LoggerFactory.getLogger(clazz)); }
}
