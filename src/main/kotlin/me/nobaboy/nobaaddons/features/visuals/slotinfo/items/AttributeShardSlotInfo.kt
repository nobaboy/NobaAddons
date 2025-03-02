package me.nobaboy.nobaaddons.features.visuals.slotinfo.items

import me.nobaboy.nobaaddons.events.impl.render.ScreenRenderEvents
import me.nobaboy.nobaaddons.features.visuals.slotinfo.ISlotInfo
import me.nobaboy.nobaaddons.utils.TextUtils.toText
import me.nobaboy.nobaaddons.utils.items.ItemUtils.asSkyBlockItem

object AttributeShardSlotInfo : ISlotInfo {
	override val enabled: Boolean get() = config.attributeShardLevel || config.attributeShardName

	override fun handle(event: ScreenRenderEvents.DrawItem) {
		val item = event.itemStack.asSkyBlockItem ?: return
		if(item.id != "ATTRIBUTE_SHARD" || item.attributes.size != 1) return

		item.attributes.let {
			if(config.attributeShardLevel) drawCount(event, it.values.first().toString())
			if(config.attributeShardName) drawInfo(event, it.keys.first().abbreviation.toText())
		}
	}
}