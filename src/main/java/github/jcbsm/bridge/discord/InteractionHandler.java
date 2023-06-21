package github.jcbsm.bridge.discord;

import github.jcbsm.bridge.BridgeDiscordClient;
import github.jcbsm.bridge.discord.commands.ApplicationCommand;
import net.dv8tion.jda.api.interactions.commands.Command;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;

/**
 * Handles interactions and registers application commands
 */
public class InteractionHandler {

    BridgeDiscordClient client;

    public InteractionHandler(BridgeDiscordClient client, ApplicationCommand... commands) {

        this.client = client;

        // For each command, register it
        for (ApplicationCommand command : commands) {
            registerApplicationCommand(command);
        }
    }

    /**
     * Register a new command + listen for interactions.
     * @param command The command to register
     */
    public void registerApplicationCommand(ApplicationCommand command) {
        // Register command
        client.getGuild().updateCommands().addCommands(new CommandData(command.getName(), command.getDescription()));
        // Handle listener
        client.getJDA().addEventListener(command.getListener());
    }
}
