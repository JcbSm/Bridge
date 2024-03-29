package com.github.jcbsm.bridge.listeners;

import com.github.jcbsm.bridge.Bridge;
import com.github.jcbsm.bridge.util.ChatRelayFormatter;
import com.github.jcbsm.bridge.util.MessageFormatHandler;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerLeaveEventListener implements Listener {

    @EventHandler(priority = EventPriority.HIGH)
    public void onPlayerQuitEvent(PlayerQuitEvent event) {
        Bridge.getPlugin().broadcastDiscordChatMessage(ChatRelayFormatter.playerLeave(event));
    }

//    @EventHandler (priority = EventPriority.HIGH)
//    public void onPlayerKickEvent(PlayerKickEvent event) {
//        Bridge.getPlugin().broadcastDiscordChatMessage(ChatRelayFormatter.playerLeave(event));
//    }
}
