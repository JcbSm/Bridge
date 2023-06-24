package github.jcbsm.bridge;

import github.jcbsm.bridge.database.IDatabaseClient;
import github.jcbsm.bridge.listeners.PlayerChatEventListener;
import github.jcbsm.bridge.listeners.PlayerDeathEventListener;
import github.jcbsm.bridge.listeners.PlayerJoinEventListener;
import github.jcbsm.bridge.listeners.PlayerLeaveEventListener;
import github.jcbsm.bridge.util.MessageFormatHandler;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

public class Bridge extends JavaPlugin {

    /**
     * Plugin variables, clients, util etc
     */
    private BridgeDiscordClient discord;
    private IDatabaseClient db;
    private FileConfiguration config = this.getConfig();

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

        // Display Load messages
        System.out.println("========= Discord Bridge has started =========");
        System.out.println("Initializing...");

        // Load config
        initConfig();

        if (token.equals(defaultToken)) {
            System.err.println("CONFIG ERROR: Token has not been set!");
            displayTerminationMessage();
            return;
        }

        // Create client
        try {
            System.out.println("Creating Discord Client...");
            discord = new BridgeDiscordClient(token, chatChannelID, consoleChannelID);
        } catch (Exception e) {
            // Error
            System.err.println("CLIENT ERROR: " + e.getLocalizedMessage());
            displayTerminationMessage();
            return;
        }

        // Create db

        // Register Bukkit event listeners
        System.out.println("Registering Listeners...");

        if (config.getBoolean("ChatRelay.Enabled")) {
            System.out.println("Enabling Minecraft Chat Relay Listeners...");
            getServer().getPluginManager().registerEvents(new PlayerChatEventListener(), this);
            getServer().getPluginManager().registerEvents(new PlayerDeathEventListener(), this);
            getServer().getPluginManager().registerEvents(new PlayerJoinEventListener(), this);
            getServer().getPluginManager().registerEvents(new PlayerLeaveEventListener(), this);

            Bukkit.getScheduler().scheduleSyncDelayedTask(this, () ->
                broadcastDiscordChatMessage(MessageFormatHandler.serverLoad())
            );
        }

        // End of enable stdout.
        System.out.println("========= Discord Bridge has loaded =========");
    }

    @Override
    public void onDisable() {
        broadcastDiscordChatMessage(MessageFormatHandler.serverClose());
    }

    /**
     * Displays a termination message to notify user the plugin did not load correctly.
     */
    private void displayTerminationMessage() {
        System.out.println("The plugin has not loaded correctly.");
        System.out.println("============ Discord Bridge out! ============");
    }

    /**
     * Saves the default config and initializes the config variables.
     */
    private void initConfig() {

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
