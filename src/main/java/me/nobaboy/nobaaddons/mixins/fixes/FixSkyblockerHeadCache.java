package me.nobaboy.nobaaddons.mixins.fixes;

import com.google.gson.JsonObject;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.moulberry.mixinconstraints.annotations.IfMinecraftVersion;
import com.moulberry.mixinconstraints.annotations.IfModLoaded;
import org.spongepowered.asm.mixin.Dynamic;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;

import java.util.Base64;
import java.util.function.Function;
import java.util.stream.Stream;

@Pseudo
@IfMinecraftVersion(maxVersion = "1.21.3")
@IfModLoaded(value = "skyblocker", maxVersion = "1.23.0-beta.2")
@Mixin(targets = "de.hysky.skyblocker.skyblock.item.PlayerHeadHashCache")
abstract class FixSkyblockerHeadCache {
	@SuppressWarnings("UnresolvedMixinReference") // i'm not adding a compile time dependency on skyblocker for this
	@WrapOperation(
		method = "loadSkins",
		at = @At(
			value = "INVOKE",
			target = "Ljava/util/stream/Stream;map(Ljava/util/function/Function;)Ljava/util/stream/Stream;",
			ordinal = 1
		),
		remap = false
	)
	private static Stream<Object> nobaaddons$fixSkyblockerHeadHashCache(Stream<Object> instance, Function<Object, Object> mapper, Operation<Stream<Object>> original) {
		return original.call(instance, (Function<Object, Object>) (Object value) -> {
			if(value instanceof JsonObject json) {
				return Base64.getDecoder().decode(json.getAsJsonObject("skin").get("value").getAsString());
			}
			return mapper.apply(value);
		});
	}
}
