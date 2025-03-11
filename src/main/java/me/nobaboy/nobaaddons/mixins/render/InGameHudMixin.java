package me.nobaboy.nobaaddons.mixins.render;

import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import me.nobaboy.nobaaddons.config.NobaConfig;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.InGameHud;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(InGameHud.class)
abstract class InGameHudMixin {
	@WrapWithCondition(method = "renderHealthBar", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/hud/InGameHud;drawHeart(Lnet/minecraft/client/gui/DrawContext;Lnet/minecraft/client/gui/hud/InGameHud$HeartType;IIZZZ)V"))
	public boolean nobaaddons$hideAbsorptionHearts(InGameHud instance, DrawContext context, InGameHud.HeartType type, int x, int y, boolean hardcore, boolean blinking, boolean half) {
		if(NobaConfig.INSTANCE.getUiAndVisuals().getRenderingTweaks().getHideAbsorptionHearts()) {
			return type != InGameHud.HeartType.ABSORBING;
		}
		return true;
	}
}
