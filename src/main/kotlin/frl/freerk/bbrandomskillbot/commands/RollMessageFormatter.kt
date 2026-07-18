package frl.freerk.bbrandomskillbot.commands

/** Which button was pressed on a `/randomskill` result message. */
enum class RerollAction {
    SLOT_1,
    SLOT_2,
    BOTH,
}

/**
 * Pure, JDA-free rendering logic for the `/randomskill` result message, kept
 * separate from [RandomSkillCommand] so it's directly unit-testable.
 */
object RollMessageFormatter {

    const val CHOOSE_PROMPT = "Choose one of these skills for your player."
    const val DUPLICATE_MESSAGE = "Duplicates are allowed according to the rulebook, so no choice for you. Sorry"

    /**
     * Renders one slot's history as a single skill name (`Wrestle`) if it has
     * never been rerolled, or as a strikethrough chain
     * (`~~Wrestle~~ → ~~Block~~ → Dauntless`) once it has. Only skill names are
     * struck through — the numbering the caller adds around this stays intact.
     */
    fun renderChain(history: List<String>): String {
        require(history.isNotEmpty()) { "history must not be empty" }
        if (history.size == 1) return history.first()
        val struckThrough = history.dropLast(1).joinToString(" → ") { "~~$it~~" }
        return "$struckThrough → ${history.last()}"
    }

    /** True if both slots' current (latest) skill are the same. */
    fun isDuplicate(slots: List<List<String>>): Boolean {
        require(slots.size == 2) { "expected exactly 2 slots, got ${slots.size}" }
        return slots[0].last() == slots[1].last()
    }

    /**
     * Full embed description: the two numbered slots, a blank line, then a
     * status line. If the two slots currently hold the same skill, the status
     * line is always [DUPLICATE_MESSAGE] regardless of what's passed in — per
     * the rules, duplicates are allowed and there's nothing to choose between.
     */
    fun description(slots: List<List<String>>, statusLine: String): String {
        require(slots.size == 2) { "expected exactly 2 slots, got ${slots.size}" }
        val lines = slots.mapIndexed { index, history -> "${index + 1}. ${renderChain(history)}" }
        val effectiveStatusLine = if (isDuplicate(slots)) DUPLICATE_MESSAGE else statusLine
        return (lines + listOf("", effectiveStatusLine)).joinToString("\n")
    }

    fun statusLineFor(action: RerollAction): String = when (action) {
        RerollAction.SLOT_1 -> "♻️ Skill #1 rerolled"
        RerollAction.SLOT_2 -> "♻️ Skill #2 rerolled"
        RerollAction.BOTH -> "♻️ Both skills rerolled"
    }
}
