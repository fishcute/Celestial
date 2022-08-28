package fishcute.celestial.util;

import fishcute.celestial.sky.CelestialSky;
import net.minecraft.client.Minecraft;

public class ClientTick {

    public static boolean dimensionHasCustomSky = false;

    static void updateStars() {
    }

    public static void reload() {
        CelestialSky.loadResources();
        Util.errorList.clear();
    }

    public static void tick() {
        //System.out.println(Minecraft.getInstance().level == null);
        if (!(Minecraft.getInstance().world == null))
            worldTick();
    }

    public static void worldTick() {
        updateStars();
        dimensionHasCustomSky = CelestialSky.dimensionSkyMap.containsKey(Minecraft.getInstance().world.getDimensionKey().getLocation().getPath());
    }
}
