package github.jcbsm.bridge.listeners;

import github.jcbsm.bridge.Bridge;
import github.jcbsm.bridge.util.PlaceholderFormatter;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;

public class PlayerDeathEventListener implements Listener {

    @EventHandler (priority = EventPriority.MONITOR)
    public void onPlayerDeathEvent(PlayerDeathEvent event) {

        String msg = PlaceholderFormatter.playerDeath(event);
        Bridge.getPlugin().broadcastDiscordChatMessage(msg);

    }
}
