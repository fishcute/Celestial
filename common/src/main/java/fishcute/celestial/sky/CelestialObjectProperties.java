package fishcute.celestial.sky;

import com.google.gson.JsonObject;
import fishcute.celestial.util.Util;

public class CelestialObjectProperties {
    public final boolean hasMoonPhases;
    public final boolean isSolid;
    public final String red;
    public final String green;
    public final String blue;
    public final String alpha;

    public CelestialObjectProperties(boolean hasMoonPhases, boolean isSolid, String red, String green, String blue, String alpha) {
        this.hasMoonPhases = hasMoonPhases;
        this.isSolid = isSolid;
        this.red = red;
        this.green = green;
        this.blue = blue;
        this.alpha = alpha;
    }
    public static CelestialObjectProperties createCelestialObjectPropertiesFromJson(JsonObject o) {
        return new CelestialObjectProperties(
                Util.getOptionalBoolean(o, "has_moon_phases", false),
                Util.getOptionalBoolean(o, "is_solid", false),
                Util.getOptionalString(o, "red", "1"),
                Util.getOptionalString(o, "green", "1"),
                Util.getOptionalString(o, "blue", "1"),
                Util.getOptionalString(o, "alpha", "1")
        );
    }
}
