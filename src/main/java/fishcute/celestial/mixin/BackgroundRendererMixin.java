package fishcute.celestial.mixin;

import com.mojang.blaze3d.systems.RenderSystem;
import fishcute.celestial.sky.CelestialSky;
import fishcute.celestial.util.Util;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.BackgroundRenderer;
import net.minecraft.client.render.Camera;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Map;

import static java.util.Map.entry;

@Mixin(BackgroundRenderer.class)
public class BackgroundRendererMixin {
    @ModifyVariable(method = "applyFog", at = @At("HEAD"), ordinal = 0, argsOnly = true)
    private static boolean applyFog(boolean thickFog) {
        if (CelestialSky.doesDimensionHaveCustomSky() && CelestialSky.getDimensionRenderInfo().environment.useSimpleFog())
            return CelestialSky.getDimensionRenderInfo().environment.hasThickFog;
        return thickFog;
    }
  @Inject(method = "applyFog", at = @At("RETURN"))
    private static void applyFog(Camera camera, BackgroundRenderer.FogType fogType, float viewDistance, boolean thickFog, float tickDelta, CallbackInfo info) {
      if (CelestialSky.doesDimensionHaveCustomSky() && !CelestialSky.getDimensionRenderInfo().environment.useSimpleFog()) {
          Map<String, String> toReplaceMap = new java.util.HashMap<>(Map.ofEntries(
                  entry("#viewDistance", viewDistance + ""),
                  entry("#minViewDistance", Math.min(viewDistance, 192.0F) + "")
          ));
          toReplaceMap.putAll(Util.getReplaceMapNormal());
          RenderSystem.setShaderFogStart((float) Util.solveEquation(CelestialSky.getDimensionRenderInfo().environment.fogStart, toReplaceMap));
          RenderSystem.setShaderFogEnd((float) Util.solveEquation(CelestialSky.getDimensionRenderInfo().environment.fogEnd, toReplaceMap));
      }
    }
}
