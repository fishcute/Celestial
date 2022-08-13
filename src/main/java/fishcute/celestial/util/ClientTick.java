package fishcute.celestial.util;

import fishcute.celestial.sky.CelestialSky;
import net.minecraft.client.MinecraftClient;
import net.minecraft.resource.ResourceReload;

public class ClientTick {

    // Mechanism for detecting resource reloads (Probably could be better, but it works)
    public static ResourceReload reload;

    static String previousDimension = "";

    public static boolean canUpdateStars = false;

    static void tickReload() {
        if (reload.isComplete()) {
            CelestialSky.loadResources();
            reload = null;
            if (MinecraftClient.getInstance().world != null && CelestialSky.doesDimensionHaveCustomSky())
                canUpdateStars = true;
            Util.errorList.clear();
        }
    }

    static void updateStars() {
        if (!previousDimension.equals(MinecraftClient.getInstance().world.getRegistryKey().getValue().getPath()))
            canUpdateStars = true;
        previousDimension = MinecraftClient.getInstance().world.getRegistryKey().getValue().getPath();
    }
    public static void tick() {
        if (reload!=null)
            tickReload();
    }

    public static void worldTick() {
        updateStars();
    }
}
