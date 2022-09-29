package fishcute.celestial.forge;

import fishcute.celestial.CelestialClient;
import net.minecraftforge.fml.common.Mod;

@Mod(CelestialClient.MOD_ID)
public class CelestialForge {
    public CelestialForge() {
        // Submit our event bus to let architectury register our content on the right time

        /*IEventBus eventBus = FMLJavaModLoadingContext.get().getModEventBus();

        eventBus.registerModEventBus(ExampleMod.MOD_ID, FMLJavaModLoadingContext.get().getModEventBus());*/
        CelestialClient.init();
    }
}
