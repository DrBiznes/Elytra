package dev.jamfemino.waystonewings.gametest;

import dev.jamfemino.waystonewings.WaystoneWings;
import dev.jamfemino.waystonewings.recipe.RecipeFamily;
import dev.jamfemino.waystonewings.recipe.RecipeStyle;
import dev.jamfemino.waystonewings.recipe.RecipeStyles;
import dev.jamfemino.waystonewings.registry.ModItems;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.level.GameType;
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

        RecipeManager recipes = helper.getLevel().getRecipeManager();
        if (!RecipeStyles.isCreateLoaded()) {
            helper.assertTrue(RecipeStyles.resolve(RecipeFamily.WAYSTONE) != RecipeStyle.CREATE,
                    "Create recipes must never be selected when Create is not installed");
        }
        assertOnlyActiveStyleLoaded(helper, recipes, RecipeFamily.WAYSTONE, "infrastructure/waystone", "waystone");
        assertOnlyActiveStyleLoaded(helper, recipes, RecipeFamily.WARP_DUST, "items/warp_dust", "warp_dust");
        assertOnlyActiveStyleLoaded(helper, recipes, RecipeFamily.BLANK_SCROLL, "items/blank_scroll", "blank_scroll");
        assertOnlyActiveStyleLoaded(helper, recipes, RecipeFamily.WAYSTONE_VARIANTS,
                "infrastructure/variants/mossy_waystone", "mossy_waystone");
        helper.assertTrue(recipes.byKey(ResourceLocation.fromNamespaceAndPath("waystones", "waystone")).isEmpty(),
                "The original Waystones recipe must be disabled by default");

        // Portstones are a non-toggleable family: always vanilla, never Create, regardless of config.
        helper.assertTrue(RecipeStyles.resolve(RecipeFamily.PORTSTONES) == RecipeStyle.VANILLA,
                "Non-toggleable families must always resolve to VANILLA");
        helper.assertTrue(
                recipes.byKey(WaystoneWings.id("vanilla/infrastructure/portstones/black")).isPresent(),
                "The vanilla Black Portstone recipe must be loaded");
        helper.assertTrue(
                recipes.byKey(WaystoneWings.id("create/infrastructure/portstones/black")).isEmpty(),
                "Portstones must never have a Create recipe");

        // The Warp Stone keeps its own Waystones recipe: we neither disable nor replace it.
        helper.assertTrue(
                recipes.byKey(ResourceLocation.fromNamespaceAndPath("waystones", "warp_stone")).isPresent(),
                "The original Waystones Warp Stone recipe must stay enabled");
        helper.assertTrue(recipes.byKey(WaystoneWings.id("create/items/warp_stone")).isEmpty()
                        && recipes.byKey(WaystoneWings.id("vanilla/items/warp_stone")).isEmpty()
                        && recipes.byKey(WaystoneWings.id("original/warp_stone")).isEmpty(),
                "Waystone Wings must not add any Warp Stone recipe of its own");

        // Items must be in the Waystones creative tab, or they are unreachable in creative
        // and invisible to recipe viewers such as JEI.
        var tab = BuiltInRegistries.CREATIVE_MODE_TAB
                .get(ResourceLocation.fromNamespaceAndPath("waystones", "waystones"));
        helper.assertTrue(tab != null, "The Waystones creative tab must exist");
        // Tab contents are built lazily when the creative menu is first opened, which never
        // happens on a headless server, so build them explicitly before asserting.
        var level = helper.getLevel();
        tab.buildContents(new CreativeModeTab.ItemDisplayParameters(
                level.enabledFeatures(), true, level.registryAccess()));
        var tabItems = tab.getDisplayItems().stream().map(ItemStack::getItem).toList();
        helper.assertTrue(tabItems.contains(ModItems.CALIBRATED_WARP_CORE.get()),
                "The Calibrated Warp Core must appear in the Waystones creative tab");
        helper.assertTrue(tabItems.contains(ModItems.INCOMPLETE_WAYSTONE.get()),
                "The transitional assembly items must appear in the Waystones creative tab");

        helper.succeed();
    }

    private static void assertOnlyActiveStyleLoaded(GameTestHelper helper, RecipeManager recipes, RecipeFamily family,
                                                    String replacementPath, String originalName) {
        RecipeStyle active = RecipeStyles.resolve(family);
        for (RecipeStyle style : RecipeStyle.values()) {
            ResourceLocation id = style == RecipeStyle.ORIGINAL
                    ? WaystoneWings.id("original/" + originalName)
                    : WaystoneWings.id(style.getSerializedName() + "/" + replacementPath);
            boolean present = recipes.byKey(id).isPresent();
            helper.assertTrue(present == (style == active),
                    "Recipe " + id + " must " + (style == active ? "" : "not ") + "be loaded (active style: " + active + ")");
        }
    }
}
