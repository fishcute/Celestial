package fishcute.celestial.mixin;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import fishcute.celestial.sky.CelestialObject;
import fishcute.celestial.sky.CelestialObjectPopulation;
import fishcute.celestial.sky.CelestialRenderInfo;
import fishcute.celestial.sky.CelestialSky;
import fishcute.celestial.util.Util;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.VertexBuffer;
import net.minecraft.client.render.*;
import net.minecraft.client.texture.TextureManager;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.util.math.Vector3d;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.util.math.*;
import org.apache.commons.lang3.tuple.MutablePair;
import org.apache.commons.lang3.tuple.MutableTriple;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.ArrayList;
import java.util.Map;

@Mixin(WorldRenderer.class)
public class LevelRendererMixin {
    @Shadow
<<<<<<< Updated upstream
    private boolean doesMobEffectBlockSky(Camera camera) {
        return false;
    }
    @Shadow
    private VertexBuffer skyBuffer;
    @Shadow
    private VertexBuffer darkBuffer;
    
    @Shadow
    private ClientLevel level;

    @Shadow
    private BufferBuilder.RenderedBuffer drawStars(BufferBuilder buffer) {
        return null;
    }
=======
    private VertexFormat skyVertexFormat;

    @Shadow
    private VertexBuffer lightSkyBuffer;
>>>>>>> Stashed changes

    @Shadow
    private ClientWorld world;
    private static Matrix4f setRotation(MatrixStack matrices, Quaternion i, Quaternion j, Quaternion k, Vector3d move) {
        matrices.push();

        if (i != null) {
            matrices.multiply(i);
        }
        if (j != null) {
            matrices.multiply(j);
        }
        if (k != null) {
            matrices.multiply(k);
        }

        matrices.translate(move.x, move.y, move.z);

        Matrix4f matrix4f = matrices.peek().getModel();
        matrices.pop();

        return matrix4f;
    }

    @Shadow
    private int ticks = 0;

    @Shadow @Final private TextureManager textureManager;

    @Shadow private @Nullable VertexBuffer darkSkyBuffer;

