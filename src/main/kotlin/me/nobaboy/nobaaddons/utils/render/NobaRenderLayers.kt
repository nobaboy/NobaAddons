package me.nobaboy.nobaaddons.utils.render

//? if >=1.21.5 {
/*import net.minecraft.client.gl.RenderPipelines
*///?} else {
import net.minecraft.client.render.VertexFormat.DrawMode
import net.minecraft.client.render.VertexFormats
//?}

import net.minecraft.client.render.RenderLayer
import net.minecraft.client.render.RenderLayer.MultiPhase
import net.minecraft.client.render.RenderLayer.MultiPhaseParameters
import net.minecraft.client.render.RenderPhase
import net.minecraft.util.Util
import java.util.OptionalDouble

// FIXME this could be done better
object NobaRenderLayers {
	val FILLED: MultiPhase = RenderLayer.of(
		"nobaaddons:filled",
		//? if <1.21.5 {
		VertexFormats.POSITION_COLOR,
		DrawMode.TRIANGLE_STRIP,
		//?}
		RenderLayer.CUTOUT_BUFFER_SIZE,
		false,
		true,
		//? if >=1.21.5 {
		/*RenderPipelines.DEBUG_FILLED_BOX,
		MultiPhaseParameters.builder()
			.layering(RenderPhase.VIEW_OFFSET_Z_LAYERING)
			.build(false)
		*///?} else {
		MultiPhaseParameters.builder()
			.program(RenderPhase.POSITION_COLOR_PROGRAM)
			.transparency(RenderLayer.TRANSLUCENT_TRANSPARENCY)
			.depthTest(RenderLayer.LEQUAL_DEPTH_TEST)
			.cull(RenderLayer.DISABLE_CULLING)
			.layering(RenderPhase.VIEW_OFFSET_Z_LAYERING)
			.build(false)
		//?}
	)

	val FILLED_THROUGH_BLOCKS: MultiPhase = RenderLayer.of(
		"nobaaddons:filled_through_blocks",
		//? if <1.21.5 {
		VertexFormats.POSITION_COLOR,
		DrawMode.TRIANGLE_STRIP,
		//?}
		RenderLayer.CUTOUT_BUFFER_SIZE,
		false,
		true,
		//? if >=1.21.5 {
		/*NobaRenderPipelines.FILLED_THROUGH_BLOCKS,
		MultiPhaseParameters.builder()
			.layering(RenderPhase.VIEW_OFFSET_Z_LAYERING)
			.build(false)
		*///?} else {
		MultiPhaseParameters.builder()
			.program(RenderPhase.POSITION_COLOR_PROGRAM)
			.transparency(RenderLayer.TRANSLUCENT_TRANSPARENCY)
			.depthTest(RenderLayer.ALWAYS_DEPTH_TEST)
			.cull(RenderLayer.DISABLE_CULLING)
			.layering(RenderPhase.VIEW_OFFSET_Z_LAYERING)
			.build(false)
		//?}
	)

	// wtf am I doing here, I don't get it and this might be a bad way to do it, but hey, it works.
	private val LINES = Util.memoize { lineWidth: Double ->
		RenderLayer.of(
			"nobaaddons:lines",
			//? if <1.21.5 {
			VertexFormats.LINES,
			DrawMode.LINES,
			//?}
			RenderLayer.DEFAULT_BUFFER_SIZE,
			false,
			false,
			//? if >=1.21.5 {
			/*RenderPipelines.LINES,
			*///?}
			MultiPhaseParameters.builder()
				//? if <1.21.5 {
				.program(RenderPhase.LINES_PROGRAM)
				.transparency(RenderPhase.TRANSLUCENT_TRANSPARENCY)
				.depthTest(RenderPhase.LEQUAL_DEPTH_TEST)
				.cull(RenderPhase.DISABLE_CULLING)
				.writeMaskState(RenderPhase.ALL_MASK)
				//?}
				.layering(RenderPhase.VIEW_OFFSET_Z_LAYERING)
				.lineWidth(RenderPhase.LineWidth(OptionalDouble.of(lineWidth)))
				.build(false)
		)
	}

	private val LINES_THROUGH_WALLS = Util.memoize { lineWidth: Double ->
		RenderLayer.of(
			"nobaaddons:lines_through_walls",
			//? if <1.21.5 {
			VertexFormats.LINES,
			DrawMode.LINES,
			//?}
			RenderLayer.DEFAULT_BUFFER_SIZE,
			false,
			false,
			//? if >=1.21.5 {
			/*NobaRenderPipelines.LINES_THROUGH_BLOCKS,
			*///?}
			MultiPhaseParameters.builder()
				//? if <1.21.5 {
				.program(RenderPhase.LINES_PROGRAM)
				.transparency(RenderPhase.TRANSLUCENT_TRANSPARENCY)
				.depthTest(RenderPhase.ALWAYS_DEPTH_TEST)
				.cull(RenderPhase.DISABLE_CULLING)
				.writeMaskState(RenderPhase.ALL_MASK)
				//?}
				.layering(RenderPhase.VIEW_OFFSET_Z_LAYERING)
				.lineWidth(RenderPhase.LineWidth(OptionalDouble.of(lineWidth)))
				.build(false)
		)
	}

	fun getLines(lineWidth: Float): MultiPhase = LINES.apply(lineWidth.toDouble())
	fun getLinesThroughWalls(lineWidth: Float): MultiPhase = LINES_THROUGH_WALLS.apply(lineWidth.toDouble())
}