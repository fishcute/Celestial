package fishcute.celestial.sky;

import com.google.gson.JsonObject;
import fishcute.celestial.util.Util;
import net.minecraft.resources.ResourceLocation;

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
        if (!o.has("skybox")) {
            // Returns if there is no skybox entry
            return new SkyBoxObjectProperties(createDefaultSkybox(
                    new ResourceLocation(""), "1"
            ),
                    Util.getOptionalString(o, "size", "100"),
                    Util.getOptionalString(o, "texture_width", "0"),
                    Util.getOptionalString(o, "texture_height", "0"));
        }

        JsonObject skybox = o.get("skybox").getAsJsonObject();

        String texture = Util.getOptionalString(skybox, "texture", null);

        if (texture == null)
            texture = Util.getOptionalString(o, "texture", "");

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
                            Util.getOptionalString(skybox, "texture_width", "0"),
                            Util.getOptionalString(skybox, "texture_height", "0")
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
                    Util.getOptionalString(skybox, "texture_width", "0"),
                    Util.getOptionalString(skybox, "texture_height", "0")
            );
        }
        else {
            // Returns default format skybox
            return new SkyBoxObjectProperties(
                    createDefaultSkybox(
                            new ResourceLocation(texture), Util.getOptionalString(skybox, "uv_size", "1")
                    ),
                    Util.getOptionalString(skybox, "size", "100"),
                    Util.getOptionalString(skybox, "texture_width", "0"),
                    Util.getOptionalString(skybox, "texture_height", "0")
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