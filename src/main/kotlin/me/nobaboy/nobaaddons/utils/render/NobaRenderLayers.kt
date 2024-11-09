package me.nobaboy.nobaaddons.utils.render

import com.mojang.blaze3d.systems.RenderSystem
import net.minecraft.client.render.RenderLayer
import net.minecraft.client.render.RenderLayer.MultiPhase
import net.minecraft.client.render.RenderLayer.MultiPhaseParameters
import net.minecraft.client.render.RenderPhase
import net.minecraft.client.render.RenderPhase.Cull
import net.minecraft.client.render.RenderPhase.DepthTest
import net.minecraft.client.render.RenderPhase.Transparency
import net.minecraft.client.render.VertexFormat.DrawMode
import net.minecraft.client.render.VertexFormats

object NobaRenderLayers {
	val DEFAULT_TRANSPARENCY: Transparency = Transparency(
		"default_transparency",
		{
			RenderSystem.enableBlend()
			RenderSystem.defaultBlendFunc()
		},
		{ RenderSystem::disableBlend }
	)

	val FILLED: MultiPhase = RenderLayer.of(
		"filled",
		VertexFormats.POSITION_COLOR,
		DrawMode.TRIANGLE_STRIP,
		RenderLayer.CUTOUT_BUFFER_SIZE,
		false,
		true,
		MultiPhaseParameters.builder()
			//? if >=1.21.2 {
			.program(RenderPhase.POSITION_COLOR_PROGRAM)
			//?} else {
			/*.program(RenderPhase.COLOR_PROGRAM*/
			//?}
			.cull(Cull.DISABLE_CULLING)
			.layering(RenderPhase.POLYGON_OFFSET_LAYERING)
			.transparency(DEFAULT_TRANSPARENCY)
			.depthTest(DepthTest.LEQUAL_DEPTH_TEST)
			.build(false)
	)

	val FILLED_THROUGH_BLOCKS: MultiPhase = RenderLayer.of(
		"filled_through_blocks",
		VertexFormats.POSITION_COLOR,
		DrawMode.TRIANGLE_STRIP,
		RenderLayer.CUTOUT_BUFFER_SIZE,
		false,
		true,
		MultiPhaseParameters.builder()
			//? if >=1.21.2 {
			.program(RenderPhase.POSITION_COLOR_PROGRAM)
			//?} else {
			/*.program(RenderPhase.COLOR_PROGRAM)*/
			//?}
			.cull(Cull.DISABLE_CULLING)
			.layering(RenderPhase.POLYGON_OFFSET_LAYERING)
			.transparency(DEFAULT_TRANSPARENCY)
			.depthTest(DepthTest.ALWAYS_DEPTH_TEST)
			.build(false)
	)
}