package dev.truewinter.twiliocallrouter.config;

import java.util.HashMap;
import java.util.Optional;

public class BaseDirectionalConfig<T extends BaseDirectionalRoutedConfig> {
    private int timeout;
    private boolean answerOnBridge;
    private String defaultNumber;
    private CustomHandlersConfig customHandlersConfig;

    private HashMap<String, BlockPrefixesConfig> blockPrefixesConfig;
    private HashMap<String, T> routedConfig;

    public BaseDirectionalConfig(int timeout, boolean answerOnBridge, String defaultNumber) {
        this.timeout = timeout;
        this.answerOnBridge = answerOnBridge;
        this.defaultNumber = defaultNumber;
    }

    public int getTimeout() {
        return timeout;
    }

    public boolean answerOnBridge() {
        return answerOnBridge;
    }

    public String getDefaultNumber() {
        return defaultNumber;
    }

    public void setCustomHandlersConfig(CustomHandlersConfig customHandlersConfig) {
        this.customHandlersConfig = customHandlersConfig;
    }

    public CustomHandlersConfig getCustomHandlersConfig() {
        return this.customHandlersConfig;
    }

    public void setRoutedConfig(HashMap<String, T> routedConfig) {
        this.routedConfig = routedConfig;
    }

    public HashMap<String, T> getRoutedConfig() {
        return this.routedConfig;
    }

    public Optional<T> getRoutedPrefixConfig(String number) {
        for (String routedPrefix : this.routedConfig.keySet()) {
            if (number.startsWith(routedPrefix)) {
                return Optional.of(this.routedConfig.get(routedPrefix));
            }
        }

        return Optional.empty();
    }

    public void setBlockPrefixesConfig(HashMap<String, BlockPrefixesConfig> blockPrefixesConfig) {
        this.blockPrefixesConfig = blockPrefixesConfig;
    }

    public HashMap<String, BlockPrefixesConfig> getBlockPrefixesConfig() {
        return this.blockPrefixesConfig;
    }

    public Optional<BlockPrefixesConfig> getBlockPrefixConfig(String number) {
        for (String blockPrefix : this.blockPrefixesConfig.keySet()) {
            if (number.startsWith(blockPrefix)) {
                return Optional.of(this.blockPrefixesConfig.get(blockPrefix));
            }
        }

        return Optional.empty();
    }
}
