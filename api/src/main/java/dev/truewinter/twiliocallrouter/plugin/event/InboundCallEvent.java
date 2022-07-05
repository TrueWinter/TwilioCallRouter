package dev.truewinter.twiliocallrouter.plugin.event;

import dev.truewinter.twiliocallrouter.plugin.Event;
import org.jetbrains.annotations.NotNull;

/**
 * This event is fired before an incoming call is handled
 */
public class InboundCallEvent extends Event {
    private final String toNumber;
    private final String fromNumber;

    /**
     * @hidden
     * @param to The called number
     * @param from The caller's number
     */
    public InboundCallEvent(@NotNull String to, @NotNull String from) {
        this.toNumber = to;
        this.fromNumber = from;
    }

    /**
     * @return The called number in E.164 format
     */
    @NotNull
    public String getToNumber() {
        return toNumber;
    }

    /**
     * @return The caller's number in E.164 format
     */
    @NotNull
    public String getFromNumber() {
        return fromNumber;
    }
}
