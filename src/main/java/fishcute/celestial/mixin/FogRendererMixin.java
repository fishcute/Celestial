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

import java.util.HashMap;
import java.util.Map;

import static java.util.Map.entry;

@Mixin(FogRenderer.class)
public class FogRendererMixin {
    @ModifyVariable(method = "setupFog(Lnet/minecraft/client/Camera;Lnet/minecraft/client/renderer/FogRenderer$FogMode;FZ)V", at = @At("HEAD"), ordinal = 0, argsOnly = true)
    private static boolean setupFog(boolean thickFog) {
        if (CelestialSky.doesDimensionHaveCustomSky() && CelestialSky.getDimensionRenderInfo().environment.useSimpleFog())
            return CelestialSky.getDimensionRenderInfo().environment.hasThickFog;
        return thickFog;
    }
  @Inject(method = "setupFog(Lnet/minecraft/client/Camera;Lnet/minecraft/client/renderer/FogRenderer$FogMode;FZ)V", at = @At("RETURN"))
    private static void setupFog(Camera p_109025_, FogRenderer.FogMode p_109026_, float p_109027_, boolean p_109028_, CallbackInfo ci) {
      if (CelestialSky.doesDimensionHaveCustomSky() && !CelestialSky.getDimensionRenderInfo().environment.useSimpleFog()) {
          Map<String, String> toReplaceMap = new java.util.HashMap<>(Map.ofEntries(
                  entry("#viewDistance", p_109027_ + ""),
                  entry("#minViewDistance", Math.min(p_109027_, 192.0F) + "")
          ));
          toReplaceMap.putAll(Util.getReplaceMapNormal());
          RenderSystem.setShaderFogStart((float) Util.solveEquation(CelestialSky.getDimensionRenderInfo().environment.fogStart, toReplaceMap));
          RenderSystem.setShaderFogEnd((float) Util.solveEquation(CelestialSky.getDimensionRenderInfo().environment.fogEnd, toReplaceMap));
      }
    }
}
