package dev.truewinter.twiliocallrouter.plugin.event;

import dev.truewinter.twiliocallrouter.plugin.Event;
import org.jetbrains.annotations.NotNull;

public class OutboundCallEvent extends Event {
    private final String toNumber;
    private final String toNumber164;
    private final String rawToNumber;
    private final String fromNumber;
    private final String rawFromNumber;

    public OutboundCallEvent(@NotNull String to, @NotNull String to164, @NotNull String rawToNumber, @NotNull String from, @NotNull String rawFromNumber) {
        this.toNumber = to;
        this.toNumber164 = to164;
        this.rawToNumber = rawToNumber;
        this.fromNumber = from;
        this.rawFromNumber = rawFromNumber;
    }

    @NotNull
    public String getToNumber() {
        return toNumber;
    }

    @NotNull
    public String getToNumber164() {
        return toNumber164;
    }

    @NotNull
    public String getRawToNumber() {
        return rawToNumber;
    }

    @NotNull
    public String getFromNumber() {
        return fromNumber;
    }

    @NotNull
    public String getRawFromNumber() {
        return rawFromNumber;
    }
}
