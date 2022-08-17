package fishcute.celestial.mixin;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import com.mojang.math.Matrix4f;
import com.mojang.math.Quaternion;
import com.mojang.math.Vector3d;
import com.mojang.math.Vector3f;
import fishcute.celestial.sky.CelestialObject;
import fishcute.celestial.sky.CelestialRenderInfo;
import fishcute.celestial.sky.CelestialSky;
import fishcute.celestial.util.ClientTick;
import fishcute.celestial.util.Util;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.*;
import net.minecraft.util.Mth;
import net.minecraft.world.level.material.FogType;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.awt.*;
import java.util.Map;
import java.util.Random;

import static java.util.Map.entry;

@Mixin(LevelRenderer.class)
public class LevelRendererMixin {
    @Shadow
    private boolean doesMobEffectBlockSky(Camera camera) {
        return false;
    }
    @Shadow
    private VertexBuffer skyBuffer;
    @Shadow
    private VertexBuffer starBuffer;
    @Shadow
    private VertexBuffer darkBuffer;
    
    @Shadow
    private ClientLevel level;

    @Shadow
    private BufferBuilder.RenderedBuffer drawStars(BufferBuilder buffer) {
        return null;
    }

    @Shadow public void renderLevel(PoseStack matrices, float tickDelta, long limitTime, boolean renderBlockOutline, Camera camera, GameRenderer gameRenderer, LightTexture lightmapTextureManager, Matrix4f positionMatrix) {}

    private static Matrix4f setRotation(PoseStack PoseStack, Quaternion i, Quaternion j, Quaternion k, Vector3d move) {
        PoseStack.pushPose();

        if (i != null) {
            PoseStack.mulPose(i);
        }
        if (j != null) {
            PoseStack.mulPose(j);
        }
        if (k != null) {
            PoseStack.mulPose(k);
        }

        PoseStack.translate(move.x, move.y, move.z);

        Matrix4f matrix4f = PoseStack.last().pose();
        PoseStack.popPose();

        return matrix4f;
    }

