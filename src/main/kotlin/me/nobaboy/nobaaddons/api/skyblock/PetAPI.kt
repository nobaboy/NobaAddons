package me.nobaboy.nobaaddons.api.skyblock

import kotlinx.serialization.Serializable
import me.nobaboy.nobaaddons.NobaAddons
import me.nobaboy.nobaaddons.core.Rarity
import me.nobaboy.nobaaddons.data.PersistentCache
import me.nobaboy.nobaaddons.data.PetData
import me.nobaboy.nobaaddons.data.json.PetInfo
import me.nobaboy.nobaaddons.events.InventoryEvents
import me.nobaboy.nobaaddons.events.skyblock.SkyBlockEvents
import me.nobaboy.nobaaddons.repo.Repo
import me.nobaboy.nobaaddons.repo.Repo.fromRepo
import me.nobaboy.nobaaddons.utils.RegexUtils.getGroupFromFullMatch
import me.nobaboy.nobaaddons.utils.RegexUtils.onFullMatch
import me.nobaboy.nobaaddons.utils.StringUtils.cleanFormatting
import me.nobaboy.nobaaddons.utils.items.ItemUtils.getSkyBlockItem
import net.fabricmc.fabric.api.client.message.v1.ClientReceiveMessageEvents
import net.minecraft.item.ItemStack
import net.minecraft.screen.slot.SlotActionType
import org.lwjgl.glfw.GLFW

object PetAPI {
	val constants by Repo.create("pets/constants.json", PetConstants.serializer())

	private val petsMenuPattern by Regex("^Pets(?: \\(\\d+/\\d+\\) )?").fromRepo("pets.menu_title")
	private val petNamePattern by Regex("^(?<favorite>⭐ )?\\[Lvl (?<level>\\d+)] (?:\\[\\d+✦] )?(?<name>[A-z- ]+)(?: ✦|\$)").fromRepo("pets.name")

	// TODO cache autopet rule pets to allow for getting complete data
	private val autopetPattern by Regex(
		"^§cAutopet §eequipped your §7\\[Lvl (?<level>\\d+)] (?:§.\\[.*] )?§(?<rarity>.)(?<name>[A-z ]+)(?:§. ✦)?§e! §a§lVIEW RULE"
	).fromRepo("pets.autopet")

	private val petUnequipPattern by Regex("^You despawned your (?<name>[A-z ]+)(?: ✦)?!").fromRepo("pets.despawn")

	private var inPetsMenu = false

	var currentPet: PetData? = null
		get() = if(SkyBlockAPI.inSkyBlock) field else null
		private set(value) {
			if(value != PersistentCache.pet) PersistentCache.pet = value
			field = value
		}

	fun init() {
		InventoryEvents.OPEN.register(this::onInventoryOpen)
		InventoryEvents.SLOT_CLICK.register(this::onInventorySlotClick)
		ClientReceiveMessageEvents.GAME.register { message, _ -> onChatMessage(message.string) }
		currentPet = PersistentCache.pet
	}

	private fun onInventoryOpen(event: InventoryEvents.Open) {
		inPetsMenu = petsMenuPattern.matches(event.inventory.title)
		if(!inPetsMenu) return

		event.inventory.items.values.forEach { itemStack ->
			val pet = getPetData(itemStack) ?: return@forEach
			if(!pet.active) return@forEach

			changePet(pet)
		}
	}

	private fun onInventorySlotClick(event: InventoryEvents.SlotClick) {
		if(!inPetsMenu) return
		if(event.button != GLFW.GLFW_MOUSE_BUTTON_1) return
		if(event.actionType != SlotActionType.PICKUP) return

		getPetData(event.itemStack)?.let { changePet(it) }
	}

	private fun onChatMessage(message: String) {
		petUnequipPattern.onFullMatch(message.cleanFormatting()) {
			changePet(null)
		}

		autopetPattern.onFullMatch(message) {
			val name = groups["name"]?.value ?: return
			val id = name.uppercase().replace(" ", "_")
			val level = groups["level"]?.value?.toInt() ?: return
			val rarity = Rarity.getByColorCode(groups["rarity"]?.value?.first() ?: return)
			if(rarity == Rarity.UNKNOWN) NobaAddons.LOGGER.warn("Failed to get pet rarity from Autopet chat message: '$message'")
			val xpRarity = if(id == "BINGO") Rarity.COMMON else rarity

			val pet = PetData(name, id, xpFromLevel(level, xpRarity, if(id == "GOLDEN_DRAGON") 200 else 100), rarity, active = true)
			changePet(pet)
		}
	}

	private fun changePet(pet: PetData?) {
		if(pet == currentPet) return

		SkyBlockEvents.PET_CHANGE.invoke(SkyBlockEvents.PetChange(currentPet, pet))
		currentPet = pet
	}

	fun xpFromLevel(level: Int, rarity: Rarity, maxLevel: Int = 100): Double {
		val constants = this.constants ?: return 0.0
		val offset = constants.petRarityOffset[rarity] ?: 0
		val levels = constants.petLevels.slice(offset until offset + maxLevel - 1)
		return runCatching { levels.slice(0 until level - 1).sum().toDouble() }.getOrDefault(0.0)
	}

	fun levelFromXp(xp: Double, rarity: Rarity, maxLevel: Int = 100): Int {
		val constants = this.constants ?: return 0
		val offset = constants.petRarityOffset[rarity] ?: 0
		val levels = constants.petLevels.slice(offset until offset + maxLevel - 1)

		var remainingXp = xp
		var level = 1

		val iter = levels.iterator()
		while(iter.hasNext()) {
			val levelXp = iter.next()
			if(levelXp <= remainingXp) {
				level += 1
				remainingXp -= levelXp
			} else {
				break
			}
		}

		return level
	}

	fun getPetData(itemStack: ItemStack): PetData? {
		val item = itemStack.getSkyBlockItem() ?: return null
		if(item.id != "PET") return null

		val petInfo: PetInfo = NobaAddons.GSON.fromJson(item.petInfo, PetInfo::class.java)
		val name = petNamePattern.getGroupFromFullMatch(itemStack.name.string, "name") ?: itemStack.name.string
		val rarity = Rarity.getRarity(petInfo.tier)

		return PetData(
			name,
			petInfo.type,
			petInfo.exp,
			rarity,
			petInfo.candyUsed,
			petInfo.active,
			petInfo.heldItem,
			petInfo.uuid
		)
	}

	@Serializable
	data class PetConstants(val petRarityOffset: Map<Rarity, Int>, val petLevels: List<Int>)
}