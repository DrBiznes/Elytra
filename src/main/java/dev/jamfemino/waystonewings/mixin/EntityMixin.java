package dev.jamfemino.waystonewings.mixin;

import dev.jamfemino.waystonewings.util.ElytraRestrictions;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Entity.class)
public abstract class EntityMixin {
    @Inject(method = "setSharedFlag", at = @At("HEAD"), cancellable = true)
    private void waystoneWings$preventFallFlyingFlag(int flag, boolean value, CallbackInfo callback) {
        Object self = this;
        if (flag == 7 && value && self instanceof LivingEntity living && ElytraRestrictions.shouldBlockFlight(living)) {
            ElytraRestrictions.warn(living);
            callback.cancel();
        }
    }
}
