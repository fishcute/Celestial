package fishcute.celestial.sky;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import fishcute.celestial.util.Util;
import net.minecraft.client.MinecraftClient;
import net.minecraft.util.Identifier;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;

public class CelestialSky {
    static Gson reader = new Gson();

    public static HashMap<String, CelestialRenderInfo> dimensionSkyMap = new HashMap<>();

    public static boolean doesDimensionHaveCustomSky() {
        assert MinecraftClient.getInstance().world != null;
        return dimensionSkyMap.containsKey(MinecraftClient.getInstance().world.getRegistryKey().getValue().getPath());
    }

    public static CelestialRenderInfo getDimensionRenderInfo() {
        return dimensionSkyMap.get(MinecraftClient.getInstance().world.getRegistryKey().getValue().getPath());
    }
    public static void loadResources() {
        dimensionSkyMap.clear();

        try { getFile("celestial:sky/dimensions.json").getAsJsonArray("dimensions"); }
        catch (Exception e) {
            Util.log("Found no dimension.json file. Skipping initialization");
            return;
        }

        int dimensionCount = 0;
        int objectCount = 0;

        JsonArray dimensionList = getFile("celestial:sky/dimensions.json").getAsJsonArray("dimensions");

        if (dimensionList == null) {
            Util.warn("Could not find dimension list in \"dimensions.json\".");
            return;
        }

        for (String dimension : getAsStringList(dimensionList)) {
            Util.log("Loading sky for dimension \"" + dimension + "\"");
            ArrayList<CelestialObject> celestialObjects = new ArrayList<>();
            for (String i : getAllCelestialObjects(dimension)) {
                Util.log("[" + dimension + "] Loading celestial object \"" + i + "\"");
                JsonObject object = getFile("celestial:sky/" + dimension + "/objects/" + i + ".json");
                celestialObjects.addAll(CelestialObject.createSkyObjectFromJson(object, i));
                objectCount++;
            }
            dimensionSkyMap.put(dimension, new CelestialRenderInfo(
                    celestialObjects,
                    CelestialStarRenderInfo.createStarRenderInfoFromJson(getFile("celestial:sky/" + dimension + "/sky.json"), dimension),
                    CelestialEnvironmentRenderInfo.createEnvironmentRenderInfoFromJson(getFile("celestial:sky/" + dimension + "/sky.json"), dimension),
                    Util.getOptionalString(getFile("celestial:sky/" + dimension + "/sky.json"), "render_type", "normal"),
                    Util.getOptionalString(getFile("celestial:sky/" + dimension + "/sky.json"), "skybox_texture", "minecraft:textures/environment/end_sky.png")

            ));
            dimensionCount++;
        }
        Util.log("Finished loading skies for " + dimensionCount + " dimension(s). Loaded " + objectCount + " celestial object(s)");
    }

    public static ArrayList<String> getAsStringList(JsonArray array) {
        ArrayList<String> returnObject = new ArrayList<>();
        for (JsonElement a : array) {
            if (a != null && !a.isJsonNull())
                returnObject.add(a.getAsString());
            else
                Util.warn("Found null JsonElement in array \"" + array + "\"");
        }
        return returnObject;
    }

    public static ArrayList<String> getAllCelestialObjects(String dimension) {
        JsonObject o = getFile("celestial:sky/" + dimension + "/sky.json");
        if (o == null) {
            Util.log("Found no sky.json for dimension\"" + dimension + "\", skipping dimension.");
            return new ArrayList<>();
        }
        JsonArray skyObjectList = o.getAsJsonArray("sky_objects");
        if (skyObjectList == null) {
            Util.log("Didn't load any celestial objects, as \"sky_objects\" was missing.");
            return new ArrayList<>();
        }
        return getAsStringList(skyObjectList);
    }

    public static JsonObject getFile(String path) {
        try {
            InputStream inputStream = MinecraftClient.getInstance().getResourceManager().getResource(new Identifier(path)).get().getInputStream();
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
            JsonElement jsonElement = reader.fromJson(bufferedReader, JsonElement.class);
            return jsonElement.getAsJsonObject();
        } catch (Exception exception) {
            return null;
        }
    }
}
