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
    @Inject(method = "getCloudColor", at = @At("RETURN"))
    private void getCloudColor(float f, CallbackInfoReturnable<Vec3> cir) {
        if (CelestialSky.doesDimensionHaveCustomSky())
            CelestialSky.getDimensionRenderInfo().environment.cloudColor.setInheritColor(new Color((int) cir.getReturnValue().x * 255, (int) cir.getReturnValue().y * 255, (int) cir.getReturnValue().z * 255));
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
}
