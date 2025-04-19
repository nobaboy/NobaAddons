package me.nobaboy.nobaaddons.core

import kotlinx.serialization.Serializable
import me.nobaboy.nobaaddons.repo.Repo

@Serializable
data class SkillData(
	val standardXpScale: List<Int>,
	val skillCaps: Map<Skill, Int>,
	val dungeonOverflowXp: Long,
	val skillXpScale: Map<Skill, List<Int>>,
	val dungeonXpScaling: List<Int>,
) {
	fun calculateSkillXp(skill: Skill?, level: Int): Int {
		val scale = if(skill == null) standardXpScale else skillXpScale.getOrDefault(skill, standardXpScale)
		return scale.slice(0 until level.coerceAtMost(scale.size)).sum()
	}

	fun calculateDungeonXp(level: Int): Long {
		val targetXpWithoutOverlevel: Int = dungeonXpScaling.slice(0 until level.coerceAtMost(50)).sum()
		val overlevelXp: Long = if(level > 50) dungeonOverflowXp * (level - 50) else 0
		return targetXpWithoutOverlevel + overlevelXp
	}

	companion object {
		val INSTANCE by Repo.create("data/skills.json", serializer())
	}
}