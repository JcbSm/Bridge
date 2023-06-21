package github.jcbsm.bridge.discord.commands;

import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

/**
 * Abstract class for Application Commands
 * Extended to create a new application command with a new name, description and run()
 */
public abstract class ApplicationCommand {

    private final String name, description;
    private ApplicationCommandListener listener;

    /**
     * Application Command Listener Adapter
     * Handles receiving the command interaction
     */
    class ApplicationCommandListener extends ListenerAdapter {

        /**
         * Handles receiving a slash command interaction & checks if it is this one.
         * @param event Emitted event
         */
        @Override
        public void onSlashCommand(SlashCommandEvent event) {
            // If name matches, run the abstract method
            if (event.getName().equals(name)) {
                run(event);
            }
        }
    }

    /**
     * Creates an instance of an Application Command
     * @param name The name of the command
     * @param description The description of the command
     */
    public ApplicationCommand(String name, String description) {
        this.name = name;
        this.description = description;
        this.listener = new ApplicationCommandListener();
    }

    /**
     * Get the event listneer
     * @return Listener
     */
    public ApplicationCommandListener getListener() {
        return listener;
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
    public abstract void run(SlashCommandEvent event);

}
