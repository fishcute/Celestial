package fishcute.celestial.sky;

import com.google.gson.JsonObject;
import fishcute.celestial.util.Util;

import java.awt.*;

public class CelestialEnvironmentRenderInfo {
    public final boolean hasThickFog;
    public final int fogColor;
    public final int skyColor;
    public final int cloudHeight;
    public final Color cloudColor;
    public final String fogStart;
    public final String fogEnd;
    public final String skyDarkness;
    public final Color twilightColor;
    public final String twilightAlpha;

    public CelestialEnvironmentRenderInfo(boolean hasThickFog, String fogColor, String skyColor, int cloudHeight, String cloudColor, String fogStart, String fogEnd, String skyDarkness, String twilightColor, String twilightAlpha) {
        this.hasThickFog = hasThickFog;
        this.fogColor = Util.getDecimal(fogColor);
        this.skyColor = Util.getDecimal(skyColor);
        this.cloudHeight = cloudHeight;
        this.cloudColor = Color.decode(cloudColor.startsWith("#") ? cloudColor : "#" + cloudColor);
        this.fogStart = fogStart;
        this.fogEnd = fogEnd;
        this.skyDarkness = skyDarkness;
        this.twilightColor = Color.decode(twilightColor.startsWith("#") ? twilightColor : "#" + twilightColor);
        this.twilightAlpha = twilightAlpha;
    }

    public static final CelestialEnvironmentRenderInfo DEFAULT = new CelestialEnvironmentRenderInfo(
            false,
            "c0d8ff",
            "78a7ff",
            128,
            "ffffff",
            "-1",
            "-1",
            "0",
            "b23333",
            "1"
    );
    public static CelestialEnvironmentRenderInfo createEnvironmentRenderInfoFromJson(JsonObject o, String dimension) {
        if (o == null) {
            Util.warn("Failed to read \"sky.json\" for dimension \"" + dimension + "\" while loading environment render info.");
            return DEFAULT;
        }
        if (!o.has("environment")) {
            Util.log("Skipped loading environment.");
            return DEFAULT;
        }
        JsonObject environment = o.getAsJsonObject("environment");
        JsonObject fog = environment.getAsJsonObject("fog");
        JsonObject clouds = environment.getAsJsonObject("clouds");
        return new CelestialEnvironmentRenderInfo(
                Util.getOptionalBoolean(fog, "has_thick_fog", false),
                Util.getOptionalString(environment, "fog_color", "bfd8ff"),
                Util.getOptionalString(environment, "sky_color", "78a7ff"),
                Util.getOptionalInteger(clouds, "height", 128),
                Util.getOptionalString(clouds, "color", "ffffff"),
                Util.getOptionalString(fog, "fog_start", "-1"),
                Util.getOptionalString(fog, "fog_end", "-1"),
                Util.getOptionalString(environment, "sky_darkness", "0"),
                Util.getOptionalString(environment, "twilight_color", "b23333"),
                Util.getOptionalString(environment, "twilight_alpha", "1")
        );
    }

    public boolean useSimpleFog() {
        return this.fogStart.equals("-1") || this.fogEnd.equals("-1");
    }
}
