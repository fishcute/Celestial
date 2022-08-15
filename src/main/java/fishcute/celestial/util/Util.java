package fishcute.celestial.util;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import fishcute.celestial.sky.CelestialSky;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Map;
import java.util.Random;

import static java.util.Map.entry;

public class Util {

    static Random random = new Random();

    /*
    Function below originally made by Boann on StackOverFlow, and slightly modified by me.
     */

    static DecimalFormat numberFormat = new DecimalFormat("#.00000000");
    public static double solveEquation(String str, Map<String, String> toReplace) {
        for (String i : toReplace.keySet()) {
            str = str.replaceAll(i, numberFormat.format(Double.valueOf(toReplace.get(i))));
        }
        String finalStr = str;

        return new Object() {
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
                        case "sqrt" -> x = Math.sqrt(x);
                        case "sin" -> x = Math.sin(Math.toRadians(x));
                        case "cos" -> x = Math.cos(Math.toRadians(x));
                        case "tan" -> x = Math.tan(Math.toRadians(x));
                        case "floor" -> x = Math.floor(Math.toRadians(x));
                        case "ceil" -> x = Math.ceil(Math.toRadians(x));
                        case "round" -> x = Math.round(Math.toRadians(x));
                        case "print" -> print(x);
                        default -> {
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
        }.parse();
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
        ));

        if (errorList.size() >= 25)
            MinecraftClient.getInstance().player.sendMessage(Text.of(Formatting.RED +
                    "[Celestial] Passing 25 error messages. Muting error messages."
            ));

        if (unloadResources) {
            MinecraftClient.getInstance().player.sendMessage(Text.of(Formatting.RED +
                    "[Celestial] Unloading Celestial resources."
            ));
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
        ));
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
        ratio = 1-ratio;
        int mask1 = 0x00ff00ff;
        int mask2 = 0xff00ff00;

        int f2 = (int)(256 * ratio);
        int f1 = 256 - f2;

        return (((((a & mask1) * f1) + ((b & mask1) * f2)) >> 8) & mask1)
                | (((((a & mask2) * f1) + ((b & mask2) * f2)) >> 8) & mask2);
    }

    public static Map<String, String> getReplaceMapNormal() {
        return Map.ofEntries(
                entry("#xPos", MinecraftClient.getInstance().player.getPos().x + ""),
                entry("#yPos", MinecraftClient.getInstance().player.getPos().y + ""),
                entry("#zPos", MinecraftClient.getInstance().player.getPos().z + ""),
                entry("#tickDelta", MinecraftClient.getInstance().getTickDelta() + ""),
                entry("#dayLight", (1.0F - MinecraftClient.getInstance().world.method_23787(MinecraftClient.getInstance().getTickDelta())) + ""),
                entry("#rainGradient", (1.0F - MinecraftClient.getInstance().world.getRainGradient(MinecraftClient.getInstance().world.method_23787(MinecraftClient.getInstance().getTickDelta()))) + ""),
                entry("#isUsingSpyglass", (MinecraftClient.getInstance().player.isUsingSpyglass() ? 1 : 0) + ""),
                entry("#isSubmerged", (MinecraftClient.getInstance().player.isSubmergedInWater() ? 1 : 0) + ""),
                entry("#getTotalTime", (MinecraftClient.getInstance().world.getTime()) + ""),
                entry("#random", Math.random() + "")
        );
    }
}
