package fishcute.celestial.sky;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import fishcute.celestial.util.ClientTick;
import fishcute.celestial.util.Util;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import org.apache.commons.lang3.tuple.Pair;

import java.awt.*;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;

public class CelestialSky {

    public static int warnings = 0;
    public static int errors = 0;
    static Gson reader = new Gson();

    public static HashMap<String, CelestialRenderInfo> dimensionSkyMap = new HashMap<>();

    public static HashMap<String, Variable> variables = new HashMap<>();

    public static boolean doesDimensionHaveCustomSky() {
        return ClientTick.dimensionHasCustomSky && getDimensionRenderInfo() != null;
    }

    public static CelestialRenderInfo getDimensionRenderInfo() {
        try {
            return dimensionSkyMap.get(Minecraft.getInstance().level.dimension().location().getPath());
        }
        catch (Exception e) {
            return null;
        }
    }
    public static void loadResources() {
        warnings = 0;
        errors = 0;

        dimensionSkyMap.clear();

        Util.log("Loading resources...");

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
                CelestialObject o = CelestialObject.createSkyObjectFromJson(object, i, dimension);
                if (o != null)
                    celestialObjects.add(o);
                objectCount++;
            }
            dimensionSkyMap.put(dimension, new CelestialRenderInfo(
                    celestialObjects,
                    CelestialEnvironmentRenderInfo.createEnvironmentRenderInfoFromJson(getFile("celestial:sky/" + dimension + "/sky.json"), dimension)
            ));
            dimensionCount++;
        }
        Util.initalizeToReplaceMap(setupVariables());

        Util.log("Finished loading skies for " + dimensionCount + " dimension(s). Loaded " + objectCount + " celestial object(s) with " + warnings + " warning(s) and " + errors + " error(s).");
        if (Minecraft.getInstance().player != null)
            Minecraft.getInstance().player.displayClientMessage(Component.literal(ChatFormatting.GRAY + "[Celestial] Reloaded with " + warnings + " warning(s) and " +errors + " error(s)."), false);
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

    public static boolean forceUpdateVariables = false;

    public static HashMap<String, Util.DynamicValue> setupVariables() {
        variables.clear();
        try {
            getFile("celestial:sky/variables.json").getAsJsonArray("variables").toString();
        }
        catch (Exception e) {
            Util.log("Found no variables.json file. Skipping variable initialization.");
            return new HashMap<>();
        }


        HashMap<String, Util.DynamicValue> variableReplaceMap = new HashMap<>();

        int variableCount = 0;
        for (JsonElement o : getFile("celestial:sky/variables.json").getAsJsonArray("variables")) {
            try {
                variables.put("#" + Util.getOptionalString(o.getAsJsonObject(), "name", "undefined"),
                        new Variable(Util.getOptionalString(o.getAsJsonObject(), "value", "0"), Util.getOptionalInteger(o.getAsJsonObject(), "update_frequency", 0)));
                variableReplaceMap.put("#" + Util.getOptionalString(o.getAsJsonObject(), "name", "undefined"),
                        new Util.DynamicValue() {
                            @Override
                            public double getValue() {
                                return
                                        variables.get("#" + Util.getOptionalString(o.getAsJsonObject(), "name", "undefined")).storedValue;
                            }
                        }
                );
                variableCount++;
            }
            catch (Exception e) {
                Util.sendErrorInGame("Failed to load empty variable entry. Skipping variable initialization.", false);
                break;
            }
        }

        Util.log("Registered " + variableCount + " variable(s).");

        forceUpdateVariables = true;

        return variableReplaceMap;
    }

    public static void updateVariableValues() {
        Variable v;
        for (String name : variables.keySet()) {
            v = variables.get(name);
            if (v.updateTick <= 0 || forceUpdateVariables) {
                v.updateTick = v.updateFrequency;
                v.updateValue();
            }
            else
                v.updateTick--;
        }

        if (doesDimensionHaveCustomSky())
            getDimensionRenderInfo().environment.updateColorEntries();

        if (forceUpdateVariables)
            forceUpdateVariables = false;
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
            InputStream inputStream = Minecraft.getInstance().getResourceManager().getResource(new ResourceLocation(path)).get().open();
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
            JsonElement jsonElement = reader.fromJson(bufferedReader, JsonElement.class);
            return jsonElement.getAsJsonObject();
        } catch (Exception exception) {
            return null;
        }
    }

    static class Variable {
        public int updateFrequency;
        public int updateTick;
        public String value;

        public double storedValue = 0;

        public Variable(String value, int updateFrequency) {
            this.value = value;
            this.updateFrequency = updateFrequency;
            this.updateTick = updateFrequency;
        }

        public void updateValue() {
            this.storedValue = Util.solveEquation(this.value, Util.getReplaceMapNormal());
        }
    }
}
