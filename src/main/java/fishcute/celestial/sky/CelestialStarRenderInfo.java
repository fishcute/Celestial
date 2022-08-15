package fishcute.celestial.sky;

import com.google.gson.JsonObject;
import fishcute.celestial.util.Util;

import java.util.ArrayList;
import java.util.Collections;

public class CelestialStarRenderInfo {
    public final String degreesX;
    public final String degreesY;
    public final String degreesZ;
    public final String baseDegreesX;
    public final String baseDegreesY;
    public final String baseDegreesZ;

    public final String starBrightness;

    public final int starCount;

    public final double starMinSize;

    public final double starMaxSize;

    public final ArrayList<String> starColors;
    public CelestialStarRenderInfo(String degreesX, String degreesY, String degreesZ, String baseDegreesX, String baseDegreesY, String baseDegreesZ, String starBrightness, int starCount, double starMinSize, double starMaxSize, ArrayList<String> starColors) {
        this.degreesX = degreesX;
        this.degreesY = degreesY;
        this.degreesZ = degreesZ;
        this.starBrightness = starBrightness;
        this.starCount = starCount;
        this.starMinSize = starMinSize;
        this.starMaxSize = starMaxSize;
        this.starColors = starColors;
        this.baseDegreesX = baseDegreesX;
        this.baseDegreesY = baseDegreesY;
        this.baseDegreesZ = baseDegreesZ;
    }

    public static final CelestialStarRenderInfo DEFAULT = new CelestialStarRenderInfo(
            "#skyAngle + 90",
            "0",
            "0",
<<<<<<< HEAD
            "-90",
            "0",
            "-90",
            "#starAlpha",
=======
            "((1.0 - #dayLight) * #rainGradient)m(1)",
>>>>>>> 330a7baf350319b9420153edb8ff8034260dc783
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
                Util.getOptionalString(rotation, "degrees_x", "#skyAngle + 90"),
                Util.getOptionalString(rotation, "degrees_y", "-90"),
                Util.getOptionalString(rotation, "degrees_z", "0"),
<<<<<<< HEAD
                Util.getOptionalString(rotation, "base_degrees_x", "-90"),
                Util.getOptionalString(rotation, "base_degrees_y", "0"),
                Util.getOptionalString(rotation, "base_degrees_z", "-90"),
                Util.getOptionalString(stars, "brightness", "#starAlpha"),
=======
                Util.getOptionalString(stars, "brightness", "((1.0 - #dayLight) * #rainGradient)m(1)"),
>>>>>>> 330a7baf350319b9420153edb8ff8034260dc783
                Util.getOptionalInteger(stars, "count", 1500),
                Util.getOptionalDouble(stars, "min_size", 0.1),
                Util.getOptionalDouble(stars, "max_size", 0.15),
                Util.getOptionalStringArray(stars, "colors", new ArrayList<>(Collections.singleton("ffffff")))
        );
    }
}
