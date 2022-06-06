package dev.truewinter.twiliocallrouter.config;

import com.twilio.twiml.voice.Say;
import dev.dejvokep.boostedyaml.YamlDocument;
import dev.dejvokep.boostedyaml.block.implementation.Section;
import dev.dejvokep.boostedyaml.dvs.versioning.BasicVersioning;
import dev.dejvokep.boostedyaml.settings.dumper.DumperSettings;
import dev.dejvokep.boostedyaml.settings.general.GeneralSettings;
import dev.dejvokep.boostedyaml.settings.loader.LoaderSettings;
import dev.dejvokep.boostedyaml.settings.updater.UpdaterSettings;

import java.io.File;
import java.util.HashMap;
import java.util.Objects;

public class Config {
    private YamlDocument config;
    private int port;
    private boolean authEnabled;
    private String username;
    private String password;
    private Say.Voice voice;
    private Say.Language language;

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
        boolean inboundAnswerOnBridge = inbound.getBoolean("answerOnBridge", true);
        String inboundDefault = inbound.getString("default", "sip:442079460123@example.sip.twilio.com");
        inboundConfig = new InboundConfig(inboundTimeout, inboundAnswerOnBridge, inboundDefault, inboundSip);

        Section inboundCustomHandlersConfig = inbound.getSection("custom_handlers");
        Section inboundCustomHandlersExactConfig = inboundCustomHandlersConfig.getSection("exact");
        HashMap<String, CustomHandlerConfig> inboundCustomHandlersExactMap = new HashMap<>();

        if (inboundCustomHandlersExactConfig != null) {
            for (String number : inboundCustomHandlersExactConfig.getRoutesAsStrings(false)) {
                inboundCustomHandlersExactMap.put(number,
                        new CustomHandlerConfig(
                            inboundCustomHandlersExactConfig.getSection(number).getString("url"),
                            CustomHandlerMethod.valueOf(inboundCustomHandlersExactConfig.getSection(number).getString("method"))
                        )
                );
            }
        }

        Section inboundCustomHandlersPrefixConfig = inboundCustomHandlersConfig.getSection("prefix");
        HashMap<String, CustomHandlerConfig> inboundCustomHandlersPrefixMap = new HashMap<>();

        if (inboundCustomHandlersPrefixConfig != null) {
            for (String number : inboundCustomHandlersPrefixConfig.getRoutesAsStrings(false)) {
                inboundCustomHandlersPrefixMap.put(number,
                        new CustomHandlerConfig(
                            inboundCustomHandlersPrefixConfig.getSection(number).getString("url"),
                            CustomHandlerMethod.valueOf(inboundCustomHandlersPrefixConfig.getSection(number).getString("method"))
                        )
                );
            }
        }

        inboundConfig.setCustomHandlersConfig(new CustomHandlersConfig(inboundCustomHandlersExactMap, inboundCustomHandlersPrefixMap));

        Section inboundBlockPrefixesConfig = inbound.getSection("block_prefixes");
        HashMap<String, BlockPrefixesConfig> inboundBlockPrefixesMap = new HashMap<>();

        if (inboundBlockPrefixesConfig != null) {
            for (String ibpCountry : inboundBlockPrefixesConfig.getRoutesAsStrings(false)) {
                Section ibpCountrySection = inboundBlockPrefixesConfig.getSection(ibpCountry);

                if (ibpCountrySection.contains("say")) {
                    if (ibpCountrySection.getString("say").equals("false")) {
                        inboundBlockPrefixesMap.put(ibpCountry, new BlockPrefixesConfig(ibpCountry));
                    } else {
                        inboundBlockPrefixesMap.put(ibpCountry, new BlockPrefixesConfig(ibpCountry, ibpCountrySection.getString("say")));
                    }

                    continue;
                }

                if (ibpCountrySection.contains("twiml")) {
                    inboundBlockPrefixesMap.put(ibpCountry, new BlockPrefixesConfig(ibpCountry)
                            .setTwiml(ibpCountrySection.getString("twiml")));
                    continue;
                }

                throw new Exception("block_prefixes config option must contain either say or twiml option");
            }
        }

        inboundConfig.setBlockPrefixesConfig(inboundBlockPrefixesMap);

        Section inboundRoutedConfig = inbound.getSection("routed");
        HashMap<String, InboundRoutedConfig> inboundRoutedConfigMap = new HashMap<>();

