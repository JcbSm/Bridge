package com.github.jcbsm.bridge.util;

import club.minnced.discord.webhook.send.WebhookEmbed;
import club.minnced.discord.webhook.send.WebhookEmbedBuilder;
import club.minnced.discord.webhook.send.WebhookMessage;
import club.minnced.discord.webhook.send.WebhookMessageBuilder;
import com.github.jcbsm.bridge.DatabaseClient;
import com.github.jcbsm.bridge.listeners.PlayerDeathEventListener;
import io.papermc.paper.event.player.AsyncChatEvent;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.utils.messages.MessageCreateBuilder;
import net.dv8tion.jda.api.utils.messages.MessageCreateData;
import org.bukkit.Bukkit;
import org.bukkit.event.Event;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.*;

import java.awt.*;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ChatRelayFormatter {

    private final static String PATH_DISCORD_TO_MINECRAFT = "ChatRelay.MessageFormat.DiscordToMinecraft.";
    private final static String PATH_MINECRAFT_TO_DISCORD = "ChatRelay.MessageFormat.MinecraftToDiscord.";
    private final static String PATH_PLAYER_EVENTS = "ChatRelay.MessageFormat.MinecraftToDiscord.PlayerEvents.";
    private final static String PATH_SERVER_EVENTS = "ChatRelay.MessageFormat.MinecraftToDiscord.ServerEvents.";

    /**
     * Regex Patterns
     */
    private final static String
            escapeRegex = "(?<!\\\\)",
            username = escapeRegex + "%username%",
            name = escapeRegex + "%name%",
            message = escapeRegex + "%message%",
            channel = escapeRegex + "%channel%",
            guild = escapeRegex + "%guild%",
            world = escapeRegex +"%world%",
            advancementTitle = escapeRegex + "%title%",
            advancementDesc = escapeRegex + "%description%";

    /**
     * Replaces all instances matching a regex pattern with a replacement string
     * @param input The input string
     * @param regex RegEx to match
     * @param replacement String to replace each match with
     * @return Modified string
     */
    private static String replaceAll(String input, String regex, String replacement) {
        Pattern p = Pattern.compile(regex);
        return p.matcher(input).replaceAll(replacement);
    }

    /**
     * Replaces all matches with replacement keys with respective values
     * @param input Input string
     * @param replacement Map of \<Pattern, replacement\>
     * @return Modified string
     */
    private static String replaceAll(String input, Map<String, String> replacement) {
        for (Map.Entry<String, String> entry : replacement.entrySet()) {
            input = replaceAll(input, entry.getKey(), entry.getValue());
        }

        return input;
    }

    /**
     * Builds a message from the config
     * @param path Path message formatting in config.yml
     * @param placeholders A function that will map each field in the config.
     *                     (String) -> String
     * @return The Message that will be sent.
     */
    private static MessageCreateData formMessage(String path, Function<String, String> placeholders) {

        // Append '.' to the path
        path = path + ".";
        String str; // Temp string value

        // Initiate message builder with content
        MessageCreateBuilder builder = new MessageCreateBuilder()
                .setContent(parseMentions(placeholders.apply(path + "Content")));

        // If embed is enabled, build it.
        if (ConfigHandler.getHandler().getBoolean(path + "Embed.Enabled")) {

            EmbedBuilder embedBuilder = new EmbedBuilder();

            // Only set title if it is set
            String title = placeholders.apply(path + "Embed.Title");
            if (!title.isEmpty())
                    embedBuilder.setTitle(title);

            // Same for description
            String description = parseMentions(placeholders.apply(path + "Embed.Description"));
            if (!description.isEmpty())
                    embedBuilder.setDescription(description);

            // Set the rest of the values
            embedBuilder
                    .setColor(Color.decode(ConfigHandler.getHandler().getString(path + "Embed.Color")).getRGB())
                    .setAuthor(
                            placeholders.apply(path + "Embed.Author.Name"),
                            null,
                            (str = placeholders.apply(path + "Embed.Author.IconURL")).isEmpty() ? null : str
                    )
                    .setFooter(
                            placeholders.apply(path + "Embed.Footer.Text"),
                            (str = placeholders.apply(path + "Embed.Footer.IconURL")).isEmpty() ? null : str
                    )
                    .setThumbnail(
                            (str = placeholders.apply(path + "Embed.ThumbnailURL")).isEmpty() ? null : str
                    );

            // Add to the message
            builder.addEmbeds(embedBuilder.build());
        }

        return builder.build();
    }

    /**
     * Handles placeholder replacement for player events
     * @param input Input string
     * @param event Event
     * @return Output string
     */
    private static String minecraftPlayerEventPlaceholders(String input, PlayerEvent event) {
        return replaceAll(input, Map.of(
                username, event.getPlayer().getName(),
                name, ComponentUtil.getPlainText(event.getPlayer().displayName()),
                world, event.getPlayer().getWorld().getName()
        ));
    }

    /**
     * Handles placeholders for player chat event
     * @param input Input string
     * @param event Event
     * @return Modified string
     */
    private static String playerChatPlaceholders(String input, AsyncChatEvent event) {
        return replaceAll(minecraftPlayerEventPlaceholders(input, event), Map.of(message, ComponentUtil.getPlainText(event.message())));
    }

    /**
     * Placeholders for the player death event
     * @param input Input string
     * @param event Event
     * @return Modified string
     */
    private static String playerDeathPlaceholders(String input, PlayerDeathEvent event) {
        return replaceAll(input, Map.of(
                username, event.getPlayer().getName(),
                name, ComponentUtil.getPlainText(event.getPlayer().displayName()),
                message, ComponentUtil.getPlainText(event.deathMessage()),
                world, event.getPlayer().getWorld().getName()
        ));
    }

    /**
     * Placeholders for the player join event
     * @param input Input String
     * @param event Event
     * @return Modified string
     */
    private static String playerJoinPlaceholders(String input, PlayerJoinEvent event) {
        return replaceAll(minecraftPlayerEventPlaceholders(input, event), Map.of(
                message, ComponentUtil.getPlainText(event.joinMessage())
        ));
    }

    /**
     * Placeholders for the player leave events
     * @param input Input String
     * @param event Event
     * @return Modified string
     */
    private static String playerLeavePlaceholders(String input, PlayerKickEvent event) {
        return replaceAll(minecraftPlayerEventPlaceholders(input, event), Map.of(
                message, ComponentUtil.getPlainText(event.leaveMessage())
        ));
    }
    /**
     * Placeholders for the player leave events
     * @param input Input String
     * @param event Event
     * @return Modified string
     */
    private static String playerLeavePlaceholders(String input, PlayerQuitEvent event) {
        return replaceAll(minecraftPlayerEventPlaceholders(input, event), Map.of(
                message, ComponentUtil.getPlainText(event.quitMessage())
        ));
    }

    /**
     * Placeholders for the player advancement events
     * @param input Input String
     * @param event Event
     * @return Modified string
     */
    // TODO: Implement this.
    private static String playerAdvancementPlaceholders(String input, PlayerAdvancementDoneEvent event) {
        return replaceAll(minecraftPlayerEventPlaceholders(input, event), Map.of(
                advancementTitle, ComponentUtil.getPlainText(event.getAdvancement().getDisplay().title()),
                advancementDesc, ComponentUtil.getPlainText((event.getAdvancement().getDisplay().description()))
        ));
    }

    /**
     * Creates a message for a AsyncChatEvent according to the config
     * @param event Event
     * @return Formed message
     */
    public static MessageCreateData playerChat(AsyncChatEvent event) {
        return formMessage(PATH_MINECRAFT_TO_DISCORD + "PlayerEvents.Chat", (path) -> playerChatPlaceholders(ConfigHandler.getHandler().getString(path), event));
    }

    /**
     * Creates a message for a PlayerDeathEvent according to the config
     * @param event Event
     * @return Formed message
     */
    public static MessageCreateData playerDeath(PlayerDeathEvent event) {
        return formMessage(PATH_MINECRAFT_TO_DISCORD + "PlayerEvents.Death", (path) -> playerDeathPlaceholders(ConfigHandler.getHandler().getString(path), event));
    }

    /**
     * Creates a message for a PlayerJoinEvent according to the config
     * @param event Event
     * @return Formed message
     */
    public static MessageCreateData playerJoin(PlayerJoinEvent event) {
        return formMessage(PATH_MINECRAFT_TO_DISCORD + "PlayerEvents.Join", (path) -> playerJoinPlaceholders(ConfigHandler.getHandler().getString(path), event));
    }

    /**
     * Creates a message for a PlayerQuitEvent according to the config
     * @param event Event
     * @return Formed message
     */
    public static MessageCreateData playerLeave(PlayerQuitEvent event) {
        return formMessage(PATH_MINECRAFT_TO_DISCORD + "PlayerEvents.Leave", (path) -> playerLeavePlaceholders(ConfigHandler.getHandler().getString(path), event));
    }

    /**
     * Creates a message for a PlayerKickEvent according to the config
     * @param event Event
     * @return Formed message
     */
    public static MessageCreateData playerLeave(PlayerKickEvent event) {
        return formMessage(PATH_MINECRAFT_TO_DISCORD + "PlayerEvents.Leave", (path) -> playerLeavePlaceholders(ConfigHandler.getHandler().getString(path), event));
    }

    /**
     * Creates a message for a PlayerAdvancementDoneEvent according to the config
     * @param event Event
     * @return Formed message
     */
    public static MessageCreateData playerAdvancement(PlayerAdvancementDoneEvent event) {
        return formMessage(PATH_MINECRAFT_TO_DISCORD + "PlayerEvents.Advancement", (path) -> playerAdvancementPlaceholders(ConfigHandler.getHandler().getString(path), event));
    }

    /**
     * Creates a message for the server startup according to the config
     */
    public static MessageCreateData serverStartup() {
        return formMessage(PATH_MINECRAFT_TO_DISCORD + "ServerEvents.Startup", (path) -> ConfigHandler.getHandler().getString(path));
    }

    /**
     * Creates a message for the server shutdown according to the config
     */
    public static MessageCreateData serverStop() {
        return formMessage(PATH_MINECRAFT_TO_DISCORD + "ServerEvents.Stop", (path) -> ConfigHandler.getHandler().getString(path));
    }

    /**
     * Produces the Minecraft chat message from a discord message
     * @param event Event to process
     * @return The string to be sent to Minecraft
     */
    public static String discordToMinecraft(MessageReceivedEvent event) {

        return replaceAll(ConfigHandler.getHandler().getString(PATH_DISCORD_TO_MINECRAFT + "Chat"), Map.of(
                username, event.getAuthor().getName(),
                name, event.getMember().getEffectiveName(),
                message, event.getMessage().getContentStripped(),
                guild, event.getGuild().getName(),
                channel, event.getChannel().getName()
        ));

    }

    /**
     * Produces a webhook message from a Minecraft chat event.
     * @param event Event ot process
     * @return The message to be broadcast
     */
    public static WebhookMessage playerChatWebhook(AsyncChatEvent event) {
        String content = playerChatPlaceholders(ConfigHandler.getHandler().getString("ChatRelay.WebhookMessages.ContentFormat"), event);

        return new WebhookMessageBuilder()
                .setContent(parseMentions(content))
                .setUsername(playerChatPlaceholders(ConfigHandler.getHandler().getString("ChatRelay.WebhookMessages.UsernameFormat"), event))
                .setAvatarUrl(playerChatPlaceholders(ConfigHandler.getHandler().getString("ChatRelay.WebhookMessages.AvatarURL"), event))
                .build();
    }

    /**
     * Searches a string
     * @param message String to be searched
     * @return String with all mentions formatted correctly for Discord.
     */
    public static String parseMentions(String message){

        ArrayList<String> usernames = new ArrayList<>();

        // Compile regex pattern for an @ followed by 2-32 word characters
        Pattern regex = Pattern.compile("@(\\w{2,32})");
        Matcher matcher = regex.matcher(message);

        // Finds all the sequences of characters that follow the @ symbol
        // 1 takes the second group - ignores @ symbol
        while (matcher.find()) {
            usernames.add(matcher.group(1));
        }

        // Iterate through usernames
        for (String username : usernames) {
            try {

                // Do nothing if player is online
                if (Bukkit.getServer().getPlayerExact(username) != null) continue;

                // Get UUID
                String uuid = MojangRequest.usernameToUUID(username);

                // Search for player in Database
                Long discordUser = DatabaseClient.getDatabase().getLinked(uuid);

                // If the user is not found in the database, do not mention
                if (discordUser == null) continue;

                // Replace usernames with mentions
                message = replaceAll(message, "@" + username, "<@" + discordUser + ">");


            } catch (IOException | SQLException e) {
                throw new RuntimeException(e);
            }
        }

        return message;
    }
}
