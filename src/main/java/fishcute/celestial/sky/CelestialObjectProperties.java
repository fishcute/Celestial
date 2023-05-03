package fishcute.celestial.sky;

import com.google.gson.JsonObject;
import fishcute.celestial.util.ColorEntry;
import fishcute.celestial.util.Util;

public class CelestialObjectProperties {
    public final boolean hasMoonPhases;
    public final String moonPhase;
    public final boolean isSolid;
    private final String red;
    private final String green;
    private final String blue;
    public final String alpha;
    public final boolean ignoreFog;
    public final ColorEntry color;

    public float getRed() {
        if (this.color == null)
            return (float) Util.solveEquation(red, Util.getReplaceMapNormal());
        return (color.storedColor.getRed() / 255.0F) * (float) Util.solveEquation(red, Util.getReplaceMapNormal());
    }
    public float getGreen() {
        if (this.color == null)
            return (float) Util.solveEquation(green, Util.getReplaceMapNormal());
        return (color.storedColor.getGreen() / 255.0F) * (float) Util.solveEquation(green, Util.getReplaceMapNormal());
    }
    public float getBlue() {
        if (this.color == null)
            return (float) Util.solveEquation(blue, Util.getReplaceMapNormal());
        return (color.storedColor.getBlue() / 255.0F) * (float) Util.solveEquation(blue, Util.getReplaceMapNormal());
    }

    public CelestialObjectProperties(boolean hasMoonPhases, String moonPhase, boolean isSolid, String red, String green, String blue, String alpha, boolean ignoreFog, ColorEntry color) {
        this.hasMoonPhases = hasMoonPhases;
        this.moonPhase = moonPhase;
        this.isSolid = isSolid;
        this.red = red;
        this.green = green;
        this.blue = blue;
        this.alpha = alpha;
        this.ignoreFog = ignoreFog;
        this.color = color;
    }
    public static CelestialObjectProperties createCelestialObjectPropertiesFromJson(JsonObject o) {
        return new CelestialObjectProperties(
                Util.getOptionalBoolean(o, "has_moon_phases", false),
                Util.getOptionalString(o, "moon_phase", "#moonPhase"),
                Util.getOptionalBoolean(o, "is_solid", false),
                Util.getOptionalString(o, "red", "1"),
                Util.getOptionalString(o, "green", "1"),
                Util.getOptionalString(o, "blue", "1"),
                Util.getOptionalString(o, "alpha", "1"),
                Util.getOptionalBoolean(o, "ignore_fog", false),
                ColorEntry.createColorEntry(o, "color", null, false)
        );
    }
}
