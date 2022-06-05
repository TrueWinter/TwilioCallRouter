package dev.truewinter.twiliocallrouter.config;

public class InboundRoutedConfig extends BaseDirectionalRoutedConfig {
    private boolean sip;

    InboundRoutedConfig(String country, boolean sip, String number) {
        super(country, number);
        this.sip = sip;
    }

    public boolean isSip() {
        return sip;
    }
}
