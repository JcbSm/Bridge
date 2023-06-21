package github.jcbsm.bridge.discord.commands;

import github.jcbsm.bridge.Bridge;
import github.jcbsm.bridge.discord.ApplicationCommand;
import github.jcbsm.bridge.util.Mojang;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.Command;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.IOException;
import java.util.UUID;

public class WhitelistCommand extends ApplicationCommand {

    public WhitelistCommand() {
        super("whitelist", "Add your Minecraft account to the whitelist");
    }

    @Override
    public CommandData getCommandData() {
        return Commands.slash(getName(), getDescription())
                .addOption(OptionType.STRING, "username", "The Minecraft username you wish to whitelist.");
    }
    @Override
    public void run(SlashCommandInteractionEvent event) {

        // Get options
        String userId = event.getUser().getId();
        OptionMapping option = event.getOption("username");

        if (option == null) {
            event.reply("You currently have \\_\\_\\_\\_\\_\\_ whitelisted on the server").setEphemeral(true).queue();
            return;
        }

        // Wait
        event.deferReply().setEphemeral(true).queue();

        // Get usernames & uuid
        String username = option.getAsString();
        UUID uuid = Mojang.getUserUUID(username);

        if (uuid == null) {
            event.getHook().sendMessage("No user exists").queue();
            return;
        }

        System.out.println("User " + event.getUser().getGlobalName() + " executed /whitelist from Discord.");

        // Add name to whitelist
        Bukkit.getScheduler().runTask(Bridge.getPlugin(), () -> Bukkit.getServer().dispatchCommand(Bukkit.getServer().getConsoleSender(), "whitelist add " + username));

        //////////////////////////////////////////////////////
        // LOGIC TO REMOVE OLD NAME FROM WHITELIST!!!!!!!!!!!!
        //////////////////////////////////////////////////////

        event.getHook().sendMessage("```Added: " + username + "\nRemoved: ______```").queue();

    }
}
