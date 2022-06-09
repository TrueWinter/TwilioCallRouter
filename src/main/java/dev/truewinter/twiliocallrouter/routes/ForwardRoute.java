package dev.truewinter.twiliocallrouter.routes;

import com.twilio.twiml.VoiceResponse;
import com.twilio.twiml.voice.Dial;
import com.twilio.twiml.voice.Hangup;
import com.twilio.twiml.voice.Sip;
import dev.truewinter.twiliocallrouter.config.Config;
import dev.truewinter.twiliocallrouter.config.ForwardingConfig;
import io.javalin.http.Context;

import java.util.Arrays;
import java.util.concurrent.atomic.AtomicReference;

public class ForwardRoute implements Route {
    @Override
    public void handleRoute(Context ctx, Config config) {
        String status = ctx.formParam("DialCallStatus");
        String from = ctx.formParam("From");
        String[] forwardOn = new String[]{"busy", "no-answer"};

        if (status == null || from == null) {
            endCall(ctx);
            return;
        }

        AtomicReference<ForwardingConfig> forwardingConfig = new AtomicReference<>();
        if (config.getInboundConfig().getRoutedPrefixConfig(from).isPresent()) {
            config.getInboundConfig().getRoutedPrefixConfig(from).get().getForwardingConfig().ifPresent(forwardingConfig::set);
        } else if (config.getInboundConfig().getForwardingConfig().isPresent()){
            forwardingConfig.set(config.getInboundConfig().getForwardingConfig().get());
        }

        if (Arrays.asList(forwardOn).contains(status) && forwardingConfig.get() != null) {
            VoiceResponse.Builder voiceResponse = new VoiceResponse.Builder();
            Dial.Builder dial = new Dial.Builder();

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
    }

    private void endCall(Context ctx) {
        VoiceResponse.Builder builder = new VoiceResponse.Builder();
        Hangup.Builder hangup = new Hangup.Builder();
        builder.hangup(hangup.build());
        ctx.result(builder.build().toXml());
    }
}
