package me.nobaboy.nobaaddons.utils.render

//? if <1.21.5 {
import com.mojang.blaze3d.platform.GlConst
import com.mojang.blaze3d.systems.RenderSystem
//?}

import me.nobaboy.nobaaddons.mixins.accessors.OverlayTextureAccessor
import me.nobaboy.nobaaddons.utils.NobaColor
import net.minecraft.client.render.OverlayTexture
import net.minecraft.util.math.ColorHelper

private const val SIZE = 16

class TintOverlayTexture : OverlayTexture() {
	var lastColor: NobaColor? = null
		private set

	fun setColor(color: NobaColor) {
		if(color == lastColor) return
		lastColor = color

		val texture = getTexture(this)
		val image = texture.image
		checkNotNull(image)

		repeat(SIZE) { x ->
			repeat(SIZE) { y ->
				val color = if(x < 8) {
					0xB2FF0000.toInt()
				} else {
					val alpha = ((1.0f - y.toFloat() / 15.0f * 0.75f) * 255.0f).toInt()
					ColorHelper/*? if <1.21.2 {*//*.Argb*//*?}*/.withAlpha(alpha, color.rgb)
				}
				//? if >=1.21.2 {
				image.setColorArgb(x, y, color)
				//?} else {
				/*image.setColor(x, y, color)
				*///?}
			}
		}

		//? if <1.21.5 {
		RenderSystem.activeTexture(GlConst.GL_TEXTURE1)
		texture.bindTexture()
		//?}

		texture.setFilter(false, false)
		//? if >=1.21.4 {
		texture.setClamp(true)
		//?}

		//? if >=1.21.5 {
		/*texture.upload()
		*///?} else if 1.21.4 {
		image.upload(0, 0, 0, 0, 0, image.width, image.height, false)
		//?} else {
		/*image.upload(0, 0, 0, 0, 0, image.width, image.height, false, true, false, false)
		*///?}

		//? if <1.21.5 {
		RenderSystem.activeTexture(GlConst.GL_TEXTURE0)
		//?}
	}

	companion object {
		private fun getTexture(overlay: OverlayTexture) = (overlay as OverlayTextureAccessor).texture
	}
}