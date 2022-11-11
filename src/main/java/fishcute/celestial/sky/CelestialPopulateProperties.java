package fishcute.celestial.sky;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import fishcute.celestial.util.Util;

import java.util.ArrayList;

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
    public final boolean perObjectCalculations;

    public CelestialPopulateProperties(int count,
                                       double minDegreesX, double maxDegreesX,
                                       double minDegreesY, double maxDegreesY,
                                       double minDegreesZ, double maxDegreesZ,
                                       double minScale, double maxScale,
                                       double minPosX, double maxPosX,
                                       double minPosY, double maxPosY,
                                       double minPosZ, double maxPosZ,
                                       double minDistance, double maxDistance,
                                       boolean perObjectCalculations) {
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
        this.perObjectCalculations = perObjectCalculations;
    }

    public static CelestialPopulateProperties getPopulationPropertiesFromJson(JsonObject o) {
        JsonObject rotation = o.getAsJsonObject("rotation");
        JsonObject display = o.getAsJsonObject("display");
        return new CelestialPopulateProperties(
                Util.getOptionalInteger(o, "count", 0),
                Util.getOptionalDouble(rotation, "min_degrees_x", 0),
                Util.getOptionalDouble(rotation, "max_degrees_x", 0),
                Util.getOptionalDouble(rotation, "min_degrees_y", 0),
                Util.getOptionalDouble(rotation, "max_degrees_y", 0),
                Util.getOptionalDouble(rotation, "min_degrees_z", 0),
                Util.getOptionalDouble(rotation, "max_degrees_z", 0),
                Util.getOptionalDouble(display, "min_scale", 0),
                Util.getOptionalDouble(display, "max_scale", 0),
                Util.getOptionalDouble(display, "min_pos_x", 0),
                Util.getOptionalDouble(display, "max_pos_x", 0),
                Util.getOptionalDouble(display, "min_pos_y", 0),
                Util.getOptionalDouble(display, "max_pos_y", 0),
                Util.getOptionalDouble(display, "min_pos_z", 0),
                Util.getOptionalDouble(display, "max_pos_z", 0),
                Util.getOptionalDouble(display, "min_distance", 0),
                Util.getOptionalDouble(display, "max_distance", 0),
                Util.getOptionalBoolean(o, "per_object_calculations", false)
        );
    }

    public CelestialObject generateObject(CelestialObject object) {
        return new CelestialObject(
                object.type,
                object.texture,
                object.scale,
                Util.generateRandomDouble(this.minScale, this.maxScale),
                Util.generateRandomDouble(this.minPosX, this.maxPosX),
                Util.generateRandomDouble(this.minPosY, this.maxPosY),
                Util.generateRandomDouble(this.minPosZ, this.maxPosZ),
                object.distance,
                Util.generateRandomDouble(this.minDistance, this.maxDistance),
                Util.generateRandomDouble(this.minDegreesX, this.maxDegreesX),
                Util.generateRandomDouble(this.minDegreesY, this.maxDegreesY),
                Util.generateRandomDouble(this.minDegreesZ, this.maxDegreesZ),
                object.baseDegreesX,
                object.baseDegreesY,
                object.baseDegreesZ,
                object.celestialObjectProperties,
                object.solidColor,
                object.vertexList
                );
    }

    public CelestialObjectPopulation generatePopulateObjects(CelestialObject base, JsonObject populate) {
        ArrayList<CelestialObject> objectList = new ArrayList<>();
        for (int i = 0; i <= this.count; i++) {
            objectList.add(this.generateObject(base));
        }

        if (populate.has("objects")) {
            for (JsonElement e : populate.getAsJsonArray("objects")) {
                JsonObject o = e.getAsJsonObject();
                objectList.add(new CelestialObject(
                    base.type, base.texture, base.scale,
                        Util.getOptionalDouble(o, "scale", 0),
                        Util.getOptionalDouble(o, "pos_x", 0),
                        Util.getOptionalDouble(o, "pos_y", 0),
                        Util.getOptionalDouble(o, "pos_z", 0),
                        base.distance,
                        Util.getOptionalDouble(o, "distance", 0),
                        Util.getOptionalDouble(o, "degrees_x", 0),
                        Util.getOptionalDouble(o, "degrees_y", 0),
                        Util.getOptionalDouble(o, "degrees_z", 0),
                        base.baseDegreesX,
                        base.baseDegreesY,
                        base.baseDegreesZ,
                        base.celestialObjectProperties,
                        base.solidColor,
                        base.vertexList
                ));
            }
        }

        return new CelestialObjectPopulation(objectList, base, this.perObjectCalculations);
    }
}
