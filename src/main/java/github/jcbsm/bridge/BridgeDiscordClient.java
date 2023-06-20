package github.jcbsm.bridge;


import github.jcbsm.bridge.exceptions.InvalidConfigException;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.TextChannel;

import javax.security.auth.login.LoginException;

public class BridgeDiscordClient {

    private JDA jda;
    private TextChannel chat, console;

    public BridgeDiscordClient(String token, String chatChannelID, String consoleChanenlID) throws LoginException, InvalidConfigException, InterruptedException {

        System.out.println("Attempting log in...");
        jda = JDABuilder.createDefault(token).build();

        // Wait for the client to log in properly
        jda.awaitReady();

        chat = jda.getTextChannelById(chatChannelID);
        console = jda.getTextChannelById(consoleChanenlID);
    }

    /**
     * Sends a message to the 'chat' discord channel
     * @param username The username of the player who sent the message
     * @param content The content of the message sent
     */
    public void sendChatMessage(String username, String content) {

        // Send the message & queue
        chat.sendMessage("**" + username + ": **" + content).queue();
    }
}
