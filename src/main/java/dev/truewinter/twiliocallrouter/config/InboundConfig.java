package dev.truewinter.twiliocallrouter.config;

import java.util.Optional;

public class InboundConfig extends BaseDirectionalConfig<InboundRoutedConfig> {
    private boolean sip;
    private ForwardingConfig forwardingConfig = null;

    public InboundConfig(int timeout, String defaultNumber,
                         boolean sip) {
        super(timeout, defaultNumber);
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
