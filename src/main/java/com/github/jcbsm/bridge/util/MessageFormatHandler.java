package com.github.jcbsm.bridge.util;

import com.github.jcbsm.bridge.Bridge;
import io.papermc.paper.event.player.AsyncChatEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.kyori.adventure.text.TextComponent;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.*;

import java.util.regex.Pattern;

public class MessageFormatHandler {

    private final static ConfigurationSection config = Bridge.getPlugin().getConfig().getConfigurationSection("ChatRelay.MessageFormat");

    private final static String
        escapeRegex = "(?<!\\\\)",
        username = escapeRegex + "%username%",
        displayName = escapeRegex + "%name%",
        message = escapeRegex + "%message%",
        channel = escapeRegex + "%channel%",
        world = escapeRegex +"%world%",
        advancementTitle = escapeRegex + "%title%";

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
        String str = config.getString("MinecraftToDiscord.PlayerEvents.Death");

        str = replaceAll(str, username, event.getEntity().getName());
        str = replaceAll(str, displayName, ComponentUtil.getPlainText(event.getEntity().displayName()));
        str = replaceAll(str, message, ComponentUtil.getPlainText(event.deathMessage()));
        str = replaceAll(str, world, event.getEntity().getWorld().getName());

        return str;

    }

    private static String playerEventPlaceholders(String str, PlayerEvent event) {

        str = replaceAll(str, username, event.getPlayer().getName());
        str = replaceAll(str, displayName, ComponentUtil.getPlainText(event.getPlayer().displayName()));

        return str;

    }

    public static String playerChat(AsyncChatEvent event) {

        String str = config.getString("MinecraftToDiscord.PlayerEvents.Chat");

        str = playerEventPlaceholders(str, event);

        str = replaceAll(str, message, ComponentUtil.getPlainText(event.message()));
        str = replaceAll(str, world, event.getPlayer().getWorld().getName());

        return str;
    }

    public static String playerJoin(PlayerJoinEvent event) {

        String str = config.getString("MinecraftToDiscord.PlayerEvents.Join");

        str = playerEventPlaceholders(str, event);

        str = replaceAll(str, message, ComponentUtil.getPlainText(event.joinMessage()));
        str = replaceAll(str, world, event.getPlayer().getWorld().getName());

        return str;
    }

    public static String playerLeave(PlayerQuitEvent event) {

        String str = config.getString("MinecraftToDiscord.PlayerEvents.Leave");

        str = playerEventPlaceholders(str, event);
        str = replaceAll(str, message, ComponentUtil.getPlainText(event.quitMessage()));
        str = replaceAll(str, world, event.getPlayer().getWorld().getName());

        return str;
    }

    public static String playerKick(PlayerKickEvent event) {

        String str = config.getString("MinecraftToDiscord.PlayerEvents.Leave");

        str = playerEventPlaceholders(str, event);
        str = replaceAll(str, message, ComponentUtil.getPlainText(event.leaveMessage()));
        str = replaceAll(str, world, event.getPlayer().getWorld().getName());

        return str;
    }

    public static String playerAdvancement(PlayerAdvancementDoneEvent event) {

        String str = config.getString("MinecraftToDiscord.PlayerEvents.Advancement");

        str = playerEventPlaceholders(str, event);
        str = replaceAll(str, advancementTitle, ComponentUtil.getPlainText(event.getAdvancement().displayName()));

        return str;
    }

    public static String serverLoad() {
        return config.getString("MinecraftToDiscord.ServerEvents.Startup.Content");
    }

    public static String serverClose() {
        return config.getString("MinecraftToDiscord.ServerEvents.Stop.Content");
    }
}
