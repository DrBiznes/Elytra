package dev.jamfemino.waystonewings.client;

import dev.jamfemino.waystonewings.WaystoneWings;
import dev.jamfemino.waystonewings.config.ClientConfig;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ModelEvent;

@EventBusSubscriber(modid = WaystoneWings.MOD_ID, value = Dist.CLIENT)
public final class ClientEvents {
    // Side-loaded models must use the standalone variant; NeoForge rejects any other variant
    // during ModelEvent.RegisterAdditional, which aborts the initial resource reload.
    private static final ModelResourceLocation WARP_CORE_MODEL = ModelResourceLocation.standalone(
            WaystoneWings.id("item/elytra_warp_core"));
    private static final ModelResourceLocation VANILLA_ELYTRA_MODEL = ModelResourceLocation.inventory(
            ResourceLocation.withDefaultNamespace("elytra"));

    private ClientEvents() {
    }

    @SubscribeEvent
    public static void registerAdditionalModels(ModelEvent.RegisterAdditional event) {
        event.register(WARP_CORE_MODEL);
    }

    @SubscribeEvent
    public static void replaceElytraModel(ModelEvent.ModifyBakingResult event) {
        if (ClientConfig.USE_WARP_CORE_TEXTURE.get()) {
            var warpCore = event.getModels().get(WARP_CORE_MODEL);
            if (warpCore != null) {
                event.getModels().put(VANILLA_ELYTRA_MODEL, warpCore);
            }
        }
    }
}
