package fishcute.celestial.sky;

import net.minecraft.util.ResourceLocation;

import java.util.ArrayList;

public class CelestialRenderInfo {
    public final ArrayList<CelestialObject> skyObjects;

    public final CelestialEnvironmentRenderInfo environment;

    public final RenderType renderType;

    public final ResourceLocation skyboxTexture;
    public CelestialRenderInfo(ArrayList<CelestialObject> skyObjects, CelestialEnvironmentRenderInfo environment, String renderType, String skyboxTexture) {
        this.skyObjects = skyObjects;
        this.environment = environment;
        this.renderType = value(renderType);
        this.skyboxTexture = new ResourceLocation(skyboxTexture);

    }

    public enum RenderType {
        NORMAL,
        SKYBOX
    }
    public static RenderType value(String i) {
        switch (i) {
            case "skybox":
                return RenderType.SKYBOX;
            default:
                return RenderType.NORMAL;
        }
    }
}
