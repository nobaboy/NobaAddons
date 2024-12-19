package me.nobaboy.nobaaddons.api.skyblock

import me.nobaboy.nobaaddons.NobaAddons
import me.nobaboy.nobaaddons.core.ItemRarity
import me.nobaboy.nobaaddons.data.PersistentCache
import me.nobaboy.nobaaddons.data.PetData
import me.nobaboy.nobaaddons.data.json.PetInfo
import me.nobaboy.nobaaddons.events.InventoryEvents
import me.nobaboy.nobaaddons.events.skyblock.SkyBlockEvents
import me.nobaboy.nobaaddons.utils.RegexUtils.matchMatcher
import me.nobaboy.nobaaddons.utils.RegexUtils.matches
import me.nobaboy.nobaaddons.utils.StringUtils.cleanFormatting
import me.nobaboy.nobaaddons.utils.items.ItemUtils.getSkyBlockItem
import net.fabricmc.fabric.api.client.message.v1.ClientReceiveMessageEvents
import net.minecraft.item.ItemStack
import net.minecraft.screen.slot.SlotActionType
import org.lwjgl.glfw.GLFW
import java.util.regex.Pattern

object PetAPI {
	private val petsMenuPattern = Pattern.compile("^Pets(?: \\(\\d+/\\d+\\) )?")
	private val petNamePattern = Pattern.compile("^(?<favorite>⭐ )?\\[Lvl (?<level>\\d+)] (?:\\[\\d+✦] )?(?<name>[A-z- ]+)(?: ✦|\$)")

	// TODO cache autopet rule pets to allow for getting complete data
	private val autopetPattern = Pattern.compile(
		"^§cAutopet §eequipped your §7\\[Lvl (?<level>\\d+)] (?:§.\\[.*] )?§(?<rarity>.)(?<name>[A-z ]+)(?:§. ✦)?§e! §a§lVIEW RULE"
	)

	private val petUnequipPattern = Pattern.compile("^You despawned your (?<name>[A-z ]+)(?: ✦|\$)")

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
		petUnequipPattern.matchMatcher(message.cleanFormatting()) {
			changePet(null)
		}

		autopetPattern.matchMatcher(message) {
			val name = group("name")
			val id = name.uppercase().replace(" ", "_")
			val level = group("level").toInt()
			val rarity = ItemRarity.getByColorCode(group("rarity")[0])
			if(rarity == ItemRarity.UNKNOWN) NobaAddons.LOGGER.warn("Failed to get pet rarity from Autopet chat message: '$message'")

			val pet = PetData(name, id, level, 0.0, rarity, active = true)
			changePet(pet)
		}
	}

	private fun changePet(pet: PetData?) {
		if(pet == currentPet) return

		SkyBlockEvents.PET_CHANGE.invoke(SkyBlockEvents.PetChange(currentPet, pet))
		currentPet = pet
	}

	fun getPetData(itemStack: ItemStack): PetData? {
		petNamePattern.matchMatcher(itemStack.name.string) {
			val item = itemStack.getSkyBlockItem() ?: return null
			if(item.id != "PET") return null

			val petInfo: PetInfo = NobaAddons.GSON.fromJson(item.petInfo, PetInfo::class.java)

			val name = group("name")
			val level = group("level").toInt()
			val rarity = ItemRarity.getRarity(petInfo.tier)

			return PetData(
				name,
				petInfo.type,
				level,
				petInfo.exp,
				rarity,
				petInfo.candyUsed,
				petInfo.active,
				petInfo.heldItem,
				petInfo.uuid
			)
		}

		return null
	}
}