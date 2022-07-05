package dev.truewinter.twiliocallrouter.plugin;

// The purpose of this "plugin" is to return at least something from
// the API when developers are still developing their plugin.
public class NoopPlugin extends Plugin {
    NoopPlugin() {}

    @Override
    public void onLoad() {

    }

    @Override
    public void onUnload() {

    }
}
