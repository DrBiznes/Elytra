package dev.jamfemino.waystonewings.config;

import dev.jamfemino.waystonewings.recipe.RecipeFamily;
import net.minecraft.network.chat.Component;
import net.neoforged.neoforge.common.ModConfigSpec;
import net.neoforged.neoforge.common.TranslatableEnum;

import java.util.Collections;
import java.util.EnumMap;
import java.util.Map;

public final class CommonConfig {
    private static final ModConfigSpec.Builder BUILDER = new ModConfigSpec.Builder();

    public static final ModConfigSpec.BooleanValue DISABLE_FLIGHT;
    public static final ModConfigSpec.BooleanValue DISABLE_FIREWORK_BOOSTING;
    public static final ModConfigSpec.BooleanValue DISABLE_EQUIPPING;
    public static final ModConfigSpec.EnumValue<AlreadyEquippedBehavior> HANDLE_ALREADY_EQUIPPED;
    public static final ModConfigSpec.BooleanValue SHOW_FLIGHT_WARNING;
    public static final ModConfigSpec.IntValue WARNING_COOLDOWN_SECONDS;
    public static final ModConfigSpec.ConfigValue<String> FLIGHT_WARNING_MESSAGE;
    public static final ModConfigSpec.BooleanValue REPLACE_WAYSTONES_RECIPES;
    public static final ModConfigSpec.BooleanValue REQUIRE_ELYTRA_WARP_CORE;
    public static final ModConfigSpec.BooleanValue REPLACE_WAYSTONES_ITEM_RECIPES;
    public static final ModConfigSpec.EnumValue<DefaultRecipeStyle> DEFAULT_RECIPE_STYLE;
    private static final Map<RecipeFamily, ModConfigSpec.EnumValue<FamilyRecipeStyle>> RECIPE_STYLE_OVERRIDES;
    public static final ModConfigSpec.BooleanValue ALLOW_ORIGINAL_RECIPES;

    public static final ModConfigSpec SPEC;

