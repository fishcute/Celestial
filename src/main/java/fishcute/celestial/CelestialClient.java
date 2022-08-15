package fishcute.celestial;

import fishcute.celestial.util.ClientTick;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import org.lwjgl.glfw.GLFW;

@Environment(EnvType.CLIENT)
public class CelestialClient implements ClientModInitializer {
    private static KeyBinding reloadSky;
    @Override
    public void onInitializeClient() {
        ClientTickEvents.START_WORLD_TICK.register((client) -> ClientTick.worldTick());

        ClientTickEvents.END_WORLD_TICK.register((client) -> {
            while (reloadSky.wasPressed())
                ClientTick.reload();
        });

        reloadSky = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.reload_sky",
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_F10,
                "key.categories.misc"
        ));
    }
}
