# A Guide to `config.yml`
### Prerequisites

0. Make a [discord application](https://discordjs.guide/preparations/setting-up-a-bot-application.html#creating-your-bot) (don't worry about installing node/discord.js), make note of the **token**, you'll need this later.
1. [Add your bot to your server(s)](https://discordjs.guide/preparations/adding-your-bot-to-servers.html).
   1. Choose the scopes `bot` and `application.commands`
   2. Select the permissions
       - Send Messages
       - Send Messages in Threads
       - Embed Links
       - Attach Files
       - Read Message History
       - Add Reactions
2. Ensure `Bot > Privelidge Gateway Intents > MESSAGE CONTENT INTENT` is enabled, along with `SERVER MEMBERS INTENT`.

---

1. Navigate to your `/server/plugins/Bridge/` directory and open `config.yml`.

```yml
# Discord Bot Token
# From your application at discord.com/developers
BotToken: "BOT_TOKEN_HERE"
```

2. Replace `"BOT_TOKEN_HERE"` with your **token** from step `1.`
3. Add the IDs of all the channels you want the Bot to access to the `Chat:` list

```yml
  # Channel IDs
  Channels:
    Chat: [
      "000000000000000000"
    ]
    Console: "000000000000000000"
```

> - The bot must have the necessary permissions for all channels
> - If you don't see the option to *copy channel ID* when right-clicking a channel. Enable `User Settings > Advanced > Developer mode`

4. Modify the rest of the file to your specifications and requirements.
5. Restart the server.
