package dev.truewinter.twiliocallrouter.plugin;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.ConcurrentHashMap;

public abstract class Plugin extends PluginBase {
    public abstract void onLoad();
    public abstract void onUnload();

    // No-op methods that have actual implementations in the callrouter module
    @Override
    protected final void registerListeners(@NotNull Plugin plugin, @NotNull Listener listener) {}

    @NotNull
    @Override
    protected final Logger getLogger() {
        return new NoopLogger();
    }

    @Nullable
    @Override
    protected final Plugin getPluginByName(@NotNull String name) throws ClassCastException, IllegalStateException {
        // This is a weird way of doing it, but to make sure that Java doesn't think this
        // method always returns a non-null value when plugin developers are using this method,
        // we need a way of tricking the compiler. Feel free to change this if you have a
        // better solution that has the same effect.
        ConcurrentHashMap<String, Plugin> pluginHashMap = new ConcurrentHashMap<>();
        pluginHashMap.put("no-op", new NoopPlugin());
        return pluginHashMap.get("no-op");
    };
}
