package me.nobaboy.nobaaddons.core.mayor

import me.nobaboy.nobaaddons.api.skyblock.MayorAPI.ActiveMayor
import me.nobaboy.nobaaddons.core.mayor.MayorPerk.Companion.toPerk
import me.nobaboy.nobaaddons.data.json.Perk

enum class Mayor(displayName: String? = null, vararg val perks: MayorPerk) {
	// Normal Mayors
	AATROX("Aatrox", MayorPerk.SLAYER_XP_BUFF, MayorPerk.PATHFINDER, MayorPerk.SLASHED_PRICING),
	COLE("Cole", MayorPerk.MINING_FIESTA, MayorPerk.MINING_XP_BUFF, MayorPerk.MOLTEN_FORGE, MayorPerk.PROSPECTION),
	DIANA("Diana", MayorPerk.PET_XP_BUFF, MayorPerk.LUCKY, MayorPerk.MYTHOLOGICAL_RITUAL, MayorPerk.SHARING_IS_CARING),
	DIAZ("Diaz", MayorPerk.LONG_TERM_INVESTMENT, MayorPerk.SHOPPING_SPREE, MayorPerk.STOCK_EXCHANGE, MayorPerk.VOLUME_TRADING),
	FINNEGAN("Finnegan", MayorPerk.BLOOMING_BUSINESS, MayorPerk.GOATED, MayorPerk.PELT_POCALYPSE, MayorPerk.PEST_ERADICATOR),
	FOXY("Foxy", MayorPerk.A_TIME_FOR_GIVING, MayorPerk.CHIVALROUS_CARNIVAL, MayorPerk.EXTRA_EVENT_MINING, MayorPerk.EXTRA_EVENT_FISHING, MayorPerk.EXTRA_EVENT_SPOOKY, MayorPerk.SWEET_BENEVOLENCE),
	MARINA("Marina", MayorPerk.DOUBLE_TROUBLE, MayorPerk.FISHING_XP_BUFF, MayorPerk.FISHING_FESTIVAL, MayorPerk.LUCK_OF_THE_SEA),
	PAUL("Paul", MayorPerk.BENEDICTION, MayorPerk.MARAUDER, MayorPerk.EZPZ),

	// Special Mayors
	JERRY("Jerry", MayorPerk.PERKPOCALYPSE, MayorPerk.STATSPOCALYPSE, MayorPerk.JERRYPOCALYPSE),
	DERPY("Derpy", MayorPerk.QUAD_TAXES, MayorPerk.TURBO_MINIONS, MayorPerk.DOUBLE_MOBS_HP, MayorPerk.MOAR_SKILLZ),
	SCORPIUS("Scorpius", MayorPerk.BRIBE, MayorPerk.DARKER_AUCTIONS),

	UNKNOWN;

	val displayName = displayName ?: name

	fun with(perks: List<Perk>) = ActiveMayor(this, perks.mapNotNull { it.toPerk() })
	fun withAll() = ActiveMayor(this, perks.toList())
	fun withNone() = ActiveMayor(this, emptyList())

	companion object {
		fun getByName(name: String): Mayor? = entries.firstOrNull { it.displayName.equals(name, ignoreCase = true) }
		fun getByPerk(perk: MayorPerk): Mayor? = entries.firstOrNull { perk in it.perks }
	}
}