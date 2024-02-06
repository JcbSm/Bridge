package com.github.jcbsm.bridge.listeners;

import com.github.jcbsm.bridge.Bridge;
import com.github.jcbsm.bridge.discord.BridgeDiscordClient;
import com.github.jcbsm.bridge.util.ChatRelayFormatter;
import com.github.jcbsm.bridge.util.ConfigHandler;
import com.github.jcbsm.bridge.util.MessageFormatHandler;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.bukkit.Bukkit;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.SoundCategory;
import org.bukkit.entity.Player;

import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DiscordChatEventListener extends ListenerAdapter {

    BridgeDiscordClient client;

    public DiscordChatEventListener(BridgeDiscordClient client) {
        this.client = client;
    }

    @Override
    public void onMessageReceived(MessageReceivedEvent event) {

        // Ignore bots, Ensure chat relay is enabled and ignore non-relay channel messages
        if (event.getAuthor().isBot() || !ConfigHandler.getHandler().getBoolean("ChatRelay.Enabled") || !Bridge.getPlugin().getDiscord().getRelayChannels().containsKey(event.getChannel().getId()))
            return;

        // Broadcast to MC
        Bridge.getPlugin().broadcastMinecraftChatMessage(ChatRelayFormatter.discordToMinecraft(event));

        // If Enabled: Play a Noise in MC if mention is online.
        if(Bridge.getPlugin().getConfig().getBoolean("ChatRelay.NoiseToMinecraft.Enabled")) {
            noiseToMentioned(event);
        }

        // If D2D is enabled, broadcast the message
        if (ConfigHandler.getHandler().getBoolean("ChatRelay.DiscordToDiscord.Enabled")) {
            Bridge.getPlugin().broadcastDiscordChatMessage(event);
        }
    }

    /**
     * Plays noise and gives particle if mentioned player is online.
     * @param event
     */
    private void noiseToMentioned(MessageReceivedEvent event){

        // Compile regex pattern for an @ followed by 2-32 word characters
        Pattern regex = Pattern.compile("@(\\w{2,32})");
        Matcher matcher = regex.matcher(event.getMessage().getContentDisplay());

        // Finds all the sequences of characters that follow the @ symbol
        // 1 takes the second group - ignores @ symbol
        while (matcher.find()) {
            for(Player player : Bukkit.getOnlinePlayers()){
                if(Objects.equals(player.getName(),matcher.group(1))){
                    player.playSound(player.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, SoundCategory.VOICE, (float) 10.0, (float) 0.5);
                    player.spawnParticle(Particle.SMALL_FLAME,player.getLocation().toHighestLocation(),50);
                }
            }
        }

    }
}
