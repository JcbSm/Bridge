package com.github.jcbsm.bridge.discord.commands;

import com.github.jcbsm.bridge.discord.ApplicationCommand;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.Commands;

public class PingCommand extends ApplicationCommand {

    public PingCommand() { super("ping"); }

    @Override
    public void run(SlashCommandInteractionEvent event) {
        event.deferReply(true).queue();

        event.getHook().sendMessage("Pong!").queue();
    }

    @Override
    public CommandData getCommandData() {
        return Commands.slash(getName(), "Ping");
    }
}
