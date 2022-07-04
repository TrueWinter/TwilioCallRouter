package dev.truewinter.twiliocallrouter.plugin;

import dev.truewinter.twiliocallrouter.TwilioCallRouter;

import java.io.IOException;

// IMPORTANT: If you add methods to this class intended for plugin use, you
// will also need to add no-op methods to the Plugin class in the api module.
// This weird arrangement of classes is required to keep the internal methods
// hidden from the plugin, while still allowing everything to work well.
public abstract class Plugin {
    private String name;

    public abstract void onLoad();
    public abstract void onUnload();

    protected void registerListeners(Plugin plugin, Listener listener) {
        try {
            PluginManager.registerListener(plugin, listener);
        } catch (Exception e) {
            TwilioCallRouter.getLogger().error("Failed to register event listeners, unloading plugin");
            try {
                PluginManager.unloadPlugin(plugin);
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
            e.printStackTrace();
        }
    }

    protected final Logger getLogger() {
        return new PluginLogger(this);
    }

    protected Plugin getPluginByName(String name) throws ClassCastException, IllegalStateException {
        try {
            return PluginManager.getPluginByName(this, name);
        } catch (Exception e) {
            try {
                PluginManager.unloadPlugin(this);
            } catch (Exception ex) {
                TwilioCallRouter.getLogger().error("Failed to unload plugin", ex);
            }
            throw e;
        }
    }

    // INTERNAL METHOD
    public String getName() {
        return name;
    }

    // INTERNAL METHOD
    public void setName(String name) {
        this.name = name;
    }
}
