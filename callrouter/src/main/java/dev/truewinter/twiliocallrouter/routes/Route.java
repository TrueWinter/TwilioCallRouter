package dev.truewinter.twiliocallrouter.routes;

import com.twilio.twiml.VoiceResponse;
import com.twilio.twiml.voice.Reject;
import dev.truewinter.twiliocallrouter.config.Config;
import io.javalin.http.Context;

public abstract class Route {
    abstract void handleRoute(Context ctx, Config config);

    protected void respondWithReject(Context ctx, int statusCode) {
        VoiceResponse.Builder builder = new VoiceResponse.Builder();
        Reject.Builder reject = new Reject.Builder();
        reject.reason(Reject.Reason.REJECTED);
        builder.reject(reject.build());

        ctx.status(statusCode);
        ctx.result(builder.build().toXml());
    }
}
