package nl.dictu.bbrandomskillbot.skills

/**
 * Skill category, matching the letters used on Blood Bowl 2025 (Season 3 / Third
 * Season Edition) team roster sheets: A (Agility), D (Devious), G (General),
 * M (Mutation), P (Passing), S (Strength).
 *
 * Each category has exactly 12 skills. Traits (the "Extraordinary" abilities like
 * Bone-head, Really Stupid, Loner) are fixed on specific player types and are not
 * part of the random-skill-roll system, so they are intentionally not included here.
 */
enum class SkillCategory(val displayName: String, val letter: Char) {
    AGILITY("Agility", 'A'),
    DEVIOUS("Devious", 'D'),
    GENERAL("General", 'G'),
    MUTATION("Mutation", 'M'),
    PASSING("Passing", 'P'),
    STRENGTH("Strength", 'S'),
}

/**
 * Ordered skill tables (positions 1-12) for the BB2025 random skill roll.
 *
 * Rolling procedure (per the BB2025 rulebook):
 *  1. Roll a D6. 1-3 selects the top half (positions 1-6), 4-6 selects the
 *     bottom half (positions 7-12).
 *  2. Roll a second D6 (1-6). This maps 1:1 to the position within the chosen
 *     half (no offset), giving the final skill.
 */
object SkillTables {

    private val tables: Map<SkillCategory, List<String>> = mapOf(
        SkillCategory.AGILITY to listOf(
            "Catch",
            "Diving Catch",
            "Diving Tackle",
            "Dodge",
            "Defensive",
            "Hit and Run",
            "Jump Up",
            "Leap",
            "Safe Pair of Hands",
            "Sidestep",
            "Sprint",
            "Sure Feet",
        ),
        SkillCategory.DEVIOUS to listOf(
            "Dirty Player",
            "Eye Gouge",
            "Fumblerooski",
            "Lethal Flight",
            "Lone Fouler",
            "Pile Driver",
            "Put the Boot In",
            "Quick Foul",
            "Saboteur",
            "Shadowing",
            "Sneaky Git",
            "Violent Innovator",
        ),
        SkillCategory.GENERAL to listOf(
            "Block",
            "Dauntless",
            "Fend",
            "Frenzy",
            "Kick",
            "Pro",
            "Steady Footing",
            "Strip Ball",
            "Sure Hands",
            "Tackle",
            "Taunt",
            "Wrestle",
        ),
        SkillCategory.MUTATION to listOf(
            "Big Hand",
            "Claws",
            "Disturbing Presence",
            "Extra Arms",
            "Foul Appearance",
            "Horns",
            "Iron Hard Skin",
            "Monstrous Mouth",
            "Prehensile Tail",
            "Tentacles",
            "Two Heads",
            "Very Long Legs",
        ),
        SkillCategory.PASSING to listOf(
            "Accurate",
            "Cannoneer",
            "Cloud Burster",
            "Dump-Off",
            "Give and Go",
            "Hail Mary Pass",
            "Leader",
            "Nerves of Steel",
            "On the Ball",
            "Pass",
            "Punt",
            "Safe Pass",
        ),
        SkillCategory.STRENGTH to listOf(
            "Arm Bar",
            "Brawler",
            "Break Tackle",
            "Bullseye",
            "Grab",
            "Guard",
            "Juggernaut",
            "Mighty Blow",
            "Multiple Block",
            "Stand Firm",
            "Strong Arm",
            "Thick Skull",
        ),
    )

    init {
        tables.forEach { (category, skills) ->
            require(skills.size == 12) {
                "Category $category must have exactly 12 skills, has ${skills.size}"
            }
        }
    }

    /** Returns the skill list (1-12) for the given category. */
    fun skillsFor(category: SkillCategory): List<String> =
        tables.getValue(category)

    /**
     * Looks up a single skill by the two D6 results.
     *
     * @param firstDie 1-6, determines top half (1-3) or bottom half (4-6)
     * @param secondDie 1-6, position within the chosen half
     */
    fun lookup(category: SkillCategory, firstDie: Int, secondDie: Int): String {
        require(firstDie in 1..6) { "firstDie must be 1-6, was $firstDie" }
        require(secondDie in 1..6) { "secondDie must be 1-6, was $secondDie" }
        val half = if (firstDie <= 3) 0 else 1
        val index = half * 6 + (secondDie - 1)
        return tables.getValue(category)[index]
    }
}
