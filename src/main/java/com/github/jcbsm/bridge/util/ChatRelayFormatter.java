package com.github.jcbsm.bridge.util;

import io.papermc.paper.event.player.AsyncChatEvent;
import net.dv8tion.jda.api.utils.messages.MessageCreateBuilder;
import net.dv8tion.jda.api.utils.messages.MessageCreateData;

public class ChatRelayFormatter {

    private final static String PATH_PLAYER_EVENTS = "ChatRelay.MessageFormat.MinecraftToDiscord.PlayerEvents";
    private final static String PATH_SERVER_EVENTS = "ChatRelay.MessageFormat.MinecraftToDiscord.ServerEvents";

    public static MessageCreateData createMessage(AsyncChatEvent event) {
        MessageCreateBuilder builder = new MessageCreateBuilder();
    }
}
