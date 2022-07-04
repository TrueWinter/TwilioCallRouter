package dev.truewinter.twiliocallrouter.plugin;

import dev.truewinter.twiliocallrouter.TwilioCallRouter;

public class PluginLogger implements Logger {
    private final org.slf4j.Logger logger = TwilioCallRouter.getLogger();
    private final Plugin plugin;

    protected PluginLogger(Plugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public void info(String s) {
        logger.info(String.format("[%s] %s", plugin.getName(), s));
    }

    @Override
    public void warn(String s) {
        logger.warn(String.format("[%s] %s", plugin.getName(), s));
    }

    @Override
    public void error(String s) {
        logger.error(String.format("[%s] %s", plugin.getName(), s));
    }

    @Override
    public void error(String s, Throwable t) {
        logger.error(String.format("[%s] %s", plugin.getName(), s), t);
    }
}
