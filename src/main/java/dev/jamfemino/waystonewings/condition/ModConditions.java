package dev.jamfemino.waystonewings.condition;

import com.mojang.serialization.MapCodec;
import dev.jamfemino.waystonewings.WaystoneWings;
import net.neoforged.neoforge.common.conditions.ICondition;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

public final class ModConditions {
    public static final DeferredRegister<MapCodec<? extends ICondition>> CONDITION_CODECS =
            DeferredRegister.create(NeoForgeRegistries.Keys.CONDITION_CODECS, WaystoneWings.MOD_ID);

    public static final DeferredHolder<MapCodec<? extends ICondition>, MapCodec<RecipeConfigCondition>> RECIPE_CONFIG =
            CONDITION_CODECS.register("recipe_config", () -> RecipeConfigCondition.CODEC);

    private ModConditions() {
    }
}
