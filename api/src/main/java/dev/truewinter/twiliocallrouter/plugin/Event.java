package dev.truewinter.twiliocallrouter.plugin;

import com.twilio.twiml.TwiMLException;
import com.twilio.twiml.VoiceResponse;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public abstract class Event {
    private boolean isCancelled = false;
    private VoiceResponse customTwiML = null;

    public void setCancelled(@NotNull boolean cancelled) {
        isCancelled = cancelled;
    }

    @NotNull
    public boolean isCancelled() {
        return isCancelled;
    }

    public void setCustomTwiML(@NotNull VoiceResponse customTwiML) {
        this.customTwiML = customTwiML;
    }

    // If the plugin developer prefers to use another library,
    // they can convert that libraries output to XML and use
    // this method to pass it to TwilioCallRouter.
    public void setCustomTwiML(@NotNull String customTwiML) throws TwiMLException {
        this.customTwiML = VoiceResponse.Builder.fromXml(customTwiML).build();
    }

    @NotNull
    public Optional<VoiceResponse> getCustomTwiML() {
        return Optional.ofNullable(customTwiML);
    }

    // If the plugin developer prefers to use another library,
    // they can use this method to get the TwiML as a string
    // to pass it to their preferred library.
    @NotNull
    public Optional<String> getCustomTwiMLAsString() {
        if (customTwiML != null) {
            return Optional.of(customTwiML.toXml());
        } else {
            return Optional.empty();
        }
    }
}
