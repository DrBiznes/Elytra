package dev.jamfemino.waystonewings.recipe;

import dev.jamfemino.waystonewings.WaystoneWings;
import dev.jamfemino.waystonewings.config.CommonConfig;
import net.neoforged.fml.ModList;

import java.util.EnumSet;
import java.util.Set;

/** Resolves which {@link RecipeStyle} is active for a {@link RecipeFamily} from the config and mod list. */
public final class RecipeStyles {
    public static final String CREATE_MOD_ID = "create";

    private static final Set<RecipeFamily> WARNED = EnumSet.noneOf(RecipeFamily.class);

    private RecipeStyles() {
    }

    public static boolean isCreateLoaded() {
        return ModList.get().isLoaded(CREATE_MOD_ID);
    }

    public static RecipeStyle resolve(RecipeFamily family) {
        if (CommonConfig.ALLOW_ORIGINAL_RECIPES.get() || !CommonConfig.REPLACE_WAYSTONES_RECIPES.get()) {
            return RecipeStyle.ORIGINAL;
        }
        if (family.group() == RecipeFamily.Group.ITEMS && !CommonConfig.REPLACE_WAYSTONES_ITEM_RECIPES.get()) {
            return RecipeStyle.ORIGINAL;
        }
        if (!family.toggleable()) {
            // No Create recipe exists for this family; it is always crafted at a table.
            return RecipeStyle.VANILLA;
        }

        CommonConfig.DefaultRecipeStyle requested = switch (CommonConfig.recipeStyleOverride(family).get()) {
            case DEFAULT -> CommonConfig.DEFAULT_RECIPE_STYLE.get();
            case CREATE -> CommonConfig.DefaultRecipeStyle.CREATE;
            case VANILLA -> CommonConfig.DefaultRecipeStyle.VANILLA;
        };

        return switch (requested) {
            case VANILLA -> RecipeStyle.VANILLA;
            case AUTO -> isCreateLoaded() ? RecipeStyle.CREATE : RecipeStyle.VANILLA;
            case CREATE -> {
                if (isCreateLoaded()) {
                    yield RecipeStyle.CREATE;
                }
                warnMissingCreate(family);
                yield RecipeStyle.VANILLA;
            }
        };
    }

    private static synchronized void warnMissingCreate(RecipeFamily family) {
        if (WARNED.add(family)) {
            WaystoneWings.LOGGER.warn("Recipe style CREATE was requested for '{}' but Create is not installed; "
                    + "using the vanilla crafting-table recipes instead.", family.configKey());
        }
    }
}
