package dev.truewinter.twiliocallrouter.routes;

import com.twilio.twiml.VoiceResponse;
import com.twilio.twiml.voice.Dial;
import com.twilio.twiml.voice.Reject;
import com.twilio.twiml.voice.Sip;
import dev.truewinter.twiliocallrouter.config.Config;
import io.javalin.http.Context;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ReferRoute implements Route {
    @Override
    public void handleRoute(Context ctx, Config config) {
        String transferTo = ctx.formParam("ReferTransferTarget");

        if (transferTo == null) {
            handleError(ctx);
            return;
        }

        System.out.println("Received request to transfer call to \"" + transferTo + "\"");

        Pattern pattern = Pattern.compile("<?([^<>]+)>?");
        Matcher matcher = pattern.matcher(transferTo);

        if (!matcher.find()) {
            System.out.println("Matcher failed");
            handleError(ctx);
            return;
        }

        String transferToSip = matcher.group(1);

        VoiceResponse.Builder voiceResponse = new VoiceResponse.Builder();
        Dial.Builder dial = new Dial.Builder();
        Sip.Builder sip = new Sip.Builder(transferToSip);
        dial.sip(sip.build());
        voiceResponse.dial(dial.build());

        ctx.result(voiceResponse.build().toXml());
    }

    private void handleError(Context ctx) {
        VoiceResponse.Builder builder = new VoiceResponse.Builder();
        Reject.Builder reject = new Reject.Builder();
        reject.reason(Reject.Reason.REJECTED);
        builder.reject(reject.build());

        ctx.status(400);
        ctx.result(builder.build().toXml());
    }
}
