package dev.truewinter.twiliocallrouter.config;

public class ForwardingConfig {
    private boolean sip;
    private String number;

    public ForwardingConfig(boolean sip, String number) {
        this.sip = sip;
        this.number = number;
    }

    public boolean isSip() {
        return sip;
    }

    public String getNumber() {
        return number;
    }
}
