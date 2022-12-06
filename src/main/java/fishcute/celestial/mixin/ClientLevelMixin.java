package fishcute.celestial.mixin;

import fishcute.celestial.sky.CelestialSky;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.awt.*;

@Mixin(ClientLevel.class)
public class ClientLevelMixin {
    @Inject(method = "getCloudColor", at = @At("RETURN"), cancellable = true)
    private void getCloudColor(float f, CallbackInfoReturnable<Vec3> info) {
        if (CelestialSky.doesDimensionHaveCustomSky()) {
            if (CelestialSky.getDimensionRenderInfo().environment.cloudColor.ignoreSkyEffects) {
                CelestialSky.getDimensionRenderInfo().environment.cloudColor.setInheritColor(new Color(255,  255, 255));
                info.setReturnValue(new Vec3(CelestialSky.getDimensionRenderInfo().environment.cloudColor.storedColor.getRed() / 255.0F, CelestialSky.getDimensionRenderInfo().environment.cloudColor.storedColor.getGreen() / 255.0F, CelestialSky.getDimensionRenderInfo().environment.cloudColor.storedColor.getBlue() / 255.0F));
            }
            else
                CelestialSky.getDimensionRenderInfo().environment.cloudColor.setInheritColor(new Color(255, 255, 255));
        }
    }
    @ModifyVariable(method = "getCloudColor", at = @At("STORE"), ordinal = 3)
    private float getRed(float h) {
        if (CelestialSky.doesDimensionHaveCustomSky())
            return (CelestialSky.getDimensionRenderInfo().environment.cloudColor.storedColor.getRed() / 255f) * h;
        return h;
    }
    @ModifyVariable(method = "getCloudColor", at = @At("STORE"), ordinal = 4)
    private float getGreen(float i) {
        if (CelestialSky.doesDimensionHaveCustomSky())
            return (CelestialSky.getDimensionRenderInfo().environment.cloudColor.storedColor.getGreen() / 255f) * i;
        return i;
    }
    @ModifyVariable(method = "getCloudColor", at = @At("STORE"), ordinal = 5)
    private float getBlue(float j) {
        if (CelestialSky.doesDimensionHaveCustomSky())
            return (CelestialSky.getDimensionRenderInfo().environment.cloudColor.storedColor.getBlue() / 255f) * j;
        return j;
    }
    @Inject(method = "getSkyColor", at = @At("RETURN"), cancellable = true)
    private void getSkyColor(Vec3 vec3, float f, CallbackInfoReturnable<Vec3> info) {
        if (CelestialSky.doesDimensionHaveCustomSky() && CelestialSky.getDimensionRenderInfo().environment.skyColor.ignoreSkyEffects) {
            info.setReturnValue(new Vec3(((float) CelestialSky.getDimensionRenderInfo().environment.skyColor.storedColor.getRed()) / 255,
                    ((float) CelestialSky.getDimensionRenderInfo().environment.skyColor.storedColor.getGreen()) / 255,
                    ((float) CelestialSky.getDimensionRenderInfo().environment.skyColor.storedColor.getBlue()) / 255));
        }
    }
}