        if (inboundRoutedConfig != null) {
            for (String ircCountry : inboundRoutedConfig.getRoutesAsStrings(false)) {
                boolean ircCountrySip = inboundRoutedConfig.getSection(ircCountry).getBoolean("sip");
                String ircCountryNumber = inboundRoutedConfig.getSection(ircCountry).getString("number");

                InboundRoutedConfig irc = new InboundRoutedConfig(ircCountry, ircCountrySip, ircCountryNumber);
                inboundRoutedConfigMap.put(ircCountry, irc);
            }
        }

        inboundConfig.setRoutedConfig(inboundRoutedConfigMap);

        // Outbound config
        Section outbound = config.getSection("outbound");
        int outboundTimeout = outbound.getInt("timeout", 20);
        boolean outboundAnswerOnBridge = outbound.getBoolean("answerOnBridge", true);
        String outboundDefault = outbound.getString("default", "+442079460123");
        String outboundDefaultCountryCode = outbound.getString("default_country_code", "+44");
        outboundConfig = new OutboundConfig(outboundTimeout, outboundAnswerOnBridge, outboundDefault, outboundDefaultCountryCode);

        Section outboundCustomHandlersConfig = outbound.getSection("custom_handlers");
        Section outboundCustomHandlersExactConfig = outboundCustomHandlersConfig.getSection("exact");
        HashMap<String, CustomHandlerConfig> outboundCustomHandlersExactMap = new HashMap<>();

        if (outboundCustomHandlersExactConfig != null) {
            for (String number : outboundCustomHandlersExactConfig.getRoutesAsStrings(false)) {
                outboundCustomHandlersExactMap.put(number,
                        new CustomHandlerConfig(
                            outboundCustomHandlersExactConfig.getSection(number).getString("url"),
                            CustomHandlerMethod.valueOf(outboundCustomHandlersExactConfig.getSection(number).getString("method"))
                        )
                );
            }
        }

        Section outboundCustomHandlersPrefixConfig = inboundCustomHandlersConfig.getSection("prefix");
        HashMap<String, CustomHandlerConfig> outboundCustomHandlersPrefixMap = new HashMap<>();

        if (outboundCustomHandlersPrefixConfig != null) {
            for (String number : outboundCustomHandlersPrefixConfig.getRoutesAsStrings(false)) {
                outboundCustomHandlersPrefixMap.put(number,
                        new CustomHandlerConfig(
                            outboundCustomHandlersPrefixConfig.getSection(number).getString("url"),
                            CustomHandlerMethod.valueOf(outboundCustomHandlersPrefixConfig.getSection(number).getString("method"))
                        )
                );
            }
        }

        outboundConfig.setCustomHandlersConfig(new CustomHandlersConfig(outboundCustomHandlersExactMap, outboundCustomHandlersPrefixMap));

        Section outboundBlockPrefixesConfig = outbound.getSection("block_prefixes");
        HashMap<String, BlockPrefixesConfig> outboundBlockPrefixesMap = new HashMap<>();

        if (outboundBlockPrefixesConfig != null) {
            for (String obpCountry : outboundBlockPrefixesConfig.getRoutesAsStrings(false)) {
                Section obpCountrySection = outboundBlockPrefixesConfig.getSection(obpCountry);

                if (obpCountrySection.contains("say")) {
                    if (obpCountrySection.getString("say").equals("false")) {
                        outboundBlockPrefixesMap.put(obpCountry, new BlockPrefixesConfig(obpCountry));
                    } else {
                        outboundBlockPrefixesMap.put(obpCountry, new BlockPrefixesConfig(obpCountry, obpCountrySection.getString("say")));
                    }

                    continue;
                }

                if (obpCountrySection.contains("twiml")) {
                    outboundBlockPrefixesMap.put(obpCountry, new BlockPrefixesConfig(obpCountry)
                            .setTwiml(obpCountrySection.getString("twiml")));
                    continue;
                }

                throw new Exception("block_prefixes config option must contain either say or twiml option");
            }
        }

        outboundConfig.setBlockPrefixesConfig(outboundBlockPrefixesMap);

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

    public InboundConfig getInboundConfig() {
        return inboundConfig;
    }

    public OutboundConfig getOutboundConfig() {
        return outboundConfig;
    }
}

