package nl.dictu.bbrandomskillbot.skills

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class SkillTablesTest {

    @Test
    fun `every category has exactly 12 skills`() {
        SkillCategory.entries.forEach { category ->
            assertEquals(12, SkillTables.skillsFor(category).size, "category=$category")
        }
    }

    @Test
    fun `first die 1-3 selects the top half, position maps 1-1 with second die`() {
        val skills = SkillTables.skillsFor(SkillCategory.GENERAL)
        assertEquals(skills[0], SkillTables.lookup(SkillCategory.GENERAL, firstDie = 1, secondDie = 1))
        assertEquals(skills[5], SkillTables.lookup(SkillCategory.GENERAL, firstDie = 3, secondDie = 6))
    }

    @Test
    fun `first die 4-6 selects the bottom half`() {
        val skills = SkillTables.skillsFor(SkillCategory.GENERAL)
        assertEquals(skills[6], SkillTables.lookup(SkillCategory.GENERAL, firstDie = 4, secondDie = 1))
        assertEquals(skills[11], SkillTables.lookup(SkillCategory.GENERAL, firstDie = 6, secondDie = 6))
    }

    @Test
    fun `random skill roller produces 4 dice and 2 skills from the correct category`() {
        val result = RandomSkillRoller.roll(SkillCategory.STRENGTH, random = kotlin.random.Random(42))
        assertEquals(2, result.rolls.size)
        result.rolls.forEach { roll ->
            assertTrue(roll.firstDie in 1..6)
            assertTrue(roll.secondDie in 1..6)
            assertTrue(SkillTables.skillsFor(SkillCategory.STRENGTH).contains(roll.skill))
        }
    }

    @Test
    fun `rollOne produces a single valid skill from the requested category, for use on a reroll`() {
        val roll = RandomSkillRoller.rollOne(SkillCategory.PASSING, random = kotlin.random.Random(7))
        assertTrue(roll.firstDie in 1..6)
        assertTrue(roll.secondDie in 1..6)
        assertTrue(SkillTables.skillsFor(SkillCategory.PASSING).contains(roll.skill))
    }
}
