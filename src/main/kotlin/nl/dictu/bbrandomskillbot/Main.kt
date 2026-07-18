package nl.dictu.bbrandomskillbot

import nl.dictu.bbrandomskillbot.commands.RandomSkillCommand
import net.dv8tion.jda.api.JDABuilder
import net.dv8tion.jda.api.requests.GatewayIntent
import org.slf4j.LoggerFactory
import kotlin.system.exitProcess

private val logger = LoggerFactory.getLogger("BBRandomSkillBot")

fun main() {
    val token = System.getenv("DISCORD_TOKEN")
    if (token.isNullOrBlank()) {
        logger.error("Missing DISCORD_TOKEN environment variable. Set it to your bot's token and try again.")
        exitProcess(1)
    }

    val jda = JDABuilder.createDefault(token, emptyList<GatewayIntent>())
        .addEventListeners(RandomSkillCommand())
        .build()

    jda.awaitReady()

    // Global slash command registration. Can take up to ~1 hour to propagate on
    // first deploy; use jda.getGuildById(id)?.updateCommands() instead while
    // developing for instant updates on a single test server.
    jda.updateCommands()
        .addCommands(RandomSkillCommand.commandData())
        .queue(
            { logger.info("Registered /${RandomSkillCommand.NAME} as a global slash command.") },
            { error -> logger.error("Failed to register slash commands", error) },
        )

    logger.info("BB Random Skill Bot is up and running as ${jda.selfUser.asTag}.")
}
