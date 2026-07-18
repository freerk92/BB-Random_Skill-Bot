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
}
