package me.nobaboy.nobaaddons.events.skyblock.mythological

import me.nobaboy.nobaaddons.features.events.mythological.BurrowType
import me.nobaboy.nobaaddons.utils.NobaVec
import net.fabricmc.fabric.api.event.EventFactory

object MythologicalEvents {
	val GUESS = EventFactory.createArrayBacked(BurrowGuessEvent::class.java) { listeners ->
		BurrowGuessEvent { location ->
			listeners.forEach { it.onBurrowGuess(location) }
		}
	}

	val BURROW_FIND = EventFactory.createArrayBacked(BurrowFindEvent::class.java) { listeners ->
		BurrowFindEvent { location, type ->
			listeners.forEach { it.onBurrowFind(location, type) }
		}
	}

	val BURROW_DIG = EventFactory.createArrayBacked(BurrowDigEvent::class.java) { listeners ->
		BurrowDigEvent { location ->
			listeners.forEach { it.onBurrowDig(location) }
		}
	}

	val INQUISITOR = EventFactory.createArrayBacked(InquisitorSpawnEvent::class.java) { listeners ->
		InquisitorSpawnEvent { location ->
			listeners.forEach { it.onInquisitorSpawn(location) }
		}
	}

	fun interface BurrowGuessEvent {
		fun onBurrowGuess(location: NobaVec)
	}

	fun interface BurrowFindEvent {
		fun onBurrowFind(location: NobaVec, type: BurrowType)
	}

	fun interface BurrowDigEvent {
		fun onBurrowDig(location: NobaVec)
	}

	fun interface InquisitorSpawnEvent {
		fun onInquisitorSpawn(location: NobaVec)
	}
}