package me.nobaboy.nobaaddons.mixins.events;

import com.llamalad7.mixinextras.sugar.Local;
import me.nobaboy.nobaaddons.events.impl.client.InventoryEvents;
import net.minecraft.client.network.ClientPlayerInteractionManager;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.SlotActionType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientPlayerInteractionManager.class)
abstract class SlotClickEventMixin {
	@Inject(
		method = "clickSlot",
		at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/client/network/ClientPlayNetworkHandler;sendPacket(Lnet/minecraft/network/packet/Packet;)V"
		)
	)
	public void nobaaddons$onSlotClick(
		int syncId,
		int slotId,
		int button,
		SlotActionType actionType,
		PlayerEntity player,
		CallbackInfo ci,
		@Local ScreenHandler screenHandler
	) {
		InventoryEvents.SLOT_CLICK.invoke(new InventoryEvents.SlotClick(screenHandler.getCursorStack(), button, slotId, actionType));
	}
}
