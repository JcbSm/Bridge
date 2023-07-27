package com.github.jcbsm.bridge.listeners;

import com.github.jcbsm.bridge.Bridge;
import com.github.jcbsm.bridge.util.ChatRelayFormatter;
import com.github.jcbsm.bridge.util.MessageFormatHandler;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerAdvancementDoneEvent;

public class PlayerAdvancementDoneEventListener implements Listener {

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerAdvancementDoneEvent(PlayerAdvancementDoneEvent event) {
        Bridge.getPlugin().broadcastDiscordChatMessage(ChatRelayFormatter.playerAdvancement(event));
    }
}
