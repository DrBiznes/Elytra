package dev.jamfemino.waystonewings.condition;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.jamfemino.waystonewings.config.CommonConfig;
import net.neoforged.neoforge.common.conditions.ICondition;

public record RecipeConfigCondition(String group, boolean original, Boolean elytraRequired) implements ICondition {
    public static final MapCodec<RecipeConfigCondition> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            Codec.STRING.optionalFieldOf("group", "core").forGetter(RecipeConfigCondition::group),
            Codec.BOOL.optionalFieldOf("original", false).forGetter(RecipeConfigCondition::original),
            Codec.BOOL.optionalFieldOf("elytra_required").forGetter(condition ->
                    java.util.Optional.ofNullable(condition.elytraRequired()))
    ).apply(instance, (group, original, required) -> new RecipeConfigCondition(group, original, required.orElse(null))));

    @Override
    public boolean test(IContext context) {
        boolean overhaul = CommonConfig.REPLACE_WAYSTONES_RECIPES.get();
        boolean itemGroupEnabled = !"items".equals(group) || CommonConfig.REPLACE_WAYSTONES_ITEM_RECIPES.get();
        boolean originalsAllowed = CommonConfig.ALLOW_ORIGINAL_RECIPES.get();

        boolean selected = original
                ? originalsAllowed || !overhaul || !itemGroupEnabled
                : overhaul && !originalsAllowed && itemGroupEnabled;

        if (selected && elytraRequired != null) {
            selected = elytraRequired == CommonConfig.REQUIRE_ELYTRA_WARP_CORE.get();
        }
        return selected;
    }

    @Override
    public MapCodec<? extends ICondition> codec() {
        return CODEC;
    }
}
