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

This is a skeleton: it doesn't yet implement the "reroll if the player already has
the skill / can't use it" rule or the optional "roll twice, choose one" advancement
rule — both are easy to layer on top of `RandomSkillRoller` later.

## Project layout

```
src/main/kotlin/nl/dictu/bbrandomskillbot/
  Main.kt                        bot bootstrap, reads token, registers the slash command
  commands/RandomSkillCommand.kt slash command definition + interaction handling
  skills/SkillTables.kt          the 6 skill category tables + dice-to-skill lookup
  skills/RandomSkillRoller.kt    rolls the 4 dice and resolves 2 skills
src/test/kotlin/...              unit tests for the dice/table logic
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
