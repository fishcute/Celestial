package fishcute.celestial.sky;

import com.google.gson.JsonObject;
import fishcute.celestial.util.Util;
import net.minecraft.util.Identifier;

import java.util.ArrayList;
import java.util.Collections;

public class CelestialObject {
    public final Identifier texture;
    public final String scale;
    public final String posX;
    public final String posY;
    public final String posZ;
    public final String distance;
    public final String degreesX;
    public final String degreesY;
    public final String degreesZ;
    public final CelestialObjectProperties celestialObjectProperties;

    public CelestialObject(String texturePath, String scale, String posX, String posY, String posZ, String distance, String degreesX, String degreesY, String degreesZ, CelestialObjectProperties celestialObjectProperties) {
        this.texture = new Identifier(texturePath);
        this.scale = scale;
        this.posX = posX;
        this.posY = posY;
        this.posZ = posZ;
        this.distance = distance;
        this.degreesX = degreesX;
        this.degreesY = degreesY;
        this.degreesZ = degreesZ;
        this.celestialObjectProperties = celestialObjectProperties;
    }

    public CelestialObject(Identifier texture, String scale, String posX, String posY, String posZ, String distance, String degreesX, String degreesY, String degreesZ, CelestialObjectProperties celestialObjectProperties) {
        this.texture = texture;
        this.scale = scale;
        this.posX = posX;
        this.posY = posY;
        this.posZ = posZ;
        this.distance = distance;
        this.degreesX = degreesX;
        this.degreesY = degreesY;
        this.degreesZ = degreesZ;
        this.celestialObjectProperties = celestialObjectProperties;
    }

    public static ArrayList<CelestialObject> createSkyObjectFromJson(JsonObject o, String name) {
        if (o == null) {
            Util.warn("Failed to load celestial object \"" + name + ".json\", as it did not exist.");
            return new ArrayList<>();
        }
        JsonObject display = o.getAsJsonObject("display");
        JsonObject rotation = o.getAsJsonObject("rotation");
        //I love parameters
        CelestialObject object = new CelestialObject(
                Util.getOptionalString(o, "texture", "minecraft:textures/environment/sun.png"),
                Util.getOptionalString(display, "scale", "30"),
                Util.getOptionalString(display, "pos_x", "0"),
                Util.getOptionalString(display, "pos_y", "0"),
                Util.getOptionalString(display, "pos_z", "0"),
                Util.getOptionalString(display, "distance", "100"),
                Util.getOptionalString(rotation, "degrees_x", "0"),
                Util.getOptionalString(rotation, "degrees_y", "0"),
                Util.getOptionalString(rotation, "degrees_z", "0"),
                CelestialObjectProperties.createCelestialObjectPropertiesFromJson(o.getAsJsonObject("properties"))
        );

        //Check if it's normal
        if (!o.has("populate")) {
            return new ArrayList<>(Collections.singleton(object));
        }
        //Or... it's not :(
        else {
            ArrayList<CelestialObject> objectList = new ArrayList<>();
            CelestialPopulateProperties properties = CelestialPopulateProperties.getPopulationPropertiesFromJson(o.getAsJsonObject("populate"));
            for (int i = 0; i <= o.getAsJsonObject("populate").get("count").getAsInt(); i++) {
                objectList.add(properties.generateObject(object));
            }
            return objectList;
        }
    }
}
