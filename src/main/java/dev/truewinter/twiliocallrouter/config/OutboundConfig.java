package dev.truewinter.twiliocallrouter.config;

public class OutboundConfig extends BaseDirectionalConfig<OutboundRoutedConfig> {
    private String defaultCountryCode;

    public OutboundConfig(int timeout, String defaultNumber, String defaultCountryCode) {
        super(timeout, defaultNumber);
        this.defaultCountryCode = defaultCountryCode;
    }

    public String getDefaultCountryCode() {
        return defaultCountryCode;
    }
}
