package nl.dictu.bbrandomskillbot.commands

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

    /** Full embed description: the two numbered slots, a blank line, then the status line. */
    fun description(slots: List<List<String>>, statusLine: String): String {
        require(slots.size == 2) { "expected exactly 2 slots, got ${slots.size}" }
        val lines = slots.mapIndexed { index, history -> "${index + 1}. ${renderChain(history)}" }
        return (lines + listOf("", statusLine)).joinToString("\n")
    }

    fun statusLineFor(action: RerollAction): String = when (action) {
        RerollAction.SLOT_1 -> "♻️ Skill #1 rerolled"
        RerollAction.SLOT_2 -> "♻️ Skill #2 rerolled"
        RerollAction.BOTH -> "♻️ Both skills rerolled"
    }
}
