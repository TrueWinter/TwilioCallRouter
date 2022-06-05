package dev.truewinter.twiliocallrouter.routes;

import com.twilio.twiml.VoiceResponse;
import com.twilio.twiml.voice.*;
import dev.truewinter.twiliocallrouter.config.Config;
import dev.truewinter.twiliocallrouter.Util;
import dev.truewinter.twiliocallrouter.config.CustomHandlerConfig;
import dev.truewinter.twiliocallrouter.config.OutboundRoutedConfig;
import io.javalin.http.Context;

import java.util.Optional;

public class OutboundRoute implements Route {
    @Override
    public void handleRoute(Context ctx, Config config) {
        if (ctx.formParam("To") == null) {
            System.err.println("Received request without To header, responding with <Reject> and HTTP 400");

            VoiceResponse.Builder builder = new VoiceResponse.Builder();
            Reject.Builder reject = new Reject.Builder();
            reject.reason(Reject.Reason.REJECTED);
            builder.reject(reject.build());

            ctx.status(400);
            ctx.result(builder.build().toXml());
            return;
        }

        Optional<CustomHandlerConfig> exact = config.getOutboundConfig().getCustomHandlersConfig().getExact(Util.extractNumberFromSipUrl(ctx.formParam("To")));
        Optional<CustomHandlerConfig> prefix = config.getOutboundConfig().getCustomHandlersConfig().getPrefix(Util.extractNumberFromSipUrl(ctx.formParam("To")));

        if (exact.isPresent() || prefix.isPresent()) {
            CustomHandlerConfig customHandlerConfig = exact.orElseGet(prefix::get);

            VoiceResponse.Builder response = new VoiceResponse.Builder();
            Redirect.Builder redirect = new Redirect.Builder(customHandlerConfig.getUrl());
            redirect.method(customHandlerConfig.getMethod().getMethod());
            response.redirect(redirect.build());

            System.out.println("Used custom handler for call to \"" + Util.extractNumberFromSipUrl(ctx.formParam("To")) + "\"");
            ctx.result(response.build().toXml());

            return;
        }

        String number = Util.extractE164FromSipUrl(config.getOutboundConfig().getDefaultCountryCode(), ctx.formParam("To"));

        config.getOutboundConfig().getBlockPrefixConfig(number).ifPresentOrElse(b -> {
            if (b.hasCustomTwiml()) {
                System.out.println("Call to \"" + number + "\" blocked, responded with custom TwiML");
                ctx.result(b.getVoiceResponse().toXml());
                return;
            }

            VoiceResponse.Builder voiceResponse = new VoiceResponse.Builder();

            if (b.hasSayString()) {
                Say.Builder say = new Say.Builder(b.getSayString());
                say.voice(config.getVoice());

                if (config.getLanguage() != null) {
                    say.language(config.getLanguage());
                }

                voiceResponse.say(say.build());
                voiceResponse.hangup(new Hangup.Builder().build());
                System.out.println("Call to \"" + number + "\" blocked, responded with voice string");
            } else {
                Reject.Builder reject = new Reject.Builder();
                reject.reason(Reject.Reason.REJECTED);
                voiceResponse.reject(reject.build());
                System.out.println("Call to \"" + number + "\" blocked, rejected");
            }

            ctx.result(voiceResponse.build().toXml());
        }, () -> {
            VoiceResponse.Builder voiceResponse = new VoiceResponse.Builder();
            Dial.Builder dial = new Dial.Builder();
            dial.timeout(config.getOutboundConfig().getTimeout());
            dial.answerOnBridge(config.getOutboundConfig().answerOnBridge());

            Optional<OutboundRoutedConfig> routedConfig = config.getOutboundConfig().getRoutedPrefixConfig(number);

            if (routedConfig.isPresent()) {
                dial.callerId(routedConfig.get().getNumber());
                System.out.println("Calling \"" + number + "\" from \"" + routedConfig.get().getNumber() + "\"");
            } else {
                dial.callerId(config.getOutboundConfig().getDefaultNumber());
                System.out.println("Calling \"" + number + "\" from \"" + config.getOutboundConfig().getDefaultNumber() + "\"");
            }

            dial.number(number);
            voiceResponse.dial(dial.build());
            ctx.result(voiceResponse.build().toXml());

            /*VoiceResponse.Builder temp = new VoiceResponse.Builder();
            Reject.Builder tempReject = new Reject.Builder();
            tempReject.reason(Reject.Reason.REJECTED);
            temp.reject(tempReject.build());
            ctx.result(temp.build().toXml());*/
        });
    }
}
