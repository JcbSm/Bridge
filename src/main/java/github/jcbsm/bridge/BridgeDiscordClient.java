package github.jcbsm.bridge;


import github.jcbsm.bridge.discord.ApplicationCommandHandler;
import github.jcbsm.bridge.discord.commands.PlayerListCommand;
import github.jcbsm.bridge.discord.commands.WhitelistCommand;
import github.jcbsm.bridge.exceptions.InvalidConfigException;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;

import javax.security.auth.login.LoginException;

public class BridgeDiscordClient {

    private JDA jda;
    private Guild guild;
    private TextChannel chat, console;
    private ApplicationCommandHandler applicationCommandHandler;

    public BridgeDiscordClient(String token, String chatChannelID, String consoleChannelID) throws LoginException, InvalidConfigException, InterruptedException {

        System.out.println("Attempting log in...");
        jda = JDABuilder.createDefault(token).build();

        // Wait for the client to log in properly
        jda.awaitReady();

        chat = jda.getTextChannelById(chatChannelID);
        console = jda.getTextChannelById(consoleChannelID);
        guild = chat.getGuild();

        System.out.println("Registering Application commands");
        applicationCommandHandler = new ApplicationCommandHandler(
                this,
                new PlayerListCommand(),
                new WhitelistCommand()
        );

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

    public Guild getGuild() {
        return guild;
    }

    public JDA getJDA() {
        return jda;
    }
}