    static {
        BUILDER.comment("Rules that prevent the Elytra from functioning as a flight item.")
                .translation("config.waystone_wings.section.flight")
                .push("flight");
        DISABLE_FLIGHT = BUILDER.comment("Prevent starting or maintaining Elytra gliding in every dimension.")
                .translation("config.waystone_wings.disableFlight")
                .define("disableFlight", true);
        DISABLE_FIREWORK_BOOSTING = BUILDER.comment("Prevent using fireworks as a boost while fall-flying.")
                .translation("config.waystone_wings.disableFireworkBoosting")
                .define("disableFireworkBoosting", true);
        BUILDER.pop();

        BUILDER.comment("Rules for chest-slot equipment and existing equipped Elytras.")
                .translation("config.waystone_wings.section.equipment")
                .push("equipment");
        DISABLE_EQUIPPING = BUILDER.comment("Reject Elytra placement in the chest equipment slot.")
                .translation("config.waystone_wings.disableEquipping")
                .define("disableEquipping", true);
        HANDLE_ALREADY_EQUIPPED = BUILDER.comment("What to do when a player already has an Elytra equipped. UNEQUIP preserves it in inventory or drops it if full.")
                .translation("config.waystone_wings.handleAlreadyEquipped")
                .defineEnum("handleAlreadyEquipped", AlreadyEquippedBehavior.UNEQUIP);
        BUILDER.pop();

        BUILDER.comment("Action-bar feedback shown when blocked flight is attempted.")
                .translation("config.waystone_wings.section.notifications")
                .push("notifications");
        SHOW_FLIGHT_WARNING = BUILDER.comment("Show a throttled action-bar warning when restricted flight is attempted.")
                .translation("config.waystone_wings.showFlightWarning")
                .define("showFlightWarning", true);
        WARNING_COOLDOWN_SECONDS = BUILDER.comment("Minimum seconds between flight warning messages for each player.")
                .translation("config.waystone_wings.warningCooldownSeconds")
                .defineInRange("warningCooldownSeconds", 2, 1, 30);
        FLIGHT_WARNING_MESSAGE = BUILDER.comment("Literal server-controlled warning text. Must contain 1 to 128 visible characters.")
                .translation("config.waystone_wings.flightWarningMessage")
                .define("flightWarningMessage", "The Elytra Warp Core can no longer be used for flight.",
                        value -> value instanceof String message && !message.isBlank() && message.length() <= 128);
        BUILDER.pop();

        BUILDER.comment("Progression overhaul for permanent Waystones infrastructure.")
                .translation("config.waystone_wings.section.infrastructureRecipes")
                .push("infrastructureRecipes");
        REPLACE_WAYSTONES_RECIPES = BUILDER.comment("Enable the Waystone Wings recipe overhaul (Create machinery or vanilla crafting, see recipeStyle).")
                .translation("config.waystone_wings.replaceWaystonesRecipes")
                .worldRestart()
                .define("replaceWaystonesRecipes", true);
        REQUIRE_ELYTRA_WARP_CORE = BUILDER.comment("Require an Elytra as the input to Calibrated Warp Core assembly; otherwise a Nether Star is used.")
                .translation("config.waystone_wings.requireElytraWarpCore")
                .worldRestart()
                .define("requireElytraWarpCore", true);
        BUILDER.pop();

        BUILDER.comment("Recipe coverage for scrolls, dust, shards, tools, and utility items.")
                .translation("config.waystone_wings.section.itemRecipes")
                .push("itemRecipes");
        REPLACE_WAYSTONES_ITEM_RECIPES = BUILDER.comment("Also overhaul scrolls, dust, shards, the Warp Stone, Twinbound Feather, and Epitaph.")
                .translation("config.waystone_wings.replaceWaystonesItemRecipes")
                .worldRestart()
                .define("replaceWaystonesItemRecipes", true);
        BUILDER.pop();

        BUILDER.comment("Whether the overhauled recipes use Create machinery or the vanilla crafting table.",
                        "Create is an optional dependency: without it every recipe uses the vanilla crafting table.")
                .translation("config.waystone_wings.section.recipeStyle")
                .push("recipeStyle");
        DEFAULT_RECIPE_STYLE = BUILDER.comment(
                        "AUTO: Create recipes when Create is installed, vanilla crafting-table recipes otherwise.",
                        "CREATE: Create recipes (falls back to vanilla for any recipe when Create is missing).",
                        "VANILLA: vanilla crafting-table recipes even when Create is installed.")
                .translation("config.waystone_wings.defaultRecipeStyle")
                .worldRestart()
                .defineEnum("defaultRecipeStyle", DefaultRecipeStyle.AUTO);

        BUILDER.comment("Per-recipe overrides. DEFAULT follows defaultRecipeStyle; CREATE and VANILLA force that style for the recipe.")
                .translation("config.waystone_wings.section.recipeStyleOverrides")
                .push("overrides");
        Map<RecipeFamily, ModConfigSpec.EnumValue<FamilyRecipeStyle>> overrides = new EnumMap<>(RecipeFamily.class);
        for (RecipeFamily family : RecipeFamily.values()) {
            if (!family.toggleable()) {
                // No Create recipe exists for this family, so there is nothing to override.
                continue;
            }
            overrides.put(family, BUILDER.comment(family.description())
                    .translation("config.waystone_wings.recipeStyle." + family.configKey())
                    .worldRestart()
                    .defineEnum(family.configKey(), FamilyRecipeStyle.DEFAULT));
        }
        RECIPE_STYLE_OVERRIDES = Collections.unmodifiableMap(overrides);
        BUILDER.pop(2);

        BUILDER.comment("Fallback controls for compatibility with modpacks and external data packs.")
                .translation("config.waystone_wings.section.compatibility")
                .push("compatibility");
        ALLOW_ORIGINAL_RECIPES = BUILDER.comment("Emergency compatibility switch: restore recreated versions of the original Waystones recipes.")
                .translation("config.waystone_wings.allowOriginalRecipes")
                .worldRestart()
                .define("allowOriginalRecipes", false);
        BUILDER.pop();

        SPEC = BUILDER.build();
    }

    private CommonConfig() {
    }

    /** The per-family style override for {@code family}. */
    public static ModConfigSpec.EnumValue<FamilyRecipeStyle> recipeStyleOverride(RecipeFamily family) {
        return RECIPE_STYLE_OVERRIDES.get(family);
    }

    public enum AlreadyEquippedBehavior implements TranslatableEnum {
        UNEQUIP("config.waystone_wings.handleAlreadyEquipped.unequip"),
        ALLOW("config.waystone_wings.handleAlreadyEquipped.allow");

        private final String translationKey;

        AlreadyEquippedBehavior(String translationKey) {
            this.translationKey = translationKey;
        }

        @Override
        public Component getTranslatedName() {
            return Component.translatable(translationKey);
        }
    }

    /** Global recipe style. */
    public enum DefaultRecipeStyle implements TranslatableEnum {
        AUTO("config.waystone_wings.recipeStyle.auto"),
        CREATE("config.waystone_wings.recipeStyle.create"),
        VANILLA("config.waystone_wings.recipeStyle.vanilla");

        private final String translationKey;

        DefaultRecipeStyle(String translationKey) {
            this.translationKey = translationKey;
        }

        @Override
        public Component getTranslatedName() {
            return Component.translatable(translationKey);
        }
    }

    /** Per-family recipe style; DEFAULT defers to {@link #DEFAULT_RECIPE_STYLE}. */
    public enum FamilyRecipeStyle implements TranslatableEnum {
        DEFAULT("config.waystone_wings.recipeStyle.default"),
        CREATE("config.waystone_wings.recipeStyle.create"),
        VANILLA("config.waystone_wings.recipeStyle.vanilla");

        private final String translationKey;

        FamilyRecipeStyle(String translationKey) {
            this.translationKey = translationKey;
        }

        @Override
        public Component getTranslatedName() {
            return Component.translatable(translationKey);
        }
    }
}
