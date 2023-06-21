package github.jcbsm.bridge.discord;

import github.jcbsm.bridge.BridgeDiscordClient;
import net.dv8tion.jda.api.interactions.commands.Command;
import net.dv8tion.jda.api.interactions.commands.build.Commands;

import java.util.Arrays;

/**
 * Handles interactions and registers application commands
 */
public class ApplicationCommandHandler {

    BridgeDiscordClient client;

    public ApplicationCommandHandler(BridgeDiscordClient client, ApplicationCommand... commands) {

        this.client = client;

        overWriteApplicationCommands(commands);

    }

    /**
     * Register a new command + listen for interactions.
     * @param command The command to register
     */
    public void registerApplicationCommand(ApplicationCommand command) {
        // Register command
        client.getGuild().upsertCommand(Commands.slash(command.getName(), command.getDescription())).queue();
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
                System.out.println("Deleting global command: '" + command.getFullCommandName() + "'");
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

                System.out.println("Deleting guild command: '" + command.getFullCommandName() + "'");
                command.delete().queue();
            }

            // For each command, register it
            for (ApplicationCommand command : commands) {
                registerApplicationCommand(command);
            }
        }));
    }
}
