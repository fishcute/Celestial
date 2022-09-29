package fishcute.celestial.fabric;

import fishcute.celestial.CelestialClient;
import net.fabricmc.api.ModInitializer;

public class CelestialFabric implements ModInitializer {
    @Override
    public void onInitialize() {
        CelestialClient.init();
    }
}
