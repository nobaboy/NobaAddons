package me.nobaboy.nobaaddons.mixins.events;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import me.nobaboy.nobaaddons.events.PacketEvents;
import net.minecraft.network.ClientConnection;
import net.minecraft.network.NetworkSide;
import net.minecraft.network.listener.PacketListener;
import net.minecraft.network.packet.Packet;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientConnection.class)
abstract class PacketEventsMixin {
	@Shadow private Channel channel;
	@Shadow @Final private NetworkSide side;

	@Inject(method = "send(Lnet/minecraft/network/packet/Packet;)V", at = @At("RETURN"))
	private void nobaaddons$onPacketSend(Packet<?> packet, CallbackInfo ci) {
		if(this.side != NetworkSide.CLIENTBOUND) return;

		PacketEvents.SEND.invoke(new PacketEvents.Send(packet));
	}

	@Inject(
		method = "handlePacket",
		at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/network/packet/Packet;apply(Lnet/minecraft/network/listener/PacketListener;)V"
		)
	)
	private static void nobaaddons$prePacketReceive(Packet<?> packet, PacketListener listener, CallbackInfo ci) {
		PacketEvents.PRE_RECEIVE.invoke(new PacketEvents.Receive(packet));
	}

	@Inject(
		method = "channelRead0(Lio/netty/channel/ChannelHandlerContext;Lnet/minecraft/network/packet/Packet;)V",
		at = @At("RETURN")
	)
	private void nobaaddons$postPacketReceive(ChannelHandlerContext context, Packet<?> packet, CallbackInfo ci) {
		if(!this.channel.isOpen()) return;
		if(this.side != NetworkSide.CLIENTBOUND) return;

		PacketEvents.POST_RECEIVE.invoke(new PacketEvents.Receive(packet));
	}
}