package dev.truewinter.twiliocallrouter.plugin.event;

import dev.truewinter.twiliocallrouter.plugin.Event;
import org.jetbrains.annotations.NotNull;

public class ForwardCallEvent extends Event {
    private final String status;
    private final String from;
    private final String to;

    public ForwardCallEvent(@NotNull String status, @NotNull String from, @NotNull String to) {
        this.status = status;
        this.from = from;
        this.to = to;
    }

    @NotNull
    public String getStatus() {
        return status;
    }

    @NotNull
    public String getFrom() {
        return from;
    }

    @NotNull
    public String getTo() {
        return to;
    }
}
