package dev.jamfemino.waystonewings.gametest;

import dev.jamfemino.waystonewings.WaystoneWings;
import dev.jamfemino.waystonewings.registry.ModItems;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.level.GameType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.neoforged.neoforge.gametest.GameTestHolder;
import net.neoforged.neoforge.gametest.PrefixGameTestTemplate;

@GameTestHolder(WaystoneWings.MOD_ID)
@PrefixGameTestTemplate(false)
public final class WaystoneWingsGameTests {
    private WaystoneWingsGameTests() {
    }

    @GameTest(template = "empty")
    public static void restrictionsAndRecipesLoad(GameTestHelper helper) {
        var player = helper.makeMockPlayer(GameType.SURVIVAL);
        var elytra = new ItemStack(Items.ELYTRA);

        helper.assertFalse(elytra.canEquip(EquipmentSlot.CHEST, player),
                "The Elytra must not be equipable in the chest slot");
        helper.assertTrue(BuiltInRegistries.ITEM.getKey(ModItems.CALIBRATED_WARP_CORE.get())
                        .equals(WaystoneWings.id("calibrated_warp_core")),
                "The Calibrated Warp Core must be registered");
        helper.assertTrue(helper.getLevel().getRecipeManager()
                        .byKey(WaystoneWings.id("infrastructure/waystone")).isPresent(),
                "The Create Waystone replacement recipe must load");
        helper.assertTrue(helper.getLevel().getRecipeManager()
                        .byKey(net.minecraft.resources.ResourceLocation.fromNamespaceAndPath("waystones", "waystone")).isEmpty(),
                "The original Waystones recipe must be disabled by default");

        helper.succeed();
    }
}
