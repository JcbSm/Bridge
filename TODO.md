<p align="center"><img src="/assets/touhou-sakuya.gif"><br><samp>pain and suffering</samp></p>

## EDGE CASES:
1. Database has a `UNIQUE` constraint on both `minecraft.minecraft_uuid` and `minecraft.minecraft_name` keys. Cannot have the same account linked more than twice.
2. Whitelist command will delete a linked account regardless of who invoked the command and which user the account is linked to.

## TODO:
1. Subcommands support and classes (might need to rewrite the `ApplicationCommand` classes)
2. OnMemberJoin event thingy - add member to database automatically
3. UpdateNameEvent - update members name in the database automatically.

## FEATS:
- OAuth2 with microsoft's servers to prove the account is legit maybe? - or go with discordsrv's approach. (current implementation works as unsafe registering)
- 