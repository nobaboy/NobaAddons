package me.nobaboy.nobaaddons.mixins.events;

import me.nobaboy.nobaaddons.events.impl.client.WorldEvents;
import net.minecraft.block.BlockState;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockView;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientWorld.class)
abstract class WorldEventsMixin_ClientWorld implements BlockView {
	@Inject(
		method = "handleBlockUpdate",
		at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/world/World;setBlockState(Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/BlockState;II)Z"
		)
	)
	public void nobaaddons$onBlockUpdate(BlockPos pos, BlockState state, int flags, CallbackInfo ci) {
		WorldEvents.BLOCK_UPDATE.dispatch(new WorldEvents.BlockUpdate(pos, state, getBlockState(pos)));
	}

	@Inject(method = "setTime", at = @At("HEAD"))
	public void nobaaddons$onSetTime(long time, long timeOfDay, boolean shouldTickTimeOfDay, CallbackInfo ci) {
		WorldEvents.TIME_UPDATE.dispatch(new WorldEvents.TimeUpdate(time));
	}
}
