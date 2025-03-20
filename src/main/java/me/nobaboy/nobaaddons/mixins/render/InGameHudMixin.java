package me.nobaboy.nobaaddons.mixins.render;

//? if <1.21.2 {
/*import org.spongepowered.asm.mixin.injection.Slice;*/
//?}

import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import me.nobaboy.nobaaddons.api.skyblock.SkyBlockAPI;
import me.nobaboy.nobaaddons.config.NobaConfig;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

import java.util.function.Function;

@Mixin(InGameHud.class)
abstract class InGameHudMixin {
	//? if >=1.21.2 {
	@WrapWithCondition(method = "renderAirBubbles", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/DrawContext;drawGuiTexture(Ljava/util/function/Function;Lnet/minecraft/util/Identifier;IIII)V"))
	public boolean nobaaddons$hideAirBubbles(DrawContext instance, Function<Identifier, RenderLayer> renderLayers, Identifier sprite, int x, int y, int width, int height) {
	//?} else {
	/*@WrapWithCondition(
		method = "renderStatusBars",
		at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/DrawContext;drawGuiTexture(Lnet/minecraft/util/Identifier;IIII)V"),
		slice = @Slice(from = @At(value = "INVOKE", target = "Lnet/minecraft/entity/player/PlayerEntity;isSubmergedIn(Lnet/minecraft/registry/tag/TagKey;)Z")),
		require = 2,
		allow = 2
	)
	public boolean nobaaddons$hideAirBubbles(DrawContext instance, Identifier texture, int x, int y, int width, int height) {
	*///?}
		if(SkyBlockAPI.inSkyBlock()) {
			return !NobaConfig.INSTANCE.getUiAndVisuals().getRenderingTweaks().getHideAirBubbles();
		}
		return true;
	}

	@ModifyVariable(method = "renderHealthBar", at = @At("HEAD"), argsOnly = true, ordinal = 6)
	public int nobaaddons$hideAbsorptionHearts(int absorption) {
		if(SkyBlockAPI.inSkyBlock() && NobaConfig.INSTANCE.getUiAndVisuals().getRenderingTweaks().getHideAbsorptionHearts()) {
			return 0;
		}
		return absorption;
	}
}
