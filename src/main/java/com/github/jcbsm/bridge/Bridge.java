package com.github.jcbsm.bridge;

import com.github.jcbsm.bridge.discord.BridgeDiscordClient;
import com.github.jcbsm.bridge.listeners.PlayerDeathEventListener;
import com.github.jcbsm.bridge.listeners.PlayerJoinEventListener;
import com.github.jcbsm.bridge.listeners.PlayerLeaveEventListener;
import com.github.jcbsm.bridge.util.MessageFormatHandler;
import com.github.jcbsm.bridge.database.IDatabaseClient;
import com.github.jcbsm.bridge.listeners.PlayerChatEventListener;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Bridge extends JavaPlugin {

    /**
     * Plugin variables, clients, util etc
     */
    private BridgeDiscordClient discord;
    private IDatabaseClient db;
    private FileConfiguration config = this.getConfig();

    private final Logger logger = LoggerFactory.getLogger(Bridge.class.getSimpleName());

    /**
     * The default config variables.
     */
    private final String defaultChanenlID = "000000000000000000", defaultToken = "BOT_TOKEN_HERE";
    /**
     * Discord Bot token
     */
    private String token;
    /**
     * Discord Channel IDs for the client.
     */
    private String chatChannelID;
    private String consoleChannelID;

    @Override
    public void onEnable() {

        logger.info("Starting Bridge...");

        // Load config
        initConfig();

        if (token.equals(defaultToken)) {
            logger.warn("BotToken has not been set. Please change this in /plugins/Bridge/config.yml and restart the server.");
            return;
        }

        // Create client
        try {
            logger.info("Initialising Discord Client");
            discord = new BridgeDiscordClient(token, chatChannelID, consoleChannelID);
        } catch (Exception e) {
            // Error
            logger.error("CLIENT ERROR: {}", e.getLocalizedMessage());
            return;
        }

        // Create db

        // Register Bukkit event listeners
        logger.info("Registering bukkit listeners...");

        if (config.getBoolean("ChatRelay.Enabled")) {
            logger.info("Enabling Minecraft Chat Relay Listeners...");
            getServer().getPluginManager().registerEvents(new PlayerChatEventListener(), this);
            getServer().getPluginManager().registerEvents(new PlayerDeathEventListener(), this);
            getServer().getPluginManager().registerEvents(new PlayerJoinEventListener(), this);
            getServer().getPluginManager().registerEvents(new PlayerLeaveEventListener(), this);

            Bukkit.getScheduler().scheduleSyncDelayedTask(this, () ->
                broadcastDiscordChatMessage(MessageFormatHandler.serverLoad())
            );
        }

        // End of enable stdout.
        logger.info("Done.");
    }

    @Override
    public void onDisable() {
        broadcastDiscordChatMessage(MessageFormatHandler.serverClose());
    }

    /**
     * Saves the default config and initializes the config variables.
     */
    private void initConfig() {

        logger.debug("Saving the default config.");

        // Save default config
        saveDefaultConfig();

        // Define variables
        token = config.getString("BotToken");
        chatChannelID = config.getString("ChatRelay.Channels.Chat");
        consoleChannelID = config.getString("ChatRelay.Channels.Console");

    }

    /**
     * Gets the plugin
     * @return plugin
     */
    public static Bridge getPlugin() {
        return getPlugin(Bridge.class);
    }

    /**
     * Get the database
     * @return
     */
    public IDatabaseClient getDB() { return db; }

    public String getChatChannelID() { return chatChannelID; }

    /**
     * Broadcasts a minecraft chat message, replacing '&' with the colour char
     * @param message The message to send
     */
    public void broadcastMinecraftChatMessage(String message) {
        Bukkit.getScheduler().runTaskAsynchronously(this, () ->
                Bukkit.getServer().broadcastMessage(
                        ChatColor.translateAlternateColorCodes('&', message)
                ));
    }

    /**
     * Broadcasts a discord message to the Chat channel
     * @param message Message to send
     */
    public void broadcastDiscordChatMessage(String message) {
        discord.sendChatMessage(message);
    }
}
