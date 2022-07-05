package dev.truewinter.twiliocallrouter.plugin;

import com.twilio.twiml.TwiMLException;
import com.twilio.twiml.VoiceResponse;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public abstract class Event {
    private boolean isCancelled = false;
    private VoiceResponse customTwiML = null;

    /**
     * This method allows plugin developers to reject the call
     * @param cancelled whether this call should be rejected
     */
    public void setCancelled(boolean cancelled) {
        isCancelled = cancelled;
    }

    /**
     * @return true if the call has been cancelled
     */
    public boolean isCancelled() {
        return isCancelled;
    }

    /**
     * Using this method allows plugin developers to control how this call (or part of call) will be handled
     * @param customTwiML The TwiML to provide to Twilio, in a {@link VoiceResponse} object
     */
    public void setCustomTwiML(@NotNull VoiceResponse customTwiML) {
        this.customTwiML = customTwiML;
    }

    /**
     * Using this method allows plugin developers to control how this call (or part of call) will be handled.
     * This is a convenience method provided to developers who wish to use a different TwiML library.
     * @see Event#setCustomTwiML(VoiceResponse)
     * @param customTwiML The TwiML to provide to Twilio
     * @throws TwiMLException if the TwiML is invalid
     */
    public void setCustomTwiML(@NotNull String customTwiML) throws TwiMLException {
        this.customTwiML = VoiceResponse.Builder.fromXml(customTwiML).build();
    }

    /**
     * @return The custom TwiML set, wrapped in an {@link Optional}
     */
    @NotNull
    public Optional<VoiceResponse> getCustomTwiML() {
        return Optional.ofNullable(customTwiML);
    }

    /**
     * @see Event#getCustomTwiML()
     * @return The custom TwiML set, wrapped in an {@link Optional}
     */
    @NotNull
    public Optional<String> getCustomTwiMLAsString() {
        if (customTwiML != null) {
            return Optional.of(customTwiML.toXml());
        } else {
            return Optional.empty();
        }
    }
}
