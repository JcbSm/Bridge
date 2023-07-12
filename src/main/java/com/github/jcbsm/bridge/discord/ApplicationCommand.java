package com.github.jcbsm.bridge.discord;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Abstract class for Application Commands
 * Extended to create a new application command with a new name, description and run()
 * Application Command Listener Adapter
 * Handles receiving the command interaction
 */
public abstract class ApplicationCommand extends ListenerAdapter {

    private final String name, description;
    private final Logger logger;

    /**
     * Creates an instance of an Application Command
     * @param name The name of the command
     * @param description The description of the command
     */
    public ApplicationCommand(String name, String description) {
        this.name = name;
        this.description = description;
        this.logger = LoggerFactory.getLogger(this.getClass().getSimpleName());
    }

    /**
     * Handles and runs the executing code when a Slash Command Interaction is received.
     * @param event Event
     */
    @Override
    public void onSlashCommandInteraction (SlashCommandInteractionEvent event) {
        // If name matches, run the abstract method
        if (event.getName().equals(name)) {
            run(event);
        }
    }

    /**
     * Get the command name
     * @return Name
     */
    public String getName() {
        return name;
    }

    /**
     * Get the command description
     * @return Description
     */
    public String getDescription() {
        return description;
    }

    /**
     * Abstract method to be run on event emission
     * @param event Event
     */
    public abstract void run(SlashCommandInteractionEvent event);

    /**
     * Get the CommandData
     * @return CommandData
     */
    public abstract CommandData getCommandData();

    public Logger getLogger() {
        return logger;
    }
}
