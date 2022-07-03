package dev.truewinter.twiliocallrouter.plugin.event;

import dev.truewinter.twiliocallrouter.plugin.Event;
import org.jetbrains.annotations.NotNull;

public class TransferCallEvent extends Event {
    private final String transferTo;
    private final String direction;
    private final String from;
    private final String to;

    public TransferCallEvent(@NotNull String transferTo, @NotNull String direction, @NotNull String from, @NotNull String to) {
        this.transferTo = transferTo;
        this.direction = direction;
        this.from = from;
        this.to = to;
    }

    @NotNull
    public String getTransferTo() {
        return transferTo;
    }

    @NotNull
    public String getDirection() {
        return direction;
    }

    public String getFrom() {
        return from;
    }

    public String getTo() {
        return to;
    }
}
