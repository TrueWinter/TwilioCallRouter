package dev.truewinter.twiliocallrouter.plugin;

/**
 * If your plugin integrates with another plugin, it must implement this interface
 */
public interface ExternalPlugin {
    /**
     * This event will be called once all plugins are loaded. You may only use the {@link Plugin#getPluginByName(String)} method after this has been called.
     */
    void onAllPluginsLoaded();
}
