package me.nobaboy.nobaaddons.mixins.events;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.sugar.Local;
import me.nobaboy.nobaaddons.events.EntityNametagRenderEvents;
import net.minecraft.client.render.entity.ArmorStandEntityRenderer;
import net.minecraft.entity.decoration.ArmorStandEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(ArmorStandEntityRenderer.class)
abstract class NametagRenderEventsMixin_ArmorStandEntityRenderer {
	@ModifyReturnValue(method = "hasLabel(Lnet/minecraft/entity/decoration/ArmorStandEntity;D)Z", at = @At("RETURN"))
	public boolean nobaaddons$modifyNametagVisibility(boolean original, @Local(argsOnly = true) ArmorStandEntity entity) {
		var event = new EntityNametagRenderEvents.Visibility(entity, original);
		EntityNametagRenderEvents.VISIBILITY.invoke(event);
		return event.getShouldRender();
	}
}
