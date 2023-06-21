package github.jcbsm.bridge.discord.commands;

import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public abstract class ApplicationCommand {

    private final String name, description;
    private ApplicationCommandListener listener;

    class ApplicationCommandListener extends ListenerAdapter {
        @Override
        public void onSlashCommand(SlashCommandEvent event) {
            if (event.getName().equals(name)) {
                run(event);
            }
        }
    }

    public ApplicationCommand(String name, String description) {
        this.name = name;
        this.description = description;
        this.listener = new ApplicationCommandListener();
    }

    public ApplicationCommandListener getListener() {
        return listener;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public abstract void run(SlashCommandEvent event);

}
