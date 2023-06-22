package github.jcbsm.bridge;

import github.jcbsm.bridge.database.IDatabaseClient;
import github.jcbsm.bridge.listeners.PlayerChatEventListener;
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
    private final String defaultChanenlID = "0", defaultToken = "BOT_TOKEN_HERE";
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

        // Register event listeners
        System.out.println("Registering Listeners...");
        getServer().getPluginManager().registerEvents(new PlayerChatEventListener(), this);

        // End of enable stdout.
        System.out.println("========= Discord Bridge has loaded =========");
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
        token = config.getString("token");
        chatChannelID = config.getString("channels.chat");
        consoleChannelID = config.getString("channels.console");

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

    /**
     * Processes a chat message
     * @param username Username
     * @param message Message content
     */
    public void processChatMessage(String username, String message) {
        System.out.println("Sending to discord client...");
        discord.sendChatMessage(username, message);
    }
}
