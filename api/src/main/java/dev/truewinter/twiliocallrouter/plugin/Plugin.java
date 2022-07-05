package dev.truewinter.twiliocallrouter.plugin;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.ConcurrentHashMap;

/**
 * All plugins must extend this class
 */
public abstract class Plugin extends PluginBase {
    /**
     * This method is called when the plugin is loaded. It is only safe to interact with TwilioCallRouter after this is called.
     * @see ExternalPlugin#onAllPluginsLoaded()
     */
    public abstract void onLoad();

    /**
     * This method is called when the plugin is unloaded. This will happen when TwilioCallRouter is shut down,
     * or when an exception is thrown while the plugin is loading, registering listeners, or incorrectly using
     * the {@link Plugin#getPluginByName(String)} method.
     */
    public abstract void onUnload();

    // No-op methods that have actual implementations in the callrouter module

    /**
     * Use this method to register event listeners
     * @param plugin An instance of the plugin
     * @param listener An instance of the event listener class
     */
    @Override
    protected final void registerListeners(@NotNull Plugin plugin, @NotNull Listener listener) {}

    /**
     * @return the logger
     */
    @NotNull
    @Override
    protected final Logger getLogger() {
        return new NoopLogger();
    }

    /**
     * This method allows plugin developers to get an instance of another plugin installed on the
     * same instance of TwilioCallRouter. Using this method is only allowed after the
     * {@link ExternalPlugin#onAllPluginsLoaded()} method has been called. The requesting plugin
     * (the one calling this method) must implement {@link ExternalPlugin} to use this.
     * @param name The name of the plugin to get, as set in the plugin's plugin.yml
     * @return The plugin, or null if it isn't loaded
     * @throws ClassCastException if the requesting plugin does not implement {@link ExternalPlugin}
     * @throws IllegalStateException if this method is called before all plugins have loaded
     */
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
