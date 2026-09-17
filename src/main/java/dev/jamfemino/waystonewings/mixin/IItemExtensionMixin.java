package dev.jamfemino.waystonewings.mixin;

import dev.jamfemino.waystonewings.config.CommonConfig;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.neoforged.neoforge.common.extensions.IItemExtension;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/** Intercepts NeoForge's actual equipment-policy extension point. */
@Mixin(value = IItemExtension.class, remap = false)
public interface IItemExtensionMixin {
    @Inject(method = "canEquip", at = @At("HEAD"), cancellable = true)
    private void waystoneWings$preventEquipping(ItemStack stack, EquipmentSlot slot, LivingEntity entity,
                                                CallbackInfoReturnable<Boolean> callback) {
        if (slot == EquipmentSlot.CHEST && stack.is(Items.ELYTRA) && CommonConfig.DISABLE_EQUIPPING.get()) {
            callback.setReturnValue(false);
        }
    }
}
