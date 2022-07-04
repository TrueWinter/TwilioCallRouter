package dev.truewinter.twiliocallrouter.plugin;

public interface Logger {
    void info(String s);
    void warn(String s);
    void error(String s);
    void error(String s, Throwable t);
}
