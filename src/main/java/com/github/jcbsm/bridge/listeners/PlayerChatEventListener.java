package com.github.jcbsm.bridge.listeners;

import com.github.jcbsm.bridge.Bridge;
import com.github.jcbsm.bridge.util.ChatRelayFormatter;
import com.github.jcbsm.bridge.util.ConfigHandler;
import com.github.jcbsm.bridge.util.MessageFormatHandler;
import io.papermc.paper.event.player.AsyncChatEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

public class PlayerChatEventListener implements Listener {

    /**
     * Handles chat messages from the Minecraft chat
     * @param event
     */
    @EventHandler(priority = EventPriority.MONITOR)
    public void onAsyncPlayerChat(AsyncChatEvent event) {

        if (event.isCancelled()) return;

        if (ConfigHandler.getHandler().getBoolean("ChatRelay.WebhookMessages.Enabled")) {

            Bridge.getPlugin().broadcastDiscordChatMessage(ChatRelayFormatter.playerChatWebhook(event));
        } else {
            Bridge.getPlugin().broadcastDiscordChatMessage(ChatRelayFormatter.playerChat(event));
        }
    }
}
