package me.nobaboy.nobaaddons.api

import com.google.gson.Gson
import me.nobaboy.nobaaddons.NobaAddons
import me.nobaboy.nobaaddons.core.ItemRarity
import me.nobaboy.nobaaddons.data.InventoryData
import me.nobaboy.nobaaddons.data.PetData
import me.nobaboy.nobaaddons.data.json.PetInfo
import me.nobaboy.nobaaddons.events.InventoryEvents
import me.nobaboy.nobaaddons.events.skyblock.PetChangeEvent
import me.nobaboy.nobaaddons.utils.RegexUtils.matchMatcher
import me.nobaboy.nobaaddons.utils.RegexUtils.matches
import me.nobaboy.nobaaddons.utils.StringUtils.cleanFormatting
import me.nobaboy.nobaaddons.utils.items.ItemUtils.getSkyBlockItem
import me.nobaboy.nobaaddons.utils.items.ItemUtils.lore
import net.fabricmc.fabric.api.client.message.v1.ClientReceiveMessageEvents
import net.minecraft.item.ItemStack
import net.minecraft.screen.slot.SlotActionType
import org.lwjgl.glfw.GLFW
import java.util.regex.Pattern

object PetAPI {
	private val petsMenuPattern = Pattern.compile("^Pets(?: \\(\\d+/\\d+\\) )?")
	private val petNamePattern = Pattern.compile("^(?<favorite>⭐ )?\\[Lvl (?<level>\\d+)] (?:\\[\\d+✦] )?(?<name>[A-z ]+)(?: ✦|\$)")

	private val petUnequipPattern = Pattern.compile("^You despawned your (?<name>[A-z ]+)(?: ✦|\$)")
//	private val petItemChangePattern = Pattern.compile("^Your pet is now holding (?<item>[A-z0-9- ]+)\\.")
	private val autopetPattern = Pattern.compile(
		"^§cAutopet §eequipped your §7\\[Lvl (?<level>\\d+)] (?:§.\\[.*] )?§(?<rarity>.)(?<name>[A-z ]+)(?:§. ✦)?§e! §a§lVIEW RULE"
	)

	var currentPet: PetData? = null
		private set

	fun init() {
		InventoryEvents.READY.register { handleInventoryReady(it) }
		InventoryEvents.SLOT_CLICK.register { stack, button, _, actionType -> handleInventorySlotClick(stack, button, actionType) }
		ClientReceiveMessageEvents.GAME.register { message, _ -> handleChatEvent(message.string) }
	}

	private fun handleInventoryReady(inventory: InventoryData) {
		if(!petsMenuPattern.matches(inventory.title)) return

		inventory.items.values.forEach { stack ->
			petNamePattern.matchMatcher(stack.name.string) {
				val item = stack.getSkyBlockItem() ?: return@forEach
				if(item.id != "PET") return@forEach

				val petInfo: PetInfo = Gson().fromJson(item.petInfo, PetInfo::class.java)
				if(!petInfo.active) return@forEach

				val name = group("name")
				val level = group("level").toInt()
				val rarity = ItemRarity.rarities[petInfo.tier] ?: ItemRarity.UNKNOWN

				val pet = PetData(name, petInfo.type, level, petInfo.exp, rarity, petInfo.heldItem, petInfo.uuid)
				changePet(pet)
			}
		}
	}

	private fun handleInventorySlotClick(stack: ItemStack, button: Int, actionType: SlotActionType) {
		if(button != GLFW.GLFW_MOUSE_BUTTON_1) return
		if(actionType != SlotActionType.PICKUP) return

		petNamePattern.matchMatcher(stack.name.string) {
			if(stack.lore.lines.map { it.string.cleanFormatting() }.reversed().any { it == "Click to despawn!" }) {
				changePet(null)
				return
			}

			val item = stack.getSkyBlockItem() ?: return
			if(item.id != "PET") return

			val petInfo: PetInfo = Gson().fromJson(item.petInfo, PetInfo::class.java)

			val name = group("name")
			val level = group("level").toInt()
			val rarity = ItemRarity.rarities[petInfo.tier] ?: ItemRarity.UNKNOWN

			val pet = PetData(name, petInfo.type, level, petInfo.exp, rarity, petInfo.heldItem, petInfo.uuid)
			changePet(pet)
		}
	}

	private fun handleChatEvent(message: String) {
		petUnequipPattern.matchMatcher(message.cleanFormatting()) {
			changePet(null)
		}

		autopetPattern.matchMatcher(message) {
			val name = group("name")
			val id = name.uppercase().replace(" ", "_")
			val level = group("level").toInt()
			val rarity = ItemRarity.getByColorCode(group("rarity")[0])
			if(rarity == ItemRarity.UNKNOWN) NobaAddons.LOGGER.warn("Failed to get pet rarity from Autopet chat message")

			val pet = PetData(name, id, level, 0.0, rarity)
			changePet(pet)
		}
	}

	private fun changePet(pet: PetData?) {
		if(pet?.uuid == currentPet?.uuid) return

		PetChangeEvent.EVENT.invoker().onPetChange(currentPet, pet) // old, new
		currentPet = pet
	}
}