    @Inject(method = "renderSky", at = @At("HEAD"), cancellable = true)
    private void renderSky(PoseStack matrices, Matrix4f projectionMatrix, float tickDelta, Camera camera, boolean bl, Runnable runnable, CallbackInfo info) {
        if (ClientTick.canUpdateStars) {
            reloadStars();
            ClientTick.canUpdateStars = false;
        }
        if (CelestialSky.doesDimensionHaveCustomSky()) {
            info.cancel();
            runnable.run();
            if (!bl) {
                FogType fogType = camera.getFluidInCamera();
                if (fogType != FogType.POWDER_SNOW && fogType != FogType.LAVA && !(doesMobEffectBlockSky(camera))) {
                    if (this.level.effects().skyType() == DimensionSpecialEffects.SkyType.NORMAL) {
                        RenderSystem.disableTexture();
                        Vec3 Vector3d = this.level.getSkyColor(Minecraft.getInstance().gameRenderer.getMainCamera().getPosition(), tickDelta);
                        float f = (float) Vector3d.x;
                        float g = (float) Vector3d.y;
                        float h = (float) Vector3d.z;
                        FogRenderer.levelFogColor();
                        BufferBuilder bufferBuilder = Tesselator.getInstance().getBuilder();
                        RenderSystem.depthMask(false);
                        RenderSystem.setShaderColor(f, g, h, 1.0F);
                        ShaderInstance shader = RenderSystem.getShader();
                        float[] fs = Minecraft.getInstance().level.effects().getSunriseColor(this.level.getTimeOfDay(tickDelta), tickDelta);
                        float i;
                        float k;
                        float o;
                        float p;
                        float q;

                        CelestialRenderInfo renderInfo = CelestialSky.getDimensionRenderInfo();

                        VertexBuffer.unbind();
                        RenderSystem.enableBlend();
                        RenderSystem.defaultBlendFunc();

                        if (renderInfo.renderType.equals(CelestialRenderInfo.RenderType.NORMAL)) {
                            this.skyBuffer.bind();
                            this.skyBuffer.drawWithShader(matrices.last().pose(), projectionMatrix, shader);
                            if (fs != null) {
                                RenderSystem.setShader(GameRenderer::getPositionColorShader);
                                RenderSystem.disableTexture();
                                RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
                                matrices.pushPose();
                                matrices.mulPose(Vector3f.XP.rotationDegrees(90.0F));
                                i = Mth.sin(this.level.getTimeOfDay(tickDelta)) < 0.0F ? 180.0F : 0.0F;
                                matrices.mulPose(Vector3f.ZP.rotationDegrees(i));
                                matrices.mulPose(Vector3f.ZP.rotationDegrees(90.0F));
                                float j = fs[0];
                                k = fs[1];
                                float l = fs[2];
                                Matrix4f matrix4f = matrices.last().pose();;
                                bufferBuilder.begin(VertexFormat.Mode.TRIANGLE_FAN, DefaultVertexFormat.POSITION_COLOR);
                                bufferBuilder.vertex(matrix4f, 0.0F, 100.0F, 0.0F).color(j, k, l, fs[3]).endVertex();

                                for (int n = 0; n <= 16; ++n) {
                                    o = (float) n * 6.2831855F / 16.0F;
                                    p = Mth.sin(o);
                                    q = Mth.cos(o);
                                    bufferBuilder.vertex(matrix4f, p * 120.0F, q * 120.0F, -q * 40.0F * fs[3]).color(fs[0], fs[1], fs[2], 0.0F).endVertex();
                                }

                                BufferUploader.drawWithShader(bufferBuilder.end());
                                matrices.popPose();
                            }
                        }
                        else if (renderInfo.renderType.equals(CelestialRenderInfo.RenderType.SKYBOX)) {
                            RenderSystem.setShader(GameRenderer::getPositionTexColorShader);
                            RenderSystem.setShaderTexture(0, renderInfo.skyboxTexture);
                            Tesselator tesselator = Tesselator.getInstance();

                            for(int j = 0; j < 6; ++j) {
                                matrices.pushPose();
                                if (j == 1) {
                                    matrices.mulPose(Vector3f.XP.rotationDegrees(90.0F));
                                }

                                if (j == 2) {
                                    matrices.mulPose(Vector3f.XP.rotationDegrees(-90.0F));
                                }

                                if (j == 3) {
                                    matrices.mulPose(Vector3f.XP.rotationDegrees(180.0F));
                                }

                                if (j == 4) {
                                    matrices.mulPose(Vector3f.ZP.rotationDegrees(90.0F));
                                }

                                if (j == 5) {
                                    matrices.mulPose(Vector3f.ZP.rotationDegrees(-90.0F));
                                }

                                Matrix4f matrix4f = matrices.last().pose();;
                                bufferBuilder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR_TEX);
                                bufferBuilder.vertex(matrix4f, -100.0F, -100.0F, -100.0F).uv(0.0F, 0.0F).color(255, 255, 255, 255).endVertex();
                                bufferBuilder.vertex(matrix4f, -100.0F, -100.0F, 100.0F).uv(0.0F, 16.0F).color(255, 255, 255, 255).endVertex();
                                bufferBuilder.vertex(matrix4f, 100.0F, -100.0F, 100.0F).uv(16.0F, 16.0F).color(255, 255, 255, 255).endVertex();
                                bufferBuilder.vertex(matrix4f, 100.0F, -100.0F, -100.0F).uv(16.0F, 0.0F).color(255, 255, 255, 255).endVertex();
                                tesselator.end();
                                matrices.popPose();
                            }
                        }

                        float a = level.dimensionType().timeOfDay(level.dayTime()) * 360;

                        Map<String, String> toReplaceMapRotation = new java.util.HashMap<>(Map.ofEntries(
                                entry("#skyAngle", a + "")
                        ));

                        toReplaceMapRotation.putAll(Util.getReplaceMapNormal());

                        i = 1.0F - this.level.getRainLevel(tickDelta);

                        toReplaceMapRotation.putAll(Util.getReplaceMapNormal());

                        Map<String, String> toReplaceMapStarBrightness = new java.util.HashMap<>(Map.ofEntries(
                                entry("#starAlpha", this.level.getStarBrightness(tickDelta) * i + "")
                        ));

                        toReplaceMapStarBrightness.putAll(Util.getReplaceMapNormal());

                        matrices.pushPose();

                        matrices.mulPose(Vector3f.XP.rotationDegrees((float) Util.solveEquation(renderInfo.stars.baseDegreesX, toReplaceMapRotation)));
                        matrices.mulPose(Vector3f.YP.rotationDegrees((float) Util.solveEquation(renderInfo.stars.baseDegreesY, toReplaceMapRotation)));
                        matrices.mulPose(Vector3f.ZP.rotationDegrees((float) Util.solveEquation(renderInfo.stars.baseDegreesZ, toReplaceMapRotation)));


                        // Stars

                        float brightness = (float) Util.solveEquation(renderInfo.stars.starBrightness, toReplaceMapStarBrightness);
                        if (brightness > 0.0F) {
                            RenderSystem.setShaderColor(brightness, brightness, brightness, brightness);
                            FogRenderer.levelFogColor();
                            this.starBuffer.bind();
                            this.starBuffer.drawWithShader(setRotation(matrices,
                                    Vector3f.XP.rotationDegrees((float) Util.solveEquation(renderInfo.stars.degreesX, toReplaceMapRotation)),
                                    Vector3f.YP.rotationDegrees((float) Util.solveEquation(renderInfo.stars.degreesY, toReplaceMapRotation)),
                                    Vector3f.ZP.rotationDegrees((float) Util.solveEquation(renderInfo.stars.degreesZ, toReplaceMapRotation)),
                                    new Vector3d(0, 0, 0)), projectionMatrix, GameRenderer.getPositionColorShader());
                            VertexBuffer.unbind();
                            runnable.run();
                        }

                        matrices.popPose();

                        RenderSystem.enableTexture();
                        RenderSystem.enableBlend();
                        RenderSystem.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);

                        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, i);

                        Matrix4f matrix4f2;

                        for (CelestialObject c : renderInfo.skyObjects) {
                            matrices.pushPose();

                            matrices.mulPose(Vector3f.XP.rotationDegrees((float) Util.solveEquation(c.baseDegreesX, toReplaceMapRotation)));
                            matrices.mulPose(Vector3f.YP.rotationDegrees((float) Util.solveEquation(c.baseDegreesY, toReplaceMapRotation)));
                            matrices.mulPose(Vector3f.ZP.rotationDegrees((float) Util.solveEquation(c.baseDegreesZ, toReplaceMapRotation)));

                            matrix4f2 = setRotation(matrices,
                                    Vector3f.XP.rotationDegrees((float) Util.solveEquation(c.degreesX, toReplaceMapRotation)),
                                            Vector3f.YP.rotationDegrees((float) Util.solveEquation(c.degreesY, toReplaceMapRotation)),
                                            Vector3f.ZP.rotationDegrees((float) Util.solveEquation(c.degreesZ, toReplaceMapRotation)),
                                            new Vector3d(
                                                    (float) Util.solveEquation(c.posX, Util.getReplaceMapNormal()),
                                                    (float) Util.solveEquation(c.posY, Util.getReplaceMapNormal()),
                                                    (float) Util.solveEquation(c.posZ, Util.getReplaceMapNormal())
                                            ));

                            float distance = (float) Util.solveEquation(c.distance, Util.getReplaceMapNormal());
                            // Set scale
                            k = (float) Util.solveEquation(c.scale, Util.getReplaceMapNormal());

                            float k1 = distance < 0 ? k : -k;

                            float k2 = distance < 0 ? -k : k;


                            RenderSystem.setShader(GameRenderer::getPositionTexColorShader);

                            // Set texture
                            RenderSystem.setShaderTexture(0, c.texture);

                            int r1 = (int) (((float) Util.solveEquation(c.celestialObjectProperties.red, Util.getReplaceMapNormal())) * 255);
                            int g1 = (int) (((float) Util.solveEquation(c.celestialObjectProperties.green, Util.getReplaceMapNormal())) * 255);
                            int b1 = (int) (((float) Util.solveEquation(c.celestialObjectProperties.blue, Util.getReplaceMapNormal())) * 255);
                            int a1 = (int) (((float) Util.solveEquation(c.celestialObjectProperties.alpha, Util.getReplaceMapNormal())) * 255);

                            if (c.celestialObjectProperties.isSolid)
                                RenderSystem.defaultBlendFunc();

                            if (c.celestialObjectProperties.hasMoonPhases) {
                                int r = this.level.getMoonPhase();
                                int s = r % 4;
                                int m = r / 4 % 2;
                                float t = (float)(s) / 4.0F;
                                o = (float)(m) / 2.0F;
                                p = (float)(s + 1) / 4.0F;
                                q = (float)(m + 1) / 2.0F;
                                bufferBuilder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX_COLOR);
                                bufferBuilder.vertex(matrix4f2, -k, distance, k1).uv(p, q).color(r1, g1, b1, a1).endVertex();
                                bufferBuilder.vertex(matrix4f2, k, distance, k1).uv(t, q).color(r1, g1, b1, a1).endVertex();
                                bufferBuilder.vertex(matrix4f2, k, distance, k2).uv(t, o).color(r1, g1, b1, a1).endVertex();
                                bufferBuilder.vertex(matrix4f2, -k, distance, k2).uv(p, o).color(r1, g1, b1, a1).endVertex();
                            }
                            else {
                                bufferBuilder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX_COLOR);
                                bufferBuilder.vertex(matrix4f2, -k, distance, k1).uv(0.0F, 0.0F).color(r1, g1, b1, a1).endVertex();
                                bufferBuilder.vertex(matrix4f2, k, distance, k1).uv(1.0F, 0.0F).color(r1, g1, b1, a1).endVertex();
                                bufferBuilder.vertex(matrix4f2, k, distance, k2).uv(1.0F, 1.0F).color(r1, g1, b1, a1).endVertex();
                                bufferBuilder.vertex(matrix4f2, -k, distance, k2).uv(0.0F, 1.0F).color(r1, g1, b1, a1).endVertex();
                            }

                            BufferUploader.drawWithShader(bufferBuilder.end());

                            if (c.celestialObjectProperties.isSolid)
                                RenderSystem.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);

