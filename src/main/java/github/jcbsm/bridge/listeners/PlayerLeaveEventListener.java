package github.jcbsm.bridge.listeners;

import github.jcbsm.bridge.Bridge;
import github.jcbsm.bridge.util.PlaceholderFormatter;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerLeaveEventListener implements Listener {

    @EventHandler(priority = EventPriority.HIGH)
    public void onPlayerQuitEvent(PlayerQuitEvent event) {
        Bridge.getPlugin().broadcastDiscordChatMessage(PlaceholderFormatter.playerLeave(event));
    }

    @EventHandler (priority = EventPriority.HIGH)
    public void onPlayerKickEvent(PlayerKickEvent event) {
        Bridge.getPlugin().broadcastDiscordChatMessage(PlaceholderFormatter.playerKick(event));
    }
}
