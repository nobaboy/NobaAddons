package me.nobaboy.nobaaddons.api.skyblock

import kotlinx.serialization.Serializable
import me.nobaboy.nobaaddons.NobaAddons
import me.nobaboy.nobaaddons.core.Rarity
import me.nobaboy.nobaaddons.core.profile.ProfileData
import me.nobaboy.nobaaddons.data.PetData
import me.nobaboy.nobaaddons.data.json.PetNbt
import me.nobaboy.nobaaddons.events.impl.chat.ChatMessageEvents
import me.nobaboy.nobaaddons.events.impl.client.InventoryEvents
import me.nobaboy.nobaaddons.events.impl.skyblock.SkyBlockEvents
import me.nobaboy.nobaaddons.repo.Repo
import me.nobaboy.nobaaddons.repo.Repo.fromRepo
import me.nobaboy.nobaaddons.utils.ErrorManager
import me.nobaboy.nobaaddons.utils.RegexUtils.getGroupFromFullMatch
import me.nobaboy.nobaaddons.utils.RegexUtils.onFullMatch
import me.nobaboy.nobaaddons.utils.StringUtils.cleanFormatting
import me.nobaboy.nobaaddons.utils.annotations.ApiModule
import me.nobaboy.nobaaddons.utils.items.ItemUtils.asSkyBlockItem
import net.minecraft.item.ItemStack
import net.minecraft.screen.slot.SlotActionType
import org.lwjgl.glfw.GLFW

@ApiModule
object PetAPI {
	val constants by Repo.create("pets/constants.json", PetConstants.serializer())

	private val PETS_MENU_REGEX by Regex("^Pets(?: \\(\\d+/\\d+\\) )?").fromRepo("pets.menu_title")
	private val PET_NAME_REGEX by Regex("^(?<favorite>⭐ )?\\[Lvl (?<level>\\d+)] (?:\\[\\d+✦] )?(?<name>[A-z- ]+)(?: ✦|\$)").fromRepo("pets.name")

	// TODO cache autopet rule pets to allow for getting complete data
	private val AUTOPET_REGEX by Regex(
		"^§cAutopet §eequipped your §7\\[Lvl (?<level>\\d+)] (?:§.\\[.*] )?§(?<rarity>.)(?<name>[A-z ]+)(?:§. ✦)?§e! §a§lVIEW RULE"
	).fromRepo("pets.autopet")

	private val PET_DESPAWN_REGEX by Regex("^You despawned your (?<name>[A-z ]+)(?: ✦)?!").fromRepo("pets.despawn")

	private var inPetsMenu = false

	var currentPet: PetData? = null
		get() = if(SkyBlockAPI.inSkyBlock) field else null
		private set

	init {
		InventoryEvents.OPEN.register(this::onInventoryOpen)
		InventoryEvents.SLOT_CLICK.register(this::onInventorySlotClick)
		ChatMessageEvents.CHAT.register(this::onChatMessage)
		SkyBlockEvents.PROFILE_DATA_LOADED.register { (_, data) -> currentPet = data.pet }
	}

	private fun onInventoryOpen(event: InventoryEvents.Open) {
		if(!SkyBlockAPI.inSkyBlock) return

		inPetsMenu = PETS_MENU_REGEX.matches(event.inventory.title)
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

	private fun onChatMessage(event: ChatMessageEvents.Chat) {
		if(!SkyBlockAPI.inSkyBlock) return

		val message = event.message.string

		PET_DESPAWN_REGEX.onFullMatch(message.cleanFormatting()) {
			changePet(null)
		}

		AUTOPET_REGEX.onFullMatch(message) {
			val name = groups["name"]?.value ?: return
			val id = name.uppercase().replace(" ", "_")
			val level = groups["level"]?.value?.toInt() ?: return
			val rarity = Rarity.getByColorCode(groups["rarity"]?.value?.first() ?: return)
			if(rarity == Rarity.UNKNOWN) {
				ErrorManager.logError(
					"Failed to get pet rarity from Autopet chat message",
					Error(),
					"Full message" to message
				)
			}
			val xpRarity = if(id == "BINGO") Rarity.COMMON else rarity

			val pet = PetData(
				name,
				id,
				xpFromLevel(level, xpRarity, if(id == "GOLDEN_DRAGON") 200 else 100),
				rarity,
				active = true
			)
			changePet(pet)
		}
	}

	private fun changePet(pet: PetData?) {
		if(pet == currentPet) return

		SkyBlockEvents.PET_CHANGE.dispatch(SkyBlockEvents.PetChange(currentPet, pet))
		ProfileData.PROFILE.pet = pet
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
		val item = itemStack.asSkyBlockItem ?: return null
		if(item.id != "PET" || item.petInfo == null) return null

		val petNbt = NobaAddons.JSON.decodeFromString<PetNbt>(item.petInfo ?: return null)
		val name = PET_NAME_REGEX.getGroupFromFullMatch(itemStack.name.string, "name") ?: itemStack.name.string
		val rarity = Rarity.getRarity(petNbt.tier)

		return PetData(
			name,
			petNbt.type,
			petNbt.exp,
			rarity,
			petNbt.candyUsed,
			petNbt.active,
			petNbt.heldItem,
			item.uuid
		)
	}

	@Serializable
	data class PetConstants(val petRarityOffset: Map<Rarity, Int>, val petLevels: List<Int>)
}