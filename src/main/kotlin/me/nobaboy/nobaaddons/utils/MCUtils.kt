package me.nobaboy.nobaaddons.utils

import me.nobaboy.nobaaddons.NobaAddons
import net.minecraft.client.MinecraftClient
import net.minecraft.client.font.TextRenderer
import net.minecraft.client.network.ClientPlayNetworkHandler
import net.minecraft.client.network.ClientPlayerEntity
import net.minecraft.client.option.GameOptions
import net.minecraft.client.render.WorldRenderer
import net.minecraft.client.util.Window
import net.minecraft.client.world.ClientWorld
import net.minecraft.network.packet.Packet

object MCUtils {
	val client: MinecraftClient get() = MinecraftClient.getInstance()

	val world: ClientWorld? get() = client.world

	val player: ClientPlayerEntity? get() = client.player
	val playerName: String? get() = player?.name?.string

	val networkHandler: ClientPlayNetworkHandler? get() = client.networkHandler

	val options: GameOptions get() = client.options

	val window: Window get() = client.window

	val textRenderer: TextRenderer get() = client.textRenderer
	val worldRenderer: WorldRenderer get() = client.worldRenderer

	fun sendPacket(packet: Packet<*>) {
		if(client.networkHandler == null) {
			NobaAddons.LOGGER.error("Tried to send a packet {} but connection was null", packet::class.java.simpleName)
			return
		}

		client.networkHandler!!.sendPacket(packet)
	}
}