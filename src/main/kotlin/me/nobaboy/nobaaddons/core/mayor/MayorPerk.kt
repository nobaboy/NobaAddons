package me.nobaboy.nobaaddons.core.mayor

import me.nobaboy.nobaaddons.api.skyblock.MayorAPI.foxyExtraEventPattern
import me.nobaboy.nobaaddons.data.json.Perk
import me.nobaboy.nobaaddons.utils.RegexUtils.mapFullMatch

enum class MayorPerk(val perkName: String) {
	// Aatrox
	SLAYER_XP_BUFF("Slayer XP Buff"),
	PATHFINDER("Pathfinder"),
	SLASHED_PRICING("SLASHED Pricing"),

	// Cole
	MINING_FIESTA("Mining Fiesta"),
	MINING_XP_BUFF("Mining XP Buff"),
	MOLTEN_FORGE("Molten Forge"),
	PROSPECTION("Prospection"),

	// Diana
	PET_XP_BUFF("Pet XP Buff"),
	LUCKY("Lucky!"),
	MYTHOLOGICAL_RITUAL("Mythological Ritual"),
	SHARING_IS_CARING("Sharing is Caring"),

	// Diaz
	LONG_TERM_INVESTMENT("Long Term Investment"),
	SHOPPING_SPREE("Shopping Spree"),
	STOCK_EXCHANGE("Stock Exchange"),
	VOLUME_TRADING("Volume Trading"),

	// Finnegan
	BLOOMING_BUSINESS("Blooming Business"),
	GOATED("GOATed"),
	PELT_POCALYPSE("Pelt-pocalypse"),
	PEST_ERADICATOR("Pest Eradicator"),

	// Foxy
	A_TIME_FOR_GIVING("A Time for Giving"),
	CHIVALROUS_CARNIVAL("Chivalrous Carnival"),
	EXTRA_EVENT_MINING("Extra Event (Mining)"),
	EXTRA_EVENT_FISHING("Extra Event (Fishing)"),
	EXTRA_EVENT_SPOOKY("Extra Event (Spooky)"),
	SWEET_BENEVOLENCE("Sweet Benevolence"),

	// Marina
	DOUBLE_TROUBLE("Double Trouble"),
	FISHING_XP_BUFF("Fishing XP Buff"),
	FISHING_FESTIVAL("Fishing Festival"),
	LUCK_OF_THE_SEA("Luck of the Sea 2.0"),

	// Paul
	BENEDICTION("Benediction"),
	MARAUDER("Marauder"),
	EZPZ("EZPZ"),

	// Jerry
	PERKPOCALYPSE("Perkpocalypse"),
	STATSPOCALYPSE("Statspocalypse"),
	JERRYPOCALYPSE("Jerrypocalypse"),

	// Derpy
	QUAD_TAXES("QUAD TAXES!!!"),
	TURBO_MINIONS("TURBO MINIONS!!!"),
	DOUBLE_MOBS_HP("DOUBLE MOBS HP!!!"),
	MOAR_SKILLZ("MOAR SKILLZ!!!"),

	// Scorpius
	BRIBE("Bribe"),
	DARKER_AUCTIONS("Darker Auctions");

	// FIXME noba why the fuck are these values mutable
	var isActive = false
	var description = "Failed to load description from API"
	var minister = false

	override fun toString(): String = "$perkName: $description"

	companion object {
		fun getByName(name: String): MayorPerk? = entries.firstOrNull { it.perkName == name }

		fun disableAll() = entries.forEach { it.isActive = false }

		fun Perk.toPerk(): MayorPerk? = getByName(this.renameFoxyPerks())?.apply {
			description = this@toPerk.description
			minister = this@toPerk.minister
		}

		private fun Perk.renameFoxyPerks(): String {
			val foxyExtraEventPairs = mapOf(
				"Mining Fiesta" to "Extra Event (Mining)",
				"Fishing Festival" to "Extra Event (Fishing)",
				"Spooky Festival" to "Extra Event (Spooky)"
			)

			return foxyExtraEventPattern.mapFullMatch(this.description) {
				foxyExtraEventPairs.entries.firstOrNull { it.key == groups["event"]?.value }?.value
			} ?: this.name
		}
	}
}