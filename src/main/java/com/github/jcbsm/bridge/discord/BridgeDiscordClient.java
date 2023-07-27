package com.github.jcbsm.bridge.discord;


import club.minnced.discord.webhook.WebhookClient;
import club.minnced.discord.webhook.WebhookClientBuilder;
import club.minnced.discord.webhook.WebhookCluster;
import club.minnced.discord.webhook.send.WebhookMessage;
import club.minnced.discord.webhook.send.WebhookMessageBuilder;
import com.github.jcbsm.bridge.Bridge;
import com.github.jcbsm.bridge.exceptions.InvalidConfigException;
import com.github.jcbsm.bridge.listeners.DiscordChatEventListener;
import com.github.jcbsm.bridge.util.ConfigHandler;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Icon;
import net.dv8tion.jda.api.entities.Webhook;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.requests.RestAction;
import net.dv8tion.jda.api.requests.restaction.WebhookAction;
import net.dv8tion.jda.api.utils.cache.CacheFlag;
import net.dv8tion.jda.api.utils.messages.MessageCreateData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.security.auth.login.LoginException;
import java.net.URL;
import java.util.*;
import java.util.stream.Stream;

public class BridgeDiscordClient {

    private JDA jda;
    private Map<String, TextChannel> relayChannels = new HashMap<>();
    private Map<String, Webhook> hooks = new HashMap<>();
    private WebhookCluster hookCluster = new WebhookCluster(relayChannels.size());
    private final Logger logger = LoggerFactory.getLogger(this.getClass().getSimpleName());

    public BridgeDiscordClient(String token, List<String> relayChannelIds, String consoleChannelId) throws InterruptedException {

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
            builder.addEventListeners(new DiscordChatEventListener(this));
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
            Webhook hook = fetchClientWebhook(channel);
            hooks.put(hook.getId(), hook);
            hookCluster.buildWebhook(hook.getIdLong(), hook.getToken());
        }

        logger.info(relayChannels.toString());

        logger.info("Registering Application commands...");
        new ApplicationCommandHandler(this);
    }

    public JDA getJda() { return jda; }

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

    /**
     * Broadcast a message via webhooks
     * @param message Message to broadcast
     */
    public void broadcastMessage(WebhookMessage message) {
        hookCluster.broadcast(message);
    }

    /**
     * Send a message to all other channels that are linked
     * @param event Message received event
     */
    public void broadcastMessage(MessageReceivedEvent event) {

        // Build message
        WebhookMessageBuilder builder = new WebhookMessageBuilder()
                .setAvatarUrl(event.getMember().getEffectiveAvatarUrl())
                .setUsername(String.format("%s [ %s #%s ]", event.getMember().getEffectiveName(), event.getGuild().getName(), event.getChannel().getName()))
                .setContent(event.getMessage().getContentRaw());

        // Filter out the hook which the message was sent in and send.
        hookCluster.multicast((client -> !hooks.get(Long.toString(client.getId())).getChannel().getId().equals(event.getChannel().getId())), builder.build());
    }

    public void broadcastMessage(MessageCreateData message) {
        // Send the message & queue
        for (TextChannel channel : relayChannels.values()) {

            try {
                channel.sendMessage(message).queue();
            } catch (Exception e) {
                logger.warn("Error sending message to channel #{} [ID: {}]. {}", channel.getName(), channel.getId(), e.getLocalizedMessage());
            }
        }
    }

    /**
     * Get the chat relay text channels
     * @return Map of text channels, keyed by channel ID
     */
    public Map<String, TextChannel> getRelayChannels() {
        return relayChannels;
    }

    /**
     * Fetches [the first] Webhook that is owned by the client for a specific text channel, if not, creates one.
     * @param channel The channel to search in
     * @return Client owned webhook for that channel.
     */
    private Webhook fetchClientWebhook(TextChannel channel) {

        List<Webhook> hooks = channel.retrieveWebhooks().complete();

        // Try and find a hook
        Optional<Webhook> res = hooks.stream()
                .filter((hook) -> hook.getOwnerAsUser().getId().equals(jda.getSelfUser().getId()))
                .findFirst();

        // If present, return that
        if (res.isPresent()) {
            return res.get();

        // Otherwise, make one
        } else {

            WebhookAction builder = channel.createWebhook(jda.getSelfUser().getName());

            try {
                builder.setAvatar(Icon.from(new URL(jda.getSelfUser().getAvatarUrl()).openStream()));
            } catch (Exception e) {
                e.printStackTrace();
            }

            return builder.complete();
        }
    }
}
