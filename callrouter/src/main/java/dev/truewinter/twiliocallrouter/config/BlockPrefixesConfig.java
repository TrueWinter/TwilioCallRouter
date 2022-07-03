package dev.truewinter.twiliocallrouter.config;

import com.twilio.twiml.TwiMLException;
import com.twilio.twiml.VoiceResponse;

public class BlockPrefixesConfig {
    private String prefix;
    private boolean shouldSay;
    private String sayString;
    private String playURL = null;
    private VoiceResponse voiceResponse = null;

    BlockPrefixesConfig(String prefix) {
        this.prefix = prefix;
        this.shouldSay = false;
    }

    BlockPrefixesConfig(String prefix, String sayString) {
        this.prefix = prefix;
        this.shouldSay = true;
        this.sayString = sayString;
    }

    public BlockPrefixesConfig setPlayURL(String url) {
        this.playURL = url;
        return this;
    }

    public BlockPrefixesConfig setTwiml(String twiml) throws TwiMLException {
        voiceResponse = VoiceResponse.Builder.fromXml(twiml).build();
        return this;
    }

    public boolean hasCustomTwiml() {
        return voiceResponse != null;
    }

    public String getPrefix() {
        return prefix;
    }

    public boolean hasSayString() {
        return this.shouldSay;
    }

    public String getSayString() {
        return sayString;
    }

    public boolean hasPlayURL() {
        return this.playURL != null;
    }

    public String getPlayURL() {
        return this.playURL;
    }

    public VoiceResponse getVoiceResponse() {
        return voiceResponse;
    }
}
