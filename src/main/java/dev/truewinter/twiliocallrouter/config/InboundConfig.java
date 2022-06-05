package dev.truewinter.twiliocallrouter.config;

public class InboundConfig extends BaseDirectionalConfig<InboundRoutedConfig> {
    private boolean sip;

    public InboundConfig(int timeout, boolean answerOnBridge, String defaultNumber,
                         boolean sip) {
        super(timeout, answerOnBridge, defaultNumber);
        this.sip = sip;
    }

    public boolean isSip() {
        return sip;
    }
}
