package fishcute.celestial.util;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import fishcute.celestial.sky.CelestialSky;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import org.apache.commons.lang3.tuple.MutablePair;
import org.apache.commons.lang3.tuple.MutableTriple;

import java.text.DecimalFormat;
import java.util.*;

public class Util {

    static Random random = new Random();

    static DecimalFormat numberFormat = new DecimalFormat("#.00000000");
    public static double solveEquation(String str, Map<String, String> toReplace) {
        StringBuilder builder = new StringBuilder(str);
        for (String i : toReplace.keySet()) {
            while (builder.indexOf(i) != -1)
                builder.replace(builder.indexOf(i), builder.indexOf(i) + i.length(), numberFormat.format(Double.valueOf(toReplace.get(i))));
        }
        String finalStr = builder.toString();

        return new Equation(finalStr).parse();
    }

    /*
    Function below originally made by Boann on StackOverFlow, and slightly modified by me.
    */
    private static class Equation {
        public Equation(String finalStr) {
            this.finalStr = finalStr;
        }
        final String finalStr;
        boolean foundIssue = false;
        int pos = -1, ch;

        void nextChar() {
            ch = (++pos < finalStr.length()) ? finalStr.charAt(pos) : -1;
        }

        boolean eat(int charToEat) {
            while (ch == ' ') nextChar();
            if (ch == charToEat) {
                nextChar();
                return true;
            }
            return false;
        }

        double parse() {
            nextChar();
            double x = parseExpression();
            if (pos < finalStr.length()) {
                if (!foundIssue) {
                    sendErrorInGame("Failed to perform math function \"" + finalStr + "\": Unexpected character '" + (char) ch + "'", false);
                    foundIssue = true;
                }
                return 0;
            }
            if (foundIssue)
                return 0;
            return x;
        }

        double parseExpression() {
            double x = parseTerm();
            for (;;) {
                if      (eat('+')) x += parseTerm(); // addition
                else if (eat('-')) x -= parseTerm(); // subtraction
                else return x;
            }
        }

        double parseTerm() {
            double x = parseFactor();
            for (;;) {
                if      (eat('*')) x *= parseFactor(); // multiplication
                else if (eat('/')) x /= parseFactor(); // division
                else return x;
            }
        }

        double parseFactor() {
            if (eat('+')) return +parseFactor(); // unary plus
            if (eat('-')) return -parseFactor(); // unary minus

            double x;
            int startPos = this.pos;
            if (eat('(')) { // parentheses
                x = parseExpression();
                if (!eat(')')) {
                    if (!foundIssue) {
                        sendErrorInGame("Failed to perform math function \"" + finalStr + "\": Missing closing parenthesis in function", false);
                        foundIssue = true;
                    }
                    return 0;
                }
            } else if ((ch >= '0' && ch <= '9') || ch == '.') { // numbers
                while ((ch >= '0' && ch <= '9') || ch == '.') nextChar();
                x = Double.parseDouble(finalStr.substring(startPos, this.pos));
            } else if (ch >= 'a' && ch <= 'z') { // functions
                while (ch >= 'a' && ch <= 'z') nextChar();
                String func = finalStr.substring(startPos, this.pos);
                if (eat('(')) {
                    x = parseExpression();
                    if (!eat(')')) {
                        if (!foundIssue) {
                            sendErrorInGame("Failed to perform math function \"" + finalStr + "\": Missing closing parenthesis in function after argument to \"" + func + "\"", false);
                            foundIssue = true;
                        }
                        return 0;
                    }
                } else {
                    x = parseFactor();
                }

                switch (func) {
                    case "sqrt":
                        x = Math.sqrt(x);
                        break;
                    case "sin":
                        x = Math.sin(Math.toRadians(x));
                        break;
                    case "cos":
                        x = Math.cos(Math.toRadians(x));
                        break;
                    case "tan":
                        x = Math.tan(Math.toRadians(x));
                        break;
                    case "floor":
                        x = Math.floor(Math.toRadians(x));
                        break;
                    case "ceil":
                        x = Math.ceil(Math.toRadians(x));
                        break;
                    case "round":
                        x = Math.round(Math.toRadians(x));
                        break;
                    case "print":
                        print(x);
                        break;
                    default: {
                        if (!foundIssue) {
                            sendErrorInGame("Failed to perform math function \"" + finalStr + "\": Unknown math function \"" + func + "\"", false);
                            foundIssue = true;
                        }
                        return 0;
                    }
                }
            } else {
                if (!foundIssue) {
                    sendErrorInGame("Failed to perform math function \"" + finalStr + "\": Unexpected character '" + (char) ch + "'", false);
                    foundIssue = true;
                }
                return 0;
            }

            if (eat('^')) x = Math.pow(x, parseFactor()); // exponentiation
            else if (eat('m')) x = Math.min(x, parseFactor()); // min
            else if (eat('M')) x = Math.max(x, parseFactor()); // max

            return x;
        }
    }

