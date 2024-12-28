package me.nobaboy.nobaaddons.mixins.render;

/*? if <1.21.2 {*/
/*import net.minecraft.item.ArmorItem;*/
/*?}*/

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import me.nobaboy.nobaaddons.api.skyblock.SkyBlockAPI;
import me.nobaboy.nobaaddons.utils.items.ItemUtils;
import net.minecraft.component.ComponentType;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Slice;

import java.util.function.Consumer;

@Mixin(ItemStack.class)
abstract class ItemStackMixin {
	@ModifyReturnValue(method = "hasGlint", at = @At("RETURN"))
	public boolean nobaaddons$modifyGlint(boolean original) {
		var item = (ItemStack)(Object)this;
		boolean isArmor = /*? if >=1.21.2 {*/item.get(DataComponentTypes.EQUIPPABLE) != null/*?} else {*//*item.getItem() instanceof ArmorItem*//*?}*/;
		if(!isArmor) {
			return original;
		}

		return ItemUtils.shouldArmorHaveEnchantGlint(item, original);
	}

	// TODO making this work on 1.21 is not going to be fun
	@WrapWithCondition(
		method = "getTooltip",
		at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/item/ItemStack;appendTooltip(Lnet/minecraft/component/ComponentType;Lnet/minecraft/item/Item$TooltipContext;Ljava/util/function/Consumer;Lnet/minecraft/item/tooltip/TooltipType;)V"
		),
		// instead of using brittle ordinals we're just comparing the ComponentType, so reduce how many operations
		// we wrap by only affecting the region the calls we want to affect are in
		slice = @Slice(
			from = @At(value = "INVOKE", target = "Lnet/minecraft/item/ItemStack;getItem()Lnet/minecraft/item/Item;", ordinal = 0),
			to = @At(value = "INVOKE", target = "Lnet/minecraft/item/ItemStack;appendAttributeModifiersTooltip(Ljava/util/function/Consumer;Lnet/minecraft/entity/player/PlayerEntity;)V")
		)
	)
	public boolean nobaaddons$removeVanillaEnchants(ItemStack instance, ComponentType<?> componentType, Item.TooltipContext context, Consumer<Text> textConsumer, TooltipType type) {
		if(componentType != DataComponentTypes.STORED_ENCHANTMENTS && componentType != DataComponentTypes.ENCHANTMENTS) return true;
		if(!SkyBlockAPI.inSkyBlock()) return true;
		return !ItemUtils.isSkyBlockItem((ItemStack) (Object) this);
	}
}
