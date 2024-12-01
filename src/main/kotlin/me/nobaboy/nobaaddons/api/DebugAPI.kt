package me.nobaboy.nobaaddons.api

import me.nobaboy.nobaaddons.events.SoundEvents
import me.nobaboy.nobaaddons.utils.ModAPIUtils.listen
import net.hypixel.modapi.HypixelModAPI
import net.hypixel.modapi.packet.impl.clientbound.event.ClientboundLocationPacket
import net.minecraft.sound.SoundCategory
import net.minecraft.util.Identifier
import java.awt.event.WindowAdapter
import java.awt.event.WindowEvent
import javax.swing.JFrame
import javax.swing.JTextArea

/**
 * Debug utilities API
 */
object DebugAPI {
	// the default debug window size allows for ~55 entries
	private const val HISTORY_LIMIT = 50
	private val recentSounds = mutableListOf<Sound>()
	private var debugWindow: SoundHistory? = null

	lateinit var lastLocationPacket: ClientboundLocationPacket
		private set

	fun init() {
		SoundEvents.SOUND.register {
			val sound = Sound(it.id, it.category, it.pitch, it.volume)
			recentSounds.add(sound)
			while(recentSounds.size > HISTORY_LIMIT) {
				recentSounds.removeFirst()
			}
			debugWindow?.refresh()
		}
		HypixelModAPI.getInstance().listen<ClientboundLocationPacket> { lastLocationPacket = it }
	}

	// this technically isn't fully correct, but it's a close enough approximation.
	val isAwtAvailable: Boolean get() = System.getProperty("java.awt.headless") == "false"

	fun openSoundDebugMenu() {
		debugWindow?.let { if(it.isVisible) return }
		debugWindow = SoundHistory()
	}

	data class Sound(val id: Identifier, val category: SoundCategory, val pitch: Float, val volume: Float) {
		override fun toString(): String = "$id ($category) : $pitch / $volume"
	}

	private class SoundHistory : JFrame("Sound Event Log") {
		private val text = JTextArea().also {
			it.isEditable = false
		}

		init {
			refresh()
			add(text)

			setSize(500, 800)
			isResizable = true
			addWindowListener(Adapter)
			defaultCloseOperation = DISPOSE_ON_CLOSE

			isVisible = true
		}

		fun refresh() {
			text.text = recentSounds.joinToString("\n")
		}

		private object Adapter : WindowAdapter() {
			override fun windowClosed(e: WindowEvent) {
				debugWindow = null
			}
		}
	}
}