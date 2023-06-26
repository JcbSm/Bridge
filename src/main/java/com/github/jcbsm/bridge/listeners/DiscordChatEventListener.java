package com.github.jcbsm.bridge.listeners;

import com.github.jcbsm.bridge.Bridge;
import com.github.jcbsm.bridge.util.MessageFormatHandler;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class DiscordChatEventListener extends ListenerAdapter {
    @Override
    public void onMessageReceived(MessageReceivedEvent event) {

        // Ignore bots
        if (event.getAuthor().isBot())
            return;

        // Process event
        if (event.getChannel().getId().equals(Bridge.getPlugin().getChatChannelID())) {
            Bridge.getPlugin().broadcastMinecraftChatMessage(MessageFormatHandler.discordMessage(event));
        }
    }
}
