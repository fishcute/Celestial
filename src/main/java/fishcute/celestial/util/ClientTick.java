package fishcute.celestial.util;

import com.mojang.blaze3d.systems.RenderSystem;
import fishcute.celestial.CelestialClient;
import fishcute.celestial.sky.CelestialSky;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.util.CubicSampler;
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

        if (CelestialSky.doesDimensionHaveCustomSky()) {
            CelestialSky.getDimensionRenderInfo().environment.skyColor.setInheritColor(Util.getSkyColor());
            CelestialSky.getDimensionRenderInfo().environment.fogColor.setInheritColor(Util.getFogColor());
        }

        while (CelestialClient.reloadSky.consumeClick()) {
            CelestialSky.loadResources();
            if (!CelestialClient.hasShownWarning) {
                CelestialClient.hasShownWarning = true;
                Util.sendMessage(ChatFormatting.RED + "Note: This will not reload textures. Use F3-T to reload textures.", false);
            }
        }
    }
}
