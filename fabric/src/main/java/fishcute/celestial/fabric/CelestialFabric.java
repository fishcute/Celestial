package fishcute.celestial.fabric;

import com.mojang.blaze3d.platform.InputConstants;
import fishcute.celestial.CelestialClient;
import fishcute.celestial.util.ClientTick;
import net.fabricmc.api.ClientModInitializer;
import net.minecraft.client.KeyMapping;
import org.lwjgl.glfw.GLFW;

public class CelestialFabric implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        CelestialClient.onInitializeClient();
    }
}