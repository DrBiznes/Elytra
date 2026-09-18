package dev.jamfemino.waystonewings.recipe;

import com.mojang.serialization.Codec;
import net.minecraft.util.StringRepresentable;

/**
 * A group of Waystones recipes that share one crafting style setting.
 * <p>
 * Toggleable families have both a Create and a vanilla recipe file, and get a per-family
 * config override so the crafting style can be forced independent of the global default.
 * Non-toggleable families only ever have a vanilla recipe: the item doesn't have a
 * multi-stage or machine-specific process worth wrapping in Create machinery, so it is
 * always crafted at a table and carries no override entry in the config.
 */
public enum RecipeFamily implements StringRepresentable {
    CALIBRATED_WARP_CORE("calibrated_warp_core", "calibratedWarpCore", Group.INFRASTRUCTURE, true,
            "Calibrated Warp Core assembly (the gate for all Waystones infrastructure)."),
    WAYSTONE("waystone", "waystone", Group.INFRASTRUCTURE, true,
            "The plain Waystone."),
    WAYSTONE_VARIANTS("waystone_variants", "waystoneVariants", Group.INFRASTRUCTURE, true,
            "Material variants of the Waystone (mossy, sandy, deepslate, blackstone, ...)."),
    WARP_PLATE("warp_plate", "warpPlate", Group.INFRASTRUCTURE, true,
            "The Warp Plate."),
    PORTSTONES("portstones", "portstones", Group.INFRASTRUCTURE, false,
            "All sixteen coloured Portstones. Always crafted at a table: a single dye step isn't worth a machine."),
    SHARESTONES("sharestones", "sharestones", Group.INFRASTRUCTURE, false,
            "All sixteen coloured Sharestones. Always crafted at a table: a single reinforcing step isn't worth a machine."),
    BLANK_SCROLL("blank_scroll", "blankScroll", Group.ITEMS, true,
            "Blank Scrolls."),
    WARP_SCROLL("warp_scroll", "warpScroll", Group.ITEMS, false,
            "Warp Scrolls. Always crafted at a table from a Blank Scroll."),
    RETURN_SCROLL("return_scroll", "returnScroll", Group.ITEMS, false,
            "Return Scrolls. Always crafted at a table from a Blank Scroll."),
    PORTAL_SCROLL("portal_scroll", "portalScroll", Group.ITEMS, false,
            "Portal Scrolls. Always crafted at a table from a Blank Scroll."),
    WARP_DUST("warp_dust", "warpDust", Group.ITEMS, true,
            "Warp Dust."),
    DORMANT_SHARD("dormant_shard", "dormantShard", Group.ITEMS, false,
            "Dormant Shards. Always crafted at a table: no custom overhaul, matches the original Waystones recipe."),
    DEEPSLATE_SHARD("deepslate_shard", "deepslateShard", Group.ITEMS, true,
            "Deepslate Shards."),
    TWINBOUND_FEATHER("twinbound_feather", "twinboundFeather", Group.ITEMS, false,
            "The Twinbound Feather. Always crafted at a table."),
    EPITAPH("epitaph", "epitaph", Group.ITEMS, false,
            "The Epitaph. Always crafted at a table.");

    public static final Codec<RecipeFamily> CODEC = StringRepresentable.fromEnum(RecipeFamily::values);

    private final String serializedName;
    private final String configKey;
    private final Group group;
    private final boolean toggleable;
    private final String description;

    RecipeFamily(String serializedName, String configKey, Group group, boolean toggleable, String description) {
        this.serializedName = serializedName;
        this.configKey = configKey;
        this.group = group;
        this.toggleable = toggleable;
        this.description = description;
    }

    @Override
    public String getSerializedName() {
        return serializedName;
    }

    /** Key used for this family inside the common config file. */
    public String configKey() {
        return configKey;
    }

    public Group group() {
        return group;
    }

    /** Whether this family has both a Create and a vanilla recipe, and a config override. */
    public boolean toggleable() {
        return toggleable;
    }

    public String description() {
        return description;
    }

    /** Which of the two coarse overhaul switches governs this family. */
    public enum Group {
        INFRASTRUCTURE,
        ITEMS
    }
}
