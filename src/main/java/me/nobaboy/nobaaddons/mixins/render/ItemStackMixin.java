package me.nobaboy.nobaaddons.mixins.render;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import me.nobaboy.nobaaddons.utils.items.ItemUtils;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

/*? if <1.21.2 {*/
/*import net.minecraft.item.ArmorItem;*/
/*?}*/

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
}
