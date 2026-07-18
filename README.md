# BB-Random_Skill-Bot

A Discord bot (Kotlin + [JDA](https://github.com/discord-jda/JDA)) that rolls random
Blood Bowl 2025 (Season 3 / Third Season Edition) player skills.

## What it does

`/randomskill category:<Agility|Devious|General|Mutation|Passing|Strength>` rolls
4 D6 (two independent 2D6 sub-rolls) and returns the 2 resulting skills, following
the official random skill mechanic:

1. Roll a D6 — 1-3 picks the top half of that category's 12-skill table, 4-6 picks
   the bottom half.
2. Roll a second D6 (1-6) — picks the exact skill within that half.
3. Repeat once more for a second skill.

The skill tables (`src/main/kotlin/.../skills/SkillTables.kt`) are sourced from the
BB2025 rulebook via bloodbowlbase.ru and mordorbihan.fr.

Duplicates between the two rolled skills are allowed, per the rules — it's up to
the coach to reroll if a rolled skill turns out to be one their player already
has or can't use. The result message has three buttons for that:

- **Reroll #1** / **Reroll #2** — rerolls just that slot
- **Reroll Both** — rerolls both slots

Only the coach who ran `/randomskill` can use the buttons on their own roll;
anyone else gets an ephemeral "only the person who rolled these skills can
reroll them" message. Every reroll edits the original message in place rather
than posting a new one, and keeps the full history for that slot with earlier
values struck through, e.g.:

```
🎲 Random General Skills
1. ~~Wrestle~~ → ~~Block~~ → Dauntless
2. Tackle

♻️ Skill #1 rerolled
```

Session state (which user rolled, and each slot's history) is held in memory,
keyed by message ID — it does not survive a bot restart, and nothing currently
evicts old sessions. Fine for a skeleton; worth revisiting with TTL eviction or
a small DB if the bot runs long-term with heavy use.

## Project layout

```
src/main/kotlin/nl/dictu/bbrandomskillbot/
  Main.kt                          bot bootstrap, reads token, registers the slash command
  commands/RandomSkillCommand.kt   slash command + button interaction handling
  commands/RollMessageFormatter.kt pure rendering logic for the result embed (unit-tested)
  session/RollSession.kt           in-memory reroll state, keyed by message ID
  skills/SkillTables.kt            the 6 skill category tables + dice-to-skill lookup
  skills/RandomSkillRoller.kt      rolls the dice and resolves skills (full roll or single reroll)
src/test/kotlin/...                unit tests for the dice/table/message logic
```

## Prerequisites

- JDK 21+
- Gradle (this repo does not vendor the Gradle wrapper jar; either install Gradle
  yourself — e.g. `brew install gradle` / [sdkman](https://sdkman.io) — or open the
  folder in IntelliJ IDEA, which bundles Gradle and can generate the wrapper for
  you on first import)
- A Discord bot application + token: create one at
  https://discord.com/developers/applications → **Bot** tab → **Reset Token**.
  Under **Bot** → **Privileged Gateway Intents**, no privileged intents are needed
  for this bot (it only uses slash commands).

## Setup

1. Invite the bot to your server using an OAuth2 URL with the `bot` and
   `applications.commands` scopes (Developer Portal → OAuth2 → URL Generator).
2. Set the bot token as an environment variable:

   ```bash
   export DISCORD_TOKEN="your-token-here"
   ```

   (On Railway, set this as a variable named `DISCORD_TOKEN` in the project's Variables tab.)

3. Build and run:

   ```bash
   gradle run
   ```

   Or build a runnable fat jar and run it directly:

   ```bash
   gradle shadowJar
   java -jar build/libs/bb-random-skill-bot.jar
   ```

Slash commands are registered globally, which can take up to ~1 hour to appear
the first time. For instant updates while developing, swap the global
`jda.updateCommands()` call in `Main.kt` for
`jda.getGuildById(YOUR_TEST_SERVER_ID)?.updateCommands()`.

## Tests

```bash
gradle test
```
