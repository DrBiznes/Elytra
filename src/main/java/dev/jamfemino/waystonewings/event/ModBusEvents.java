package dev.jamfemino.waystonewings.event;

import dev.jamfemino.waystonewings.WaystoneWings;
import dev.jamfemino.waystonewings.registry.ModItems;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;

/** Mod-bus event handlers that run on both sides. */
@EventBusSubscriber(modid = WaystoneWings.MOD_ID)
public final class ModBusEvents {
    /**
     * The Waystones creative tab, which this mod's items are added to so they sit alongside the
     * blocks they build. Items that belong to no tab are also invisible to recipe viewers such
     * as JEI, which source their item list from creative tab contents.
     */
    private static final ResourceKey<CreativeModeTab> WAYSTONES_TAB = ResourceKey.create(
            Registries.CREATIVE_MODE_TAB, ResourceLocation.fromNamespaceAndPath("waystones", "waystones"));

    private ModBusEvents() {
    }

    @SubscribeEvent
    public static void addToCreativeTabs(BuildCreativeModeTabContentsEvent event) {
        if (!WAYSTONES_TAB.equals(event.getTabKey())) {
            return;
        }
        // The finished component first, then the sequenced-assembly intermediates.
        event.accept(ModItems.CALIBRATED_WARP_CORE);
        event.accept(ModItems.INCOMPLETE_WARP_CORE);
        event.accept(ModItems.INCOMPLETE_WAYSTONE);
        event.accept(ModItems.INCOMPLETE_WARP_PLATE);
        event.accept(ModItems.INCOMPLETE_SCROLL);
    }
}
