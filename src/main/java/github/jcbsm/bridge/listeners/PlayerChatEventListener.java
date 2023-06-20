package github.jcbsm.bridge.listeners;

import github.jcbsm.bridge.Bridge;
import org.bukkit.Bukkit;
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
    public void onAsyncPlayerChat(AsyncPlayerChatEvent event) {

        // (No idea why this works)
        Bukkit.getScheduler().runTaskAsynchronously(Bridge.getPlugin(), () ->
                Bridge.getPlugin().processChatMessage(
                        event.getPlayer().getName(),
                        event.getMessage()
                )
        );
    }
}
