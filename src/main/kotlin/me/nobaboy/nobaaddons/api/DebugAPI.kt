package me.nobaboy.nobaaddons.api

import me.nobaboy.nobaaddons.events.impl.client.SoundEvents
import me.nobaboy.nobaaddons.utils.ModAPIUtils.listen
import net.hypixel.modapi.HypixelModAPI
import net.hypixel.modapi.packet.impl.clientbound.event.ClientboundLocationPacket
import net.minecraft.sound.SoundCategory
import net.minecraft.util.Identifier
import java.awt.event.WindowAdapter
import java.awt.event.WindowEvent
import java.util.function.Supplier
import javax.swing.JFrame
import javax.swing.JTextArea

/**
 * Debug utilities API
 */
object DebugAPI {
	private const val HISTORY_LIMIT = 50
	private val recentSounds = mutableListOf<SoundEvents.Sound>()
	private var debugWindow: SoundHistory? = null

	lateinit var lastLocationPacket: ClientboundLocationPacket
		private set

	fun init() {
		SoundEvents.Sound.register(::addSound)
		HypixelModAPI.getInstance().listen<ClientboundLocationPacket> { lastLocationPacket = it }
	}

	private fun addSound(event: SoundEvents.Sound) {
		recentSounds.add(event)
		while(recentSounds.size > HISTORY_LIMIT) {
			recentSounds.removeFirst()
		}
		debugWindow?.refresh()
	}

	// this technically isn't fully correct, but it's a close enough approximation.
	val isAwtAvailable: Boolean get() = System.getProperty("java.awt.headless") == "false"

	fun openSoundDebugMenu() {
		debugWindow?.let { if(it.isVisible) return }
		debugWindow = SoundHistory()
	}

	object RequiresAWT : Supplier<Boolean> {
		override fun get(): Boolean = isAwtAvailable
	}

	private class SoundHistory : JFrame("Sound Event Log") {
		private val text = JTextArea().also {
			it.isEditable = false
		}

		init {
			refresh()
			add(text)

			setSize(500, 850)
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