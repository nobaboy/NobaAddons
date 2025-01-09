package me.nobaboy.nobaaddons.mixins.render;

import com.mojang.blaze3d.platform.GlConst;
import com.mojang.blaze3d.systems.RenderSystem;
import me.nobaboy.nobaaddons.ducks.OverlayTextureDuck;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.texture.NativeImageBackedTexture;
import net.minecraft.util.math.ColorHelper;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

import java.awt.*;

/**
 * @see me.nobaboy.nobaaddons.utils.render.EntityOverlay
 */
@SuppressWarnings("unused")
@Mixin(OverlayTexture.class)
abstract class OverlayTextureMixin implements OverlayTextureDuck {
	private @Shadow @Final NativeImageBackedTexture texture;
	private @Unique int previous = -1291911168;

	@Unique
	private void setColor(int color) {
		var image = texture.getImage();

		for(int i = 0; i < 16; i++) {
			for(int j = 0; j < 16; j++) {
				if(i < 8) {
					assert image != null;
					image./*? if >=1.21.2 {*/setColorArgb/*?} else {*//*setColor*//*?}*/(j, i, color);
				}
			}
		}

		RenderSystem.activeTexture(GlConst.GL_TEXTURE1);
		this.texture.bindTexture();
		this.texture.setFilter(false, false);
		//? if >=1.21.4 {
		this.texture.setClamp(true);
		image.upload(0, 0, 0, 0, 0, image.getWidth(), image.getHeight(), false);
		//?} else {
		/*image.upload(0, 0, 0, 0, 0, image.getWidth(), image.getHeight(), false, true, false, false);
		*///?}
		RenderSystem.activeTexture(GlConst.GL_TEXTURE0);
	}

	@Unique
	private static int getColorInt(int red, int green, int blue, int alpha) {
		alpha = 255 - alpha;
		return ColorHelper./*? if >=1.21.2 {*/getArgb/*?} else {*//*Argb.getArgb*//*?}*/(alpha, red, green, blue);
	}

	public void nobaaddons$setColor(@Nullable Color color) {
		if(color != null) {
			var image = texture.getImage();
			assert image != null;
			previous = image./*? if >=1.21.2 {*/getColorArgb/*?} else {*//*getColor*//*?}*/(0, 0);
		}

		setColor(color != null ? getColorInt(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()) : previous);
	}
}
