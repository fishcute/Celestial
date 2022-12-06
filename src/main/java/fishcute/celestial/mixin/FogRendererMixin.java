package fishcute.celestial.mixin;

import com.mojang.blaze3d.systems.RenderSystem;
import fishcute.celestial.sky.CelestialSky;
import fishcute.celestial.util.Util;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.FogRenderer;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.scores.criteria.ObjectiveCriteria;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.awt.*;
import java.util.Map;

import static java.util.Map.entry;

@Mixin(FogRenderer.class)
public class FogRendererMixin {
    @ModifyVariable(method = "setupFog", at = @At("HEAD"), ordinal = 0, argsOnly = true)
    private static boolean setupFog(boolean thickFog) {
        if (CelestialSky.doesDimensionHaveCustomSky() && CelestialSky.getDimensionRenderInfo().environment.useSimpleFog())
            return CelestialSky.getDimensionRenderInfo().environment.hasThickFog;
        return thickFog;
    }
    @Inject(method = "setupFog", at = @At("RETURN"))
    private static void setupFog(Camera camera, FogRenderer.FogMode fogType, float viewDistance, boolean thickFog, float tickDelta, CallbackInfo info) {
        if (CelestialSky.doesDimensionHaveCustomSky() && !CelestialSky.getDimensionRenderInfo().environment.useSimpleFog()) {
            RenderSystem.setShaderFogStart((float) Util.solveEquation(CelestialSky.getDimensionRenderInfo().environment.fogStart, Util.getReplaceMapNormal()));
            RenderSystem.setShaderFogEnd((float) Util.solveEquation(CelestialSky.getDimensionRenderInfo().environment.fogEnd, Util.getReplaceMapNormal()));
        }
    }


    @Shadow
    private static float fogRed;

    @Shadow
    private static float fogGreen;

    @Shadow
    private static float fogBlue;

    @Inject(method = "levelFogColor", at = @At("RETURN"))
    private static void setupColor(CallbackInfo ci) {
        if (CelestialSky.doesDimensionHaveCustomSky() && CelestialSky.getDimensionRenderInfo().environment.fogColor.ignoreSkyEffects) {
            fogRed = CelestialSky.getDimensionRenderInfo().environment.fogColor.storedColor.getRed() / 255.0F;
            fogGreen = CelestialSky.getDimensionRenderInfo().environment.fogColor.storedColor.getGreen() / 255.0F;
            fogBlue = CelestialSky.getDimensionRenderInfo().environment.fogColor.storedColor.getBlue() / 255.0F;
            RenderSystem.clearColor(fogRed, fogGreen, fogBlue, 0);
        }
    }
}
