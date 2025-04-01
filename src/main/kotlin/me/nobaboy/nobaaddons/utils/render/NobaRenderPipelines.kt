package me.nobaboy.nobaaddons.utils.render

//? if >=1.21.5 {
/*import com.mojang.blaze3d.pipeline.RenderPipeline
import com.mojang.blaze3d.platform.DepthTestFunction
import com.mojang.blaze3d.vertex.VertexFormat
import me.nobaboy.nobaaddons.NobaAddons
import net.minecraft.client.gl.RenderPipelines
import net.minecraft.client.render.VertexFormats
import net.minecraft.util.Identifier

object NobaRenderPipelines {
	val FILLED_THROUGH_BLOCKS: RenderPipeline = RenderPipelines.register(
		RenderPipeline.builder(RenderPipelines.POSITION_COLOR_SNIPPET)
			.withLocation(Identifier.of(NobaAddons.MOD_ID, "pipeline/filled_through_blocks"))
			.withDepthTestFunction(DepthTestFunction.NO_DEPTH_TEST)
			.withVertexFormat(VertexFormats.POSITION_COLOR, VertexFormat.DrawMode.TRIANGLE_STRIP)
			.build()
	)

	val LINES_THROUGH_BLOCKS: RenderPipeline = RenderPipelines.register(
		RenderPipeline.builder(RenderPipelines.RENDERTYPE_LINES_SNIPPET)
			.withLocation(Identifier.of(NobaAddons.MOD_ID, "pipeline/lines_through_blocks"))
			.withDepthTestFunction(DepthTestFunction.NO_DEPTH_TEST)
			.build()
	)
}
*///?}