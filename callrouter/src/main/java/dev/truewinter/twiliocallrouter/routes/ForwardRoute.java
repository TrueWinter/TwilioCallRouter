package dev.truewinter.twiliocallrouter.routes;

import com.twilio.twiml.VoiceResponse;
import com.twilio.twiml.voice.Dial;
import com.twilio.twiml.voice.Hangup;
import com.twilio.twiml.voice.Sip;
import dev.truewinter.twiliocallrouter.config.Config;
import dev.truewinter.twiliocallrouter.config.ForwardingConfig;
import dev.truewinter.twiliocallrouter.plugin.PluginManager;
import dev.truewinter.twiliocallrouter.plugin.event.ForwardCallEvent;
import io.javalin.http.Context;

import java.util.Arrays;
import java.util.concurrent.atomic.AtomicReference;

public class ForwardRoute extends Route {
    @Override
    public void handleRoute(Context ctx, Config config) {
        String status = ctx.formParam("DialCallStatus");
        String from = ctx.formParam("From");
        String to = ctx.formParam("To");
        String[] forwardOn = new String[]{"busy", "no-answer"};

        if (status == null || from == null || to == null) {
            System.out.println("Not forwarding call due to request not containing Status, From, and To fields.");
            endCall(ctx);
            return;
        }

        ForwardCallEvent event = PluginManager.fireEvent(new ForwardCallEvent(
                status,
                from,
                to
        ));

        if (event.isCancelled()) {
            respondWithReject(ctx, 200);
            return;
        }

        event.getCustomTwiML().ifPresentOrElse(voiceResponse -> {
            ctx.result(voiceResponse.toXml());
        }, () -> {
            AtomicReference<ForwardingConfig> forwardingConfig = new AtomicReference<>();
            if (config.getInboundConfig().getRoutedPrefixConfig(from).isPresent()) {
                config.getInboundConfig().getRoutedPrefixConfig(from).get().getForwardingConfig().ifPresent(forwardingConfig::set);
            } else if (config.getInboundConfig().getForwardingConfig().isPresent()) {
                forwardingConfig.set(config.getInboundConfig().getForwardingConfig().get());
            }

            if (Arrays.asList(forwardOn).contains(status) && forwardingConfig.get() != null) {
                VoiceResponse.Builder voiceResponse = new VoiceResponse.Builder();
                Dial.Builder dial = new Dial.Builder();
                dial.timeout(config.getInboundConfig().getTimeout());
                dial.answerOnBridge(config.getInboundConfig().answerOnBridge());

                if (forwardingConfig.get().isSip()) {
                    Sip.Builder sip = new Sip.Builder(forwardingConfig.get().getNumber());
                    dial.sip(sip.build());
                } else {
                    dial.number(forwardingConfig.get().getNumber());
                }

                System.out.println("Forwarding call from \"" + from + "\" to \"" + forwardingConfig.get().getNumber() + "\" due to \"" + status + "\" status");
                voiceResponse.dial(dial.build());
                ctx.result(voiceResponse.build().toXml());
            } else {
                endCall(ctx);
            }
        });
    }

    private void endCall(Context ctx) {
        VoiceResponse.Builder builder = new VoiceResponse.Builder();
        Hangup.Builder hangup = new Hangup.Builder();
        builder.hangup(hangup.build());
        ctx.result(builder.build().toXml());
    }
}
