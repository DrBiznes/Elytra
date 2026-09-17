package dev.jamfemino.waystonewings.config;

import net.neoforged.neoforge.common.ModConfigSpec;

public final class ClientConfig {
    private static final ModConfigSpec.Builder BUILDER = new ModConfigSpec.Builder();

    public static final ModConfigSpec.BooleanValue RENAME_ELYTRA;
    public static final ModConfigSpec.BooleanValue SHOW_PURPOSE_TOOLTIP;
    public static final ModConfigSpec.BooleanValue USE_WARP_CORE_TEXTURE;

    public static final ModConfigSpec SPEC;

    static {
        BUILDER.comment("The client-side name used for the vanilla Elytra.")
                .translation("config.waystone_wings.section.identity")
                .push("identity");
        RENAME_ELYTRA = BUILDER.comment("Display the vanilla Elytra as 'Elytra Warp Core'.")
                .translation("config.waystone_wings.renameElytra")
                .define("renameElytra", true);
        BUILDER.pop();

        BUILDER.comment("Extra inventory guidance for the Elytra Warp Core.")
                .translation("config.waystone_wings.section.tooltips")
                .push("tooltips");
        SHOW_PURPOSE_TOOLTIP = BUILDER.comment("Add flight restriction and Waystones-purpose lines to the Elytra tooltip.")
                .translation("config.waystone_wings.showPurposeTooltip")
                .define("showPurposeTooltip", true);
        BUILDER.pop();

        BUILDER.comment("Inventory model and texture presentation.")
                .translation("config.waystone_wings.section.appearance")
                .push("appearance");
        USE_WARP_CORE_TEXTURE = BUILDER.comment("Use the Waystone Wings inventory model for the Elytra.")
                .translation("config.waystone_wings.useWarpCoreTexture")
                .gameRestart()
                .define("useWarpCoreTexture", true);
        BUILDER.pop();

        SPEC = BUILDER.build();
    }

    private ClientConfig() {
    }
}
