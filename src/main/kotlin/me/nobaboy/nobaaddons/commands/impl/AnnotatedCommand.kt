package me.nobaboy.nobaaddons.commands.impl

import com.mojang.brigadier.builder.ArgumentBuilder
import com.mojang.brigadier.builder.LiteralArgumentBuilder
import com.mojang.brigadier.context.CommandContext
import me.nobaboy.nobaaddons.NobaAddons
import me.nobaboy.nobaaddons.commands.annotations.Command
import me.nobaboy.nobaaddons.commands.annotations.EnabledIf
import me.nobaboy.nobaaddons.commands.annotations.RootCommand
import me.nobaboy.nobaaddons.commands.impl.CommandUtil.isSubclassOf
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource
import kotlin.reflect.KClass
import kotlin.reflect.KFunction
import kotlin.reflect.KParameter
import kotlin.reflect.full.callSuspendBy
import kotlin.reflect.full.findAnnotation
import kotlin.reflect.full.hasAnnotation
import kotlin.reflect.full.instanceParameter
import kotlin.reflect.full.isSubclassOf
import kotlin.reflect.full.valueParameters
import kotlin.reflect.jvm.jvmErasure

data class AnnotatedCommand(val function: KFunction<*>, val instance: Any) : ICommand {
	init {
		require(function.isCommand()) { "Command must be annotated with @Command or @RootCommand" }
		require(function.hasAnnotation<Command>() != function.hasAnnotation<RootCommand>()) {
			"Function must have only one of either @Command or @RootCommand"
		}
		require(function.firstParameterIs(CommandContext::class)) { "Function must accept only a single CommandContext argument" }
	}

	private val commandAnnotation = function.findAnnotation<Command>()
	private val contextParam by lazy { function.valueParameters.first { it.type.isSubclassOf(CommandContext::class) } }
	private val instanceParam by lazy { function.instanceParameter!! }
	private val params by lazy { function.valueParameters.filter { it != contextParam } }

	override val name: String = commandAnnotation?.name?.takeIf { it.isNotBlank() } ?: function.name.lowercase()
	override val aliases: List<String> = commandAnnotation?.aliases?.toList() ?: emptyList()
	override val enabled: Boolean
		get() {
			val enabled = function.findAnnotation<EnabledIf>() ?: return true
			return enabled.predicate.objectInstance!!.get()
		}

	internal val isRoot: Boolean = function.hasAnnotation<RootCommand>()

	fun build(builder: LiteralArgumentBuilder<FabricClientCommandSource>) {
		val params = function.valueParameters.filter { it != contextParam }
		CommandUtil.parameterSanityCheck(params)

		if(params.all { it.isOptional }) builder.executes(this::execute)
		if(params.isEmpty()) return

		var tree: ArgumentBuilder<FabricClientCommandSource, *>? = null
		var last: KParameter? = null
		params.asReversed().forEach { param ->
			val arg = ClientCommandManager.argument(param.name!!, CommandUtil.getType(param))
			if(tree == null || last?.isOptional == true) arg.executes(this::execute)
			last = param
			tree?.let { arg.then(tree) }
			tree = arg
		}
		builder.then(tree)
	}

	override fun create(name: String): LiteralArgumentBuilder<FabricClientCommandSource> =
		ClientCommandManager.literal(name).also(::build)

	override fun execute(ctx: Context): Int {
		val requiredArgs = mapOf<KParameter, Any?>(
			instanceParam to instance,
			contextParam to ctx,
		)
		val paramArgs = CommandUtil.resolveCallArgs(params, ctx)
		val invokeArgs = requiredArgs + paramArgs

		if(function.isSuspend) {
			NobaAddons.runAsync {
				try {
					function.callSuspendBy(invokeArgs)
				} catch(e: Throwable) {
					ICommand.handleCaught(name, e)
				}
			}
		} else {
			try {
				function.callBy(invokeArgs)
			} catch(e: Throwable) {
				ICommand.handleCaught(name, e)
			}
		}

		return 0
	}

	companion object {
		private fun KFunction<*>.firstParameterIs(cls: KClass<*>) =
			valueParameters
				.also { require(it.isNotEmpty()) { "Function takes no value parameters, but at least one was expected" } }
				.first()
				.type.jvmErasure.isSubclassOf(cls)

		internal fun KFunction<*>.isCommand(): Boolean = hasAnnotation<Command>() || hasAnnotation<RootCommand>()
	}
}