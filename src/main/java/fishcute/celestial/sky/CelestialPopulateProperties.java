package fishcute.celestial.sky;

import com.google.gson.JsonObject;
import fishcute.celestial.util.Util;

public class CelestialPopulateProperties {
    public final int count;
    public final double minDegreesX;
    public final double maxDegreesX;
    public final double minDegreesY;
    public final double maxDegreesY;
    public final double minDegreesZ;
    public final double maxDegreesZ;
    public final double minScale;
    public final double maxScale;

    public final double minPosX;
    public final double maxPosX;
    public final double minPosY;
    public final double maxPosY;
    public final double minPosZ;
    public final double maxPosZ;

    public final double minDistance;
    public final double maxDistance;

    public CelestialPopulateProperties(int count,
                                       double minDegreesX, double maxDegreesX,
                                       double minDegreesY, double maxDegreesY,
                                       double minDegreesZ, double maxDegreesZ,
                                       double minScale, double maxScale,
                                       double minPosX, double maxPosX,
                                       double minPosY, double maxPosY,
                                       double minPosZ, double maxPosZ,
                                       double minDistance, double maxDistance) {
        this.count = count;
        this.minDegreesX = minDegreesX;
        this.maxDegreesX = maxDegreesX;
        this.minDegreesY = minDegreesY;
        this.maxDegreesY = maxDegreesY;
        this.minDegreesZ = minDegreesZ;
        this.maxDegreesZ = maxDegreesZ;
        this.minScale = minScale;
        this.maxScale = maxScale;
        this.minPosX = minPosX;
        this.maxPosX = maxPosX;
        this.minPosY = minPosY;
        this.maxPosY = maxPosY;
        this.minPosZ = minPosZ;
        this.maxPosZ = maxPosZ;
        this.minDistance = minDistance;
        this.maxDistance = maxDistance;
    }

    public static CelestialPopulateProperties DEFAULT = new CelestialPopulateProperties(
            0, 0, 360, 0, 360, 0, 360, 10, 10, 0, 0, 0, 0, 0, 0, 0, 0
    );

    public static CelestialPopulateProperties getPopulationPropertiesFromJson(JsonObject o) {
        JsonObject rotation = o.getAsJsonObject("rotation");
        JsonObject display = o.getAsJsonObject("display");
        return new CelestialPopulateProperties(
                Util.getOptionalInteger(o, "count", 0),
                Util.getOptionalDouble(rotation, "min_degrees_x", 0),
                Util.getOptionalDouble(rotation, "max_degrees_x", 360),
                Util.getOptionalDouble(rotation, "min_degrees_y", 0),
                Util.getOptionalDouble(rotation, "max_degrees_y", 360),
                Util.getOptionalDouble(rotation, "min_degrees_z", 0),
                Util.getOptionalDouble(rotation, "max_degrees_z", 360),
                Util.getOptionalDouble(display, "min_scale", 10),
                Util.getOptionalDouble(display, "max_scale", 10),
                Util.getOptionalDouble(display, "min_pos_x", 0),
                Util.getOptionalDouble(display, "max_pos_x", 0),
                Util.getOptionalDouble(display, "min_pos_y", 0),
                Util.getOptionalDouble(display, "max_pos_y", 0),
                Util.getOptionalDouble(display, "min_pos_z", 0),
                Util.getOptionalDouble(display, "max_pos_z", 0),
                Util.getOptionalDouble(display, "min_distance", 0),
                Util.getOptionalDouble(display, "max_distance", 0)
        );
    }

    public CelestialObject generateObject(CelestialObject object) {
        return new CelestialObject(
                object.texture,
                Util.generateRandomDouble(this.minScale, this.maxScale) + "",
                Util.generateRandomDouble(this.minPosX, this.maxPosX) + "",
                Util.generateRandomDouble(this.minPosY, this.maxPosY) + "",
                Util.generateRandomDouble(this.minPosZ, this.maxPosZ) + "",
                Util.generateRandomDouble(this.minDistance, this.maxDistance) + "",
                object.degreesX + "+" + Util.generateRandomDouble(this.minDegreesX, this.maxDegreesX),
                object.degreesY + "+" + Util.generateRandomDouble(this.minDegreesY, this.maxDegreesY),
                object.degreesZ + "+" + Util.generateRandomDouble(this.minDegreesZ, this.maxDegreesZ),
                object.celestialObjectProperties
                );
    }
}
