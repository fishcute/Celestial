package fishcute.celestial.util;

import fishcute.celestial.sky.CelestialSky;
import net.minecraft.client.Minecraft;

public class ClientTick {
    static String previousDimension = "";

    public static boolean canUpdateStars = false;

    static void updateStars() {
        //TODO: make stars do stuff when game reload
        if (!previousDimension.equals(Minecraft.getInstance().level.dimension().location().getPath()))
            canUpdateStars = true;
        previousDimension = Minecraft.getInstance().level.dimension().location().getPath();
    }

    public static void reload() {
        CelestialSky.loadResources();
        ClientTick.canUpdateStars = true;
        Util.errorList.clear();
    }

    public static void tick() {
        //System.out.println(Minecraft.getInstance().level == null);
        if (!(Minecraft.getInstance().level == null))
            worldTick();
    }

    public static void worldTick() {
        updateStars();
    }
}
