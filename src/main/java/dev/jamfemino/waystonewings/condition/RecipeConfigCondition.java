package dev.jamfemino.waystonewings.condition;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.jamfemino.waystonewings.config.CommonConfig;
import dev.jamfemino.waystonewings.recipe.RecipeFamily;
import dev.jamfemino.waystonewings.recipe.RecipeStyle;
import dev.jamfemino.waystonewings.recipe.RecipeStyles;
import net.neoforged.neoforge.common.conditions.ICondition;

import java.util.Optional;

/**
 * Loads a recipe only when the configured style for its {@link RecipeFamily} matches the file's style.
 * <pre>
 * "neoforge:conditions": [{ "type": "waystone_wings:recipe_config", "recipe": "waystone", "style": "vanilla" }]
 * </pre>
 * {@code elytra_required} additionally ties the recipe to the {@code requireElytraWarpCore} setting.
 */
public record RecipeConfigCondition(RecipeFamily recipe, RecipeStyle style, Optional<Boolean> elytraRequired)
        implements ICondition {
    public static final MapCodec<RecipeConfigCondition> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            RecipeFamily.CODEC.fieldOf("recipe").forGetter(RecipeConfigCondition::recipe),
            RecipeStyle.CODEC.fieldOf("style").forGetter(RecipeConfigCondition::style),
            Codec.BOOL.optionalFieldOf("elytra_required").forGetter(RecipeConfigCondition::elytraRequired)
    ).apply(instance, RecipeConfigCondition::new));

    @Override
    public boolean test(IContext context) {
        if (RecipeStyles.resolve(recipe) != style) {
            return false;
        }
        return elytraRequired
                .map(required -> required == CommonConfig.REQUIRE_ELYTRA_WARP_CORE.get())
                .orElse(true);
    }

    @Override
    public MapCodec<? extends ICondition> codec() {
        return CODEC;
    }
}
