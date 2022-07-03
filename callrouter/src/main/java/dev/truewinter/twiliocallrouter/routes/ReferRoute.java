package dev.truewinter.twiliocallrouter.routes;

import com.twilio.twiml.VoiceResponse;
import com.twilio.twiml.voice.Dial;
import com.twilio.twiml.voice.Sip;
import dev.truewinter.twiliocallrouter.config.Config;
import dev.truewinter.twiliocallrouter.plugin.PluginManager;
import dev.truewinter.twiliocallrouter.plugin.event.TransferCallEvent;
import io.javalin.http.Context;

import java.util.concurrent.atomic.AtomicReference;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ReferRoute extends Route {
    @Override
    public void handleRoute(Context ctx, Config config) {
        String transferTo = ctx.formParam("ReferTransferTarget");
        String direction = ctx.formParam("Direction");
        String fromNumber = ctx.formParam("From");
        String toNumber = ctx.formParam("To");
        AtomicReference<String> callerId = new AtomicReference<>();

        if (transferTo == null || direction == null || toNumber == null || fromNumber == null) {
            System.out.println("Not transferring call due to missing fields in request.");
            respondWithReject(ctx, 400);
            return;
        }

        TransferCallEvent event = PluginManager.fireEvent(new TransferCallEvent(
                transferTo,
                direction,
                fromNumber,
                toNumber
        ));

        if (event.isCancelled()) {
            respondWithReject(ctx, 200);
            return;
        }

        event.getCustomTwiML().ifPresentOrElse(voiceResponse -> {
            ctx.result(voiceResponse.toXml());
        }, () -> {
            if (direction.startsWith("outbound") && ctx.formParam("To") != null) {
                callerId.set(ctx.formParam("To"));
            } else if (direction.equals("inbound") && ctx.formParam("From") != null) {
                callerId.set(ctx.formParam("From"));
            }

            System.out.println("Received request to transfer call to \"" + transferTo + "\"");

            Pattern pattern = Pattern.compile("<?([^<>]+)>?");
            Matcher matcher = pattern.matcher(transferTo);

            if (!matcher.find()) {
                System.out.println("Matcher failed");
                respondWithReject(ctx, 400);
                return;
            }

            String transferToSip = matcher.group(1);

            VoiceResponse.Builder voiceResponse = new VoiceResponse.Builder();
            Dial.Builder dial = new Dial.Builder();

            if (callerId.get() != null) {
                dial.callerId(callerId.get());
            }

            Sip.Builder sip = new Sip.Builder(transferToSip);
            dial.sip(sip.build());
            voiceResponse.dial(dial.build());

            ctx.result(voiceResponse.build().toXml());
        });
    }
}
