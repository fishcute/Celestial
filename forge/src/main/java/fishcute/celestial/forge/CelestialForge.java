package fishcute.celestial.forge;

import fishcute.celestial.CelestialClient;
import net.minecraftforge.fml.common.Mod;

@Mod(CelestialClient.MOD_ID)
public class CelestialForge {
    public CelestialForge() {
        CelestialClient.onInitializeClient();
    }
}