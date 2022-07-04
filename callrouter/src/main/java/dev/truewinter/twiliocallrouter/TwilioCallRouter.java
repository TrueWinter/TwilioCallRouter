package dev.truewinter.twiliocallrouter;

import dev.truewinter.twiliocallrouter.config.Config;
import dev.truewinter.twiliocallrouter.plugin.PluginManager;
import org.eclipse.jetty.util.log.Slf4jLog;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Path;

public class TwilioCallRouter {
    private static Config config;
    private static WebServer webServer;
    private static Logger logger = LoggerFactory.getLogger(TwilioCallRouter.class);

    public static void main(String[] args) {
        try {
            logger.info("Starting TwilioCallRouter v" + Util.getVersion());
        } catch (IOException e) {
            logger.info("Starting TwilioCallRouter (unknown version)");
        }

        try {
            Path configPath = Path.of(Util.getInstallPath().toString(), "config.yml");
            config = new Config(configPath.toFile());
        } catch (Exception e) {
            logger.error("Failed to load config", e);
        }

        try {
            PluginManager.loadPlugins(Util.getPluginJars());
        } catch (Exception e) {
            logger.error("Failed to load plugins", e);
        }

        webServer = new WebServer(config);
        webServer.start();

        Thread shutdownThread = new Thread(new Runnable() {
            @Override
            public void run() {
                PluginManager.handleShutdown();
            }
        });

        Runtime.getRuntime().addShutdownHook(shutdownThread);
    }

    public static Logger getLogger() {
        return logger;
    }
}