    @Inject(method = "renderSky", at = @At("HEAD"), cancellable = true)
    private void renderSky(MatrixStack matrices, float tickDelta, CallbackInfo info) {
        // In the midst of chaos, there is also opportunity - Sun Tzu
        
        // Oh, and who could forget this famous quote:
        // Java 8 and MinecraftClient 1.16 mappings are awful - Sun Tzu

        ClientWorld world = MinecraftClient.getInstance().world;
        MinecraftClient mc = MinecraftClient.getInstance();

        if (CelestialSky.doesDimensionHaveCustomSky()) {
            info.cancel();
<<<<<<< Updated upstream
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
                                Matrix4f matrix4f = matrices.last().pose();
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

                                Matrix4f matrix4f = matrices.last().pose();
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
=======
            if (MinecraftClient.getInstance().world.getSkyProperties().getSkyType() == SkyProperties.SkyType.NORMAL) {
                RenderSystem.disableTexture();
                Vec3d vector3d = this.world.method_23777(MinecraftClient.getInstance().gameRenderer.getCamera().getBlockPos(), tickDelta);
                float f = (float) vector3d.x;
                float g = (float) vector3d.y;
                float h = (float) vector3d.z;
                BackgroundRenderer.setFogBlack();
                BufferBuilder bufferBuilder = Tessellator.getInstance().getBuffer();
                RenderSystem.depthMask(false);
                RenderSystem.enableFog();
                RenderSystem.color4f(f, g, h, 1.0F);

                this.lightSkyBuffer.bind();
                this.skyVertexFormat.startDrawing(0L);
                this.lightSkyBuffer.draw(matrices.peek().getModel(), 7);
                VertexBuffer.unbind();
                this.skyVertexFormat.endDrawing();
                RenderSystem.disableFog();
                RenderSystem.disableAlphaTest();
                RenderSystem.enableBlend();
                RenderSystem.defaultBlendFunc();

                CelestialRenderInfo renderInfo = CelestialSky.getDimensionRenderInfo();
                float[] fs = this.world.getSkyProperties().getFogColorOverride(this.world.getSkyAngle(tickDelta), tickDelta);

                if (renderInfo.renderType.equals(CelestialRenderInfo.RenderType.NORMAL)) {
                    if (fs != null) {
                        RenderSystem.disableTexture();
                        RenderSystem.shadeModel(7425);
                        matrices.push();
                        matrices.multiply(Vec3f.POSITIVE_X.getDegreesQuaternion(90.0F));
                        float f3 = MathHelper.sin(this.world.getSkyAngleRadians(tickDelta)) < 0.0F ? 180.0F : 0.0F;
                        matrices.multiply(Vec3f.POSITIVE_Z.getDegreesQuaternion(f3));
                        matrices.multiply(Vec3f.POSITIVE_Z.getDegreesQuaternion(90.0F));
                        float f4 = fs[0];
                        float f5 = fs[1];
                        float f6 = fs[2];
                        Matrix4f matrix4f = matrices.peek().getModel();
                        bufferBuilder.begin(6, VertexFormats.POSITION_COLOR);
                        bufferBuilder.vertex(matrix4f, 0.0F, 100.0F, 0.0F).color(f4, f5, f6, fs[3]).next();

                        for (int j = 0; j <= 16; ++j) {
                            float f7 = (float) j * ((float) Math.PI * 2F) / 16.0F;
                            float f8 = MathHelper.sin(f7);
                            float f9 = MathHelper.cos(f7);
                            bufferBuilder.vertex(matrix4f, f8 * 120.0F, f9 * 120.0F, -f9 * 40.0F * fs[3]).color(fs[0], fs[1], fs[2], 0.0F).next();
                        }

                        bufferBuilder.end();
                        BufferRenderer.draw(bufferBuilder);
                        matrices.pop();
                        RenderSystem.shadeModel(7424);
                    }
                } else if (renderInfo.renderType.equals(CelestialRenderInfo.RenderType.SKYBOX)) {
                    RenderSystem.enableTexture();
                    this.textureManager.bindTexture(renderInfo.skyboxTexture);
                    Tessellator tessellator = Tessellator.getInstance();
                    BufferBuilder bufferbuilder = tessellator.getBuffer();

                    for (int l = 0; l < 6; ++l) {
                        matrices.push();
                        if (l == 1) {
                            matrices.multiply(Vec3f.POSITIVE_X.getDegreesQuaternion(90.0F));
                        }

                        if (l == 2) {
                            matrices.multiply(Vec3f.POSITIVE_X.getDegreesQuaternion(-90.0F));
                        }
>>>>>>> Stashed changes

                        if (l == 3) {
                            matrices.multiply(Vec3f.POSITIVE_X.getDegreesQuaternion(180.0F));
                        }

                        if (l == 4) {
                            matrices.multiply(Vec3f.POSITIVE_Z.getDegreesQuaternion(90.0F));
                        }

                        if (l == 5) {
                            matrices.multiply(Vec3f.POSITIVE_Z.getDegreesQuaternion(-90.0F));
                        }

                        Matrix4f matrix4f = matrices.peek().getModel();
                        bufferbuilder.begin(7, VertexFormats.POSITION_TEXTURE_COLOR);
                        bufferbuilder.vertex(matrix4f, -100.0F, -100.0F, -100.0F).texture(0.0F, 0.0F).color(255, 255, 255, 255).next();
                        bufferbuilder.vertex(matrix4f, -100.0F, -100.0F, 100.0F).texture(0.0F, 16.0F).color(255, 255, 255, 255).next();
                        bufferbuilder.vertex(matrix4f, 100.0F, -100.0F, 100.0F).texture(16.0F, 16.0F).color(255, 255, 255, 255).next();
                        bufferbuilder.vertex(matrix4f, 100.0F, -100.0F, -100.0F).texture(16.0F, 0.0F).color(255, 255, 255, 255).next();
                        tessellator.draw();
                        matrices.pop();
                    }
                }

                float a = this.world.getSkyAngleRadians(tickDelta) * 360.0F;

                Map<String, String> toReplaceMapRotation = new HashMap<>();
                toReplaceMapRotation.put("#skyAngle", a + "");
                toReplaceMapRotation = Util.getReplaceMapAdd(toReplaceMapRotation);

                RenderSystem.enableTexture();
                RenderSystem.enableBlend();
                RenderSystem.blendFuncSeparate(GlStateManager.SrcFactor.SRC_ALPHA, GlStateManager.DstFactor.ONE, GlStateManager.SrcFactor.ONE, GlStateManager.DstFactor.ZERO);

                RenderSystem.blendFuncSeparate(GlStateManager.SrcFactor.SRC_ALPHA, GlStateManager.DstFactor.ONE, GlStateManager.SrcFactor.ONE, GlStateManager.DstFactor.ZERO);

                //I probably shouldn't have to do this
                ArrayList<CelestialObject> renderOrderColor = new ArrayList<>();

                ArrayList<CelestialObject> renderOrderTexture = new ArrayList<>();

                ArrayList<CelestialObject> renderOrder = new ArrayList<>();

                for (CelestialObject c : renderInfo.skyObjects) {
                    if (c.isPopulation() && ((CelestialObjectPopulation) c).population.size() > 0) {

                        if (((CelestialObjectPopulation) c).baseObject.texture != null)
                            renderOrderTexture.add(c);
                        else if (((CelestialObjectPopulation) c).baseObject.solidColor != null)
                            renderOrderColor.add(c);
                    } else {
                        if (c.texture != null)
                            renderOrderTexture.add(c);
                        else if (c.solidColor != null)
                            renderOrderColor.add(c);
                    }
                }

                renderOrder.addAll(renderOrderColor);
                renderOrder.addAll(renderOrderTexture);

                // In case you are wondering, Celestial barely works, and it's probably going to stay that way for a while.

                for (CelestialObject c : renderOrder) {

                    matrices.push();

                    if (c.isPopulation()) {
                        matrices.multiply(Vec3f.POSITIVE_X.getDegreesQuaternion((float) Util.solveEquation(((CelestialObjectPopulation) c).baseObject.baseDegreesX, toReplaceMapRotation)));
                        matrices.multiply(Vec3f.POSITIVE_Y.getDegreesQuaternion((float) Util.solveEquation(((CelestialObjectPopulation) c).baseObject.baseDegreesY, toReplaceMapRotation)));
                        matrices.multiply(Vec3f.POSITIVE_Z.getDegreesQuaternion((float) Util.solveEquation(((CelestialObjectPopulation) c).baseObject.baseDegreesZ, toReplaceMapRotation)));

                        Object[] dataArray = null;

                        if (!((CelestialObjectPopulation) c).perObjectCalculation)
                            dataArray = getObjectDataArray(((CelestialObjectPopulation) c).baseObject, toReplaceMapRotation);

                        for (CelestialObject c2 : ((CelestialObjectPopulation) c).population) {
                            if (((CelestialObjectPopulation) c).perObjectCalculation)
                                dataArray = getObjectDataArray(((CelestialObjectPopulation) c).baseObject, toReplaceMapRotation);

                            // Making things people will never understand is my passion
                            // The function below this text is truly beautiful, isn't it?

                            renderSkyObject(bufferBuilder,
                                    setRotation(matrices,
                                            Vec3f.POSITIVE_X.getDegreesQuaternion((float) ((float) dataArray[0] + c2.populateDegreesX)),
                                            Vec3f.POSITIVE_Y.getDegreesQuaternion((float) ((float) dataArray[1] + c2.populateDegreesY)),
                                            Vec3f.POSITIVE_Z.getDegreesQuaternion((float) ((float) dataArray[2] + c2.populateDegreesZ)),
                                            new Vector3d(
                                                    (float) dataArray[3] + c2.populatePosX,
                                                    (float) dataArray[4] + c2.populatePosY,
                                                    (float) dataArray[5] + c2.populatePosZ
                                            ))
                                    , c2,
                                    (Vec3f) dataArray[11],
                                    (Vec3f) dataArray[12],
                                    (float) dataArray[6],
                                    (float) dataArray[7],
                                    (float) dataArray[8],
                                    (int) dataArray[9],
                                    (ArrayList<MutablePair<MutableTriple<Float, Float, Float>, MutablePair<Float, Float>>>) dataArray[10]
                            );
                        }
                    } else {
                        matrices.multiply(Vec3f.POSITIVE_X.getDegreesQuaternion((float) Util.solveEquation(c.baseDegreesX, toReplaceMapRotation)));
                        matrices.multiply(Vec3f.POSITIVE_Y.getDegreesQuaternion((float) Util.solveEquation(c.baseDegreesY, toReplaceMapRotation)));
                        matrices.multiply(Vec3f.POSITIVE_Z.getDegreesQuaternion((float) Util.solveEquation(c.baseDegreesZ, toReplaceMapRotation)));

                        Object[] dataArray = getObjectDataArray(c, toReplaceMapRotation);

                        renderSkyObject(bufferBuilder,
                                setRotation(matrices,
                                        Vec3f.POSITIVE_X.getDegreesQuaternion((float) dataArray[0]),
                                        Vec3f.POSITIVE_Y.getDegreesQuaternion((float) dataArray[1]),
                                        Vec3f.POSITIVE_Z.getDegreesQuaternion((float) dataArray[2]),
                                        new Vector3d(
                                                (float) dataArray[3],
                                                (float) dataArray[4],
                                                (float) dataArray[5]
                                        ))
                                , c,
                                (Vec3f) dataArray[11],
                                (Vec3f) dataArray[12],
                                (float) dataArray[6],
                                (float) dataArray[7],
                                (float) dataArray[8],
                                (int) dataArray[9],
                                (ArrayList<MutablePair<MutableTriple<Float, Float, Float>, MutablePair<Float, Float>>>) dataArray[10]
                        );
                    }

                    matrices.pop();
                }

                RenderSystem.enableFog();

                RenderSystem.disableBlend();
                RenderSystem.disableTexture();
                RenderSystem.color4f(0.0F, 0.0F, 0.0F, 1.0F);


                double d = MinecraftClient.getInstance().player.getCameraPosVec((float) tickDelta).y - this.world.getLevelProperties().getSkyDarknessHeight();
                if (d < 0.0) {
                    matrices.push();
                    matrices.translate(0.0, 12.0, 0.0);
                    this.darkSkyBuffer.bind();
                    this.skyVertexFormat.startDrawing(0L);
                    this.darkSkyBuffer.draw(matrices.peek().getModel(), 7);
                    VertexBuffer.unbind();
                    this.skyVertexFormat.endDrawing();
                    matrices.pop();
                }
                if (this.world.getSkyProperties().isAlternateSkyColor()) {
                    RenderSystem.color3f(f * 0.2f + 0.04f, g * 0.2f + 0.04f, h * 0.6f + 0.1f);
                } else {
                    RenderSystem.color3f(f, g, h);
                }
                RenderSystem.enableTexture();
                RenderSystem.depthMask(true);
                RenderSystem.disableFog();
            }
        }
    }

