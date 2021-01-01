# kolorek
Simple Discord BOT made to manage color roles

Kolorek creates colored roles to differentiate your guild members.
They can set a role name and a role color, using the ->kolorek command (->kolorek role name #ROLECOLORINRGBHEX)

This bot also keeps leaving members' roles in Heroku Postgres and restores them after they return (You need to enable privileged intents).

Two major environment variables:
- **DISCORD_TOKEN** (Discord bot token)
- **DEFAULT_ROLE** (Default guild role, used for setting roles positions)
