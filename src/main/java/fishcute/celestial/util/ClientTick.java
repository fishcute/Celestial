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
        if (!(Minecraft.getInstance().level == null))
            worldTick();
    }

    public static void worldTick() {
        dimensionHasCustomSky = CelestialSky.dimensionSkyMap.containsKey(Minecraft.getInstance().level.dimension().location().getPath());
        updateStars();
        CelestialSky.updateVariableValues();
    }
}
