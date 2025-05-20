package me.nobaboy.nobaaddons.commands.debug

import dev.celestialfault.commander.annotations.Command
import dev.celestialfault.commander.annotations.Group
import dev.celestialfault.commander.annotations.RootCommand
import me.nobaboy.nobaaddons.commands.debug.DebugCommands.dumpInfo
import me.nobaboy.nobaaddons.commands.impl.Context
import me.nobaboy.nobaaddons.utils.mc.MCUtils
import me.nobaboy.nobaaddons.utils.mc.TextUtils.buildText
import me.nobaboy.nobaaddons.utils.mc.TextUtils.darkAqua
import me.nobaboy.nobaaddons.utils.mc.TextUtils.toText
import me.nobaboy.nobaaddons.utils.TimeUtils.elapsedSince
import me.nobaboy.nobaaddons.utils.items.ItemUtils.asSkyBlockItem
import me.nobaboy.nobaaddons.utils.items.ItemUtils.isSkyBlockItem
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
@Group("item")
object ItemDebugCommands {
	@RootCommand
	fun root(ctx: Context) {
		val item = MCUtils.player!!.mainHandStack
		if(item.isEmpty || !item.isSkyBlockItem) {
			ctx.source.sendError(Text.literal("You aren't holding a valid SkyBlock item"))
			return
		}

		val itemData = item.asSkyBlockItem!!
		ctx.dumpInfo(
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

	@Command
	fun nbt(ctx: Context, path: NbtPathArgumentType.NbtPath? = null) {
		val item = MCUtils.player!!.mainHandStack
		if(item.isEmpty) {
			ctx.source.sendError(Text.literal("You aren't holding an item"))
			return
		}

		var nbt: NbtElement = item.getOrDefault(DataComponentTypes.CUSTOM_DATA, NbtComponent.DEFAULT).copyNbt()
		if(path != null) {
			nbt = path.get(nbt).first()
		}

		ctx.source.sendFeedback(buildText {
			append("Item NBT: ".toText().darkAqua())
			append(NbtHelper.toPrettyPrintedText(nbt))
		})
	}

	@Command
	fun lore(ctx: Context, line: Int? = null) {
		val item = MCUtils.player!!.mainHandStack
		if(item.isEmpty || !item.contains(DataComponentTypes.LORE)) {
			ctx.source.sendError(Text.literal("You aren't holding an item with lore"))
			return
		}

		var nbt: NbtElement = NbtList().apply {
			item.getOrDefault(DataComponentTypes.LORE, LoreComponent.DEFAULT).lines.forEach {
				add(NbtString.of(it.string))
			}
		}
		if(line != null) {
			val path = NbtPathArgumentType.NbtPath.parse("[$line]")
			nbt = path.get(nbt).first()
		}

		ctx.source.sendFeedback(buildText {
			append("Item lore: ".toText().darkAqua())
			append(NbtHelper.toPrettyPrintedText(nbt))
		})
	}
}