    private Object[] getObjectDataArray(CelestialObject c, Map<String, String> replaceMapRotation) {
        Object[] dataArray = new Object[13];

        //degrees x 0
        dataArray[0] = ((float) Util.solveEquation(c.degreesX, replaceMapRotation));

        //degrees y 1
        dataArray[1] = ((float) Util.solveEquation(c.degreesY, replaceMapRotation));

        //degrees z 2
        dataArray[2] = ((float) Util.solveEquation(c.degreesZ, replaceMapRotation));

        //pos x 3
        dataArray[3] = ((float) Util.solveEquation(c.posX, Util.getReplaceMapNormal()));

        //pos y 4
        dataArray[4] = ((float) Util.solveEquation(c.posY, Util.getReplaceMapNormal()));

        //pos z 5
        dataArray[5] = ((float) Util.solveEquation(c.posZ, Util.getReplaceMapNormal()));

        //alpha 6
        dataArray[6] = (((float) Util.solveEquation(c.celestialObjectProperties.alpha, Util.getReplaceMapNormal())));

        //distance 7
        dataArray[7] = ((float) Util.solveEquation(c.distance, Util.getReplaceMapNormal()));

        //scale 8
        dataArray[8] = ((float) Util.solveEquation(c.scale, Util.getReplaceMapNormal()));

        //moon phase 9

        Map<String, String> toReplaceMap = new HashMap<>();
        toReplaceMap.put("#moonPhase", this.world.getMoonPhase() + "");
        
        dataArray[9] = ((int) Util.solveEquation(c.celestialObjectProperties.moonPhase, Util.getReplaceMapAdd(toReplaceMap)));

        ArrayList<MutablePair<MutableTriple<Float, Float, Float>, MutablePair<Float, Float>>> vertexList = new ArrayList<>();

        if (c.vertexList != null && c.vertexList.size() > 0)

            for (MutablePair<MutableTriple<String, String, String>, MutablePair<String, String>> v : c.vertexList)
                vertexList.add(new MutablePair<>(new MutableTriple<>(
                        (float) Util.solveEquation(v.getLeft().getLeft(), Util.getReplaceMapNormal()),
                        (float) Util.solveEquation(v.getLeft().getMiddle(), Util.getReplaceMapNormal()),
                        (float) Util.solveEquation(v.getLeft().getRight(), Util.getReplaceMapNormal())
                ), new MutablePair<>(
                        (float) Util.solveEquation(v.getRight().getLeft(), Util.getReplaceMapNormal()),
                        (float) Util.solveEquation(v.getRight().getRight(), Util.getReplaceMapNormal())
                )));

        // vertex list 10
        dataArray[10] = (vertexList);

        // colors 11
        dataArray[11] = (new Vec3f(
                (float) ((Util.solveEquation(c.celestialObjectProperties.red, Util.getReplaceMapNormal()))),
                (float) ((Util.solveEquation(c.celestialObjectProperties.green, Util.getReplaceMapNormal()))),
                (float) ((Util.solveEquation(c.celestialObjectProperties.blue, Util.getReplaceMapNormal())))));

        //solid colors 12
        if (c.solidColor != null)
<<<<<<< Updated upstream
            dataArray[12] = (new Vector3f(
                    (c.solidColor.getRed() * 255) * (((Vector3f) dataArray[11]).x()),
                    (c.solidColor.getGreen() * 255) * (((Vector3f) dataArray[11]).y()),
                    (c.solidColor.getBlue() * 255) * (((Vector3f) dataArray[11]).z())));
=======
            dataArray[12] = (new Vec3f(
                    (c.solidColor.getRed()) * (((Vec3f) dataArray[11]).getX()),
                    (c.solidColor.getGreen()) * (((Vec3f) dataArray[11]).getY()),
                    (c.solidColor.getBlue()) * (((Vec3f) dataArray[11]).getZ())));
>>>>>>> Stashed changes
        else
            dataArray[12] = (null);
        return dataArray;
    }
    
