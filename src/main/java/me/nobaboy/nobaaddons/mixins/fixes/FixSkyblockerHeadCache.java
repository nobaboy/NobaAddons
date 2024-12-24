package me.nobaboy.nobaaddons.mixins.fixes;

import com.google.gson.JsonObject;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.moulberry.mixinconstraints.annotations.IfModLoaded;
import org.spongepowered.asm.mixin.Dynamic;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;

import java.util.Base64;
import java.util.function.Function;
import java.util.stream.Stream;

// only target versions that are definitely affected by this issue
//? if <1.21.4 {
@Pseudo
// further ensure that we only modify affected versions
@IfModLoaded(value = "skyblocker", maxVersion = "1.23.0-beta.2")
@Mixin(targets = "de.hysky.skyblocker.skyblock.item.PlayerHeadHashCache")
abstract class FixSkyblockerHeadCache {
	@Dynamic
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
//?}
