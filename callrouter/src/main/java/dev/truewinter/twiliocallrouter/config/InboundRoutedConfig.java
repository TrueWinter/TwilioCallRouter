package dev.truewinter.twiliocallrouter.config;

import java.util.Optional;

public class InboundRoutedConfig extends BaseDirectionalRoutedConfig {
    private boolean sip;
    private ForwardingConfig forwardingConfig = null;

    InboundRoutedConfig(String country, boolean sip, String number) {
        super(country, number);
        this.sip = sip;
    }

    public boolean isSip() {
        return sip;
    }

    public boolean hasForwardingConfig() {
        return this.forwardingConfig != null;
    }

    public void setForwardingConfig(ForwardingConfig forwardingConfig) {
        this.forwardingConfig = forwardingConfig;
    }

    public Optional<ForwardingConfig> getForwardingConfig() {
        if (forwardingConfig == null) {
            return Optional.empty();
        } else {
            return Optional.of(forwardingConfig);
        }
    }
}
