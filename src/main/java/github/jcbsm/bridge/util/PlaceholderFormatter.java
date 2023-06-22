package github.jcbsm.bridge.util;

import github.jcbsm.bridge.Bridge;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.regex.Pattern;

public class PlaceholderFormatter {

    private final static FileConfiguration config = Bridge.getPlugin().getConfig();

    private final static String
        escapeRegex = "(?<!\\\\)",
        username = "%username%",
        displayName = "%name%",
        message = "%message%",
        channel = "%channel%",
        world = "%world%",
        achievementTitle = "%title%",
        achievementDescription = "%description%";

    /**
     * Form a string based off the format in config.yml
     * @param event The event to use
     * @return A formatted string message
     */
    public static String discordMessage(MessageReceivedEvent event) {

        // Get config string
        String str = config.getString("MessageFormat.DiscordToMinecraft.Chat");

        // Replace all %username%s
        Pattern p = Pattern.compile(escapeRegex + username);
        str = p.matcher(str).replaceAll(event.getAuthor().getGlobalName());

        // Replace all %message%s
        p = Pattern.compile(escapeRegex + message);
        str = p.matcher(str).replaceAll(event.getMessage().getContentStripped());

        return str;

    }
}
