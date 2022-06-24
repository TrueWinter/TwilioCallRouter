package dev.truewinter.twiliocallrouter.config;

public class OutboundConfig extends BaseDirectionalConfig<OutboundRoutedConfig> {
    private String defaultCountryCode;

    public OutboundConfig(int timeout, boolean answerOnBridge, String defaultNumber, String defaultCountryCode) {
        super(timeout, answerOnBridge, defaultNumber);
        this.defaultCountryCode = defaultCountryCode;
    }

    public String getDefaultCountryCode() {
        return defaultCountryCode;
    }
}
