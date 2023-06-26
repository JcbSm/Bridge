package com.github.jcbsm.bridge.listeners;

import com.github.jcbsm.bridge.Bridge;
import com.github.jcbsm.bridge.discord.BridgeDiscordClient;
import com.github.jcbsm.bridge.util.MessageFormatHandler;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class DiscordChatEventListener extends ListenerAdapter {

    BridgeDiscordClient client;

    public DiscordChatEventListener(BridgeDiscordClient client) {
        this.client = client;
    }

    @Override
    public void onMessageReceived(MessageReceivedEvent event) {

        // Ignore bots
        if (event.getAuthor().isBot())
            return;

        // Process event
        if (client.getRelayChannels().get(event.getChannel().getId()) != null) {
            Bridge.getPlugin().broadcastMinecraftChatMessage(MessageFormatHandler.discordMessage(event));
        }
    }
}
