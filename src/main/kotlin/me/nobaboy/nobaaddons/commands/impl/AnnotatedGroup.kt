package me.nobaboy.nobaaddons.commands.impl

import com.mojang.brigadier.builder.LiteralArgumentBuilder
import me.nobaboy.nobaaddons.commands.annotations.Group
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource
import kotlin.reflect.full.findAnnotation

class AnnotatedGroup(private val obj: Any) : ICommand {
	val children: List<ICommand> by lazy { CommandUtil.findCommands(obj) }

	val root: AnnotatedCommand? get() = children
		.firstOrNull { (it as? AnnotatedCommand)?.isRoot == true }
		?.let { it as AnnotatedCommand }
		?.takeIf { it.enabled }

	override val name: String = obj::class.findAnnotation<Group>()?.name?.takeIf { it.isNotBlank() } ?: obj::class.simpleName!!.lowercase()
	override val aliases: List<String> = obj::class.findAnnotation<Group>()?.aliases?.toList() ?: emptyList()
	override val enabled: Boolean = true

	override fun create(name: String): LiteralArgumentBuilder<FabricClientCommandSource> {
		val builder = ClientCommandManager.literal(name)
		CommandUtil.addAll(builder, children.filter { it != root })
		root?.build(builder)
		return builder
	}

	override fun execute(ctx: Context): Int {
		val root = this.root
		if(root == null) throw UnsupportedOperationException("This group does not have a root command")
		return root.execute(ctx)
	}
}