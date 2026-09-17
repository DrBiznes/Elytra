package dev.jamfemino.waystonewings.util;

import dev.jamfemino.waystonewings.config.CommonConfig;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

public final class ElytraRestrictions {
    private ElytraRestrictions() {
    }

    public static boolean isRestrictedElytra(ItemStack stack) {
        return stack.is(Items.ELYTRA);
    }

    public static boolean shouldBlockFlight(LivingEntity entity) {
        return CommonConfig.DISABLE_FLIGHT.get() && entity instanceof net.minecraft.world.entity.player.Player;
    }

    public static void warn(LivingEntity entity) {
        if (!(entity instanceof ServerPlayer player) || !CommonConfig.SHOW_FLIGHT_WARNING.get()) {
            return;
        }
        long previous = player.getPersistentData().getLong("waystone_wings:last_flight_warning");
        long cooldownTicks = CommonConfig.WARNING_COOLDOWN_SECONDS.get() * 20L;
        if (player.tickCount - previous < cooldownTicks) {
            return;
        }
        player.getPersistentData().putLong("waystone_wings:last_flight_warning", player.tickCount);
        player.displayClientMessage(Component.literal(CommonConfig.FLIGHT_WARNING_MESSAGE.get()), true);
    }
}
