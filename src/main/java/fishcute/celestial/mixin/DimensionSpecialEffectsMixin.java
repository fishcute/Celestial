package fishcute.celestial.mixin;

import fishcute.celestial.sky.CelestialSky;
import fishcute.celestial.util.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.DimensionSpecialEffects;
import net.minecraft.util.Mth;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.HashMap;
import java.util.Map;

import static java.util.Map.entry;

@Mixin(DimensionSpecialEffects.class)
public class DimensionSpecialEffectsMixin {
    private final float[] rgba = new float[4];

    @Inject(method = "getCloudHeight", at = @At("RETURN"), cancellable = true)
    private void getCloudsHeight(CallbackInfoReturnable<Float> info) {
        if (Minecraft.getInstance().level != null &&
                CelestialSky.doesDimensionHaveCustomSky())
            info.setReturnValue((float) CelestialSky.getDimensionRenderInfo().environment.cloudHeight);
    }
    @Inject(method = "getSunriseColor", at = @At("RETURN"), cancellable = true)
    private void getFogColorOverride(float skyAngle, float tickDelta, CallbackInfoReturnable<float[]> info) {
        if (CelestialSky.doesDimensionHaveCustomSky()) {
            float g = Mth.cos(skyAngle * 6.2831855F) - 0.0F;
            if (g >= -0.4F && g <= 0.4F) {
                float i = (g + 0.0F) / 0.4F * 0.5F + 0.5F;
                float j = 1.0F - (1.0F - Mth.sin(i * 3.1415927F)) * 0.99F;
                j *= j;
                this.rgba[0] = i * 0.3F + (CelestialSky.getDimensionRenderInfo().environment.twilightColor.getRed() / 255.0F);
                this.rgba[1] = i * i * 0.7F + (CelestialSky.getDimensionRenderInfo().environment.twilightColor.getGreen() / 255.0F);
                this.rgba[2] = i * i * 0.0F + (CelestialSky.getDimensionRenderInfo().environment.twilightColor.getBlue() / 255.0F);

                Map<String, String> toReplaceMap = Util.getReplaceMapAdd(Map.ofEntries(
                        entry("#twilightAlpha", j + "")
                ));

                this.rgba[3] = Math.min(j, (float) Util.solveEquation(CelestialSky.getDimensionRenderInfo().environment.twilightAlpha, toReplaceMap));
                info.setReturnValue(this.rgba);
            } else {
                info.setReturnValue(null);
            }
        }
    }
}
