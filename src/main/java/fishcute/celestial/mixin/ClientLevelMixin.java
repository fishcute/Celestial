package fishcute.celestial.mixin;

import fishcute.celestial.sky.CelestialSky;
import net.minecraft.client.multiplayer.ClientLevel;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(ClientLevel.class)
public class ClientLevelMixin {
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
