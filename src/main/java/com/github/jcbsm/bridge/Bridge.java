package com.github.jcbsm.bridge;

import club.minnced.discord.webhook.send.WebhookMessage;
import com.github.jcbsm.bridge.discord.BridgeDiscordClient;
import com.github.jcbsm.bridge.listeners.*;
import com.github.jcbsm.bridge.util.ChatRelayFormatter;
import com.github.jcbsm.bridge.util.ConfigHandler;
import com.github.jcbsm.bridge.util.MessageFormatHandler;
import io.papermc.paper.event.player.AsyncChatEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.utils.messages.MessageCreateData;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Bridge extends JavaPlugin {

    /**
     * Plugin variables, clients, util etc
     */
    private BridgeDiscordClient discord;
    private DatabaseClient db;
    private ConfigHandler config;

    private final Logger logger = LoggerFactory.getLogger(Bridge.class.getSimpleName());

    @Override
    public void onEnable() {

        logger.info("Starting Bridge...");

        // Load config
        config = ConfigHandler.getHandler();

        // Check bot token has been set.
        if (config.isDefaultValue("BotToken")) {
            logger.warn("BotToken has not been set. Please change this in /plugins/Bridge/config.yml and restart the server.");
            return;
        }

        // Create client
        try {
            logger.info("Initialising Discord Client");

            discord = new BridgeDiscordClient(
                    config.getString("BotToken"),
                    config.getStringList("ChatRelay.Channels.Chat"),
                    config.getString("ChatRelay.Channels.Console"));

        } catch (Exception e) {
            // Error
            logger.error("Discord Client Initialisation: {}", e.getMessage());
            return;
        }

        // Register Bukkit event listeners
        logger.info("Registering bukkit listeners...");

        if (config.getBoolean("ChatRelay.Enabled")) {
            logger.info("Enabling Minecraft Chat Relay Listeners...");
            getServer().getPluginManager().registerEvents(new PlayerChatEventListener(), this);
            getServer().getPluginManager().registerEvents(new PlayerDeathEventListener(), this);
            getServer().getPluginManager().registerEvents(new PlayerJoinEventListener(), this);
            getServer().getPluginManager().registerEvents(new PlayerLeaveEventListener(), this);
            getServer().getPluginManager().registerEvents(new PlayerAdvancementDoneEventListener(), this);

            Bukkit.getScheduler().scheduleSyncDelayedTask(this, () ->
                broadcastDiscordChatMessage(ChatRelayFormatter.serverStartup())
            );
        }

        // sanamorii: initialise database.
        this.db = DatabaseClient.getDatabase();

        // End of enable stdout.
        logger.info("Done.");
    }

    @Override
    public void onDisable() {
        broadcastDiscordChatMessage(ChatRelayFormatter.serverStop());
    }

    /**
     * Gets the plugin
     * @return plugin
     */
    public static Bridge getPlugin() {
        return getPlugin(Bridge.class);
    }

    /**
     * Get the discord client
     * @return The discord client
     */
    public BridgeDiscordClient getDiscord() { return discord; }

    /**
     * Broadcasts a minecraft chat message, replacing '&' with the colour char
     * @param message The message to send
     */
    public void broadcastMinecraftChatMessage(String message) {
        Bukkit.getScheduler().runTaskAsynchronously(this, () ->
                Bukkit.getServer().broadcast(LegacyComponentSerializer.legacyAmpersand().deserialize(message)));
    }

    /**
     * Broadcasts a string to all Discord chat relay channels
     * @param content Message content to send
     */
    public void broadcastDiscordChatMessage(String content) {
        discord.broadcastMessage(content);
    }

    /**
     * Broadcasts a Message to all Discord chat relay channels
     * @param message Message to send
     */
    public void broadcastDiscordChatMessage(MessageCreateData message) {
        discord.broadcastMessage(message);
    }

    /**
     * Broadcasts a Webhook message to all Discord chat relay channels
     * @param message Webhook message to send
     */
    public void broadcastDiscordChatMessage(WebhookMessage message) {
        discord.broadcastMessage(message);
    }

    /**
     * Broadcasts a Discord message to all other Discord chat relay channels
     * @param event The message event to broadcast
     */
    public void broadcastDiscordChatMessage(MessageReceivedEvent event) {
        discord.broadcastMessage(event);
    }

}
