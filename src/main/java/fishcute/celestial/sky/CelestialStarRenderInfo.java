package fishcute.celestial.sky;

import com.google.gson.JsonObject;
import fishcute.celestial.util.Util;

import java.util.ArrayList;
import java.util.Collections;

public class CelestialStarRenderInfo {
    public final String degreesX;
    public final String degreesY;
    public final String degreesZ;
    public final String starBrightness;

    public final int starCount;

    public final double starMinSize;

    public final double starMaxSize;

    public final ArrayList<String> starColors;
    public CelestialStarRenderInfo(String degreesX, String degreesY, String degreesZ, String starBrightness, int starCount, double starMinSize, double starMaxSize, ArrayList<String> starColors) {
        this.degreesX = degreesX;
        this.degreesY = degreesY;
        this.degreesZ = degreesZ;
        this.starBrightness = starBrightness;
        this.starCount = starCount;
        this.starMinSize = starMinSize;
        this.starMaxSize = starMaxSize;
        this.starColors = starColors;
    }

    public static final CelestialStarRenderInfo DEFAULT = new CelestialStarRenderInfo(
            "skyAngle + 90",
            "0",
            "0",
            "((1.0 - dayLight) * rainGradient)m(1)",
            1500,
            0.2,
            0.25,
            new ArrayList<>(Collections.singleton("#ffffff"))
    );
    public static CelestialStarRenderInfo createStarRenderInfoFromJson(JsonObject o, String dimension) {
        if (o == null) {
            Util.warn("Failed to read \"sky.json\" for dimension \"" + dimension + "\" while loading star render info.");
            return DEFAULT;
        }
        if (!o.has("stars")) {
            Util.log("Skipped loading stars.");
            return DEFAULT;
        }
        JsonObject stars = o.getAsJsonObject("stars");
        JsonObject rotation = stars.getAsJsonObject("rotation");
        return new CelestialStarRenderInfo(
                Util.getOptionalString(rotation, "degrees_x", "skyAngle + 90"),
                Util.getOptionalString(rotation, "degrees_y", "-90"),
                Util.getOptionalString(rotation, "degrees_z", "0"),
                Util.getOptionalString(stars, "brightness", "((1.0 - dayLight) * rainGradient)m(1)"),
                Util.getOptionalInteger(stars, "count", 1500),
                Util.getOptionalDouble(stars, "min_size", 0.1),
                Util.getOptionalDouble(stars, "max_size", 0.15),
                Util.getOptionalStringArray(stars, "colors", new ArrayList<>(Collections.singleton("ffffff")))
        );
    }
}
