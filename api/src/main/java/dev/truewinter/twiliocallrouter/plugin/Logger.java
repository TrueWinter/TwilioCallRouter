package dev.truewinter.twiliocallrouter.plugin;

public interface Logger {
    /**
     * Logs an informational string to console
     * @param s The string to log
     */
    void info(String s);

    /**
     * Logs a warning to console
     * @param s The string to log
     */
    void warn(String s);

    /**
     * Logs an error to console
     * @param s The string to log
     */
    void error(String s);

    /**
     * Logs an error to console
     * @param s The string to log
     * @param t The exception thrown by this error
     */
    void error(String s, Throwable t);
}
