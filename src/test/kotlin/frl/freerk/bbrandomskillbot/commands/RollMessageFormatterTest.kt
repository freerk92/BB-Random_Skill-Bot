package frl.freerk.bbrandomskillbot.commands

import kotlin.test.Test
import kotlin.test.assertEquals

class RollMessageFormatterTest {

    @Test
    fun `a slot with no rerolls renders as just the skill name`() {
        assertEquals("Tackle", RollMessageFormatter.renderChain(listOf("Tackle")))
    }

    @Test
    fun `a slot rerolled once renders as a struck-through chain`() {
        assertEquals(
            "~~Wrestle~~ → Fend",
            RollMessageFormatter.renderChain(listOf("Wrestle", "Fend")),
        )
    }

    @Test
    fun `a slot rerolled twice keeps the full history, only the latest value unstruck`() {
        assertEquals(
            "~~Wrestle~~ → ~~Block~~ → Dauntless",
            RollMessageFormatter.renderChain(listOf("Wrestle", "Block", "Dauntless")),
        )
    }

    @Test
    fun `description numbers both slots and appends the status line after a blank line`() {
        val description = RollMessageFormatter.description(
            slots = listOf(listOf("Wrestle"), listOf("Tackle")),
            statusLine = RollMessageFormatter.CHOOSE_PROMPT,
        )
        assertEquals(
            "1. Wrestle\n2. Tackle\n\nChoose one of these skills for your player.",
            description,
        )
    }

    @Test
    fun `status line reflects which slot(s) were just rerolled`() {
        assertEquals("♻️ Skill #1 rerolled", RollMessageFormatter.statusLineFor(RerollAction.SLOT_1))
        assertEquals("♻️ Skill #2 rerolled", RollMessageFormatter.statusLineFor(RerollAction.SLOT_2))
        assertEquals("♻️ Both skills rerolled", RollMessageFormatter.statusLineFor(RerollAction.BOTH))
    }

    @Test
    fun `isDuplicate compares only the current (latest) value of each slot`() {
        assertEquals(true, RollMessageFormatter.isDuplicate(listOf(listOf("Block"), listOf("Block"))))
        assertEquals(false, RollMessageFormatter.isDuplicate(listOf(listOf("Block"), listOf("Tackle"))))
        // Slot 1 used to be "Block" (same as slot 2) but was rerolled to "Fend" - no longer a duplicate.
        assertEquals(false, RollMessageFormatter.isDuplicate(listOf(listOf("Block", "Fend"), listOf("Block"))))
    }

    @Test
    fun `description shows the duplicate message instead of the choose prompt when both slots match`() {
        val description = RollMessageFormatter.description(
            slots = listOf(listOf("Block"), listOf("Block")),
            statusLine = RollMessageFormatter.CHOOSE_PROMPT,
        )
        assertEquals(
            "1. Block\n2. Block\n\nDuplicates are allowed according to the rulebook, so no choice for you. Sorry",
            description,
        )
    }

    @Test
    fun `description shows the duplicate message instead of a reroll status line when a reroll lands on a duplicate`() {
        val description = RollMessageFormatter.description(
            slots = listOf(listOf("Wrestle", "Block"), listOf("Block")),
            statusLine = RollMessageFormatter.statusLineFor(RerollAction.SLOT_1),
        )
        assertEquals(
            "1. ~~Wrestle~~ → Block\n2. Block\n\nDuplicates are allowed according to the rulebook, so no choice for you. Sorry",
            description,
        )
    }
}
