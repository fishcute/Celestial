package fishcute.celestial;

import fishcute.celestial.util.ClientTick;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;

@Environment(EnvType.CLIENT)
public class CelestialClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        ClientTickEvents.START_CLIENT_TICK.register((client) -> ClientTick.tick());
        ClientTickEvents.START_WORLD_TICK.register((client) -> ClientTick.worldTick());
    }
}
