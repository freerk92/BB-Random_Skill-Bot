package nl.dictu.bbrandomskillbot.session

import nl.dictu.bbrandomskillbot.skills.SkillCategory
import java.util.concurrent.ConcurrentHashMap

/**
 * Mutable state for one in-flight `/randomskill` roll: the two skill slots
 * (each holding the full reroll history, oldest first — the last entry is the
 * current result) plus who is allowed to reroll them.
 *
 * Kept in memory only, keyed by the Discord message ID. This means sessions do
 * not survive a bot restart, and the store grows for as long as the bot runs
 * (nothing ever evicts old sessions). Both are acceptable for this skeleton,
 * but worth revisiting — e.g. TTL-based eviction, or persisting sessions to a
 * small DB — if the bot ends up running for extended periods.
 */
class RollSession(
    val category: SkillCategory,
    val initiatorId: String,
    val slots: MutableList<MutableList<String>>,
) {
    init {
        require(slots.size == 2) { "Expected exactly 2 skill slots, got ${slots.size}" }
    }
}

object RollSessionStore {
    private val sessions = ConcurrentHashMap<String, RollSession>()

    fun put(messageId: String, session: RollSession) {
        sessions[messageId] = session
    }

    fun get(messageId: String): RollSession? = sessions[messageId]
}
