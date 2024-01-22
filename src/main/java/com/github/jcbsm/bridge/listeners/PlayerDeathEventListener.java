package com.github.jcbsm.bridge.listeners;

import com.github.jcbsm.bridge.Bridge;
import com.github.jcbsm.bridge.util.ChatRelayFormatter;
import com.github.jcbsm.bridge.util.MessageFormatHandler;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;

public class PlayerDeathEventListener implements Listener {

    @EventHandler (priority = EventPriority.MONITOR)
    public void onPlayerDeathEvent(PlayerDeathEvent event) {

        Bridge.getPlugin().broadcastDiscordChatMessage(ChatRelayFormatter.playerDeath(event));

    }
}
