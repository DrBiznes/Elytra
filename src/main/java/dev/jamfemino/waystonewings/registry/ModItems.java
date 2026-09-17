package dev.jamfemino.waystonewings.registry;

import dev.jamfemino.waystonewings.WaystoneWings;
import net.minecraft.world.item.Item;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class ModItems {
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(WaystoneWings.MOD_ID);

    public static final DeferredItem<Item> CALIBRATED_WARP_CORE = simple("calibrated_warp_core");
    public static final DeferredItem<Item> INCOMPLETE_WARP_CORE = simple("incomplete_warp_core");
    public static final DeferredItem<Item> INCOMPLETE_WAYSTONE = simple("incomplete_waystone");
    public static final DeferredItem<Item> INCOMPLETE_WARP_PLATE = simple("incomplete_warp_plate");
    public static final DeferredItem<Item> INCOMPLETE_WARP_STONE = simple("incomplete_warp_stone");
    public static final DeferredItem<Item> INCOMPLETE_SCROLL = simple("incomplete_scroll");

    private ModItems() {
    }

    private static DeferredItem<Item> simple(String name) {
        return ITEMS.registerSimpleItem(name, new Item.Properties());
    }
}
