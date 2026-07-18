package nl.dictu.bbrandomskillbot.commands

import nl.dictu.bbrandomskillbot.skills.RandomSkillRoller
import nl.dictu.bbrandomskillbot.skills.SkillCategory
import net.dv8tion.jda.api.EmbedBuilder
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent
import net.dv8tion.jda.api.hooks.ListenerAdapter
import net.dv8tion.jda.api.interactions.commands.OptionType
import net.dv8tion.jda.api.interactions.commands.build.Commands
import net.dv8tion.jda.api.interactions.commands.build.OptionData
import java.awt.Color

/**
 * Implements `/randomskill category:<category>`: rolls 4 D6 (two 2D6 sub-rolls)
 * and reports the two resulting skills, per the Blood Bowl 2025 random skill
 * advancement rules.
 */
class RandomSkillCommand : ListenerAdapter() {

    companion object {
        const val NAME = "randomskill"
        private const val OPTION_CATEGORY = "category"

        /** Slash command definition to register with Discord. */
        fun commandData() = Commands.slash(NAME, "Roll a random Blood Bowl 2025 skill (4 dice, 2 skills)")
            .addOptions(
                OptionData(OptionType.STRING, OPTION_CATEGORY, "Skill category to roll on", true).also { option ->
                    SkillCategory.entries.forEach { category ->
                        option.addChoice(category.displayName, category.name)
                    }
                }
            )
    }

    override fun onSlashCommandInteraction(event: SlashCommandInteractionEvent) {
        if (event.name != NAME) return

        val categoryName = event.getOption(OPTION_CATEGORY)?.asString
        val category = categoryName?.let { runCatching { SkillCategory.valueOf(it) }.getOrNull() }

        if (category == null) {
            event.reply("Unknown skill category.").setEphemeral(true).queue()
            return
        }

        val result = RandomSkillRoller.roll(category)

        val diceSummary = result.rolls.joinToString("   ") { roll ->
            "🎲 ${roll.firstDie} + 🎲 ${roll.secondDie}"
        }

        val embed = EmbedBuilder()
            .setTitle("Random ${category.displayName} Skills")
            .setColor(Color(139, 0, 0))
            .setDescription("Rolling 4 dice for **2 random ${category.displayName} skills**...")
            .addField("Dice", diceSummary, false)
            .addField(
                "Skill 1 (${result.rolls[0].firstDie}, ${result.rolls[0].secondDie})",
                result.rolls[0].skill,
                true,
            )
            .addField(
                "Skill 2 (${result.rolls[1].firstDie}, ${result.rolls[1].secondDie})",
                result.rolls[1].skill,
                true,
            )
            .setFooter("Blood Bowl 2025 · Rolled for ${event.user.name}")
            .build()

        event.replyEmbeds(embed).queue()
    }
}
