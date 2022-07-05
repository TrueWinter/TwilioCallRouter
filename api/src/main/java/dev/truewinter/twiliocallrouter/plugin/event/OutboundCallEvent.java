package dev.truewinter.twiliocallrouter.plugin.event;

import dev.truewinter.twiliocallrouter.plugin.Event;
import org.jetbrains.annotations.NotNull;

/**
 * This event is fired before an outbound call is handled
 */
public class OutboundCallEvent extends Event {
    private final String toNumber;
    private final String toNumber164;
    private final String rawToNumber;
    private final String fromNumber;
    private final String rawFromNumber;

    /**
     * @hidden
     * @param to The called number, extracted from the SIP URL
     * @param to164 The called number, extracted from the SIP URL, in E.164 format
     * @param rawToNumber The unmodified SIP URL
     * @param from The caller's number, extracted from the SIP URL
     * @param rawFromNumber The unmodified SIP URL
     */
    public OutboundCallEvent(@NotNull String to, @NotNull String to164, @NotNull String rawToNumber, @NotNull String from, @NotNull String rawFromNumber) {
        this.toNumber = to;
        this.toNumber164 = to164;
        this.rawToNumber = rawToNumber;
        this.fromNumber = from;
        this.rawFromNumber = rawFromNumber;
    }

    /**
     * @return The called number, extracted from the SIP URL
     */
    @NotNull
    public String getToNumber() {
        return toNumber;
    }

    /**
     * @return The called number, extracted from the SIP URL, in E.164 format
     */
    @NotNull
    public String getToNumber164() {
        return toNumber164;
    }

    /**
     * @return The unmodified SIP URL
     */
    @NotNull
    public String getRawToNumber() {
        return rawToNumber;
    }

    /**
     * @return The caller's number, extracted from the SIP URL
     */
    @NotNull
    public String getFromNumber() {
        return fromNumber;
    }

    /**
     * @return The unmodified SIP URL
     */
    @NotNull
    public String getRawFromNumber() {
        return rawFromNumber;
    }
}
