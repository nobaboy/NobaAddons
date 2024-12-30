package me.nobaboy.nobaaddons.utils

import me.nobaboy.nobaaddons.NobaAddons
import net.fabricmc.loader.api.FabricLoader
import net.fabricmc.loader.api.Version
import net.minecraft.client.MinecraftClient
import net.minecraft.client.font.TextRenderer
import net.minecraft.client.network.ClientPlayNetworkHandler
import net.minecraft.client.option.GameOptions
import net.minecraft.client.render.WorldRenderer
import net.minecraft.client.util.Clipboard
import net.minecraft.client.util.Window
import net.minecraft.client.world.ClientWorld
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.network.packet.Packet
import java.util.UUID

object MCUtils {
	private val clipboard = Clipboard()

	val VERSION_INFO: Version = FabricLoader.getInstance().getModContainer("minecraft").orElseThrow().metadata.version

	val client: MinecraftClient get() = MinecraftClient.getInstance()

	val world: ClientWorld? get() = client.world

	val player: PlayerEntity? get() = client.player
	val playerName: String? get() = player?.name?.string
	val playerUuid: UUID? get() = player?.uuid

	val networkHandler: ClientPlayNetworkHandler? get() = client.networkHandler

	val options: GameOptions get() = client.options

	val window: Window get() = client.window
	val Window.scaledSize: Pair<Int, Int> get() = scaledWidth to scaledHeight

	val textRenderer: TextRenderer get() = client.textRenderer
	val worldRenderer: WorldRenderer get() = client.worldRenderer

	fun sendPacket(packet: Packet<*>) {
		if(client.networkHandler == null) {
			NobaAddons.LOGGER.error("Tried to send a packet {} but connection was null", packet::class.java.simpleName)
			return
		}

		client.networkHandler!!.sendPacket(packet)
	}

	// This would fit better in a different class (like OSUtils), but I didn't want to make a new
	// class just for a single one-line method
	fun copyToClipboard(text: String) {
		clipboard.setClipboard(window.handle, text)
	}
}