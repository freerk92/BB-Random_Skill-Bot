package nl.dictu.bbrandomskillbot.skills

import java.security.SecureRandom
import kotlin.random.Random
import kotlin.random.asKotlinRandom

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
     * Backing RNG for real rolls. `kotlin.random.Random.Default` seeds itself from
     * the system clock at first use, which can produce correlated results if the
     * process restarts on a low-resolution clock (e.g. some container hosts). A
     * `SecureRandom` draws from the OS entropy pool instead, so it doesn't have
     * that failure mode. This is a single shared instance, reused (not recreated)
     * across rolls, so its internal state keeps advancing normally between calls.
     */
    private val defaultRandom: Random = SecureRandom().asKotlinRandom()

    /**
     * Rolls a single 2D6 sub-roll (2 dice) for the given category and returns the
     * one resulting skill. Used both for the initial roll and for single-slot
     * rerolls.
     */
    fun rollOne(category: SkillCategory, random: Random = defaultRandom): SkillRoll {
        val first = random.nextInt(1, 7)
        val second = random.nextInt(1, 7)
        return SkillRoll(first, second, SkillTables.lookup(category, first, second))
    }

    /**
     * Rolls 4 D6 (two independent 2D6 sub-rolls) for the given category and
     * returns the two resulting skills, per the BB2025 random skill mechanic.
     * Duplicates between the two rolls are allowed (per the rules) — the coach
     * decides whether to reroll a duplicate or an already-owned skill.
     */
    fun roll(category: SkillCategory, random: Random = defaultRandom): RandomSkillResult {
        val rolls = (1..2).map { rollOne(category, random) }
        return RandomSkillResult(category, rolls)
    }
}
