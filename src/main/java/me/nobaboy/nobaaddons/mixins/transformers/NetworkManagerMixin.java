package me.nobaboy.nobaaddons.mixins.transformers;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import me.nobaboy.nobaaddons.events.PacketEvents;
import net.minecraft.network.ClientConnection;
import net.minecraft.network.NetworkSide;
import net.minecraft.network.packet.Packet;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientConnection.class)
public class NetworkManagerMixin {
	@Shadow
	private Channel channel;

	@Shadow
	@Final
	private NetworkSide side;

	@Inject(method = "send(Lnet/minecraft/network/packet/Packet;)V", at = @At("RETURN"))
	private void onPacketSend(Packet<?> packet, CallbackInfo ci) {
		if(this.side != NetworkSide.CLIENTBOUND) return;

		PacketEvents.INSTANCE.getSEND().invoker().onPacketSend(packet);
	}

	@Inject(
		method = "channelRead0(Lio/netty/channel/ChannelHandlerContext;Lnet/minecraft/network/packet/Packet;)V",
		at = @At("RETURN")
	)
	private void onPacketReceive(ChannelHandlerContext context, Packet<?> packet, CallbackInfo ci) {
		if(!this.channel.isOpen() || this.side != NetworkSide.CLIENTBOUND) return;

		PacketEvents.INSTANCE.getRECEIVE().invoker().onPacketReceive(packet);
	}
}
