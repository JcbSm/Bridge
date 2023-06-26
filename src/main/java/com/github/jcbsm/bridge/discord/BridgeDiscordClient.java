package com.github.jcbsm.bridge.discord;


import com.github.jcbsm.bridge.Bridge;
import com.github.jcbsm.bridge.exceptions.InvalidConfigException;
import com.github.jcbsm.bridge.listeners.DiscordChatEventListener;
import com.github.jcbsm.bridge.discord.ApplicationCommandHandler;
import com.github.jcbsm.bridge.discord.commands.PlayerListCommand;
import com.github.jcbsm.bridge.discord.commands.WhitelistCommand;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.cache.CacheFlag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.security.auth.login.LoginException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BridgeDiscordClient {

    private JDA jda;
    private Guild guild;
    private Map<String, TextChannel> relayChannels = new HashMap<>();
    private ApplicationCommandHandler applicationCommandHandler;
    private final Logger logger = LoggerFactory.getLogger(this.getClass().getSimpleName());

    public BridgeDiscordClient(String token, List<String> relayChannelIds, String consoleChannelID) throws LoginException, InvalidConfigException, InterruptedException {

        logger.info("Attempting log in...");
        JDABuilder builder = JDABuilder
                .createDefault(token,
                        GatewayIntent.MESSAGE_CONTENT,
                        GatewayIntent.GUILD_MESSAGES,
                        GatewayIntent.GUILD_MEMBERS)
                .disableCache(
                        CacheFlag.VOICE_STATE,
                        CacheFlag.EMOJI,
                        CacheFlag.STICKER,
                        CacheFlag.SCHEDULED_EVENTS);

        if (Bridge.getPlugin().getConfig().getBoolean("ChatRelay.Enabled")) {
            logger.info("Enabling Discord Chat Relay listeners...");
            builder.addEventListeners(new DiscordChatEventListener());
        }

        jda = builder.build();

        // Wait for the client to log in properly
        jda.awaitReady();

        // Get channels
        for (String id : relayChannelIds) {
            TextChannel channel = jda.getTextChannelById(id);

            if (channel == null) {
                logger.warn("Channel with ID {} does not exist. Please remove this from config or ensure the Bot is in this server.");
                continue;
            }

            relayChannels.put(id, channel);
        }

        logger.info(relayChannels.toString());

        logger.info("Registering Application commands...");
        applicationCommandHandler = new ApplicationCommandHandler(this);
    }

    public JDA getJDA() {
        return jda;
    }

    /**
     * Sends a message to the 'chat' discord channel
     * @param content The content of the message sent
     */
    public void broadcastMessage(String content) {

        // Send the message & queue
        for (TextChannel channel : relayChannels.values()) {
            try {
                channel.sendMessage(content).queue();
            } catch (Exception e) {
                logger.warn("Error sending message to channel #{} [ID: {}]. {}", channel.getName(), channel.getId(), e.getLocalizedMessage());
            }
        }
    }
}
