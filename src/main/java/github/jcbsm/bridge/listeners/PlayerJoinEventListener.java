package github.jcbsm.bridge.listeners;

import github.jcbsm.bridge.Bridge;
import github.jcbsm.bridge.util.PlaceholderFormatter;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class PlayerJoinEventListener implements Listener {

    @EventHandler (priority = EventPriority.MONITOR)
    public void onPlayerJoinEvent(PlayerJoinEvent event) {
        Bridge.getPlugin().broadcastDiscordChatMessage(PlaceholderFormatter.playerJoin(event));
    }
}
