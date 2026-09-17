package dev.jamfemino.waystonewings.event;

import dev.jamfemino.waystonewings.config.ClientConfig;
import dev.jamfemino.waystonewings.config.CommonConfig;
import dev.jamfemino.waystonewings.util.ElytraRestrictions;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.neoforged.neoforge.event.entity.player.ItemTooltipEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;

public final class CommonEvents {
    private CommonEvents() {
    }

    public static void onPlayerTick(PlayerTickEvent.Post event) {
        var player = event.getEntity();
        if (player.level().isClientSide()
                || !CommonConfig.DISABLE_EQUIPPING.get()
                || CommonConfig.HANDLE_ALREADY_EQUIPPED.get() != CommonConfig.AlreadyEquippedBehavior.UNEQUIP) {
            return;
        }

        ItemStack equipped = player.getItemBySlot(EquipmentSlot.CHEST);
        if (!ElytraRestrictions.isRestrictedElytra(equipped)) {
            return;
        }

        ItemStack preserved = equipped.copy();
        player.setItemSlot(EquipmentSlot.CHEST, ItemStack.EMPTY);
        if (!player.getInventory().add(preserved)) {
            player.drop(preserved, false);
        }
    }

    public static void onItemTooltip(ItemTooltipEvent event) {
        if (!event.getItemStack().is(Items.ELYTRA) || !ClientConfig.SHOW_PURPOSE_TOOLTIP.get()) {
            return;
        }
        event.getToolTip().add(Component.translatable("tooltip.waystone_wings.elytra.flight_disabled")
                .withStyle(ChatFormatting.RED));
        event.getToolTip().add(Component.translatable("tooltip.waystone_wings.elytra.purpose")
                .withStyle(ChatFormatting.DARK_AQUA));
    }
}
