package dev.truewinter.twiliocallrouter.config;

public class BaseDirectionalRoutedConfig {
    private String prefix;
    private String number;

    public BaseDirectionalRoutedConfig(String prefix, String number) {
        this.prefix = prefix;
        this.number = number;
    }

    public String getPrefix() {
        return prefix;
    }

    public String getNumber() {
        return number;
    }
}
