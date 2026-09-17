package dev.jamfemino.waystonewings.mixin;

import dev.jamfemino.waystonewings.util.ElytraRestrictions;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin {
    @Inject(method = "updateFallFlying", at = @At("HEAD"), cancellable = true)
    private void waystoneWings$stopMaintainedFlight(CallbackInfo callback) {
        LivingEntity self = (LivingEntity) (Object) this;
        if (ElytraRestrictions.shouldBlockFlight(self)) {
            ((EntityAccessor) self).waystoneWings$setSharedFlag(7, false);
            if (self.isFallFlying()) {
                ElytraRestrictions.warn(self);
            }
            callback.cancel();
        }
    }
}
