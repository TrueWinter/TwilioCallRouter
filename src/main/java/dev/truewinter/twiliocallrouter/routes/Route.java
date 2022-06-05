package dev.truewinter.twiliocallrouter.routes;

import dev.truewinter.twiliocallrouter.config.Config;
import io.javalin.http.Context;

public interface Route {
    void handleRoute(Context ctx, Config config);
}
