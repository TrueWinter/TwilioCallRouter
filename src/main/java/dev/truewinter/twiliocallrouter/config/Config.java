package dev.truewinter.twiliocallrouter.config;

import com.twilio.twiml.voice.Say;
import dev.dejvokep.boostedyaml.YamlDocument;
import dev.dejvokep.boostedyaml.block.implementation.Section;
import dev.dejvokep.boostedyaml.dvs.versioning.BasicVersioning;
import dev.dejvokep.boostedyaml.settings.dumper.DumperSettings;
import dev.dejvokep.boostedyaml.settings.general.GeneralSettings;
import dev.dejvokep.boostedyaml.settings.loader.LoaderSettings;
import dev.dejvokep.boostedyaml.settings.updater.UpdaterSettings;

import javax.swing.text.html.Option;
import java.io.File;
import java.util.HashMap;
import java.util.Objects;
import java.util.Optional;

public class Config {
    private YamlDocument config;
    private int port;
    private boolean authEnabled;
    private String username;
    private String password;
    private Say.Voice voice;
    private Say.Language language;
    private boolean enableRefer;
    private boolean referForceHttps;

    private InboundConfig inboundConfig;
    private OutboundConfig outboundConfig;

    public Config(File configFile) throws Exception {
        config = YamlDocument.create(
                configFile,
                Objects.requireNonNull(getClass().getClassLoader().getResourceAsStream("config.yml")),
                GeneralSettings.builder(GeneralSettings.DEFAULT).setUseDefaults(false).build(),
                LoaderSettings.builder().setAutoUpdate(true).build(),
                DumperSettings.DEFAULT,
                UpdaterSettings.builder().setVersioning(new BasicVersioning("config-version")).build()
        );

        port = config.getInt("port", 8500);
        authEnabled = config.getBoolean("auth", true);
        username = config.getString("username", "twilio");
        password = config.getString("password", "callrouter");
        enableRefer = config.getBoolean("enable_refer", false);
        referForceHttps = config.getBoolean("refer_force_https", false);

        try {
            voice = Say.Voice.valueOf(config.getString("voice", "ALICE"));
            System.out.println("Using voice \"" + voice + "\"");
        } catch(Exception e) {
            System.out.println("Voice does not exist, switching to default");
            voice = Say.Voice.ALICE;
        }

        try {
            language = Say.Language.valueOf(config.getString("language", "EN_GB"));
            System.out.println("Using language \"" + language + "\"");
        } catch (Exception e) {
            System.out.println("Language invalid, switching to default");
            language = Say.Language.EN_GB;
        }

        // Inbound config
        Section inbound = config.getSection("inbound");
        int inboundTimeout = inbound.getInt("timeout", 20);
        boolean inboundSip = inbound.getBoolean("sip", true);
        String inboundDefault = inbound.getString("default", "sip:442079460123@example.sip.twilio.com");
        inboundConfig = new InboundConfig(inboundTimeout, inboundDefault, inboundSip);

        Section forwardingConfig = inbound.getSection("forward_on_no_answer");
        loadForwardingConfig(forwardingConfig).ifPresent(f -> {
            inboundConfig.setForwardingConfig(f);
        });

        Section inboundCustomHandlersConfig = inbound.getSection("custom_handlers");
        inboundConfig.setCustomHandlersConfig(loadCustomHandlersConfig(inboundCustomHandlersConfig));

        Section inboundBlockPrefixesConfig = inbound.getSection("block_prefixes");
        inboundConfig.setBlockPrefixesConfig(loadBlockedPrefixesConfig(inboundBlockPrefixesConfig));

        Section inboundRoutedConfig = inbound.getSection("routed");
        HashMap<String, InboundRoutedConfig> inboundRoutedConfigMap = new HashMap<>();

        if (inboundRoutedConfig != null) {
            for (String ircCountry : inboundRoutedConfig.getRoutesAsStrings(false)) {
                boolean ircCountrySip = inboundRoutedConfig.getSection(ircCountry).getBoolean("sip");
                String ircCountryNumber = inboundRoutedConfig.getSection(ircCountry).getString("number");

                InboundRoutedConfig irc = new InboundRoutedConfig(ircCountry, ircCountrySip, ircCountryNumber);
                Section ircForwardingConfig = inboundRoutedConfig.getSection(ircCountry).getSection("forward_on_no_answer");

                if (ircForwardingConfig != null) {
                    boolean ircForwardingSip = ircForwardingConfig.getBoolean("sip");
                    String ircForwardingNumber = ircForwardingConfig.getString("number");

                    irc.setForwardingConfig(new ForwardingConfig(ircForwardingSip, ircForwardingNumber));
                }

                inboundRoutedConfigMap.put(ircCountry, irc);
            }
        }

        inboundConfig.setRoutedConfig(inboundRoutedConfigMap);

        // Outbound config
        Section outbound = config.getSection("outbound");
        int outboundTimeout = outbound.getInt("timeout", 20);
        String outboundDefault = outbound.getString("default", "+442079460123");
        String outboundDefaultCountryCode = outbound.getString("default_country_code", "+44");
        outboundConfig = new OutboundConfig(outboundTimeout, outboundDefault, outboundDefaultCountryCode);

        Section outboundCustomHandlersConfig = outbound.getSection("custom_handlers");
        outboundConfig.setCustomHandlersConfig(loadCustomHandlersConfig(outboundCustomHandlersConfig));

        Section outboundBlockPrefixesConfig = outbound.getSection("block_prefixes");
        outboundConfig.setBlockPrefixesConfig(loadBlockedPrefixesConfig(outboundBlockPrefixesConfig));

        Section outboundRoutedConfig = outbound.getSection("routed");
        HashMap<String, OutboundRoutedConfig> outboundRoutedConfigMap = new HashMap<>();

        if (outboundRoutedConfig != null) {
            for (String orcCountry : outboundRoutedConfig.getRoutesAsStrings(false)) {
                String orcCountryNumber = outboundRoutedConfig.getString(orcCountry);

                OutboundRoutedConfig orc = new OutboundRoutedConfig(orcCountry, orcCountryNumber);
                outboundRoutedConfigMap.put(orcCountry, orc);
            }
        }

        outboundConfig.setRoutedConfig(outboundRoutedConfigMap);
    }

