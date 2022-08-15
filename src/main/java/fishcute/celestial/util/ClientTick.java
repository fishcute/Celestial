package fishcute.celestial.util;

import fishcute.celestial.sky.CelestialSky;
import net.minecraft.client.MinecraftClient;

public class ClientTick {
    static String previousDimension = "";

    public static boolean canUpdateStars = false;

    static void updateStars() {
        //TODO: make stars do stuff when game reload
        if (!previousDimension.equals(MinecraftClient.getInstance().world.getRegistryKey().getValue().getPath()))
            canUpdateStars = true;
        previousDimension = MinecraftClient.getInstance().world.getRegistryKey().getValue().getPath();
    }

    public static void reload() {
        CelestialSky.loadResources();
        ClientTick.canUpdateStars = true;
        Util.errorList.clear();
    }

    public static void worldTick() {
        updateStars();
    }
}
