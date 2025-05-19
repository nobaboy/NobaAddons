package me.nobaboy.nobaaddons.features.crimsonisle

import me.nobaboy.nobaaddons.api.skyblock.SkyBlockAPI.inIsland
import me.nobaboy.nobaaddons.config.NobaConfig
import me.nobaboy.nobaaddons.config.UISettings
import me.nobaboy.nobaaddons.core.SkyBlockIsland
import me.nobaboy.nobaaddons.events.impl.chat.ChatMessageEvents
import me.nobaboy.nobaaddons.events.impl.skyblock.SkyBlockEvents
import me.nobaboy.nobaaddons.repo.Repo.fromRepo
import me.nobaboy.nobaaddons.ui.TextHudElement
import me.nobaboy.nobaaddons.ui.UIManager
import me.nobaboy.nobaaddons.utils.NobaColor
import me.nobaboy.nobaaddons.utils.NobaVec
import me.nobaboy.nobaaddons.utils.RegexUtils.onFullMatch
import me.nobaboy.nobaaddons.utils.TextUtils.bold
import me.nobaboy.nobaaddons.utils.TextUtils.buildText
import me.nobaboy.nobaaddons.utils.TextUtils.green
import me.nobaboy.nobaaddons.utils.TextUtils.red
import me.nobaboy.nobaaddons.utils.TextUtils.toText
import me.nobaboy.nobaaddons.utils.Timestamp
import me.nobaboy.nobaaddons.utils.Timestamp.Companion.toShortString
import me.nobaboy.nobaaddons.utils.tr
import me.owdding.ktmodules.Module
import net.minecraft.client.gui.DrawContext
import net.minecraft.text.Text
import kotlin.time.Duration.Companion.minutes
import kotlin.time.Duration.Companion.seconds

// TODO use translations
// TODO fix areas, add timers in world space
// TODO fix display timer not showing correctly, it currently shows `Unknown!` instead of `Soon!` after the countdown
@Module
object MinibossTimers {
	private val config get() = NobaConfig.crimsonIsle.minibossTimers
	private val enabled: Boolean get() = config.enabled && SkyBlockIsland.CRIMSON_ISLE.inIsland()

	private val MINIBOSS_SPAWN_REGEX by Regex("BEWARE - (?:The )?(?<name>[A-Za-z ]+) [Ii]s [Ss]pawning\\.?").fromRepo("crimson_isle.miniboss_spawn")
	private val MINIBOSS_DOWN_REGEX by Regex("^[ ]+(?<name>[A-Z ]+) DOWN!").fromRepo("crimson_isle.miniboss_down")

	private val minibosses = mutableMapOf<Miniboss, MinibossState>()

	init {
		SkyBlockEvents.ISLAND_CHANGE.register { minibosses.clear() }
		ChatMessageEvents.CHAT.register(this::onChatMessage)
		UIManager.add(MinibossTimersHudElement)
	}

	private fun onChatMessage(event: ChatMessageEvents.Chat) {
		if(!enabled) return

		val message = event.cleaned

		MINIBOSS_SPAWN_REGEX.onFullMatch(message) {
			val name = groups["name"]?.value ?: return
			val miniboss = Miniboss.getByName(name) ?: return

			minibosses.getOrPut(miniboss) { MinibossState() }.apply {
				alive = true
				respawnTime = null
			}

			return
		}

		MINIBOSS_DOWN_REGEX.onFullMatch(message) {
			val name = groups["name"]?.value ?: return
			val miniboss = Miniboss.getByName(name) ?: return

			minibosses.getOrPut(miniboss) { MinibossState() }.apply {
				alive = false
				respawnTime = Timestamp.now() + 2.minutes
			}
		}
	}

//	private fun onWorldRender(context: WorldRenderContext) {
//		if(!enabled) return
//
//		val playerLocation = LocationUtils.playerLocation
//
//		minibosses.forEach { (miniboss, state) ->
//			if(playerLocation !in miniboss.area || state.alive) return@forEach
//
//			val (start, end) = miniboss.area
//			val location = NobaVec(
//				(start.x + end.x) / 2,
//				min(start.y, end.y) + 2,
//				(start.z + end.z) / 2
//			).center()
//
//			RenderUtils.renderText(context, location, state.status, yOffset = -5f, throughBlocks = true)
//		}
//	}

	private object MinibossTimersHudElement : TextHudElement(UISettings.minibossTimers) {
		override val name: Text = tr("nobaadons.ui.minibossTimers", "Miniboss Timers")
		override val size: Pair<Int, Int> = 150 to 50
		override val enabled: Boolean get() = MinibossTimers.enabled

		override fun renderText(context: DrawContext) {
			renderLines(context, Miniboss.entries.map { miniboss ->
				buildText {
					append(miniboss.formattedName)
					append(": ")
					append(minibosses[miniboss]?.status ?: "Unknown!".toText().red())
				}
			})
		}
	}

	private enum class Miniboss(
		val displayName: String,
		val color: NobaColor,
		val area: Pair<NobaVec, NobaVec>,
	) {
		ASHFANG("Ashfang", NobaColor.DARK_GRAY, NobaVec(-462, 155, -1035) to NobaVec(-507, 131, -955)),
		BARBARIAN_DUKE_X("Barbarian Duke X", NobaColor.DARK_GRAY, NobaVec(-550, 101, -890) to NobaVec(-522, 131, -918)),
		BLADESOUL("Bladesoul", NobaColor.DARK_GRAY, NobaVec(-322, 80, -491) to NobaVec(-268, 107, -545)),
		MAGE_OUTLAW("Mage Outlaw", NobaColor.DARK_PURPLE, NobaVec(-200, 98, -843) to NobaVec(-162, 116, -878)),
		MAGMA_BOSS("Magma Boss", NobaColor.DARK_RED, NobaVec(-318, 59, -751) to NobaVec(-442, 90, -851)),
		;

		val formattedName by lazy { displayName.toText().formatted(color.formatting).bold() }

		companion object {
			fun getByName(name: String): Miniboss? = entries.firstOrNull { it.displayName.equals(name, ignoreCase = true) }
		}
	}

	private data class MinibossState(
		var alive: Boolean = false,
		var respawnTime: Timestamp? = null,
	) {
		val status: Text
			get() = when {
				alive -> "Alive!".toText().green()
				respawnTime == null -> "Unknown!".toText().red()
				(respawnTime!! + 3.seconds).isPast() -> "Alive!".toText().green()
				respawnTime!!.isFuture() -> respawnTime!!.timeRemaining().toShortString().toText().red() // should say soon
				else -> "Unknown!".toText().red()
			}
	}
}