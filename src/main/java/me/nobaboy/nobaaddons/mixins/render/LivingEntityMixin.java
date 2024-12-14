package me.nobaboy.nobaaddons.mixins.render;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import me.nobaboy.nobaaddons.config.NobaConfigManager;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import java.util.Objects;

@Mixin(LivingEntity.class)
public class LivingEntityMixin {
	@ModifyReturnValue(method = "getHandSwingDuration", at = @At("RETURN"))
	public int nobaaddons$modifySwingDuration(int original) {
		var entity = (LivingEntity)(Object)this;
		var client = MinecraftClient.getInstance();
		var config = NobaConfigManager.getConfig().getUiAndVisuals().getSwingAnimation();

		if(entity instanceof AbstractClientPlayerEntity player) {
			if(!Objects.equals(player, client.player) && !config.getApplyToAllPlayers()) {
				return original;
			}

			var ticks = config.getSwingDuration();
			if(ticks > 1) {
				return ticks;
			}
		}

		return original;
	}
}
