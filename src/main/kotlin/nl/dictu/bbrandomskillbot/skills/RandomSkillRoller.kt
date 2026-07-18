package nl.dictu.bbrandomskillbot.skills

import kotlin.random.Random

/** One 2D6 sub-roll and the skill it resolved to. */
data class SkillRoll(
    val firstDie: Int,
    val secondDie: Int,
    val skill: String,
)

/** Result of a full random-skill roll: two independent 2D6 sub-rolls (4 dice total). */
data class RandomSkillResult(
    val category: SkillCategory,
    val rolls: List<SkillRoll>,
)

object RandomSkillRoller {

    /**
     * Rolls 4 D6 (two independent 2D6 sub-rolls) for the given category and
     * returns the two resulting skills, per the BB2025 random skill mechanic.
     */
    fun roll(category: SkillCategory, random: Random = Random.Default): RandomSkillResult {
        val rolls = (1..2).map {
            val first = random.nextInt(1, 7)
            val second = random.nextInt(1, 7)
            SkillRoll(first, second, SkillTables.lookup(category, first, second))
        }
        return RandomSkillResult(category, rolls)
    }
}