    static void print(double i) {
        MinecraftClient.getInstance().player.sendMessage(Text.of("Value: " + i), true);
    }
    public static void log(Object i) {
        if (!MinecraftClient.getInstance().isPaused())
            System.out.println("[Celestial] " + i.toString());
    }

    public static void warn(Object i) {
        CelestialSky.warnings++;
        if (!MinecraftClient.getInstance().isPaused()) {
            log("[Warn] " + i.toString());
            sendWarnInGame(i.toString());
        }
    }

    public static ArrayList<String> errorList = new ArrayList<>();

    public static void sendErrorInGame(String i, boolean unloadResources) {
        CelestialSky.errors++;
        if (MinecraftClient.getInstance().player == null)
            return;
        if (errorList.contains(i) || errorList.size() > 25)
            return;
        errorList.add(i);
        MinecraftClient.getInstance().player.sendMessage(Text.of(Formatting.RED +
                "[Celestial] " + i
        ), false);

        if (errorList.size() >= 25)
            MinecraftClient.getInstance().player.sendMessage(Text.of(Formatting.RED +
                    "[Celestial] Passing 25 error messages. Muting error messages."
            ), false);

        if (unloadResources) {
            MinecraftClient.getInstance().player.sendMessage(Text.of(Formatting.RED +
                    "[Celestial] Unloading Celestial resources."
            ), false);
        }
    }

    public static void sendWarnInGame(String i) {
        if (MinecraftClient.getInstance().player == null)
            return;
        if (errorList.contains(i))
            return;
        errorList.add(i);
        MinecraftClient.getInstance().player.sendMessage(Text.of(Formatting.YELLOW +
                "[Celestial] " + i
        ), false);
    }

    public static boolean getOptionalBoolean(JsonObject o, String toGet, boolean ifNull) {
        return o != null && o.has(toGet) ? o.get(toGet).getAsBoolean() : ifNull;
    }

    public static String getOptionalString(JsonObject o, String toGet, String ifNull) {
        return o != null && o.has(toGet) ? o.get(toGet).getAsString() : ifNull;
    }

    public static double getOptionalDouble(JsonObject o, String toGet, double ifNull) {
        return o != null && o.has(toGet) ? o.get(toGet).getAsDouble() : ifNull;
    }

    public static int getOptionalInteger(JsonObject o, String toGet, int ifNull) {
        return o != null && o.has(toGet) ? o.get(toGet).getAsInt() : ifNull;
    }

    public static ArrayList<String> getOptionalStringArray(JsonObject o, String toGet, ArrayList<String> ifNull) {
        return o != null && o.has(toGet) ? convertToStringArrayList(o.get(toGet).getAsJsonArray()) : ifNull;
    }

    public static ArrayList<String> convertToStringArrayList(JsonArray array) {
        ArrayList<String> toReturn = new ArrayList<>();
        for (JsonElement o : array) {
            toReturn.add(o.getAsString());
        }
        return toReturn;
    }

    public static int getDecimal(String hex){
        String digits = "0123456789ABCDEF";
        hex = hex.toUpperCase();
        int val = 0;
        for (int i = 0; i < hex.length(); i++)
        {
            char c = hex.charAt(i);
            int d = digits.indexOf(c);
            val = 16*val + d;
        }
        return val;
    }

    public static double generateRandomDouble(double min, double max) {
        return min + ((max - min) * random.nextDouble());
    }

    public static int blendColors(int a, int b, float ratio){
        if (ratio == 0)
            return b;
        else if (ratio == 1)
            return a;

        ratio = 1-ratio;

        int f2 = (int)(256 * ratio);
        int f1 = 256 - f2;

        return (((((a & 0x00ff00ff) * f1) + ((b & 0x00ff00ff) * f2)) >> 8) & 0x00ff00ff)
                | (((((a & 0xff00ff00) * f1) + ((b & 0xff00ff00) * f2)) >> 8) & 0xff00ff00);
    }

