package me.nobaboy.nobaaddons.mixins.events;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.sugar.Local;
import me.nobaboy.nobaaddons.events.impl.render.EntityNametagRenderEvents;
import me.nobaboy.nobaaddons.utils.MixinKeys;
import net.minecraft.client.render.entity.ArmorStandEntityRenderer;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.LivingEntityRenderer;
import net.minecraft.client.render.entity.MobEntityRenderer;
import net.minecraft.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Coerce;

@Mixin({EntityRenderer.class, ArmorStandEntityRenderer.class, LivingEntityRenderer.class, MobEntityRenderer.class})
abstract class NametagRenderEventsMixin_Visibility<T extends Entity> {
	@ModifyReturnValue(method = "hasLabel(Lnet/minecraft/entity/Entity;D)Z", at = @At("RETURN"))
	public boolean nobaaddons$modifyNametagVisibility(boolean original, @Local(argsOnly = true) @Coerce T entity) {
		var event = new EntityNametagRenderEvents.Visibility(entity, original);
		EntityNametagRenderEvents.VISIBILITY.dispatch(event);
		MixinKeys.RENDER_ORIGINAL_ENTITY_NAME.put(entity, event.getRenderOriginalNametag());
		return event.getShouldRender();
	}
}
