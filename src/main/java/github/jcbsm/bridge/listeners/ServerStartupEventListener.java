package github.jcbsm.bridge.listeners;

import github.jcbsm.bridge.Bridge;
import github.jcbsm.bridge.util.PlaceholderFormatter;
import org.bukkit.event.Listener;
import org.bukkit.event.server.ServerLoadEvent;

public class ServerStartupEventListener implements Listener {

    public void onServerLoadEvent(ServerLoadEvent event) {

        if (event.getType() == ServerLoadEvent.LoadType.STARTUP) {
            Bridge.getPlugin().broadcastDiscordChatMessage(PlaceholderFormatter.serverLoad(event));
        }

    }
}
