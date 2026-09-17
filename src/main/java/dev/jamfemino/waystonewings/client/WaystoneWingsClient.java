package dev.jamfemino.waystonewings.client;

import dev.jamfemino.waystonewings.WaystoneWings;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.client.gui.ConfigurationScreen;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;

/** Client-only entry point for integration with NeoForge's Mods screen. */
@Mod(value = WaystoneWings.MOD_ID, dist = Dist.CLIENT)
public final class WaystoneWingsClient {
    public WaystoneWingsClient(ModContainer container) {
        container.registerExtensionPoint(IConfigScreenFactory.class, ConfigurationScreen::new);
    }
}
