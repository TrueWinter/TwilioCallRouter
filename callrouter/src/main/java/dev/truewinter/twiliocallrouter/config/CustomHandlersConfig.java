package dev.truewinter.twiliocallrouter.config;

import java.util.HashMap;
import java.util.Optional;

public class CustomHandlersConfig {
    private HashMap<String, CustomHandlerConfig> exact;
    private HashMap<String, CustomHandlerConfig> prefixes;

    CustomHandlersConfig(HashMap<String, CustomHandlerConfig> exact, HashMap<String, CustomHandlerConfig> prefixes) {
        this.exact = exact;
        this.prefixes = prefixes;
    }

    public HashMap<String, CustomHandlerConfig> getExactMap() {
        return exact;
    }

    public HashMap<String, CustomHandlerConfig> getPrefixMap() {
        return prefixes;
    }

    public boolean hasExact(String number) {
        return exact.containsKey(number);
    }

    public Optional<CustomHandlerConfig> getExact(String number) {
        if (exact.containsKey(number)) {
            return Optional.of(exact.get(number));
        } else {
            return Optional.empty();
        }
    }

    public Optional<CustomHandlerConfig> getPrefix(String number) {
        for (String prefix : prefixes.keySet()) {
            if (number.startsWith(prefix)) {
                return Optional.of(prefixes.get(prefix));
            }
        }

        return Optional.empty();
    }
}