    public static Map<String, String> getReplaceMapNormal() {
        Map<String, String> toReplaceMap = new HashMap<>();
        toReplaceMap.put("#xPos", MinecraftClient.getInstance().player.getX() + "");
        toReplaceMap.put("#yPos", MinecraftClient.getInstance().player.getY() + "");
        toReplaceMap.put("#zPos", MinecraftClient.getInstance().player.getZ() + "");
        toReplaceMap.put("#tickDelta", MinecraftClient.getInstance().getTickDelta() + "");
        toReplaceMap.put("#dayLight", (1.0F - MinecraftClient.getInstance().world.method_23787(MinecraftClient.getInstance().getTickDelta())) + "");
        toReplaceMap.put("#rainGradient", (1.0F - MinecraftClient.getInstance().world.method_23787(MinecraftClient.getInstance().getTickDelta())) + "");
        toReplaceMap.put("#isUsingSpyglass", 0 + "");
        // Spyglasses don't exist in 1.16, will return 0 for version compatibility
        toReplaceMap.put("#isSubmerged", (MinecraftClient.getInstance().player.isSubmergedInWater() ? 1 : 0) + "");
        toReplaceMap.put("#getTotalTime", (MinecraftClient.getInstance().world.getTime()) + "");
        toReplaceMap.put("#random", Math.random() + "");

        return toReplaceMap;
    }

    public static Map<String, String> getReplaceMapAdd(Map<String, String> extraEntries) {
        Map<String, String> toReturn = new HashMap<>();
        toReturn.put("#xPos", MinecraftClient.getInstance().player.getX() + "");
        toReturn.put("#yPos", MinecraftClient.getInstance().player.getY() + "");
        toReturn.put("#zPos", MinecraftClient.getInstance().player.getZ() + "");
        toReturn.put("#tickDelta", MinecraftClient.getInstance().getTickDelta() + "");
        toReturn.put("#dayLight", (1.0F - MinecraftClient.getInstance().world.method_23787(MinecraftClient.getInstance().getTickDelta())) + "");
        toReturn.put("#rainGradient", (1.0F - MinecraftClient.getInstance().world.method_23787(MinecraftClient.getInstance().getTickDelta())) + "");
        toReturn.put("#isUsingSpyglass", 0 + "");
        // Spyglasses don't exist in 1.16, will return 0 for version compatibility
        toReturn.put("#isSubmerged", (MinecraftClient.getInstance().player.isSubmergedInWater() ? 1 : 0) + "");
        toReturn.put("#getTotalTime", (MinecraftClient.getInstance().world.getTime()) + "");
        toReturn.put("#random", Math.random() + "");

        toReturn.putAll(extraEntries);
        return toReturn;
    }

    //I have nightmares about ArrayList<MutablePair<MutableTriple<String, String, String>, MutablePair<String, String>>>
    //This was a mistake
    public static ArrayList<MutablePair<MutableTriple<String, String, String>, MutablePair<String, String>>> convertToPointUvList(ArrayList<String> array) {
        //There's always a better way to do things, and here, I really don't care.

        ArrayList<MutablePair<MutableTriple<String, String, String>, MutablePair<String, String>>> returnArray = new ArrayList<>();
        String[] splitString1;
        String[] splitString2;
        try {
            if (array == null)
                return new ArrayList<>();
            for (String i : array) {
                //1, 2, 3 : 1, 2

                // If there is UV stuff
                if (i.contains(":")) {

                    //vertex points
                    String a = i.split(":")[0];

                    //uv
                    String b = i.split(":")[1];

                    splitString1 = a.split(",");
                    splitString2 = b.split(",");
                    returnArray.add(new MutablePair<>(new MutableTriple<>(splitString1[0], splitString1[1], splitString1[2]), new MutablePair<>(splitString2[0], splitString2[1])));
                }
                // If there is no UV stuff
                else {
                    splitString1 = i.split(",");
                    returnArray.add(new MutablePair<>(new MutableTriple<>(splitString1[0], splitString1[1], splitString1[2]), new MutablePair<>("0", "0")));
                }
            }
            return returnArray;
        }
        catch (Exception e) {
            warn("Failed to parse vertex point array \"" + array.toString() + "\"");
            return new ArrayList<>();
        }
    }
}
