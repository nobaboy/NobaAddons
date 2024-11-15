package me.nobaboy.nobaaddons.mixins;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import me.nobaboy.nobaaddons.config.NobaConfigManager;
import net.minecraft.client.option.Perspective;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(Perspective.class)
public class PerspectiveMixin {
	@ModifyReturnValue(method = "next", at = @At("RETURN"))
	public Perspective nobaaddons$removeFrontFacingThirdPerson(Perspective original) {
		var config = NobaConfigManager.getConfig().getUiAndVisuals().getRenderingTweaks();
		if(config.getRemoveFrontFacingThirdPerson() && original == Perspective.THIRD_PERSON_FRONT) {
			return Perspective.FIRST_PERSON;
		}

		return original;
	}
}
