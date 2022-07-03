package dev.truewinter.twiliocallrouter.plugin.event;

import dev.truewinter.twiliocallrouter.plugin.Event;
import org.jetbrains.annotations.NotNull;

public class InboundCallEvent extends Event {
    private final String toNumber;
    private final String fromNumber;

    public InboundCallEvent(@NotNull String to, @NotNull String from) {
        this.toNumber = to;
        this.fromNumber = from;
    }

    @NotNull
    public String getToNumber() {
        return toNumber;
    }

    @NotNull
    public String getFromNumber() {
        return fromNumber;
    }
}
