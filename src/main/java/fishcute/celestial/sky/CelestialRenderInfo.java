package fishcute.celestial.sky;

import net.minecraft.util.Identifier;

import java.util.ArrayList;

public class CelestialRenderInfo {
    public final ArrayList<CelestialObject> skyObjects;

    public final CelestialStarRenderInfo stars;

    public final CelestialEnvironmentRenderInfo environment;

    public final RenderType renderType;

    public final Identifier skyboxTexture;
    public CelestialRenderInfo(ArrayList<CelestialObject> skyObjects, CelestialStarRenderInfo stars, CelestialEnvironmentRenderInfo environment, String renderType, String skyboxTexture) {
        this.skyObjects = skyObjects;
        this.stars = stars;
        this.environment = environment;
        this.renderType = value(renderType);
        this.skyboxTexture = new Identifier(skyboxTexture);

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
