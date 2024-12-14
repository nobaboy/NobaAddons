package me.nobaboy.nobaaddons.mixins.accessors;

import dev.isxander.yacl3.gui.OptionListWidget;
import dev.isxander.yacl3.gui.YACLScreen.CategoryTab;
import dev.isxander.yacl3.gui.tab.ListHolderWidget;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(CategoryTab.class)
public interface CategoryTabAccessor {
	@Accessor(remap = false) ListHolderWidget<OptionListWidget> getOptionList();
}
