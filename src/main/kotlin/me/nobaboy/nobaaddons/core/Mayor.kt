package me.nobaboy.nobaaddons.core

import me.nobaboy.nobaaddons.core.MayorPerk.Companion.toPerk
import me.nobaboy.nobaaddons.data.jsonobjects.Perk
import me.nobaboy.nobaaddons.utils.StringUtils.title

enum class Mayor(vararg val perks: MayorPerk) {
	// Normal Mayors
	AATROX(
		MayorPerk.SLAYER_XP_BUFF,
		MayorPerk.PATHFINDER,
		MayorPerk.SLASHED_PRICING
	),
	COLE(
		MayorPerk.MINING_FIESTA,
		MayorPerk.MINING_XP_BUFF,
		MayorPerk.MOLTEN_FORGE,
		MayorPerk.PROSPECTION
	),
	DIANA(
		MayorPerk.PET_XP_BUFF,
		MayorPerk.LUCKY,
		MayorPerk.MYTHOLOGICAL_RITUAL,
		MayorPerk.SHARING_IS_CARING
	),
	DIAZ(
		MayorPerk.LONG_TERM_INVESTMENT,
		MayorPerk.SHOPPING_SPREE,
		MayorPerk.STOCK_EXCHANGE,
		MayorPerk.VOLUME_TRADING
	),
	FINNEGAN(
		MayorPerk.BLOOMING_BUSINESS,
		MayorPerk.GOATED,
		MayorPerk.PELT_POCALYPSE,
		MayorPerk.PEST_ERADICATOR
	),
	FOXY(
		MayorPerk.A_TIME_FOR_GIVING,
		MayorPerk.CHIVALROUS_CARNIVAL,
		MayorPerk.EXTRA_EVENT_MINING,
		MayorPerk.EXTRA_EVENT_FISHING,
		MayorPerk.EXTRA_EVENT_SPOOKY,
		MayorPerk.SWEET_BENEVOLENCE
	),
	MARINA(
		MayorPerk.DOUBLE_TROUBLE,
		MayorPerk.FISHING_XP_BUFF,
		MayorPerk.FISHING_FESTIVAL,
		MayorPerk.LUCK_OF_THE_SEA
	),
	PAUL(
		MayorPerk.BENEDICTION,
		MayorPerk.MARAUDER,
		MayorPerk.EZPZ
	),

	// Special Mayors
	JERRY(
		MayorPerk.PERKPOCALYPSE,
		MayorPerk.STATSPOCALYPSE,
		MayorPerk.JERRYPOCALYPSE
	),
	DERPY(
		MayorPerk.QUAD_TAXES,
		MayorPerk.TURBO_MINIONS,
		MayorPerk.DOUBLE_MOBS_HP,
		MayorPerk.MOAR_SKILLZ
	),
	SCORPIUS(
		MayorPerk.BRIBE,
		MayorPerk.DARKER_AUCTIONS
	),

	UNKNOWN;

	val mayorName = name.title()
	val activePerks get() = perks.filter { it.isActive }

	override fun toString(): String = mayorName

	fun activatePerks(perks: List<MayorPerk>) {
		this.perks.forEach { it.isActive = it in perks }
	}

	fun activateAllPerks(): Mayor {
		this.perks.forEach { it.isActive = true }
		return this
	}

	companion object {
		fun getMayor(name: String): Mayor? = entries.firstOrNull { it.mayorName == name }
		fun getMayor(perk: MayorPerk): Mayor? = entries.firstOrNull { it.perks.contains(perk) }

		fun getMayor(name: String, perks: List<Perk>): Mayor {
			val mayor = getMayor(name) ?: return UNKNOWN

			mayor.activatePerks(perks.mapNotNull { it.toPerk() })
			return mayor
		}
	}
}