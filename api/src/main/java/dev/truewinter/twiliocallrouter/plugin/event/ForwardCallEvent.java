package dev.truewinter.twiliocallrouter.plugin.event;

import dev.truewinter.twiliocallrouter.plugin.Event;
import org.jetbrains.annotations.NotNull;

/**
 * This event is fired before a call is forwarded
 */
public class ForwardCallEvent extends Event {
    private final String status;
    private final String from;
    private final String to;

    /**
     * @hidden
     * @param status The call's status
     * @param from The caller's number
     * @param to The called number
     */
    public ForwardCallEvent(@NotNull String status, @NotNull String from, @NotNull String to) {
        this.status = status;
        this.from = from;
        this.to = to;
    }

    /**
     * @return The status of the call (no-answer, busy, etc.)
     */
    @NotNull
    public String getStatus() {
        return status;
    }

    /**
     * @return The caller's number, in E.164 format
     */
    @NotNull
    public String getFrom() {
        return from;
    }

    /**
     * @return The number called, in E.164 format
     */
    @NotNull
    public String getTo() {
        return to;
    }
}
