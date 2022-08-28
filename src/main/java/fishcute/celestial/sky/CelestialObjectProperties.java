package fishcute.celestial.sky;

import com.google.gson.JsonObject;
import fishcute.celestial.util.Util;

public class CelestialObjectProperties {
    public final boolean hasMoonPhases;
    public final String moonPhase;
    public final boolean isSolid;
    public final String red;
    public final String green;
    public final String blue;
    public final String alpha;
    public final boolean ignoreFog;

    public CelestialObjectProperties(boolean hasMoonPhases, String moonPhase, boolean isSolid, String red, String green, String blue, String alpha, boolean ignoreFog) {
        this.hasMoonPhases = hasMoonPhases;
        this.moonPhase = moonPhase;
        this.isSolid = isSolid;
        this.red = red;
        this.green = green;
        this.blue = blue;
        this.alpha = alpha;
        this.ignoreFog = ignoreFog;
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
                Util.getOptionalBoolean(o, "ignore_fog", false)
        );
    }
}
