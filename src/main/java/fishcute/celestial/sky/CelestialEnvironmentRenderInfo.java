package fishcute.celestial.sky;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import fishcute.celestial.util.ColorEntry;
import fishcute.celestial.util.Util;
import org.apache.commons.lang3.tuple.MutablePair;
import org.apache.commons.lang3.tuple.Pair;

import java.awt.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

public class CelestialEnvironmentRenderInfo {
    public final boolean hasThickFog;
    public final ColorEntry fogColor;
    public final ColorEntry skyColor;
    public final String cloudHeight;
    public final ColorEntry cloudColor;
    public final String fogStart;
    public final String fogEnd;
    public final ColorEntry twilightColor;
    public final String twilightAlpha;

    public final String voidCullingLevel;

    public CelestialEnvironmentRenderInfo(boolean hasThickFog, ColorEntry fogColor, ColorEntry skyColor, String cloudHeight, ColorEntry cloudColor, String fogStart, String fogEnd, ColorEntry twilightColor, String twilightAlpha, String voidCullingLevel) {
        this.hasThickFog = hasThickFog;
        this.fogColor = fogColor;
        this.skyColor = skyColor;
        this.cloudHeight = cloudHeight;
        this.cloudColor = cloudColor;
        this.fogStart = fogStart;
        this.fogEnd = fogEnd;
        this.twilightColor = twilightColor;
        this.twilightAlpha = twilightAlpha;
        this.voidCullingLevel = voidCullingLevel;
    }

    public final static ColorEntry DEFAULT_COLOR_SKY = new ColorEntry(Util.decodeColor("#78a7ff"));

    public final static ColorEntry DEFAULT_COLOR_FOG = new ColorEntry(Util.decodeColor("#c0d8ff"));

    public final static ColorEntry DEFAULT_COLOR_CLOUD = new ColorEntry(Util.decodeColor("#ffffff"));

    public final static ColorEntry DEFAULT_COLOR_TWILIGHT = new ColorEntry(Util.decodeColor("#b23333"));

    public static final CelestialEnvironmentRenderInfo DEFAULT = new CelestialEnvironmentRenderInfo(
            false,
            DEFAULT_COLOR_FOG,
            DEFAULT_COLOR_SKY,
            "128",
            DEFAULT_COLOR_CLOUD,
            "-1",
            "-1",
            DEFAULT_COLOR_TWILIGHT,
            "1",
            "0"
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
                ColorEntry.createColorEntry(environment, "fog_color", DEFAULT_COLOR_FOG),
                ColorEntry.createColorEntry(environment, "sky_color", DEFAULT_COLOR_SKY),
                Util.getOptionalString(clouds, "height", "128"),
                ColorEntry.createColorEntry(clouds, "color", DEFAULT_COLOR_CLOUD),
                Util.getOptionalString(fog, "fog_start", "-1"),
                Util.getOptionalString(fog, "fog_end", "-1"),
                ColorEntry.createColorEntry(environment, "twilight_color", DEFAULT_COLOR_TWILIGHT),
                Util.getOptionalString(environment, "twilight_alpha", "1"),
                Util.getOptionalString(environment, "void_culling_level", "0")
        );
    }

    public boolean useSimpleFog() {
        return this.fogStart.equals("-1") || this.fogEnd.equals("-1");
    }

    public void updateColorEntries() {
        skyColor.tick();
        fogColor.tick();
        cloudColor.tick();
        twilightColor.tick();
    }
}
