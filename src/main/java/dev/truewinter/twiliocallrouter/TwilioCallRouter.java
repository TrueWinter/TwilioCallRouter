package dev.truewinter.twiliocallrouter;

import dev.truewinter.twiliocallrouter.config.Config;

import java.io.IOException;
import java.nio.file.Path;

public class TwilioCallRouter {
    private static Config config;
    private static WebServer webServer;

    public static void main(String[] args) {
        try {
            System.out.println("Starting TwilioCallRouter v" + Util.getVersion());
        } catch (IOException e) {
            System.out.println("Starting TwilioCallRouter (unknown version)");
        }

        try {
            Path configPath = Path.of(Util.getInstallPath().toString(), "config.yml");
            config = new Config(configPath.toFile());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        webServer = new WebServer(config);
        webServer.start();
    }
}
