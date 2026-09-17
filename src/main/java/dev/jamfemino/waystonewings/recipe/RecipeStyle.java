package dev.jamfemino.waystonewings.recipe;

import com.mojang.serialization.Codec;
import net.minecraft.util.StringRepresentable;

/** The crafting style a recipe file belongs to, after every config rule has been applied. */
public enum RecipeStyle implements StringRepresentable {
    /** Create machinery: sequenced assembly, deploying, pressing, mixing and compacting. */
    CREATE("create"),
    /** Plain vanilla crafting-table recipes with the same progression. */
    VANILLA("vanilla"),
    /** Recreations of the untouched Waystones recipes, for compatibility. */
    ORIGINAL("original");

    public static final Codec<RecipeStyle> CODEC = StringRepresentable.fromEnum(RecipeStyle::values);

    private final String serializedName;

    RecipeStyle(String serializedName) {
        this.serializedName = serializedName;
    }

    @Override
    public String getSerializedName() {
        return serializedName;
    }
}
