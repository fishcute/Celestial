package fishcute.celestial;

import com.mojang.blaze3d.platform.InputConstants;
import fishcute.celestial.util.Util;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.KeyMapping;

public class CelestialClient implements ClientModInitializer {
    public static KeyMapping reloadSky;

    public static boolean hasShownWarning = false;
    @Override
    public void onInitializeClient() {

        Util.log("Loading Celestial");

        reloadSky = KeyBindingHelper.registerKeyBinding(new KeyMapping(
                "key.reload_sky",
                InputConstants.KEY_F10,
                "key.categories.misc"
        ));
    }
}