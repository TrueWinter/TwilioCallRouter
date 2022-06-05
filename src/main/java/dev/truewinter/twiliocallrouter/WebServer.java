package dev.truewinter.twiliocallrouter;

import dev.truewinter.twiliocallrouter.config.Config;
import dev.truewinter.twiliocallrouter.routes.InboundRoute;
import dev.truewinter.twiliocallrouter.routes.OutboundRoute;
import io.javalin.Javalin;
import io.javalin.core.util.Header;
import io.javalin.http.Context;

public class WebServer extends Thread {
    private Config config;
    private Javalin server;

    public WebServer(Config config) {
        this.config = config;
    }

    private void setAuthHeaders(Context ctx) {
        ctx.header("WWW-Authenticate", "Basic realm=\"TwilioCallRouter\"");
        ctx.status(401);
        ctx.result("Login required");
    }

    private boolean doAuthIfEnabled(Context ctx) {
        if (config.isAuthEnabled()) {
            if (!ctx.basicAuthCredentialsExist()) {
                setAuthHeaders(ctx);
                return true;
            }

            if (!config.isValidLogin(ctx.basicAuthCredentials().getUsername(), ctx.basicAuthCredentials().getPassword())) {
                setAuthHeaders(ctx);
                return true;
            }
        }

        return false;
    }

    @Override
    public void run() {
        server = Javalin.create(c -> {
            c.showJavalinBanner = false;
        }).start(config.getPort());

        server.before(context -> {
            try {
                context.header(Header.SERVER, "TwilioCallRouter/" + Util.getVersion());
            } catch (Exception e) {
                context.header(Header.SERVER, "TwilioCallRouter");
            }
        });

        server.post("/inbound", ctx -> {
            if (doAuthIfEnabled(ctx)) {
                return;
            }

            ctx.header(Header.CONTENT_TYPE, "text/xml");
            new InboundRoute().handleRoute(ctx, config);
        });

        server.post("/outbound", ctx -> {
            if (doAuthIfEnabled(ctx)) {
                return;
            }

            ctx.header(Header.CONTENT_TYPE, "text/xml");
            new OutboundRoute().handleRoute(ctx, config);
        });
    }
}
