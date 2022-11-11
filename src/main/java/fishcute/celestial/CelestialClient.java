package fishcute.celestial;

import fishcute.celestial.util.Util;
import net.fabricmc.api.ClientModInitializer;

public class CelestialClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        Util.log("Loading Celestial");
    }
}