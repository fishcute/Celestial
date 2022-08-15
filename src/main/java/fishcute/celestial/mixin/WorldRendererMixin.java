package fishcute.celestial.mixin;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import fishcute.celestial.sky.CelestialObject;
import fishcute.celestial.sky.CelestialRenderInfo;
import fishcute.celestial.sky.CelestialSky;
import fishcute.celestial.util.ClientTick;
import fishcute.celestial.util.Util;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.VertexBuffer;
import net.minecraft.client.render.*;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.util.math.*;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.awt.*;
import java.util.Map;
import java.util.Random;

import static java.util.Map.entry;

@Mixin(WorldRenderer.class)
public abstract class WorldRendererMixin {
    @Shadow
    private boolean method_43788(Camera camera) {
        return false;
    }
    @Shadow
    private VertexBuffer lightSkyBuffer;
    @Shadow
    private VertexBuffer starsBuffer;
    @Shadow
    private VertexBuffer darkSkyBuffer;
    
    @Shadow
    private ClientWorld world;

    @Shadow protected abstract BufferBuilder.BuiltBuffer renderStars(BufferBuilder buffer);

    @Shadow public abstract void render(MatrixStack matrices, float tickDelta, long limitTime, boolean renderBlockOutline, Camera camera, GameRenderer gameRenderer, LightmapTextureManager lightmapTextureManager, Matrix4f positionMatrix);

    private static Matrix4f setRotation(MatrixStack matrixStack, Quaternion i, Quaternion j, Quaternion k, Vec3d move) {
        matrixStack.push();

        if (i != null) {
            matrixStack.multiply(i);
        }
        if (j != null) {
            matrixStack.multiply(j);
        }
        if (k != null) {
            matrixStack.multiply(k);
        }

        matrixStack.translate(move.x, move.y, move.z);

        Matrix4f matrix4f = matrixStack.peek().getPositionMatrix();
        matrixStack.pop();

        return matrix4f;
    }

