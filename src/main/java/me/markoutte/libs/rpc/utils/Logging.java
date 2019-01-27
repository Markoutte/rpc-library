package me.markoutte.libs.rpc.utils;

import java.util.logging.Filter;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

/**
 * Методы для упрощения логгирования.
 */
public final class Logging {
    public static final Filter SUPPRESS_ALL = new Filter() {
        @Override
        public boolean isLoggable(LogRecord record) {
            return false;
        }
    };

    private Logging() {
    }

    public static void off(Class<?> clazz) {
        off(clazz.getName());
    }

    public static void off(String loggerName) {
        level(loggerName, Level.OFF);
    }

    public static void level(Class<?> clazz, Level level) {
        level(clazz.getName(), level);
    }

    public static void level(String loggerName, Level level) {
        Logger.getLogger(loggerName).setLevel(level);
    }

    public static void clearFilter(Class<?> clazz) {
        clearFilter(clazz.getName());
    }

    public static void clearFilter(String loggerName) {
        filter(loggerName, null);
    }

    public static void filter(Class<?> clazz, Filter filter) {
        filter(clazz.getName(), filter);
    }

    public static void filter(String loggerName, Filter filter) {
        Logger.getLogger(loggerName).setFilter(filter);
    }

    public static void error(Class<?> clazz, String message, Object... parameters) {
        error(clazz.getName(), message, parameters);
    }

    public static void error(Class<?> clazz, Throwable e, String message, Object... parameters) {
        error(clazz.getName(), e, message, parameters);
    }

    public static void error(String loggerName, String message, Object... parameters) {
        error(loggerName, null, message, parameters);
    }

    public static void error(String loggerName, Throwable e, String message, Object... parameters) {
        log(loggerName, Level.SEVERE, e, message, parameters);
    }

    public static void warn(Class<?> clazz, String message, Object... parameters) {
        warn(clazz.getName(), message, parameters);
    }

    public static void warn(Class<?> clazz, Throwable e, String message, Object... parameters) {
        warn(clazz.getName(), e, message, parameters);
    }

    public static void warn(String loggerName, String message, Object... parameters) {
        warn(loggerName, null, message, parameters);
    }

    public static void warn(String loggerName, Throwable e, String message, Object... parameters) {
        log(loggerName, Level.WARNING, e, message, parameters);
    }

    public static void info(Class<?> clazz, String message, Object... parameters) {
        info(clazz.getName(), message, parameters);
    }

    public static void info(Class<?> clazz, Throwable e, String message, Object... parameters) {
        info(clazz.getName(), e, message, parameters);
    }

    public static void info(String loggerName, String message, Object... parameters) {
        info(loggerName, null, message, parameters);
    }

    public static void info(String loggerName, Throwable e, String message, Object... parameters) {
        log(loggerName, Level.INFO, e, message, parameters);
    }

    public static void trace(Class<?> clazz, String message, Object... parameters) {
        trace(clazz.getName(), message, parameters);
    }

    public static void trace(Class<?> clazz, Throwable e, String message, Object... parameters) {
        trace(clazz.getName(), e, message, parameters);
    }

    public static void trace(String loggerName, String message, Object... parameters) {
        trace(loggerName, null, message, parameters);
    }

    public static void trace(String loggerName, Throwable e, String message, Object... parameters) {
        log(loggerName, Level.FINE, e, message, parameters);
    }

    public static void log(Class<?> clazz, Level level, Throwable e, String message, Object... parameters) {
        log(clazz.getName(), level, e, message, parameters);
    }

    public static void log(String loggerName, Level level, Throwable e, String message, Object... parameters) {
        LogRecord rec = new LogRecord(level, message);
        rec.setParameters(parameters);
        rec.setThrown(e);
        rec.setLoggerName(loggerName);

        Logger.getLogger(loggerName).log(rec);
    }
}
