package dev.truewinter.twiliocallrouter.plugin;

public class NoopLogger implements Logger {
    NoopLogger() {}

    @Override
    public void info(String s) {

    }

    @Override
    public void warn(String s) {

    }

    @Override
    public void error(String s) {

    }

    @Override
    public void error(String s, Throwable t) {

    }
}