    // Render sky object
    private void renderSkyObject(BufferBuilder bufferBuilder, Matrix4f matrix4f2, CelestialObject c, Vec3f color, Vec3f colorsSolid, float alpha, float distancePre, float scalePre, int moonPhase, ArrayList<MutablePair<MutableTriple<Float, Float, Float>, MutablePair<Float, Float>>> vertexList) {
        RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);

        float distance = (float) (distancePre + c.populateDistanceAdd);

        float scale = (float) (scalePre + c.populateScaleAdd);

        RenderSystem.enableBlend();

        // Set texture
        if (c.texture != null)
            this.textureManager.bindTexture(c.texture);

        if (c.celestialObjectProperties.ignoreFog)
            RenderSystem.disableFog();
        else {
            RenderSystem.enableFog();
        }

        if (c.celestialObjectProperties.isSolid)
            RenderSystem.defaultBlendFunc();

        if (c.texture != null) {
<<<<<<< Updated upstream
            RenderSystem.setShader(GameRenderer::getPositionTexShader);
            RenderSystem.setShaderColor(color.x(), color.y(), color.z(), alpha);
=======
            RenderSystem.color4f(color.getX(), color.getY(), color.getZ(), alpha);
>>>>>>> Stashed changes

            if (c.celestialObjectProperties.hasMoonPhases) {
                int l = (moonPhase % 4);
                int i1 = (moonPhase / 4 % 2);
                float f13 = l / 4.0F;
                float f14 = i1 / 2.0F;
                float f15 = (l + 1) / 4.0F;
                float f16 = (i1 + 1) / 2.0F;
                bufferBuilder.begin(7, VertexFormats.POSITION_TEXTURE_COLOR);
                bufferBuilder.vertex(matrix4f2, -scale, -100.0F, (distance < 0 ? scale : -scale)).texture(f15, f16).color(color.getX(), color.getY(), color.getZ(), alpha).next();
                bufferBuilder.vertex(matrix4f2, scale, -100.0F, (distance < 0 ? scale : -scale)).texture(f13, f16).color(color.getX(), color.getY(), color.getZ(), alpha).next();
                bufferBuilder.vertex(matrix4f2, scale, -100.0F, (distance < 0 ? -scale : scale)).texture(f13, f14).color(color.getX(), color.getY(), color.getZ(), alpha).next();
                bufferBuilder.vertex(matrix4f2, -scale, -100.0F, (distance < 0 ? -scale : scale)).texture(f15, f14).color(color.getX(), color.getY(), color.getZ(), alpha).next();
            } else if (c.vertexList.size() > 0) {
                //Stuff for custom vertex stuff
                //Honestly, don't even ask what's going on here anymore
                bufferBuilder.begin(7, VertexFormats.POSITION_TEXTURE_COLOR);
                for (MutablePair<MutableTriple<Float, Float, Float>, MutablePair<Float, Float>> v : vertexList) {
                    bufferBuilder.vertex(matrix4f2,
                            v.getLeft().getLeft(),
                            v.getLeft().getMiddle(),
                            v.getLeft().getRight()
                    ).texture(
                            v.getRight().getLeft(),
                            v.getRight().getRight()
                    ).color(color.getX(), color.getY(), color.getZ(), alpha).next();
                }
            } else {
                bufferBuilder.begin(7, VertexFormats.POSITION_TEXTURE_COLOR);
                bufferBuilder.vertex(matrix4f2, -scale, distance, (distance < 0 ? scale : -scale)).texture(0.0F, 0.0F).color(color.getX(), color.getY(), color.getZ(), alpha).next();
                bufferBuilder.vertex(matrix4f2, scale, distance, (distance < 0 ? scale : -scale)).texture(1.0F, 0.0F).color(color.getX(), color.getY(), color.getZ(), alpha).next();
                bufferBuilder.vertex(matrix4f2, scale, distance, (distance < 0 ? -scale : scale)).texture(1.0F, 1.0F).color(color.getX(), color.getY(), color.getZ(), alpha).next();
                bufferBuilder.vertex(matrix4f2, -scale, distance, (distance < 0 ? -scale : scale)).texture(0.0F, 1.0F).color(color.getX(), color.getY(), color.getZ(), alpha).next();
            }
        }
        else if (colorsSolid != null) {
<<<<<<< Updated upstream
            RenderSystem.setShaderColor(colorsSolid.x(), colorsSolid.y(), colorsSolid.z(), alpha);
=======
            RenderSystem.color4f(1.0F, 1.0F, 1.0F, alpha);
>>>>>>> Stashed changes

            if (c.vertexList.size() > 0) {
                bufferBuilder.begin(7, VertexFormats.POSITION_COLOR);

                for (MutablePair<MutableTriple<Float, Float, Float>, MutablePair<Float, Float>> v : vertexList) {
                    bufferBuilder.vertex(matrix4f2,
                            v.getLeft().getLeft(),
                            v.getLeft().getMiddle(),
                            v.getLeft().getRight()
                    ).texture(
                            v.getRight().getLeft(),
                            v.getRight().getRight()
                    ).color(color.getX(), color.getY(), color.getZ(), alpha).next();
                }
            } else {
<<<<<<< Updated upstream
                bufferBuilder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR);
                bufferBuilder.vertex(matrix4f2, -scale, distance, (distance < 0 ? scale : -scale)).color(colorsSolid.x(), colorsSolid.y(), colorsSolid.z(), alpha).endVertex();
                bufferBuilder.vertex(matrix4f2, scale, distance, (distance < 0 ? scale : -scale)).color(colorsSolid.x(), colorsSolid.y(), colorsSolid.z(), alpha).endVertex();
                bufferBuilder.vertex(matrix4f2, scale, distance, (distance < 0 ? -scale : scale)).color(colorsSolid.x(), colorsSolid.y(), colorsSolid.z(), alpha).endVertex();
                bufferBuilder.vertex(matrix4f2, -scale, distance, (distance < 0 ? -scale : scale)).color(colorsSolid.x(), colorsSolid.y(), colorsSolid.z(), alpha).endVertex();
=======
                bufferBuilder.begin(7, VertexFormats.POSITION_COLOR);
                bufferBuilder.vertex(matrix4f2, -scale, distance, (distance < 0 ? scale : -scale)).color(colorsSolid.getX() / 255.0F, colorsSolid.getY() / 255.0F, colorsSolid.getZ() / 255.0F, alpha).next();
                bufferBuilder.vertex(matrix4f2, scale, distance, (distance < 0 ? scale : -scale)).color(colorsSolid.getX() / 255.0F, colorsSolid.getY() / 255.0F, colorsSolid.getZ() / 255.0F, alpha).next();
                bufferBuilder.vertex(matrix4f2, scale, distance, (distance < 0 ? -scale : scale)).color(colorsSolid.getX() / 255.0F, colorsSolid.getY() / 255.0F, colorsSolid.getZ() / 255.0F, alpha).next();
                bufferBuilder.vertex(matrix4f2, -scale, distance, (distance < 0 ? -scale : scale)).color(colorsSolid.getX() / 255.0F, colorsSolid.getY() / 255.0F, colorsSolid.getZ() / 255.0F, alpha).next();
>>>>>>> Stashed changes
            }

            RenderSystem.enableTexture();
        }

<<<<<<< Updated upstream
        BufferUploader.drawWithShader(bufferBuilder.end());
=======
        bufferBuilder.end();
        BufferRenderer.draw(bufferBuilder);
>>>>>>> Stashed changes

        if (c.celestialObjectProperties.isSolid)
            RenderSystem.blendFuncSeparate(GlStateManager.SrcFactor.SRC_ALPHA, GlStateManager.DstFactor.ONE, GlStateManager.SrcFactor.ONE, GlStateManager.DstFactor.ZERO);

        RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
    }
}
