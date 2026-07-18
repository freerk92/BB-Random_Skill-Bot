package frl.freerk.bbrandomskillbot.commands

import frl.freerk.bbrandomskillbot.session.RollSession
import frl.freerk.bbrandomskillbot.session.RollSessionStore
import frl.freerk.bbrandomskillbot.skills.RandomSkillRoller
import frl.freerk.bbrandomskillbot.skills.SkillCategory
import net.dv8tion.jda.api.EmbedBuilder
import net.dv8tion.jda.api.components.actionrow.ActionRow
import net.dv8tion.jda.api.components.buttons.Button
import net.dv8tion.jda.api.entities.emoji.Emoji
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent
import net.dv8tion.jda.api.hooks.ListenerAdapter
import net.dv8tion.jda.api.interactions.commands.OptionType
import net.dv8tion.jda.api.interactions.commands.build.Commands
import net.dv8tion.jda.api.interactions.commands.build.OptionData
import java.awt.Color

/**
 * Implements `/randomskill category:<category>`: rolls 4 D6 (two 2D6 sub-rolls)
 * for 2 skills, per the BB2025 random skill mechanic, then lets the initiating
 * coach reroll either slot (or both) via buttons — since duplicates are
 * allowed by the rules, and a rolled skill the player already has, or can't
 * use, needs to be rerolled.
 *
 * The result message is edited in place on every reroll rather than reposted,
 * and every previous value in a slot is kept (struck through) so the full
 * roll history stays visible in the channel.
 */
class RandomSkillCommand : ListenerAdapter() {

    companion object {
        const val NAME = "randomskill"
        private const val OPTION_CATEGORY = "category"

        private const val BUTTON_REROLL_1 = "randomskill:reroll:1"
        private const val BUTTON_REROLL_2 = "randomskill:reroll:2"
        private const val BUTTON_REROLL_BOTH = "randomskill:reroll:both"

        /** Slash command definition to register with Discord. */
        fun commandData() = Commands.slash(NAME, "Roll 2 random Blood Bowl 2025 skills, with rerolls")
            .addOptions(
                OptionData(OptionType.STRING, OPTION_CATEGORY, "Skill category to roll on", true).also { option ->
                    SkillCategory.entries.forEach { category ->
                        option.addChoice(category.displayName, category.name)
                    }
                }
            )

        private fun rerollButtonRow(): ActionRow = ActionRow.of(
            Button.secondary(BUTTON_REROLL_1, "Reroll #1").withEmoji(Emoji.fromUnicode("♻️")),
            Button.secondary(BUTTON_REROLL_2, "Reroll #2").withEmoji(Emoji.fromUnicode("♻️")),
            Button.secondary(BUTTON_REROLL_BOTH, "Reroll Both").withEmoji(Emoji.fromUnicode("♻️")),
        )

        private fun buildEmbed(category: SkillCategory, slots: List<List<String>>, statusLine: String) =
            EmbedBuilder()
                .setTitle("🎲 Random ${category.displayName} Skills")
                .setColor(Color(139, 0, 0))
                .setDescription(RollMessageFormatter.description(slots, statusLine))
                .build()
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
        val slots: MutableList<MutableList<String>> = mutableListOf(
            mutableListOf(result.rolls[0].skill),
            mutableListOf(result.rolls[1].skill),
        )

        val embed = buildEmbed(category, slots, RollMessageFormatter.CHOOSE_PROMPT)

        event.replyEmbeds(embed)
            .addComponents(rerollButtonRow())
            .queue { hook ->
                hook.retrieveOriginal().queue { message ->
                    RollSessionStore.put(message.id, RollSession(category, event.user.id, slots))
                }
            }
    }

    override fun onButtonInteraction(event: ButtonInteractionEvent) {
        val action = when (event.componentId) {
            BUTTON_REROLL_1 -> RerollAction.SLOT_1
            BUTTON_REROLL_2 -> RerollAction.SLOT_2
            BUTTON_REROLL_BOTH -> RerollAction.BOTH
            else -> return
        }

        val session = RollSessionStore.get(event.messageId)
        if (session == null) {
            event.reply("This roll has expired (bot restarted since it was rolled). Use `/randomskill` to roll again.")
                .setEphemeral(true)
                .queue()
            return
        }

        if (event.user.id != session.initiatorId) {
            event.reply("Only the person who rolled these skills can reroll them.")
                .setEphemeral(true)
                .queue()
            return
        }

        when (action) {
            RerollAction.SLOT_1 -> rerollSlot(session, 0)
            RerollAction.SLOT_2 -> rerollSlot(session, 1)
            RerollAction.BOTH -> {
                rerollSlot(session, 0)
                rerollSlot(session, 1)
            }
        }

        val embed = buildEmbed(session.category, session.slots, RollMessageFormatter.statusLineFor(action))
        event.editMessageEmbeds(embed).setComponents(rerollButtonRow()).queue()
    }

    private fun rerollSlot(session: RollSession, index: Int) {
        val newSkill = RandomSkillRoller.rollOne(session.category).skill
        session.slots[index].add(newSkill)
    }
}
