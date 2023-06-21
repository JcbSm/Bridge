package github.jcbsm.bridge.discord.commands;

import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;

public class PingCommand extends ApplicationCommand{

    public PingCommand() {
        super("ping", "Get the ping of the bot.");
    }

    @Override
    public void run(SlashCommandEvent event) {
        System.out.println("PING PIONG PING PONG!");
    }
}
