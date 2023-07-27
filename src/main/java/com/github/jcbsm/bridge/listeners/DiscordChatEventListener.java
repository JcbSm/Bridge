package com.github.jcbsm.bridge.listeners;

import com.github.jcbsm.bridge.Bridge;
import com.github.jcbsm.bridge.discord.BridgeDiscordClient;
import com.github.jcbsm.bridge.util.ChatRelayFormatter;
import com.github.jcbsm.bridge.util.ConfigHandler;
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
        if (event.getAuthor().isBot() || !ConfigHandler.getHandler().getBoolean("ChatRelay.Enabled") || !Bridge.getPlugin().getDiscord().getRelayChannels().containsKey(event.getChannel().getId()))
            return;

        // Broadcast to MC
        Bridge.getPlugin().broadcastMinecraftChatMessage(ChatRelayFormatter.discordToMinecraft(event));

        if (ConfigHandler.getHandler().getBoolean("ChatRelay.DiscordToDiscord.Enabled")) {
            Bridge.getPlugin().broadcastDiscordChatMessage(event);
        }
    }
}
