package com.github.jcbsm.bridge.util;

import com.github.jcbsm.bridge.Bridge;
import org.apache.commons.lang3.Validate;
import org.bukkit.configuration.Configuration;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

public class ConfigHandler {

    private final Map<String, Object> cache = new HashMap<>();
    private final Bridge plugin;
    private final FileConfiguration config;
    private final Configuration defaults;
    private final Logger logger = LoggerFactory.getLogger(this.getClass().getSimpleName());

    public ConfigHandler() {
        plugin = Bridge.getPlugin();
        config = plugin.getConfig();
        defaults = config.getDefaults();

        // Save default config
        plugin.saveDefaultConfig();
    }

    /**
     * Get an object from the cache
     * Fetch from config if absent
     * @param path Path to config var
     * @return Config entry
     */
    public Object get(@NotNull String path) {
        return get(path, defaults.get(path));
    }

    /**
     * Get an object form the cache, with a default value if null
     * Fetch from config if absent
     * @param path Path to config var
     * @param def Default value to return if null
     * @return Config entry/Default value
     */
    public Object get(@NotNull String path, Object def) {
        Object result = cache.computeIfAbsent(path, p -> config.get(p));
        return (result == null) ? def : result;
    }

    /**
     * Get a string value
     * @param path Path to value
     * @return String
     */
    public String getString(String path) {
        Object def = defaults.get(path);
        return getString(path, def != null ? def.toString() : null);
    }

    /**
     * Get a string value, or default if null
     * @param path Path to value
     * @param def Default value
     * @return String
     */
    public String getString(String path, String def) {
        Object val = get(path, def);
        return (val != null) ? val.toString() : def;
    }

    /**
     * Get boolean value
     * @param path Path to
     * @return Boolean
     */
    public boolean getBoolean(String path) {
        Object def = defaults.get(path);
        return getBoolean(path, (def instanceof Boolean) ? (Boolean) def : false);
    }

    /**
     * Get Boolean value or default
     * @param path Path to
     * @param def Default backup
     * @return Boolean
     */
    public boolean getBoolean(String path, boolean def) {
        Object val = get(path, def);
        return (val instanceof Boolean) ? (Boolean) val : def;
    }

    /**
     * Gets a list of from the config
     * @param path Path to value
     * @return List
     */
    public List<?> getList(String path) {
        Object def = defaults.get(path);
        return getList(path, (def instanceof List) ? (List<?>) def : null);
    }

    /**
     * Gets a list from the config, or a default value
     * @param path Path to
     * @param def Default value
     * @return List
     */
    public List<?> getList(String path, List<?> def) {
        Object val = get(path, def);
        return (List<?>) ((val instanceof List) ? val : def);
    }

    /**
     * Checks if a key has a list
     * @param path Path to value
     * @return Boolean
     */
    public boolean isList(String path) {
        Object val = get(path);
        return val instanceof List;
    }

    /**
     * Gets a list of strings from config
     * @param path Path to
     * @return List of strings
     */
    public List<String> getStringList(String path) {
        List<?> list = getList(path);

        if (list == null) {
            return new ArrayList<>(0);
        }

        List<String> result = new ArrayList<String>();

        for (Object object : list) {
            if ((object instanceof String)) {
                result.add(String.valueOf(object));
            }
        }

        return result;
    }

    /**
     * Checks if a value is default.
     * @param path Path to value
     * @return True if the value is equal to that in the default config.
     */
    public boolean isDefaultValue(String path) {
        return get(path).equals(defaults.get(path));
    }
}
