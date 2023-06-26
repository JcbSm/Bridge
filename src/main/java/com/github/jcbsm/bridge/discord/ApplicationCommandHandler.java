package com.github.jcbsm.bridge.discord;

import com.github.jcbsm.bridge.BridgeDiscordClient;
import com.github.jcbsm.bridge.discord.commands.PlayerListCommand;
import com.github.jcbsm.bridge.discord.commands.WhitelistCommand;
import net.dv8tion.jda.api.interactions.commands.Command;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;

/**
 * Handles interactions and registers application commands
 */
public class ApplicationCommandHandler {

    private final BridgeDiscordClient client;
    private final Logger logger = LoggerFactory.getLogger(this.getClass().getSimpleName());

    public ApplicationCommandHandler(BridgeDiscordClient client) {

        this.client = client;

        overWriteApplicationCommands(
                new PlayerListCommand(),
                new WhitelistCommand()
        );

    }

    /**
     * Register a new command + listen for interactions.
     * @param command The command to register
     */
    public void registerApplicationCommand(ApplicationCommand command) {
        // Register command
        client.getGuild().upsertCommand(command.getCommandData()).queue();
        // Handle listener
        client.getJDA().addEventListener(command);
    }

    /**
     * Overwrites the client's current application commands
     * Removes all global commands
     * Deletes any old guild-commands
     * Updates the existing ones.
     * @param commands
     */
    public void overWriteApplicationCommands(ApplicationCommand... commands) {
        // Retrieve global commands, delete all to ensure no wrong commands
        client.getJDA().retrieveCommands().queue((commandList) -> {

            // Delete each one
            for (Command command: commandList) {
                logger.info("Deleting global command: '{}'", command.getFullCommandName());
                command.delete().queue();
            }
        });

        // Retrieve guild commands
        client.getGuild().retrieveCommands().queue((commandList -> {

            // Delete each one
            for (Command command: commandList) {

                // Unless it's in the current ones
                if (Arrays.stream(commands).anyMatch(cmd -> cmd.getName().equals(command.getName())))
                    continue;

                logger.info("Deleting guild command: '{}'", command.getFullCommandName());
                command.delete().queue();
            }

            // For each command, register it
            for (ApplicationCommand command : commands) {
                registerApplicationCommand(command);
            }
        }));
    }
}
