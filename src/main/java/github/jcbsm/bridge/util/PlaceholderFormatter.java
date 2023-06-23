package github.jcbsm.bridge.util;

import github.jcbsm.bridge.Bridge;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.*;

import java.security.cert.CertificateFactorySpi;
import java.util.regex.Pattern;

public class PlaceholderFormatter {

    private final static ConfigurationSection config = Bridge.getPlugin().getConfig().getConfigurationSection("ChatRelay.MessageFormat");

    private final static String
        escapeRegex = "(?<!\\\\)",
        username = escapeRegex + "%username%",
        displayName = escapeRegex + "%name%",
        message = escapeRegex + "%message%",
        channel = escapeRegex + "%channel%",
        world = escapeRegex +"%world%",
        advancementTitle = escapeRegex + "%title%",
        advancementDescription = escapeRegex + "%description%";

    /**
     * Replaces all the occurences of 'regex' with 'replacement' in 'input'
     * @param input The original string
     * @param regex The RegEx to match
     * @param replacement The string to replace each match with
     * @return Output string
     */
    private static String replaceAll(String input, String regex, String replacement) {
        Pattern p = Pattern.compile(regex);
        return p.matcher(input).replaceAll(replacement);
    }

    /**
     * Form a string based off the format in config.yml
     * @param event The event to use
     * @return A formatted string message
     */
    public static String discordMessage(MessageReceivedEvent event) {

        // Get config string
        String str = config.getString("DiscordToMinecraft.Chat");

        // Replace all %username%s
        str = replaceAll(str, username, event.getAuthor().getGlobalName());
        str = replaceAll(str, message, event.getMessage().getContentStripped());

        return str;

    }

    public static String playerDeath(PlayerDeathEvent event) {

        // Get config string
        String str = config.getString("MinecraftToDiscord.PlayerDeath");

        str = replaceAll(str, username, event.getEntity().getName());
        str = replaceAll(str, displayName, event.getEntity().getDisplayName());
        str = replaceAll(str, message, event.getDeathMessage());
        str = replaceAll(str, world, event.getEntity().getWorld().getName());

        return str;

    }

    private static String playerEventPlaceholders(String str, PlayerEvent event) {

        str = replaceAll(str, username, event.getPlayer().getName());
        str = replaceAll(str, displayName, event.getPlayer().getDisplayName());

        return str;

    }

    public static String playerChat(AsyncPlayerChatEvent event) {

        String str = config.getString("MinecraftToDiscord.Chat");

        str = playerEventPlaceholders(str, event);

        str = replaceAll(str, message, event.getMessage());
        str = replaceAll(str, world, event.getPlayer().getWorld().getName());

        return str;
    }

    public static String playerJoin(PlayerJoinEvent event) {

        String str = config.getString("MinecraftToDiscord.PlayerJoin");

        str = playerEventPlaceholders(str, event);

        str = replaceAll(str, message, event.getJoinMessage());
        str = replaceAll(str, world, event.getPlayer().getWorld().getName());

        return str;
    }

    public static String playerLeave(PlayerQuitEvent event) {

        String str = config.getString("MinecraftToDiscord.PlayerLeave");

        str = playerEventPlaceholders(str, event);
        str = replaceAll(str, message, event.getQuitMessage());
        str = replaceAll(str, world, event.getPlayer().getWorld().getName());

        return str;
    }

    public static String playerKick(PlayerKickEvent event) {

        String str = config.getString("MinecraftToDiscord.PlayerLeave");

        str = playerEventPlaceholders(str, event);
        str = replaceAll(str, message, event.getLeaveMessage());
        str = replaceAll(str, world, event.getPlayer().getWorld().getName());

        return str;
    }

    public static String playerAdvancement(PlayerAdvancementDoneEvent event) {

        String str = config.getString("MinecraftToDiscord.Advancement");

        str = playerEventPlaceholders(str, event);
        str = replaceAll(str, advancementTitle, event.getAdvancement().getKey().getKey());
        str = replaceAll(str, advancementDescription, event.getAdvancement().toString());

        return str;
    }
}
