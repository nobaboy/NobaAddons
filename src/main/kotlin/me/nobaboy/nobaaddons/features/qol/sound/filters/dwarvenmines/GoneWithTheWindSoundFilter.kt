package me.nobaboy.nobaaddons.features.qol.sound.filters.dwarvenmines

import me.nobaboy.nobaaddons.api.skyblock.SkyBlockAPI.inIsland
import me.nobaboy.nobaaddons.core.SkyBlockIsland
import me.nobaboy.nobaaddons.events.impl.client.SoundEvents
import me.nobaboy.nobaaddons.features.qol.sound.filters.ISoundFilter
import net.minecraft.util.Identifier

object GoneWithTheWindSoundFilter : ISoundFilter {
	private val WIND_CHANGE_DING = Identifier.ofVanilla("block.note_block.pling")
	private val WIND_CHANGE_ELYTRA = Identifier.ofVanilla("item.elytra.flying")

	private val isRelevantIsland: Boolean
		get() = SkyBlockIsland.DWARVEN_MINES.inIsland() ||
			SkyBlockIsland.CRYSTAL_HOLLOWS.inIsland() ||
			SkyBlockIsland.MINESHAFT.inIsland()

	override val enabled: Boolean
		get() = config.muteGoneWithTheWind && isRelevantIsland

	override fun onSound(sound: SoundEvents.AllowSound) {
		val shouldFilter = when(sound.id) {
			WIND_CHANGE_DING -> sound.pitch == 4.047619f && (sound.volume == 8.0f || sound.volume == 0.8f)
			WIND_CHANGE_ELYTRA -> sound.pitch == 2.0f && sound.volume == 0.2f
			else -> false
		}

		if(shouldFilter) sound.cancel()
	}
}