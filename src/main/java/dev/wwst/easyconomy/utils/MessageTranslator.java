package dev.wwst.easyconomy.utils;

import dev.wwst.easyconomy.Easyconomy;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;

public class MessageTranslator {

    private static final String[] translations = new String[] {
            "de",
            "en"
    };

    private static MessageTranslator INSTANCE;

    private Easyconomy plugin;
    private String language;
    private String prefix;
    private YamlConfiguration cfg;


    private HashMap<String, String> messages;

    public MessageTranslator(String language) {
        if(INSTANCE != null) {
            return;
        }
        INSTANCE = this;
        plugin = Easyconomy.getInstance();
        prefix = Configuration.get().getString("prefix");
        messages = new HashMap<>();
        this.language = language;

        saveDefaults();

        System.out.println(plugin.getConfigFolderPath());

        File languageFile = new File("plugins/EasyConomy/messages_"+language+".yml");
        if(!languageFile.exists()) {
            plugin.getLogger().log(Level.SEVERE, "!!! The language chosen by you, "+language+", cannot be resolved!");
            plugin.getLogger().log(Level.SEVERE, "!!! Create a file called messages_"+language+".yml in the AdminTools folder to start!");
            plugin.getLogger().log(Level.SEVERE, "!!! For now, the ENGLISH language file will be loaded!");
            languageFile =new File("plugins/EasyConomy/messages_en.yml");
            System.out.println(languageFile.exists());
            System.out.println(languageFile.getAbsolutePath());
            System.out.println(languageFile.getName());
        }
        cfg = YamlConfiguration.loadConfiguration(languageFile);
        Map<String, Object> values = cfg.getValues(true);
        for(String key : values.keySet()) {
            messages.put(key, values.get(key).toString());
        }
        plugin.getLogger().log(Level.INFO, "Language loaded: messages_"+language+".yml");
    }

    // Loading custom language files for addons
    public void loadMessageFile(String path) {
        File languageFile = new File(path);
        if(!languageFile.exists()) {
            plugin.getLogger().log(Level.SEVERE, "Could not find a config file at "+path);
            return;
        }
        YamlConfiguration cfg = YamlConfiguration.loadConfiguration(languageFile);
        Map<String, Object> values = cfg.getValues(true);
        for(String key : values.keySet()) {
            messages.put(key, values.get(key).toString());
        }
        plugin.getLogger().log(Level.INFO, "Custom language file loaded: "+path);
    }

    private void saveDefaults() {
        for(String translation : translations) {
            plugin.saveResource("messages_"+translation+".yml", true);
            plugin.getLogger().log(Level.INFO, "Default language exported: messages_"+translation+".yml");
        }
    }

    public String getMessageAndReplace(String key, boolean addPrefix, String... replacements) {
        if(!messages.containsKey(key)) {
            return ChatColor.YELLOW+key+ ChatColor.RED +" not found!";
        }
        String message = messages.get(key);
        for(int i = 0; message.contains("%s") && replacements != null; i++) {
            if(replacements.length <= i) {
                message = message.replaceFirst("%s", "&cNO REPLACEMENT");
            } else {
                message = message.replaceFirst("%s", replacements[i]);
            }
        }
        if(addPrefix) {
            message = prefix + message;
        }

        return ChatColor.translateAlternateColorCodes('&', message);
    }

    public String getMessage(String key, boolean addPrefix) {
        return getMessageAndReplace(key,addPrefix, null,"");
    }

    public String getMessage(String key) {
        return getMessageAndReplace(key,false,null,"");
    }

    public YamlConfiguration getConfiguration() {
        return cfg;
    }

    public String getLanguage() {
        return language;
    }

    public static MessageTranslator getInstance() {
        return INSTANCE;
    }
}