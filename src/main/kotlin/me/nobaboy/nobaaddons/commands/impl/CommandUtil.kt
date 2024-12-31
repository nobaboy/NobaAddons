package me.nobaboy.nobaaddons.commands.impl

import com.mojang.brigadier.CommandDispatcher
import com.mojang.brigadier.arguments.ArgumentType
import com.mojang.brigadier.arguments.BoolArgumentType
import com.mojang.brigadier.arguments.DoubleArgumentType
import com.mojang.brigadier.arguments.FloatArgumentType
import com.mojang.brigadier.arguments.IntegerArgumentType
import com.mojang.brigadier.arguments.StringArgumentType
import com.mojang.brigadier.builder.LiteralArgumentBuilder
import me.nobaboy.nobaaddons.commands.annotations.GreedyString
import me.nobaboy.nobaaddons.commands.annotations.Group
import me.nobaboy.nobaaddons.commands.annotations.IntRange
import me.nobaboy.nobaaddons.commands.impl.AnnotatedCommand.Companion.isCommand
import me.nobaboy.nobaaddons.core.Rarity
import me.nobaboy.nobaaddons.mixins.accessors.CommandContextAccessor
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource
import net.minecraft.command.argument.NbtPathArgumentType
import net.minecraft.command.argument.NbtPathArgumentType.NbtPath
import kotlin.collections.forEach
import kotlin.reflect.KClass
import kotlin.reflect.KParameter
import kotlin.reflect.KType
import kotlin.reflect.full.findAnnotation
import kotlin.reflect.full.hasAnnotation
import kotlin.reflect.full.isSubclassOf
import kotlin.reflect.full.memberFunctions
import kotlin.reflect.full.memberProperties
import kotlin.reflect.full.starProjectedType
import kotlin.reflect.jvm.jvmErasure

object CommandUtil {
	private val commands: MutableList<ICommand> = mutableListOf()

	init {
		ClientCommandRegistrationCallback.EVENT.register { dispatch, _ ->
			commands.forEach { register(it, dispatch) }
		}
	}

	internal fun findCommands(from: Any): List<ICommand> = buildList {
		from::class.memberFunctions
			.filter { it.isCommand() }
			.map { AnnotatedCommand(it, from) }
			.also { require(it.count { it.isRoot } <= 1) { "The provided object has more than one @RootCommand" } }
			.also { addAll(it) }
		from::class.nestedClasses
			.filter { it.hasAnnotation<Group>() }
			.map { it.objectInstance!! }
			.map { AnnotatedGroup(it) }
			.also { addAll(it) }
		from::class.memberProperties
			.filter { it.returnType.isSubclassOf(ICommand::class) }
			.map { it.getter.call(from) as ICommand }
			.also { addAll(it) }
	}

	fun register(root: ICommand, dispatcher: CommandDispatcher<FabricClientCommandSource>) {
		if(!root.enabled) return
		val names = listOf(root.name, *root.aliases.toTypedArray())
		names.forEach { dispatcher.register(root.create(it)) }
	}

	fun addAll(command: LiteralArgumentBuilder<FabricClientCommandSource>, commands: List<ICommand>) {
		commands.filter { it.enabled }.forEach {
			val names = listOf(it.name, *it.aliases.toTypedArray())
			names.forEach { name -> command.then(it.create(name)) }
		}
	}

	fun registerRoot(command: Any) {
		commands.add(AnnotatedGroup(command))
	}

	fun register(command: ICommand) {
		commands.add(command)
	}

	// Types like Int? break the below when() blocks if we don't force this star projection
	private fun KType.starProjected() =
		classifier?.starProjectedType ?: throw UnsupportedOperationException("Cannot star project type $this")

	internal fun getType(param: KParameter): ArgumentType<*> {
		return when(param.type.starProjected()) {
			String::class.starProjectedType -> {
				if(param.type.hasAnnotation<GreedyString>()) {
					return StringArgumentType.greedyString()
				}
				StringArgumentType.string()
			}

			Int::class.starProjectedType -> {
				param.type.findAnnotation<IntRange>()?.let {
					return IntegerArgumentType.integer(it.min, it.max)
				}
				IntegerArgumentType.integer()
			}

			Double::class.starProjectedType -> DoubleArgumentType.doubleArg()
			Float::class.starProjectedType -> FloatArgumentType.floatArg()
			Boolean::class.starProjectedType -> BoolArgumentType.bool()
			Rarity::class.starProjectedType -> Rarity.RarityArgumentType
			NbtPath::class.starProjectedType -> NbtPathArgumentType.nbtPath()
			else -> throw UnsupportedOperationException("Unsupported type ${param.type}")
		}
	}

	private fun parameterResolver(param: KParameter): (Context, String) -> Any? = when(param.type.starProjected()) {
		String::class.starProjectedType -> StringArgumentType::getString
		Int::class.starProjectedType -> IntegerArgumentType::getInteger
		Double::class.starProjectedType -> DoubleArgumentType::getDouble
		Float::class.starProjectedType -> FloatArgumentType::getFloat
		Boolean::class.starProjectedType -> BoolArgumentType::getBool
		Rarity::class.starProjectedType -> Rarity.RarityArgumentType::getItemRarity
		NbtPath::class.starProjectedType -> getArgument<NbtPath>()
		else -> throw UnsupportedOperationException("Unsupported type ${param.type}")
	}

	private inline fun <reified T : Any> getArgument(): (Context, String) -> T? =
		{ ctx, name -> ctx.getArgument(name, T::class.java) }

	internal fun resolveCallArgs(params: List<KParameter>, context: Context): Map<KParameter, Any?> {
		val args = (context as CommandContextAccessor).arguments
		return buildMap {
			for(param in params) {
				val name = param.name ?: continue
				if(param.isOptional && name !in args) continue
				put(param, parameterResolver(param).invoke(context, name))
			}
		}
	}

	internal fun parameterSanityCheck(parameters: List<KParameter>) {
		var foundOptional = false
		var last: KParameter? = null
		parameters.forEach {
			if(it.isOptional) {
				foundOptional = true
			} else {
				check(!foundOptional) { "A required parameter must not follow an optional parameter" }
			}

			if(last?.type?.hasAnnotation<GreedyString>() == true) {
				error("No extra parameters may follow a @GreedyString")
			}

			last = it
		}
	}

	internal fun KType.isSubclassOf(cls: KClass<*>) = jvmErasure.isSubclassOf(cls)
}