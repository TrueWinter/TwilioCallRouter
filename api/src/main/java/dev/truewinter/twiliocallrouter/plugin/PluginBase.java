package dev.truewinter.twiliocallrouter.plugin;

import org.jetbrains.annotations.NotNull;

/**
 * @hidden
 */
public abstract class PluginBase {
    PluginBase() {}

    public abstract void onLoad();
    public abstract void onUnload();

    protected abstract void registerListeners(@NotNull Plugin plugin, @NotNull Listener listener);
    protected abstract Logger getLogger();
    protected abstract Plugin getPluginByName(String name) throws ClassCastException, IllegalStateException;
}
