package fishcute.celestial.util;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import fishcute.celestial.CelestialClient;
import fishcute.celestial.sky.CelestialEnvironmentRenderInfo;
import fishcute.celestial.sky.CelestialSky;
import org.apache.commons.lang3.tuple.MutablePair;
import org.apache.commons.lang3.tuple.Pair;

import java.awt.*;
import java.util.ArrayList;

public class ColorEntry {
    public static ColorEntry createColorEntry(JsonObject o, String elementName, ColorEntry defaultEntry) {
        if (o == null)
            return defaultEntry;
        try {
            o.get(elementName).getAsJsonObject().getAsJsonArray(elementName);
        }
        catch (Exception e) {
            if (o.has(elementName))
                return new ColorEntry(Util.decodeColor(Util.getOptionalString(o, elementName, "#ffffff")));
            return defaultEntry;
        }
        JsonObject skyColors = o.get(elementName).getAsJsonObject();

        ArrayList<MutablePair<Color, String>> colors = new ArrayList<>();
        String baseColor = Util.getOptionalString(skyColors, "base_color", "#ffffff");

        if (skyColors.has("colors"))
            try {
                for (JsonElement color : skyColors.getAsJsonArray("colors")) {
                    colors.add(new MutablePair<>(
                            Util.decodeColor(Util.getOptionalString(color.getAsJsonObject(), "color", "#ffffff")),
                            Util.getOptionalString(color.getAsJsonObject(), "alpha", "0")
                    ));
                }
            }
            catch (Exception e) {
                Util.sendErrorInGame("Failed to parse color entry \"" + elementName + "\".", false);
            }
        return new ColorEntry(colors, baseColor, Util.getOptionalInteger(skyColors, "update_frequency", 0));
    }
    public ColorEntry(ArrayList<MutablePair<Color, String>> colors, String baseColor, int updateFrequency) {
        this.colors = colors;
        if (baseColor.equals("inherit")) {
            this.inheritColor = true;
        }
        else
            this.baseColor = Util.decodeColor(baseColor);
        this.updateFrequency = updateFrequency;
    }

    public ColorEntry(Color color) {
        this.storedColor = color;
        this.isBasicColor = true;
        this.inheritColor = false;
    }

    public void tick() {
        if (this.isBasicColor)
            return;
        if (this.updateTick <= 0 || CelestialSky.forceUpdateVariables) {
            this.updateTick = this.updateFrequency;
            this.updateColor();
        }
        else
            this.updateTick--;
    }

    public boolean isBasicColor = false;
    public ArrayList<MutablePair<Color, String>> colors;
    public Color baseColor = new Color(255, 255, 255);

    public boolean inheritColor;

    public Color storedColor = new Color(255, 255, 255);

    public int updateFrequency;

    public int updateTick;

    public void setInheritColor(Color c) {
        if (this.inheritColor)
            this.baseColor = c;
    }

    public void updateColor() {
        this.storedColor = getResultColor();
    }

    public Color getResultColor() {
        if (colors.size() == 0)
            return new Color(baseColor.getRed(), baseColor.getGreen(), baseColor.getBlue());

        int r = baseColor.getRed(), g = baseColor.getGreen(), b = baseColor.getBlue();

        double value;
        for (Pair<Color, String> color : this.colors) {
            value = Util.solveEquation(color.getValue(), Util.getReplaceMapNormal());
            if (value > 1)
                value = 1;
            else if (value <= 0)
                continue;

            if (r <= 0)
                r = 1;
            if (g <= 0)
                g = 1;
            if (b <= 0)
                b = 1;

            r = (int) (r * (1 - (((float) (r - color.getKey().getRed()) / r) * value)));
            g = (int) (g * (1 - (((float) (g - color.getKey().getGreen()) / g) * value)));
            b = (int) (b * (1 - (((float) (b - color.getKey().getBlue()) / b) * value)));
        }

        return new Color(r, g, b);
    }
}
