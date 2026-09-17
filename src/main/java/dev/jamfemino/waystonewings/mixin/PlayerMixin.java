package dev.jamfemino.waystonewings.mixin;

import dev.jamfemino.waystonewings.util.ElytraRestrictions;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Player.class)
public abstract class PlayerMixin {
    @Inject(method = "tryToStartFallFlying", at = @At("HEAD"), cancellable = true)
    private void waystoneWings$preventStartingFlight(CallbackInfoReturnable<Boolean> callback) {
        Player self = (Player) (Object) this;
        if (ElytraRestrictions.shouldBlockFlight(self)) {
            ElytraRestrictions.warn(self);
            callback.setReturnValue(false);
        }
    }
}
