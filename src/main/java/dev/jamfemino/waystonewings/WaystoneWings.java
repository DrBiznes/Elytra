package dev.jamfemino.waystonewings;

import com.mojang.logging.LogUtils;
import dev.jamfemino.waystonewings.config.ClientConfig;
import dev.jamfemino.waystonewings.config.CommonConfig;
import dev.jamfemino.waystonewings.condition.ModConditions;
import dev.jamfemino.waystonewings.event.CommonEvents;
import dev.jamfemino.waystonewings.registry.ModItems;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.neoforge.common.NeoForge;
import org.slf4j.Logger;

@Mod(WaystoneWings.MOD_ID)
public final class WaystoneWings {
    public static final String MOD_ID = "waystone_wings";
    public static final Logger LOGGER = LogUtils.getLogger();

    public WaystoneWings(IEventBus modBus, ModContainer container) {
        ModItems.ITEMS.register(modBus);
        ModConditions.CONDITION_CODECS.register(modBus);

        container.registerConfig(ModConfig.Type.COMMON, CommonConfig.SPEC);
        container.registerConfig(ModConfig.Type.CLIENT, ClientConfig.SPEC);

        NeoForge.EVENT_BUS.addListener(CommonEvents::onPlayerTick);
        NeoForge.EVENT_BUS.addListener(CommonEvents::onItemTooltip);
    }

    public static ResourceLocation id(String path) {
        return ResourceLocation.fromNamespaceAndPath(MOD_ID, path);
    }
}