    public int getPort() {
        return port;
    }

    public boolean isAuthEnabled() {
        return authEnabled;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public boolean isValidLogin(String user, String pass) {
        return user.equals(username) && pass.equals(password);
    }

    public Say.Voice getVoice() {
        return this.voice;
    }

    public Say.Language getLanguage() {
        if (voice.toString().startsWith("Polly")) {
            return null;
        }

        return language;
    }

    public boolean isReferEnabled() {
        return enableRefer;
    }

    public boolean isReferHttpsForced() {
        return this.referForceHttps;
    }

    public InboundConfig getInboundConfig() {
        return inboundConfig;
    }

    public OutboundConfig getOutboundConfig() {
        return outboundConfig;
    }

    private HashMap<String, BlockPrefixesConfig> loadBlockedPrefixesConfig(Section section) throws Exception {
        HashMap<String, BlockPrefixesConfig> outMap = new HashMap<>();
        if (section != null) {
            for (String prefix : section.getRoutesAsStrings(false)) {
                Section prefixSection = section.getSection(prefix);

                if (prefixSection.contains("say")) {
                    if (prefixSection.getString("say").equals("false")) {
                        outMap.put(prefix, new BlockPrefixesConfig(prefix));
                    } else {
                        outMap.put(prefix, new BlockPrefixesConfig(prefix, prefixSection.getString("say")));
                    }

                    continue;
                }

                if (prefixSection.contains("twiml")) {
                    outMap.put(prefix, new BlockPrefixesConfig(prefix)
                            .setTwiml(prefixSection.getString("twiml")));
                    continue;
                }

                if (prefixSection.contains("play")) {
                    outMap.put(prefix, new BlockPrefixesConfig(prefix)
                            .setPlayURL(prefixSection.getString("play")));
                    continue;
                }

                throw new Exception("block_prefixes config option must contain one of: say, twiml, play");
            }
        }

        return outMap;
    }

    private CustomHandlersConfig loadCustomHandlersConfig(Section section) {
        Section customHandlersExactConfig = section.getSection("exact");
        HashMap<String, CustomHandlerConfig> customHandlersExactMap = new HashMap<>();

        if (customHandlersExactConfig != null) {
            for (String number : customHandlersExactConfig.getRoutesAsStrings(false)) {
                customHandlersExactMap.put(number,
                        new CustomHandlerConfig(
                                customHandlersExactConfig.getSection(number).getString("url"),
                                CustomHandlerMethod.valueOf(customHandlersExactConfig.getSection(number).getString("method"))
                        )
                );
            }
        }

        Section customHandlersPrefixConfig = section.getSection("prefix");
        HashMap<String, CustomHandlerConfig> customHandlersPrefixMap = new HashMap<>();

        if (customHandlersPrefixConfig != null) {
            for (String number : customHandlersPrefixConfig.getRoutesAsStrings(false)) {
                customHandlersPrefixMap.put(number,
                        new CustomHandlerConfig(
                                customHandlersPrefixConfig.getSection(number).getString("url"),
                                CustomHandlerMethod.valueOf(customHandlersPrefixConfig.getSection(number).getString("method"))
                        )
                );
            }
        }

        return new CustomHandlersConfig(customHandlersExactMap, customHandlersPrefixMap);
    }

    private Optional<ForwardingConfig> loadForwardingConfig(Section section) {
        if (section == null) {
            return Optional.empty();
        }

        boolean enabled = section.getBoolean("enabled");
        boolean sip = section.getBoolean("sip");
        String number = section.getString("number");

        if (!enabled) {
            return Optional.empty();
        }

        if (number.equals(inboundConfig.getDefaultNumber())) {
            System.err.println("Forward number cannot be the same as default number");
            return Optional.empty();
        }

        return Optional.of(new ForwardingConfig(sip, number));
    }
}

