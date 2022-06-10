package dev.truewinter.twiliocallrouter.routes;

import com.twilio.http.HttpMethod;
import com.twilio.twiml.VoiceResponse;
import com.twilio.twiml.voice.*;
import dev.truewinter.twiliocallrouter.Util;
import dev.truewinter.twiliocallrouter.config.Config;
import dev.truewinter.twiliocallrouter.config.CustomHandlerConfig;
import dev.truewinter.twiliocallrouter.config.InboundRoutedConfig;
import io.javalin.http.Context;

import java.net.MalformedURLException;
import java.util.Optional;

public class InboundRoute implements Route {
    public void handleRoute(Context ctx, Config config) {
        if (ctx.formParam("From") == null) {
            System.err.println("Received request without From header, responding with <Reject> and HTTP 400");

            VoiceResponse.Builder builder = new VoiceResponse.Builder();
            Reject.Builder reject = new Reject.Builder();
            reject.reason(Reject.Reason.REJECTED);
            builder.reject(reject.build());

            ctx.status(400);
            ctx.result(builder.build().toXml());
            return;
        }

        String number = ctx.formParam("From");

        Optional<CustomHandlerConfig> exact = config.getInboundConfig().getCustomHandlersConfig().getExact(number);
        Optional<CustomHandlerConfig> prefix = config.getInboundConfig().getCustomHandlersConfig().getPrefix(number);

        if (exact.isPresent() || prefix.isPresent()) {
            CustomHandlerConfig customHandlerConfig = exact.orElseGet(prefix::get);

            VoiceResponse.Builder response = new VoiceResponse.Builder();
            Redirect.Builder redirect = new Redirect.Builder(customHandlerConfig.getUrl());
            redirect.method(customHandlerConfig.getMethod().getMethod());
            response.redirect(redirect.build());

            System.out.println("Used custom handler for call from \"" + number + "\"");
            ctx.result(response.build().toXml());

            return;
        }

        config.getInboundConfig().getBlockPrefixConfig(number).ifPresentOrElse(b -> {
            if (b.hasCustomTwiml()) {
                System.out.println("Call from \"" + number + "\" blocked, responded with custom TwiML");
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
                System.out.println("Call from \"" + number + "\" blocked, responded with voice string");
            } else {
                Reject.Builder reject = new Reject.Builder();
                reject.reason(Reject.Reason.REJECTED);
                voiceResponse.reject(reject.build());
                System.out.println("Call from \"" + number + "\" blocked, rejected");
            }

            ctx.result(voiceResponse.build().toXml());
        }, () -> {
            VoiceResponse.Builder voiceResponse = new VoiceResponse.Builder();
            Dial.Builder dial = new Dial.Builder();
            dial.timeout(config.getInboundConfig().getTimeout());

            Optional<InboundRoutedConfig> routedConfig = config.getInboundConfig().getRoutedPrefixConfig(number);

            if (routedConfig.isPresent()) {
                String routedNumber = routedConfig.get().getNumber();
                boolean routedSip = routedConfig.get().isSip();

                if (routedSip) {
                    if (!routedNumber.startsWith("sip:")) {
                        System.out.println("SIP enabled for routed number \"" + routedNumber + "\", but does not start with \"sip:\". Adding this now.");
                        routedNumber = "sip:" + routedNumber;
                    }

                    Sip sip = new Sip.Builder(routedNumber).build();
                    dial.sip(sip);
                } else {
                    dial.number(routedNumber);
                }

                System.out.println("Calling \"" + routedNumber + "\" due to call from \"" + number + "\"");
            } else {
                String defaultNumber = config.getInboundConfig().getDefaultNumber();
                boolean defaultSip = config.getInboundConfig().isSip();

                if (defaultSip) {
                    if (!defaultNumber.startsWith("sip:")) {
                        System.out.println("SIP enabled for default number \"" + defaultNumber + "\", but does not start with \"sip:\". Adding this now.");
                        defaultNumber = "sip:" + defaultNumber;
                    }

                    Sip sip = new Sip.Builder(defaultNumber).build();
                    dial.sip(sip);
                } else {
                    dial.number(config.getInboundConfig().getDefaultNumber());
                }

                System.out.println("Calling \"" + defaultNumber + "\" due to call from \"" + number + "\"");
            }

            if (config.isReferEnabled()) {
                try {
                    dial.referUrl(Util.getReferUrl(ctx, config));
                    dial.referMethod(HttpMethod.POST);
                } catch (Exception e) {
                    System.err.println("Failed to get refer URL");
                    e.printStackTrace();
                }
            }

            if (config.getInboundConfig().hasForwardingConfig()) {
                try {
                    dial.action(Util.getForwardUrl(ctx, config));
                    dial.method(HttpMethod.POST);
                } catch (MalformedURLException e) {
                    System.err.println("Failed to get forward URL");
                    e.printStackTrace();
                }
            }

            voiceResponse.dial(dial.build());
            ctx.result(voiceResponse.build().toXml());
        });
    }
}
