package fishcute.celestial.sky;

import com.google.gson.JsonObject;
import com.mojang.blaze3d.platform.GlConst;
import com.mojang.blaze3d.platform.GlStateManager;
import fishcute.celestial.util.ColorEntry;
import fishcute.celestial.util.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import org.lwjgl.opengl.GL11;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

public class SkyBoxObjectProperties {
    public ArrayList<SkyBoxSideTexture> sides;
    public String skyBoxSize;

    public String textureSizeX;
    public String textureSizeY;

    public SkyBoxObjectProperties(ArrayList<SkyBoxSideTexture> sides, String  skyBoxSize, String textureSizeX, String textureSizeY) {
        this.sides = sides;
        this.skyBoxSize = skyBoxSize;
        this.textureSizeX = textureSizeX;
        this.textureSizeY = textureSizeY;
    }

    public static SkyBoxObjectProperties getSkyboxPropertiesFromJson(JsonObject o) {
        String texture;

        if (o.has("skybox"))
            texture = Util.getOptionalString(o.get("skybox").getAsJsonObject(), "texture", Util.getOptionalString(o, "texture", ""));
        else
            texture = Util.getOptionalString(o, "texture", "");

        int textureWidth = 0;
        int textureHeight = 0;
        try {
            BufferedImage b = ImageIO.read(Minecraft.getInstance().getResourceManager().getResource(new ResourceLocation(texture)).get().open());
            textureWidth = b.getWidth();
            textureHeight = b.getHeight();
        }
        catch (Exception ignored) {}

        if (!o.has("skybox")) {
            // Returns if there is no skybox entry
            return new SkyBoxObjectProperties(createDefaultSkybox(
                    new ResourceLocation(texture), (textureHeight / 2) + ""
            ),
                    Util.getOptionalString(o, "size", "100"),
                    Util.getOptionalString(o, "texture_width", textureWidth + ""),
                    Util.getOptionalString(o, "texture_height", textureHeight + ""));
        }

        JsonObject skybox = o.get("skybox").getAsJsonObject();

        if (skybox.has("sides")) {
            ArrayList<SkyBoxSideTexture> textures = new ArrayList<>();
            for (int i = 0; i < 6; i++) {
                if (skybox.get("sides").getAsJsonObject().has("all")) {
                    return new SkyBoxObjectProperties(
                            createSingleTextureSkybox(
                                    new ResourceLocation(Util.getOptionalString(skybox.get("sides").getAsJsonObject().getAsJsonObject("all"), "texture", texture)),
                                    Util.getOptionalString(skybox.get("sides").getAsJsonObject().getAsJsonObject("all"), "uv_x", "0"),
                                    Util.getOptionalString(skybox.get("sides").getAsJsonObject().getAsJsonObject("all"), "uv_y", "0"),
                                    Util.getOptionalString(skybox.get("sides").getAsJsonObject().getAsJsonObject("all"), "uv_width", "0"),
                                    Util.getOptionalString(skybox.get("sides").getAsJsonObject().getAsJsonObject("all"), "uv_height", "0")
                            ),
                            Util.getOptionalString(skybox, "size", "100"),
                            Util.getOptionalString(skybox, "texture_width", textureWidth + ""),
                            Util.getOptionalString(skybox, "texture_height", textureHeight + "")
                    );
                }
                else if (!skybox.get("sides").getAsJsonObject().has(String.valueOf(i))) {
                    textures.add(new SkyBoxSideTexture(
                            new ResourceLocation(texture), "-1", "-1", "-1", "-1"
                    ));
                }
                else {
                    JsonObject entry = skybox.get("sides").getAsJsonObject().getAsJsonObject(String.valueOf(i));
                    textures.add(new SkyBoxSideTexture(
                            new ResourceLocation(Util.getOptionalString(entry, "texture", texture)),
                            Util.getOptionalString(entry, "uv_x", "0"),
                            Util.getOptionalString(entry, "uv_y", "0"),
                            Util.getOptionalString(entry, "uv_width", "0"),
                            Util.getOptionalString(entry, "uv_height", "0")
                    ));
                }
            }
            // Returns skybox with custom format
            return new SkyBoxObjectProperties(
                    textures,
                    Util.getOptionalString(skybox, "size", "100"),
                    Util.getOptionalString(skybox, "texture_width", textureWidth + ""),
                    Util.getOptionalString(skybox, "texture_height", textureHeight + "")
            );
        }
        else {
            // Returns default format skybox
            return new SkyBoxObjectProperties(
                    createDefaultSkybox(
                            new ResourceLocation(texture), Util.getOptionalString(skybox, "uv_size", (textureHeight / 2) + "")
                    ),
                    Util.getOptionalString(skybox, "size", "100"),
                    Util.getOptionalString(skybox, "texture_width", textureWidth + ""),
                    Util.getOptionalString(skybox, "texture_height", textureHeight + "")
            );
        }
    }

    public static ArrayList<SkyBoxSideTexture> createDefaultSkybox(ResourceLocation texture, String textureSize) {
        ArrayList<SkyBoxSideTexture> textures = new ArrayList<>();

        // Bottom
        // #Green
        textures.add(new SkyBoxSideTexture(texture, textureSize, "0", textureSize, textureSize));

        // North
        // #Yellow
        textures.add(new SkyBoxSideTexture(texture, textureSize + " * 2", "0", textureSize, textureSize));

        // South
        // #Light Blue
        textures.add(new SkyBoxSideTexture(texture, textureSize, textureSize, textureSize, textureSize));


        // Up
        // #Red
        textures.add(new SkyBoxSideTexture(texture, "0", "0", textureSize, textureSize));

        // East
        // #Blue
        textures.add(new SkyBoxSideTexture(texture, "0", textureSize, textureSize, textureSize));

        // West
        // #Purple
        textures.add(new SkyBoxSideTexture(texture, textureSize + " * 2", textureSize, textureSize, textureSize));

        return textures;
    }

    public static ArrayList<SkyBoxSideTexture> createSingleTextureSkybox(ResourceLocation texture, String uvX, String uvY, String uvSizeX, String uvSizeY) {
        ArrayList<SkyBoxSideTexture> textures = new ArrayList<>();
        for (int i = 0; i < 6; i++) {
            textures.add(new SkyBoxSideTexture(texture, uvX, uvY, uvSizeX, uvSizeY));
        }

        return textures;
    }

    public static class SkyBoxSideTexture {
        public ResourceLocation texture;
        public String uvX;
        public String uvY;
        public String uvSizeX;
        public String uvSizeY;

        public SkyBoxSideTexture(ResourceLocation texture, String uvX, String uvY, String uvSizeX, String uvSizeY) {
            this.texture = texture;
            this.uvX = uvX;
            this.uvY = uvY;
            this.uvSizeX = uvSizeX;
            this.uvSizeY = uvSizeY;
        }
    }
}