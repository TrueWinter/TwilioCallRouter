package dev.truewinter.twiliocallrouter.plugin.event;

import dev.truewinter.twiliocallrouter.plugin.Event;
import org.jetbrains.annotations.NotNull;

public class TransferCallEvent extends Event {
    private final String transferTo;
    private final String direction;
    private final String from;
    private final String to;

    /**
     * @hidden
     * @param transferTo The number this call is being transferred to
     * @param direction The call direction (inbound, outbound-api, outbound-dial)
     * @param from The caller's number, in E.164 format
     * @param to The called number, in E.164 format
     */
    public TransferCallEvent(@NotNull String transferTo, @NotNull String direction, @NotNull String from, @NotNull String to) {
        this.transferTo = transferTo;
        this.direction = direction;
        this.from = from;
        this.to = to;
    }

    /**
     * @return The number this call is being transferred to
     */
    @NotNull
    public String getTransferTo() {
        return transferTo;
    }

    /**
     * @return The call direction (inbound, outbound-api, outbound-dial)
     */
    @NotNull
    public String getDirection() {
        return direction;
    }

    /**
     * @return The caller's number, in E.164 format
     */
    @NotNull
    public String getFrom() {
        return from;
    }

    /**
     * @return The called number, in E.164 format
     */
    @NotNull
    public String getTo() {
        return to;
    }
}
