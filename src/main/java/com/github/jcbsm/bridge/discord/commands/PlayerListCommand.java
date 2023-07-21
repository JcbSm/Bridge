package com.github.jcbsm.bridge.discord.commands;

import com.github.jcbsm.bridge.discord.ApplicationCommand;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.kyori.adventure.text.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class PlayerListCommand extends ApplicationCommand {

    public PlayerListCommand() {
        super("playerlist");
    }

    /**
     * Runs the interaction reply
     * @param event Event
     */
    @Override
    public void run(SlashCommandInteractionEvent event) {

        // If server is empty, reply with appropriate message
        if (Bukkit.getServer().getOnlinePlayers().size() == 0) {
            event.reply("No players are currently online.").setEphemeral(true).queue();

        // Otherwise, display player list.
        } else {

            StringBuilder msg = new StringBuilder();

            // title
            msg.append("## " + Bukkit.getServer().getOnlinePlayers().size() + " Player(s) Online:" + System.lineSeparator());

            // Append each player to the list
            for (Player player : Bukkit.getServer().getOnlinePlayers()) {
                msg.append("- " + ((TextComponent) player.displayName()).content() + System.lineSeparator());
            }

            event.reply(msg.toString()).setEphemeral(true).queue();

        }

    }

    @Override
    public CommandData getCommandData() {
        return Commands.slash(getName(), "List online players");
    }
}
