package fishcute.celestial.mixin;

import com.mojang.blaze3d.systems.RenderSystem;
import fishcute.celestial.sky.CelestialSky;
import fishcute.celestial.util.Util;
import net.minecraft.client.Camera;
import net.minecraft.client.renderer.FogRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

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
            Map<String, Double> toReplaceMapBefore = new java.util.HashMap<>(Map.ofEntries(
                    entry("#viewDistance", (double) viewDistance),
                    entry("#minViewDistance", (double) Math.min(viewDistance, 192.0F))
            ));

            Map<String, Util.DynamicValue> toReplaceMap = Util.getReplaceMapAdd(toReplaceMapBefore);

            RenderSystem.setShaderFogStart((float) Util.solveEquation(CelestialSky.getDimensionRenderInfo().environment.fogStart, toReplaceMap));
            RenderSystem.setShaderFogEnd((float) Util.solveEquation(CelestialSky.getDimensionRenderInfo().environment.fogEnd, toReplaceMap));
        }
    }
}
