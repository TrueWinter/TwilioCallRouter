# Plugins

> Important: The plugin API is still under development, and breaking changes can be introduced between versions.

We're using Maven here, but Gradle is also supported. First, add the Jitpack repository.
```xml
<repositories>
    <repository>
        <id>jitpack.io</id>
        <url>https://jitpack.io</url>
    </repository>
</repositories>
```

Then, add the API module. It may take a minute or two as the API module is built the first time it's requested, not in advance.
```xml
<dependencies>
    <dependency>
        <groupId>dev.truewinter.TwilioCallRouter</groupId>
        <artifactId>api</artifactId>
        <version>0.0.10</version>
    </dependency>
</dependencies>
```

## Plugin Information File

You will need to create a resource file called `plugin.yml`. The contents of the file should be as follows:

```yml
# A unique name for your plugin. Please only use alphanumeric characters and dashes.
name: ExamplePlugin

# The plugin's main class
main_class: com.example.exampleplugin.ExamplePlugin
```

## Usage

Now you can start developing your plugin. The plugin's main class must extend `Plugin`.
```java
public class TestPlugin extends Plugin {
    // Important: It is not safe to interact with TwilioCallRouter until the onLoad() method is called
    @Override
    public void onLoad() {
        getLogger().info("Loaded plugin");
        registerListeners(this, new EventListener());
        getLogger().info("Registered event listeners");
    }

    @Override
    public void onUnload() {
        getLogger().info("Unloaded plugin");
    }
}
```

Registering event listeners requires an instance of a class that implements `Listener`. An important thing to note is that TwilioCallRouter events are fired **before** they are handled (e.g. the OutgoingCallEvent is fired after an outgoing call is made, but before TwilioCallRouter responds to Twilio's request).
```java
public class EventListener implements Listener {
    @EventHandler
    public void onOutboundCall(OutboundCallEvent event) {
        // Block calls to New Zealand
        if (event.getToNumber164().startsWith("+64")) {
            // Cancelling events will result in Twilio receiving either a reject or
            // hangup response, depending on which is more appropriate for the situation.
            // You could also use event.setCustomTwiML() to set custom TwiML.
            event.setCancelled(true);
        }
    }
}
```

Note that all listeners are blocking, so please ensure your plugin does what it needs as quickly as possible.

### Using Other Installed Plugins

TwilioCallRouter allows plugins to interact with other plugins installed on the same instance. To do so, your main class must implement `ExternalPlugin`. Only after the `onAllPluginsLoaded()` method has been called, can you use the `getPluginByName()` method to get an instance of another plugin.

## Docs

JavaDocs for the latest release are available at [https://twiliocallrouter.truewinter.dev/docs/](https://twiliocallrouter.truewinter.dev/docs/).