                            RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
                            matrices.popPose();
                        }
                        RenderSystem.disableBlend();
                        RenderSystem.disableTexture();
                        RenderSystem.setShaderColor(0.0F, 0.0F, 0.0F, 1.0F);


                        double d = Minecraft.getInstance().player.getEyePosition(tickDelta).y - this.level.getLevelData().getHorizonHeight(this.level);
                        if (d < 0.0) {
                            matrices.pushPose();
                            matrices.translate(0.0, 12.0, 0.0);
                            this.darkBuffer.bind();
                            this.darkBuffer.drawWithShader(matrices.last().pose(), projectionMatrix, shader);
                            VertexBuffer.unbind();
                            matrices.popPose();
                        }

                        //TODO: Fix the things

                        if (this.level.effects().hasGround()) {
                            RenderSystem.setShaderColor(f * 0.2F + 0.04F, g * 0.2F + 0.04F, h * 0.6F + 0.1F, 1.0F);
                        } else {
                            RenderSystem.setShaderColor(f, g, h, 1.0F);
                        }

                        RenderSystem.enableTexture();
                        RenderSystem.depthMask(true);
                    }
                }
            }
        }
    }
    private void reloadStars() {
        Tesselator tess = Tesselator.getInstance();
        BufferBuilder builder = tess.getBuilder();
        RenderSystem.setShader(GameRenderer::getPositionColorShader);

        this.starBuffer = new VertexBuffer();
        BufferBuilder.RenderedBuffer builtBuffer;
        if (CelestialSky.doesDimensionHaveCustomSky())
            builtBuffer = this.renderCustomStars(builder);
        else
            builtBuffer = drawStars(builder);
        this.starBuffer.bind();
        this.starBuffer.upload(builtBuffer);
        VertexBuffer.unbind();
    }

    // Credits to B3Spectacled's Custom Stars for most of this code
    // https://github.com/b3spectacled/custom-stars
    private BufferBuilder.RenderedBuffer renderCustomStars(BufferBuilder buffer) {

        Random random = new Random();

        double[] ipts = new double[CelestialSky.getDimensionRenderInfo().stars.starCount];
        double[] jpts = new double[CelestialSky.getDimensionRenderInfo().stars.starCount];
        double[] kpts = new double[CelestialSky.getDimensionRenderInfo().stars.starCount];

        int stars = 0;

        while (stars < CelestialSky.getDimensionRenderInfo().stars.starCount) {
            ipts[stars] = random.nextFloat() * 2.0f - 1.0f;
            jpts[stars] = random.nextFloat() * 2.0f - 1.0f;
            kpts[stars] = random.nextFloat() * 2.0f - 1.0f;

            stars++;
        }

        buffer.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR);

        double[] xCoords = new double[4];
        double[] yCoords = new double[4];
        double[] zCoords = new double[4];

        for (int i = 0; i < CelestialSky.getDimensionRenderInfo().stars.starCount; ++i) {
            double double5 = ipts[i];
            double double7 = jpts[i];
            double double9 = kpts[i];

            double double11 = Util.generateRandomDouble(CelestialSky.getDimensionRenderInfo().stars.starMinSize, CelestialSky.getDimensionRenderInfo().stars.starMaxSize);
            double double13 = double5 * double5 + double7 * double7 + double9 * double9;
            if (double13 < 1.0 && double13 > 0.01) {
                double13 = 1.0 / Math.sqrt(double13);
                double5 *= double13;
                double7 *= double13;
                double9 *= double13;
                double double15 = double5 * 100.0;
                double double17 = double7 * 100.0;
                double double19 = double9 * 100.0;
                double double21 = Math.atan2(double5, double9);
                double double23 = Math.sin(double21);
                double double25 = Math.cos(double21);
                double double27 = Math.atan2(Math.sqrt(double5 * double5 + double9 * double9), double7);
                double double29 = Math.sin(double27);
                double double31 = Math.cos(double27);
                double double33 = random.nextDouble() * 3.141592653589793 * 2.0;
                double double35 = Math.sin(double33);
                double double37 = Math.cos(double33);

                String colorHex = CelestialSky.getDimensionRenderInfo().stars.starColors.get(random.nextInt(CelestialSky.getDimensionRenderInfo().stars.starColors.size()));
                Color starColor = Color.decode(colorHex.startsWith("#") ? colorHex : "#" + colorHex);

                for (int v = 0; v < 4; ++v) {
                    double double42 = ((v & 0x2) - 1) * double11;
                    double double44 = ((v + 1 & 0x2) - 1) * double11;
                    double double48 = double42 * double37 - double44 * double35;
                    double double52;
                    double52 = double44 * double37 + double42 * double35;
                    double double54 = double48 * double29 + 0.0 * double31;
                    double double56 = 0.0 * double29 - double48 * double31;
                    double double58 = double56 * double23 - double52 * double25;
                    double double62 = double52 * double23 + double56 * double25;

                    double x = double15 + double58;
                    double y = double17 + double54;
                    double z = double19 + double62;

                    xCoords[v] = x;
                    yCoords[v] = y;
                    zCoords[v] = z;
                }
                //Rendering can be cancelled here
                    for (int v = 0; v < 4; ++v) {
                        double x = xCoords[v];
                        double y = yCoords[v];
                        double z = zCoords[v];

                        buffer.vertex(x, y, z).color(starColor.getRed() / 255F, starColor.getGreen() / 255F, starColor.getBlue() / 255F, 1.0F).endVertex();
                    }
            }
        }

        return buffer.end();
    }
}
