package dev.jamfemino.waystonewings.mixin;

import dev.jamfemino.waystonewings.config.ClientConfig;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ItemStack.class)
public abstract class ItemStackMixin {
    @Inject(method = "getHoverName", at = @At("HEAD"), cancellable = true)
    private void waystoneWings$renameElytra(CallbackInfoReturnable<Component> callback) {
        ItemStack self = (ItemStack) (Object) this;
        if (self.is(Items.ELYTRA) && !self.has(DataComponents.CUSTOM_NAME) && ClientConfig.RENAME_ELYTRA.get()) {
            callback.setReturnValue(Component.translatable("item.waystone_wings.elytra_warp_core"));
        }
    }
}