    @Inject(method = "renderSky(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/util/math/Matrix4f;FLnet/minecraft/client/render/Camera;ZLjava/lang/Runnable;)V", at = @At("HEAD"), cancellable = true)
    private void renderSky(MatrixStack matrices, Matrix4f projectionMatrix, float tickDelta, Camera camera, boolean bl, Runnable runnable, CallbackInfo info) {
        if (ClientTick.canUpdateStars) {
            reloadStars();
            ClientTick.canUpdateStars = false;
        }
        if (CelestialSky.doesDimensionHaveCustomSky()) {
            info.cancel();
            runnable.run();
            if (!bl) {
                CameraSubmersionType cameraSubmersionType = camera.getSubmersionType();
                if (cameraSubmersionType != CameraSubmersionType.POWDER_SNOW && cameraSubmersionType != CameraSubmersionType.LAVA && !this.method_43788(camera)) {
                    if (this.world.getDimensionEffects().getSkyType() == DimensionEffects.SkyType.NORMAL) {
                        RenderSystem.disableTexture();
                        Vec3d vec3d = this.world.getSkyColor(MinecraftClient.getInstance().gameRenderer.getCamera().getPos(), tickDelta);
                        float f = (float) vec3d.x;
                        float g = (float) vec3d.y;
                        float h = (float) vec3d.z;
                        BackgroundRenderer.setFogBlack();
                        BufferBuilder bufferBuilder = Tessellator.getInstance().getBuffer();
                        RenderSystem.depthMask(false);
                        RenderSystem.setShaderColor(f, g, h, 1.0F);
                        Shader shader = RenderSystem.getShader();
                        float[] fs = this.world.getDimensionEffects().getFogColorOverride(this.world.getSkyAngle(tickDelta), tickDelta);
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
                            this.lightSkyBuffer.bind();
                            this.lightSkyBuffer.draw(matrices.peek().getPositionMatrix(), projectionMatrix, shader);
                            if (fs != null) {
                                RenderSystem.setShader(GameRenderer::getPositionColorShader);
                                RenderSystem.disableTexture();
                                RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
                                matrices.push();
                                matrices.multiply(Vec3f.POSITIVE_X.getDegreesQuaternion(90.0F));
                                i = MathHelper.sin(this.world.getSkyAngleRadians(tickDelta)) < 0.0F ? 180.0F : 0.0F;
                                matrices.multiply(Vec3f.POSITIVE_Z.getDegreesQuaternion(i));
                                matrices.multiply(Vec3f.POSITIVE_Z.getDegreesQuaternion(90.0F));
                                float j = fs[0];
                                k = fs[1];
                                float l = fs[2];
                                Matrix4f matrix4f = matrices.peek().getPositionMatrix();
                                bufferBuilder.begin(VertexFormat.DrawMode.TRIANGLE_FAN, VertexFormats.POSITION_COLOR);
                                bufferBuilder.vertex(matrix4f, 0.0F, 100.0F, 0.0F).color(j, k, l, fs[3]).next();

                                for (int n = 0; n <= 16; ++n) {
                                    o = (float) n * 6.2831855F / 16.0F;
                                    p = MathHelper.sin(o);
                                    q = MathHelper.cos(o);
                                    bufferBuilder.vertex(matrix4f, p * 120.0F, q * 120.0F, -q * 40.0F * fs[3]).color(fs[0], fs[1], fs[2], 0.0F).next();
                                }

                                BufferRenderer.drawWithShader(bufferBuilder.end());
                                matrices.pop();
                            }
                        }
                        else if (renderInfo.renderType.equals(CelestialRenderInfo.RenderType.SKYBOX)) {
                            RenderSystem.setShader(GameRenderer::getPositionTexColorShader);
                            RenderSystem.setShaderTexture(0, renderInfo.skyboxTexture);
                            Tessellator tessellator = Tessellator.getInstance();

                            for(int j = 0; j < 6; ++j) {
                                matrices.push();
                                if (j == 1) {
                                    matrices.multiply(Vec3f.POSITIVE_X.getDegreesQuaternion(90.0F));
                                }

                                if (j == 2) {
                                    matrices.multiply(Vec3f.POSITIVE_X.getDegreesQuaternion(-90.0F));
                                }

                                if (j == 3) {
                                    matrices.multiply(Vec3f.POSITIVE_X.getDegreesQuaternion(180.0F));
                                }

                                if (j == 4) {
                                    matrices.multiply(Vec3f.POSITIVE_Z.getDegreesQuaternion(90.0F));
                                }

                                if (j == 5) {
                                    matrices.multiply(Vec3f.POSITIVE_Z.getDegreesQuaternion(-90.0F));
                                }

                                Matrix4f matrix4f = matrices.peek().getPositionMatrix();
                                bufferBuilder.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_TEXTURE_COLOR);
                                bufferBuilder.vertex(matrix4f, -100.0F, -100.0F, -100.0F).texture(0.0F, 0.0F).color(255, 255, 255, 255).next();
                                bufferBuilder.vertex(matrix4f, -100.0F, -100.0F, 100.0F).texture(0.0F, 16.0F).color(255, 255, 255, 255).next();
                                bufferBuilder.vertex(matrix4f, 100.0F, -100.0F, 100.0F).texture(16.0F, 16.0F).color(255, 255, 255, 255).next();
                                bufferBuilder.vertex(matrix4f, 100.0F, -100.0F, -100.0F).texture(16.0F, 0.0F).color(255, 255, 255, 255).next();
                                tessellator.draw();
                                matrices.pop();
                            }
                        }

                        float a = this.world.getSkyAngle(tickDelta) * 360.0F;

                        Map<String, String> toReplaceMapRotation = new java.util.HashMap<>(Map.ofEntries(
                                entry("#skyAngle", a + "")
                        ));

                        toReplaceMapRotation.putAll(Util.getReplaceMapNormal());

                        i = 1.0F - this.world.getRainGradient(tickDelta);

                        toReplaceMapRotation.putAll(Util.getReplaceMapNormal());

                        Map<String, String> toReplaceMapStarBrightness = new java.util.HashMap<>(Map.ofEntries(
                                entry("#starAlpha", this.world.method_23787(tickDelta) * i + "")
                        ));

                        toReplaceMapStarBrightness.putAll(Util.getReplaceMapNormal());

                        matrices.push();

                        matrices.multiply(Vec3f.POSITIVE_X.getDegreesQuaternion((float) Util.solveEquation(renderInfo.stars.baseDegreesX, toReplaceMapRotation)));
                        matrices.multiply(Vec3f.POSITIVE_Y.getDegreesQuaternion((float) Util.solveEquation(renderInfo.stars.baseDegreesY, toReplaceMapRotation)));
                        matrices.multiply(Vec3f.POSITIVE_Z.getDegreesQuaternion((float) Util.solveEquation(renderInfo.stars.baseDegreesZ, toReplaceMapRotation)));


                        // Stars

                        float brightness = (float) Util.solveEquation(renderInfo.stars.starBrightness, toReplaceMapStarBrightness);
                        if (brightness > 0.0F) {
                            RenderSystem.setShaderColor(brightness, brightness, brightness, brightness);
                            BackgroundRenderer.clearFog();
                            this.starsBuffer.bind();
                            this.starsBuffer.draw(setRotation(matrices,
                                    Vec3f.POSITIVE_X.getDegreesQuaternion((float) Util.solveEquation(renderInfo.stars.degreesX, toReplaceMapRotation)),
                                    Vec3f.POSITIVE_Y.getDegreesQuaternion((float) Util.solveEquation(renderInfo.stars.degreesY, toReplaceMapRotation)),
                                    Vec3f.POSITIVE_Z.getDegreesQuaternion((float) Util.solveEquation(renderInfo.stars.degreesZ, toReplaceMapRotation)),
                                    new Vec3d(0, 0, 0)), projectionMatrix, GameRenderer.getPositionColorShader());
                            VertexBuffer.unbind();
                            runnable.run();
                        }

                        matrices.pop();

                        RenderSystem.enableTexture();
                        RenderSystem.enableBlend();
                        RenderSystem.blendFuncSeparate(GlStateManager.SrcFactor.SRC_ALPHA, GlStateManager.DstFactor.ONE, GlStateManager.SrcFactor.ONE, GlStateManager.DstFactor.ZERO);

                        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, i);

                        Matrix4f matrix4f2;

                        for (CelestialObject c : renderInfo.skyObjects) {
                            matrices.push();

                            matrices.multiply(Vec3f.POSITIVE_X.getDegreesQuaternion((float) Util.solveEquation(c.baseDegreesX, toReplaceMapRotation)));
                            matrices.multiply(Vec3f.POSITIVE_Y.getDegreesQuaternion((float) Util.solveEquation(c.baseDegreesY, toReplaceMapRotation)));
                            matrices.multiply(Vec3f.POSITIVE_Z.getDegreesQuaternion((float) Util.solveEquation(c.baseDegreesZ, toReplaceMapRotation)));

                            matrix4f2 = setRotation(matrices,
                                    Vec3f.POSITIVE_X.getDegreesQuaternion((float) Util.solveEquation(c.degreesX, toReplaceMapRotation)),
                                            Vec3f.POSITIVE_Y.getDegreesQuaternion((float) Util.solveEquation(c.degreesY, toReplaceMapRotation)),
                                            Vec3f.POSITIVE_Z.getDegreesQuaternion((float) Util.solveEquation(c.degreesZ, toReplaceMapRotation)),
                                            new Vec3d(
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
                                int r = this.world.getMoonPhase();
                                int s = r % 4;
                                int m = r / 4 % 2;
                                float t = (float)(s) / 4.0F;
                                o = (float)(m) / 2.0F;
                                p = (float)(s + 1) / 4.0F;
                                q = (float)(m + 1) / 2.0F;
                                bufferBuilder.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_TEXTURE_COLOR);
                                bufferBuilder.vertex(matrix4f2, -k, distance, k1).texture(p, q).color(r1, g1, b1, a1).next();
                                bufferBuilder.vertex(matrix4f2, k, distance, k1).texture(t, q).color(r1, g1, b1, a1).next();
                                bufferBuilder.vertex(matrix4f2, k, distance, k2).texture(t, o).color(r1, g1, b1, a1).next();
                                bufferBuilder.vertex(matrix4f2, -k, distance, k2).texture(p, o).color(r1, g1, b1, a1).next();
                            }
                            else {
                                bufferBuilder.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_TEXTURE_COLOR);
                                bufferBuilder.vertex(matrix4f2, -k, distance, k1).texture(0.0F, 0.0F).color(r1, g1, b1, a1).next();
                                bufferBuilder.vertex(matrix4f2, k, distance, k1).texture(1.0F, 0.0F).color(r1, g1, b1, a1).next();
                                bufferBuilder.vertex(matrix4f2, k, distance, k2).texture(1.0F, 1.0F).color(r1, g1, b1, a1).next();
                                bufferBuilder.vertex(matrix4f2, -k, distance, k2).texture(0.0F, 1.0F).color(r1, g1, b1, a1).next();
                            }
                            BufferRenderer.drawWithShader(bufferBuilder.end());

                            if (c.celestialObjectProperties.isSolid)
                                RenderSystem.blendFuncSeparate(GlStateManager.SrcFactor.SRC_ALPHA, GlStateManager.DstFactor.ONE, GlStateManager.SrcFactor.ONE, GlStateManager.DstFactor.ZERO);

                            RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
                            matrices.pop();
                        }
                        RenderSystem.disableBlend();
                        RenderSystem.disableTexture();
                        RenderSystem.setShaderColor(0.0F, 0.0F, 0.0F, 1.0F);


                        double d = MinecraftClient.getInstance().player.getCameraPosVec(tickDelta).y - this.world.getLevelProperties().getSkyDarknessHeight(this.world);
                        if (d < 0.0) {
                            matrices.push();
                            matrices.translate(0.0, 12.0, 0.0);
                            this.darkSkyBuffer.bind();
                            this.darkSkyBuffer.draw(matrices.peek().getPositionMatrix(), projectionMatrix, shader);
                            VertexBuffer.unbind();
                            matrices.pop();
                        }

                        //TODO: Fix the things

                        if (this.world.getDimensionEffects().isAlternateSkyColor()) {
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
        Tessellator tess = Tessellator.getInstance();
        BufferBuilder builder = tess.getBuffer();
        RenderSystem.setShader(GameRenderer::getPositionColorShader);

        this.starsBuffer = new VertexBuffer();
        BufferBuilder.BuiltBuffer builtBuffer;
        if (CelestialSky.doesDimensionHaveCustomSky())
            builtBuffer = this.renderCustomStars(builder);
        else
            builtBuffer = renderStars(builder);
        this.starsBuffer.bind();
        this.starsBuffer.upload(builtBuffer);
        VertexBuffer.unbind();
    }

    // Credits to B3Spectacled's Custom Stars for most of this code
    // https://github.com/b3spectacled/custom-stars
    private BufferBuilder.BuiltBuffer renderCustomStars(BufferBuilder buffer) {

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

        buffer.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_COLOR);

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

                        buffer.vertex(x, y, z).color(starColor.getRed() / 255F, starColor.getGreen() / 255F, starColor.getBlue() / 255F, 1.0F).next();
                    }
            }
        }

        return buffer.end();
    }
}
