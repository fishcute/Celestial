package fishcute.celestial.util;

import fishcute.celestial.sky.CelestialSky;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.phys.Vec3;

import java.awt.*;

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
