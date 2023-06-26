package com.github.jcbsm.bridge.discord;

import com.github.jcbsm.bridge.discord.commands.PlayerListCommand;
import com.github.jcbsm.bridge.discord.commands.WhitelistCommand;
import net.dv8tion.jda.api.entities.Guild;
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

        overwriteApplicationCommands(
                new PlayerListCommand(),
                new WhitelistCommand()
        );

    }

    /**
     * Register a new command + listen for interactions.
     * @param command The command to register
     */
    public void registerGuildApplicationCommand(Guild guild, ApplicationCommand command) {

        logger.info("Registering {} command to guild {}", command.getName(), guild.getName());
        // Register command
        guild.upsertCommand(command.getCommandData()).queue();
    }

    /**
     * Overwrites the client's current application commands
     * Removes all global commands
     * Deletes any old guild-commands
     * Updates the existing ones.
     * @param commands
     */
    public void overwriteApplicationCommands(ApplicationCommand... commands) {
        // Retrieve global commands, delete all to ensure no wrong commands
        client.getJDA().retrieveCommands().queue((commandList) -> {

            // Delete each one
            for (Command command: commandList) {
                logger.info("Deleting global command: '{}'", command.getFullCommandName());
                command.delete().queue();
            }
        });

        // For each guild
        for (Guild guild : client.getJDA().getGuilds()) {

            guild.retrieveCommands().queue((commandList -> {

                // Get each command
                for (Command command: commandList) {

                    // Ignore it's in the current ones
                    if (Arrays.stream(commands).anyMatch(cmd -> cmd.getName().equals(command.getName())))
                        continue;

                    // Otherwise, delet it
                    logger.info("Deleting {} command in guild {}", command.getFullCommandName(), guild.getName());
                    command.delete().queue();
                }

                // Register the command.
                for (ApplicationCommand command : commands) {
                    registerGuildApplicationCommand(guild, command);
                }
            }));
        }

        // Add listeners
        for (ApplicationCommand command : commands) {
            client.getJDA().addEventListener(command);
        }
    }
}
