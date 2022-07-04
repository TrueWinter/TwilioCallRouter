package dev.truewinter.twiliocallrouter.plugin;

import dev.dejvokep.boostedyaml.YamlDocument;
import dev.truewinter.twiliocallrouter.TwilioCallRouter;
import org.slf4j.Logger;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.security.InvalidParameterException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class PluginManager {
    private static final Logger logger = TwilioCallRouter.getLogger();
    private static boolean allPluginsLoaded = false;
    private static final ConcurrentHashMap<String, Plugin> plugins = new ConcurrentHashMap<>();
    private static final ConcurrentHashMap<Class<? extends Event>, LinkedHashMap<Plugin, Method>> events = new ConcurrentHashMap<>();

    public static void loadPlugins(List<File> pluginJars) {
        for (File file : pluginJars) {
            Plugin thisPlugin = null;
            try {
                URLClassLoader plugin = new URLClassLoader(
                        new URL[]{file.toURI().toURL()},
                        TwilioCallRouter.class.getClassLoader()
                );

                YamlDocument pluginConfig = getPluginYaml(plugin);
                String mainClass = getPluginMainClass(pluginConfig);
                String name = getPluginName(pluginConfig);

                Class<? extends Plugin> pluginClass = getPluginAsSubclass(plugin, mainClass);
                Plugin pluginInstance = pluginClass.getDeclaredConstructor().newInstance();
                // thisPlugin is simply here so that if there's an exception past this point,
                // the exception handler will know what plugin caused it.
                thisPlugin = pluginInstance;
                pluginInstance.setName(name);
                plugins.put(name, pluginInstance);
                pluginClass.getMethod("onLoad").invoke(pluginInstance);

                logger.info("Plugin \"" + pluginInstance.getName() + "\" loaded.");
                plugin.close();
            } catch (Exception e) {
                if (thisPlugin != null) {
                    plugins.remove(thisPlugin.getName());
                    try {
                        thisPlugin.getClass().getMethod("onUnload").invoke(thisPlugin);
                    } catch (Exception ex) {
                        throw new RuntimeException(ex);
                    }
                }

                logger.error("Unable to load plugin with file name \"" + file.getName() + "\".", e);
            }
        }

        allPluginsLoaded = true;
        plugins.forEach((name, plugin) -> {
            if (doesPluginImplementExternalPlugin(plugin)) {
                try {
                    plugin.getClass().getMethod("onAllPluginsLoaded").invoke(plugin);
                } catch (Exception e) {
                    logger.error("Failed to call onAllPluginsLoaded method for plugin \"" + name + "\".", e);
                }
            }
        });
    }

    public static void unloadPlugin(Plugin plugin) throws IOException {
        plugins.remove(plugin.getName());

        events.forEach((eventClass, listeners) -> {
            events.get(eventClass).remove(plugin);
        });

        try {
            plugin.getClass().getMethod("onUnload").invoke(plugin);
        } catch (Exception e) {
            logger.error("Unable to call onUnload method while unloading plugin \"" + plugin.getName() + "\"", e);
        }

        logger.info("Unloaded plugin \"" + plugin.getName() + "\"");
    }

    // By splitting this class into many simple methods, it makes stack traces
    // much easier to read and therefore makes troubleshooting easier too.
    private static YamlDocument getPluginYaml(URLClassLoader plugin) throws IOException {
        return YamlDocument.create(Objects.requireNonNull(plugin.getResourceAsStream("plugin.yml")));
    }

    private static String getPluginMainClass(YamlDocument pluginConfig) throws InvalidParameterException {
        String mainClass = pluginConfig.getString("main_class");

        if (mainClass == null) {
            throw new InvalidParameterException("Plugin YAML does not contain main class");
        }

        return mainClass;
    }

    private static String getPluginName(YamlDocument pluginConfig) throws InvalidParameterException {
        String name = pluginConfig.getString("name");

        if (name == null) {
            throw new InvalidParameterException("Plugin YAML does not contain name");
        }

        return name;
    }

    private static Class<? extends Plugin> getPluginAsSubclass(URLClassLoader plugin, String mainClass) throws Exception {
        try {
            return Class.forName(mainClass, false, plugin).asSubclass(Plugin.class);
        } catch (ClassCastException e) {
            throw new Exception("Plugin does not extend Plugin class");
        }
    }

    public static synchronized void registerListener(Plugin plugin, Listener listener) throws Exception {
        if (!plugins.contains(plugin)) {
            throw new Exception("Failed to register listeners for plugin \"" + plugin.getName() + "\". Plugin not loaded.");
        }

        for (Method method : listener.getClass().getMethods()) {
            if (method.isAnnotationPresent(EventHandler.class)) {
                if (method.getParameterCount() != 1) {
                    throw new Exception("Failed to load listener method \"" + method.getName() + "\" in plugin \"" + plugin.getName() + "\". Event handler methods must contain only one parameter.");
                }

                try {
                    Class<?> firstParam = method.getParameterTypes()[0];
                    // Verify it extends Event
                    firstParam.asSubclass(Event.class);

                    if (!events.containsKey(firstParam)) {
                        events.put(firstParam.asSubclass(Event.class), new LinkedHashMap<>());
                    }

                    events.get(firstParam.asSubclass(Event.class)).put(plugin, method);
                } catch (Exception e) {
                    throw new Exception("Failed to load listener method \"" + method.getName() + "\" in plugin \"" + plugin.getName() + "\":", e);
                }
            }
        }
    }

    public static synchronized <T extends Event> T fireEvent(T event) {
        if (!events.containsKey(event.getClass())) return event;

        events.get(event.getClass()).forEach((listener, method) -> {
            if (!(event.isCancelled() && !method.getAnnotation(EventHandler.class).receiveCancelled())) {
                try {
                    method.invoke(listener, event);
                } catch (Exception e) {
                    logger.error("Failed to fire event", e);
                }
            }
        });

        return event;
    }

    private static boolean doesPluginImplementExternalPlugin(Plugin requestingPlugin) {
        try {
            return ExternalPlugin.class.isAssignableFrom(requestingPlugin.getClass());
        } catch (Exception e) {
            return false;
        }
    }

    protected static synchronized Plugin getPluginByName(Plugin requestingPlugin, String name) throws ClassCastException, IllegalStateException {
        if (!doesPluginImplementExternalPlugin(requestingPlugin)) {
            throw new ClassCastException("Plugin must implement ExternalPlugin to use the getPluginByName method.");
        }

        if (!allPluginsLoaded) {
            throw new IllegalStateException("The getPluginByName method can only be called after all plugins have been loaded.");
        }

        if (!plugins.containsKey(name)) {
            return null;
        }

        return plugins.get(name);
    }

    public static synchronized void handleShutdown() {
        plugins.forEach((name, plugin) -> {
            try {
                unloadPlugin(plugin);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
    }
}
