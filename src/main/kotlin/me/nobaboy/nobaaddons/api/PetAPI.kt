package me.nobaboy.nobaaddons.api

import com.google.gson.Gson
import me.nobaboy.nobaaddons.NobaAddons
import me.nobaboy.nobaaddons.core.ItemRarity
import me.nobaboy.nobaaddons.data.PetData
import me.nobaboy.nobaaddons.data.json.PetInfo
import me.nobaboy.nobaaddons.events.InventoryEvents
import me.nobaboy.nobaaddons.events.skyblock.PetChangeEvent
import me.nobaboy.nobaaddons.utils.RegexUtils.matchMatcher
import me.nobaboy.nobaaddons.utils.RegexUtils.matches
import me.nobaboy.nobaaddons.utils.StringUtils.cleanFormatting
import me.nobaboy.nobaaddons.utils.items.ItemUtils.getSkyBlockItem
import me.nobaboy.nobaaddons.utils.items.ItemUtils.lore
import me.nobaboy.nobaaddons.utils.items.ItemUtils.stringLines
import net.fabricmc.fabric.api.client.message.v1.ClientReceiveMessageEvents
import net.minecraft.item.ItemStack
import net.minecraft.screen.slot.SlotActionType
import org.lwjgl.glfw.GLFW
import java.util.regex.Pattern

object PetAPI {
	private val petsMenuPattern = Pattern.compile("^Pets(?: \\(\\d+/\\d+\\) )?")
	private val petNamePattern = Pattern.compile("^(?<favorite>⭐ )?\\[Lvl (?<level>\\d+)] (?:\\[\\d+✦] )?(?<name>[A-z- ]+)(?: ✦|\$)")

	private val petUnequipPattern = Pattern.compile("^You despawned your (?<name>[A-z ]+)(?: ✦|\$)")
//	private val petItemChangePattern = Pattern.compile("^Your pet is now holding (?<item>[A-z0-9- ]+)\\.")
	private val autopetPattern = Pattern.compile(
		"^§cAutopet §eequipped your §7\\[Lvl (?<level>\\d+)] (?:§.\\[.*] )?§(?<rarity>.)(?<name>[A-z ]+)(?:§. ✦)?§e! §a§lVIEW RULE"
	)

	private var inPetsMenu = false

	var currentPet: PetData? = null
		private set

	fun init() {
		InventoryEvents.OPEN.register(this::onInventoryOpen)
		InventoryEvents.SLOT_CLICK.register(this::onInventoryClickSlot)
		ClientReceiveMessageEvents.GAME.register { message, _ -> onChatMessage(message.string) }
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

	private fun onInventoryClickSlot(event: InventoryEvents.SlotClick) {
		if(!inPetsMenu) return
		if(event.button != GLFW.GLFW_MOUSE_BUTTON_1) return
		if(event.actionType != SlotActionType.PICKUP) return

		val itemStack = event.itemStack
		if(itemStack.lore.stringLines.any { it == "Click to despawn!" }) {
			changePet(null)
			return
		}

		val pet = getPetData(itemStack)
		changePet(pet)
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
			if(rarity == ItemRarity.UNKNOWN) NobaAddons.LOGGER.warn("Failed to get pet rarity from Autopet chat message")

			val pet = PetData(name, id, level, 0.0, rarity, active = true)
			changePet(pet)
		}
	}

	fun getPetData(itemStack: ItemStack): PetData? {
		petNamePattern.matchMatcher(itemStack.name.string) {
			val item = itemStack.getSkyBlockItem() ?: return null
			if(item.id != "PET") return null

			val petInfo: PetInfo = Gson().fromJson(item.petInfo, PetInfo::class.java)

			val name = group("name")
			val level = group("level").toInt()
			val rarity = ItemRarity.rarities[petInfo.tier] ?: ItemRarity.UNKNOWN

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

	private fun changePet(pet: PetData?) {
		if(pet == currentPet) return

		PetChangeEvent.EVENT.invoke(PetChangeEvent(currentPet, pet))
		currentPet = pet
	}
}