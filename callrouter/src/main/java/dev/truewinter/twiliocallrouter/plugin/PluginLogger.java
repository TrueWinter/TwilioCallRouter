package dev.truewinter.twiliocallrouter.plugin;

import dev.truewinter.twiliocallrouter.TwilioCallRouter;

public class PluginLogger implements Logger {
    private final org.slf4j.Logger logger = TwilioCallRouter.getLogger();
    private final Plugin plugin;

    protected PluginLogger(Plugin plugin) {
        this.plugin = plugin;
    }

    // TODO: Try to get console colours working
    @Override
    public void info(String s) {
        logger.info(format(s));
    }

    @Override
    public void warn(String s) {
        logger.warn(format(s));
    }

    @Override
    public void error(String s) {
        logger.error(format(s));
    }

    @Override
    public void error(String s, Throwable t) {
        logger.error(format(s), t);
    }

    private String format(String s) {
        return String.format("[%s] %s", plugin.getName(), s);
    }
}
