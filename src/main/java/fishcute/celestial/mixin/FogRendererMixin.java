package fishcute.celestial.mixin;

import com.mojang.blaze3d.systems.RenderSystem;
import fishcute.celestial.sky.CelestialSky;
import fishcute.celestial.util.Util;
import net.minecraft.client.renderer.ActiveRenderInfo;
import net.minecraft.client.renderer.FogRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.HashMap;
import java.util.Map;

@Mixin(FogRenderer.class)
public class FogRendererMixin {
    @ModifyVariable(method = "setupFog(Lnet/minecraft/client/renderer/ActiveRenderInfo;Lnet/minecraft/client/renderer/FogRenderer$FogType;FZ)V", at = @At("HEAD"), ordinal = 0, argsOnly = true)
    private static boolean setupFog(boolean thickFog) {
        if (CelestialSky.doesDimensionHaveCustomSky() && CelestialSky.getDimensionRenderInfo().environment.useSimpleFog())
            return CelestialSky.getDimensionRenderInfo().environment.hasThickFog;
        return thickFog;
    }
    @Inject(method = "setupFog(Lnet/minecraft/client/renderer/ActiveRenderInfo;Lnet/minecraft/client/renderer/FogRenderer$FogType;FZ)V", at = @At("RETURN"))
    private static void setupFog(ActiveRenderInfo p_228372_0_, FogRenderer.FogType p_228372_1_, float p_228372_2_, boolean p_228372_3_, CallbackInfo ci) {
        if (CelestialSky.doesDimensionHaveCustomSky() && !CelestialSky.getDimensionRenderInfo().environment.useSimpleFog()) {

            Map<String, String> toReplaceMap = new HashMap<>();
            toReplaceMap.put("#viewDistance", p_228372_2_ + "");
            toReplaceMap.put("#minViewDistance", Math.min(p_228372_2_, 192.0F) + "");

            toReplaceMap.putAll(Util.getReplaceMapNormal());
            RenderSystem.fogStart((float) Util.solveEquation(CelestialSky.getDimensionRenderInfo().environment.fogStart, toReplaceMap));
            RenderSystem.fogEnd((float) Util.solveEquation(CelestialSky.getDimensionRenderInfo().environment.fogEnd, toReplaceMap));
        }
    }
}
