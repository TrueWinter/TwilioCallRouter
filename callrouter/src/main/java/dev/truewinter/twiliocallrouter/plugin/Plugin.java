package dev.truewinter.twiliocallrouter.plugin;

import java.io.IOException;
import java.util.Optional;

// IMPORTANT: If you add methods to this class intended for plugin use, you
// will also need to add no-op methods to the Plugin class in the api module
public abstract class Plugin {
    private String name;

    public abstract void onLoad();
    public abstract void onUnload();

    protected void registerListeners(Plugin plugin, Listener listener) {
        try {
            PluginManager.registerListener(plugin, listener);
        } catch (Exception e) {
            System.err.println("Failed to register event listeners, unloading plugin");
            try {
                PluginManager.unloadPlugin(plugin);
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
            e.printStackTrace();
        }
    }

    protected Plugin getPluginByName(String name) {
        return PluginManager.getPluginByName(name);
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
