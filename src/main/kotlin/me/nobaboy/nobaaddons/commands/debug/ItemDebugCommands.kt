package me.nobaboy.nobaaddons.commands.debug

import com.mojang.brigadier.arguments.IntegerArgumentType
import me.nobaboy.nobaaddons.commands.debug.DebugCommands.dumpInfo
import me.nobaboy.nobaaddons.commands.internal.Command
import me.nobaboy.nobaaddons.commands.internal.Group
import me.nobaboy.nobaaddons.utils.MCUtils
import me.nobaboy.nobaaddons.utils.TextUtils.aqua
import me.nobaboy.nobaaddons.utils.TextUtils.buildText
import me.nobaboy.nobaaddons.utils.TextUtils.toText
import me.nobaboy.nobaaddons.utils.items.ItemUtils.asSkyBlockItem
import me.nobaboy.nobaaddons.utils.items.ItemUtils.isSkyBlockItem
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager
import net.minecraft.command.argument.NbtPathArgumentType
import net.minecraft.component.DataComponentTypes
import net.minecraft.component.type.LoreComponent
import net.minecraft.component.type.NbtComponent
import net.minecraft.nbt.NbtElement
import net.minecraft.nbt.NbtHelper
import net.minecraft.nbt.NbtList
import net.minecraft.nbt.NbtString
import net.minecraft.text.Text

@Suppress("unused")
object ItemDebugCommands : Group("item") {
	override val root = RootCommand {
		val item = MCUtils.player!!.mainHandStack
		if(item.isEmpty || !item.isSkyBlockItem) {
			it.source.sendError(Text.literal("You aren't holding a valid SkyBlock item"))
			return@RootCommand
		}

		val itemData = item.asSkyBlockItem!!
		it.dumpInfo(
			"Item ID" to itemData.id,
			"UUID" to itemData.uuid,
			"Created" to itemData.timestamp?.elapsedSince(),
			"Reforge" to itemData.reforge,
			"Rarity" to itemData.rarity,
			"Recombobulated" to itemData.recombobulated,
			"Stars" to itemData.stars,
			"Enchants" to itemData.enchantments.mapKeys { it.key.id },
			"Runes" to itemData.runes,
			"Gemstones" to itemData.gemstones,
			"Power scroll" to itemData.powerScroll,
			"Potion" to itemData.potion,
			"Potion level" to itemData.potionLevel,
			"Potion effects" to itemData.effects,
			"Donated to Museum" to itemData.donatedToMuseum,
		)
	}

	val nbt = Command(
		"nbt",
		builder = {
			it.executes(this::execute)
				.then(ClientCommandManager.argument("path", NbtPathArgumentType.nbtPath())
					.executes(this::execute))
		}
	) {
		val item = MCUtils.player!!.mainHandStack
		if(item.isEmpty) {
			it.source.sendError(Text.literal("You aren't holding an item"))
			return@Command
		}

		// .getNbtPath() expects a server context, so we have to do this the slightly longer way around
		val path = runCatching { it.getArgument("path", NbtPathArgumentType.NbtPath::class.java) }.getOrNull()
		var nbt: NbtElement = item.getOrDefault(DataComponentTypes.CUSTOM_DATA, NbtComponent.DEFAULT).copyNbt()
		if(path != null) {
			nbt = path.get(nbt).first()
		}

		it.source.sendFeedback(buildText {
			append("Item NBT: ".toText().aqua())
			append(NbtHelper.toPrettyPrintedText(nbt))
		})
	}

	val dumpLore = Command(
		"lore",
		builder = {
			it.executes(this::execute)
				.then(ClientCommandManager.argument("line", IntegerArgumentType.integer(0))
					.executes(this::execute))
		}
	) {
		val item = MCUtils.player!!.mainHandStack
		if(item.isEmpty || !item.contains(DataComponentTypes.LORE)) {
			it.source.sendError(Text.literal("You aren't holding an item with lore"))
			return@Command
		}

		val line = runCatching { IntegerArgumentType.getInteger(it, "line") }.getOrNull()
		val path = line?.let { NbtPathArgumentType.NbtPath.parse("[$it]") }

		var nbt: NbtElement = NbtList().apply {
			item.getOrDefault(DataComponentTypes.LORE, LoreComponent.DEFAULT).lines.forEach {
				add(NbtString.of(it.string))
			}
		}
		if(path != null) {
			nbt = path.get(nbt).first()
		}

		it.source.sendFeedback(buildText {
			append("Item lore: ".toText().aqua())
			append(NbtHelper.toPrettyPrintedText(nbt))
		})
	}
}