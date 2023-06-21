package github.jcbsm.bridge.discord.commands;

import github.jcbsm.bridge.Bridge;
import github.jcbsm.bridge.discord.ApplicationCommand;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.Command;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitRunnable;

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

        // Get usernames
        String username = option.getAsString();
        String current = Bridge.getPlugin().getDB().getCurrentUsername(userId);

        if (username.equals(current)) {
            event.reply("Nothing has changed...").setEphemeral(true).queue();
            return;
        }

        // Send console commands
        Bukkit.getServer().dispatchCommand(Bukkit.getServer().getConsoleSender(), "whitelist add " + username);
        Bukkit.getServer().dispatchCommand(Bukkit.getServer().getConsoleSender(), "whitelist remove " + current);

        // Update database
        Bridge.getPlugin().getDB().setUsername(userId, username);

    }
}
