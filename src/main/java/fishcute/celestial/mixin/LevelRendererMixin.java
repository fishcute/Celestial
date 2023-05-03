package fishcute.celestial.mixin;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import com.mojang.math.*;
import fishcute.celestial.sky.*;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

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
    private VertexBuffer darkBuffer;

    @Shadow
    private ClientLevel level;

    @Shadow
    private BufferBuilder.RenderedBuffer drawStars(BufferBuilder buffer) {
        return null;
    }

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

    private HashMap<String, Util.DynamicValue> replaceMap = new HashMap<>();

    private void setupReplaceMap() {
        replaceMap = Util.getReplaceMapNormal();
        Util.DynamicValue v = new Util.DynamicValue() {
            @Override
            public double getValue() {
                return 0;
            }
        };

        replaceMap.put("#populateDegreesX", v);
        replaceMap.put("#populateDegreesY", v);
        replaceMap.put("#populateDegreesZ", v);
        replaceMap.put("#populatePosX", v);
        replaceMap.put("#populatePosY", v);
        replaceMap.put("#populatePosZ", v);
        replaceMap.put("#populateDistance", v);
        replaceMap.put("#populateScale", v);
        replaceMap.put("#populateId", v);
    }

    private final Util.MutableDynamicValue a1 = new Util.MutableDynamicValue();
    private final Util.MutableDynamicValue b1 = new Util.MutableDynamicValue();
    private final Util.MutableDynamicValue c1 = new Util.MutableDynamicValue();
    private final Util.MutableDynamicValue d1 = new Util.MutableDynamicValue();
    private final Util.MutableDynamicValue e1 = new Util.MutableDynamicValue();
    private final Util.MutableDynamicValue f1 = new Util.MutableDynamicValue();
    private final Util.MutableDynamicValue g1 = new Util.MutableDynamicValue();
    private final Util.MutableDynamicValue h1 = new Util.MutableDynamicValue();
    private final Util.MutableDynamicValue i1 = new Util.MutableDynamicValue();

    @Inject(method = "renderSky", at = @At("HEAD"), cancellable = true)
    private void renderSky(PoseStack matrices, Matrix4f projectionMatrix, float tickDelta, Camera camera, boolean bl, Runnable runnable, CallbackInfo info) {
        // In the midst of chaos, there is also opportunity - Sun Tzu
        if (CelestialSky.doesDimensionHaveCustomSky()) {
            info.cancel();

            runnable.run();
            FogType fogType = camera.getFluidInCamera();
            if (fogType != FogType.POWDER_SNOW && fogType != FogType.LAVA && !(doesMobEffectBlockSky(camera))) {
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
                float k;
                float o;
                float p;
                float q;

                CelestialRenderInfo renderInfo = CelestialSky.getDimensionRenderInfo();

                VertexBuffer.unbind();
                RenderSystem.enableBlend();
                RenderSystem.defaultBlendFunc();

                this.skyBuffer.bind();
                this.skyBuffer.drawWithShader(matrices.last().pose(), projectionMatrix, shader);
                if (fs != null) {
                    RenderSystem.setShader(GameRenderer::getPositionColorShader);
                    RenderSystem.disableTexture();
                    RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
                    matrices.pushPose();
                    matrices.mulPose(Vector3f.XP.rotationDegrees(90.0F));
                    float f3 = Mth.sin(this.level.getSunAngle(tickDelta)) < 0.0F ? 180.0F : 0.0F;
                    matrices.mulPose(Vector3f.ZP.rotationDegrees(f3));
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

                RenderSystem.enableTexture();
                RenderSystem.enableBlend();

                RenderSystem.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);

                //I probably shouldn't have to do this
                ArrayList<CelestialObject> renderOrderColor = new ArrayList<>();

                ArrayList<CelestialObject> renderOrderTexture = new ArrayList<>();

                ArrayList<CelestialObject> renderOrder = new ArrayList<>();

                for (CelestialObject c : renderInfo.skyObjects) {
                    if (c.isPopulation() && ((CelestialObjectPopulation) c).population.size() > 0) {

                        if (((CelestialObjectPopulation) c).baseObject.texture != null)
                            renderOrderTexture.add(c);
                        else if (((CelestialObjectPopulation) c).baseObject.solidColor != null) {
                            renderOrderColor.add(c);

                            ((CelestialObjectPopulation) c).baseObject.solidColor.tick();
                        }
                    } else {
                        if (c.type == CelestialObject.CelestialObjectType.DEFAULT || c.type == CelestialObject.CelestialObjectType.SKYBOX) {
                            renderOrderTexture.add(c);
                            if (c.celestialObjectProperties.color != null)
                                c.celestialObjectProperties.color.tick();
                        }
                        else if (c.solidColor != null) {
                            renderOrderColor.add(c);
                            c.solidColor.tick();
                        }
                    }
                }

                renderOrder.addAll(renderOrderColor);
                renderOrder.addAll(renderOrderTexture);

                int count;

                for (CelestialObject c : renderOrder) {
                    count = 0;

                    matrices.pushPose();

                    if (c.isPopulation()) {

                        matrices.mulPose(Vector3f.XP.rotationDegrees((float) Util.solveEquation(((CelestialObjectPopulation) c).baseObject.baseDegreesX, replaceMap)));
                        matrices.mulPose(Vector3f.YP.rotationDegrees((float) Util.solveEquation(((CelestialObjectPopulation) c).baseObject.baseDegreesY, replaceMap)));
                        matrices.mulPose(Vector3f.ZP.rotationDegrees((float) Util.solveEquation(((CelestialObjectPopulation) c).baseObject.baseDegreesZ, replaceMap)));

                        // Checks if replace map has special values yet
                        if (!replaceMap.containsKey("#populateId")) {
                            setupReplaceMap();
                        }

                        Object[] dataArray = getObjectDataArray(((CelestialObjectPopulation) c).baseObject, replaceMap);

                        for (CelestialObject c2 : ((CelestialObjectPopulation) c).population) {
                            if (((CelestialObjectPopulation) c).perObjectCalculation) {
                                a1.value = ((float) dataArray[0] + c2.populateDegreesX);
                                replaceMap.put("#populateDegreesX", a1);
                                b1.value = ((float) dataArray[1] + c2.populateDegreesY);
                                replaceMap.put("#populateDegreesY", b1);
                                c1.value = ((float) dataArray[2] + c2.populateDegreesZ);
                                replaceMap.put("#populateDegreesZ", c1);

                                d1.value = ((float) dataArray[3] + c2.populatePosX);
                                replaceMap.put("#populatePosX", d1);
                                e1.value = ((float) dataArray[4] + c2.populatePosY);
                                replaceMap.put("#populatePosY", e1);
                                f1.value = ((float) dataArray[5] + c2.populatePosZ);
                                replaceMap.put("#populatePosZ", f1);

                                g1.value = ((float) dataArray[7] + c2.populateDistanceAdd);
                                replaceMap.put("#populateDistance", g1);
                                h1.value = ((float) dataArray[8] + c2.populateScaleAdd);
                                replaceMap.put("#populateScale", h1);

                                i1.value = count;
                                replaceMap.put("#populateId", i1);

                                dataArray = getObjectDataArray(((CelestialObjectPopulation) c).baseObject, replaceMap);
                            }

                            // Making things people will never understand is my passion
                            // The function below this text is truly beautiful, isn't it?

                            renderSkyObject(bufferBuilder, matrices,
                                    setRotation(matrices,
                                            Vector3f.XP.rotationDegrees((float) ((float) dataArray[0] + c2.populateDegreesX)),
                                            Vector3f.YP.rotationDegrees((float) ((float) dataArray[1] + c2.populateDegreesY)),
                                            Vector3f.ZP.rotationDegrees((float) ((float) dataArray[2] + c2.populateDegreesZ)),
                                            new Vector3d(
                                                    (float) dataArray[3] + c2.populatePosX,
                                                    (float) dataArray[4] + c2.populatePosY,
                                                    (float) dataArray[5] + c2.populatePosZ
                                            ))
                                    , c2,
                                    (Vector3f) dataArray[11],
                                    (Vector3f) dataArray[12],
                                    (float) dataArray[6],
                                    (float) dataArray[7],
                                    (float) dataArray[8],
                                    (int) dataArray[9],
                                    (ArrayList<Util.VertexPointValue>) dataArray[10],
                                    replaceMap
                            );
                            count++;
                        }
                    } else {
                        if (!c.type.equals(CelestialObject.CelestialObjectType.SKYBOX)) {
                            matrices.mulPose(Vector3f.XP.rotationDegrees((float) Util.solveEquation(c.baseDegreesX, Util.getReplaceMapNormal())));
                            matrices.mulPose(Vector3f.YP.rotationDegrees((float) Util.solveEquation(c.baseDegreesY, Util.getReplaceMapNormal())));
                            matrices.mulPose(Vector3f.ZP.rotationDegrees((float) Util.solveEquation(c.baseDegreesZ, Util.getReplaceMapNormal())));
                        }

                        Object[] dataArray = getObjectDataArray(c);

                        if (c.type.equals(CelestialObject.CelestialObjectType.SKYBOX))
                            renderSkyObject(bufferBuilder, matrices,
                                    matrices.last().pose()
                                    , c,
                                    (Vector3f) dataArray[11],
                                    (Vector3f) dataArray[12],
                                    (float) dataArray[6],
                                    (float) dataArray[7],
                                    (float) dataArray[8],
                                    (int) dataArray[9],
                                    (ArrayList<Util.VertexPointValue>) dataArray[10],
                                    Util.getReplaceMapNormal()
                            );
                        else
                            renderSkyObject(bufferBuilder, matrices,
                                    setRotation(matrices,
                                            Vector3f.XP.rotationDegrees((float) dataArray[0]),
                                            Vector3f.YP.rotationDegrees((float) dataArray[1]),
                                            Vector3f.ZP.rotationDegrees((float) dataArray[2]),
                                            new Vector3d(
                                                    (float) dataArray[3],
                                                    (float) dataArray[4],
                                                    (float) dataArray[5]
                                            ))
                                    , c,
                                    (Vector3f) dataArray[11],
                                    (Vector3f) dataArray[12],
                                    (float) dataArray[6],
                                    (float) dataArray[7],
                                    (float) dataArray[8],
                                    (int) dataArray[9],
                                    (ArrayList<Util.VertexPointValue>) dataArray[10],
                                    Util.getReplaceMapNormal()
                            );
                    }

                    matrices.popPose();
                }

                FogRenderer.levelFogColor();

                RenderSystem.disableBlend();
                RenderSystem.disableTexture();
                RenderSystem.setShaderColor(0.0F, 0.0F, 0.0F, 1.0F);


                double d = Minecraft.getInstance().player.getEyePosition(tickDelta).y - this.level.getLevelData().getHorizonHeight(this.level);
                if (d < 0.0) {
                    matrices.pushPose();
                    matrices.translate(0.0, 12.0 + Util.solveEquation(renderInfo.environment.voidCullingLevel, Util.getReplaceMapNormal()), 0.0);
                    this.darkBuffer.bind();
                    this.darkBuffer.drawWithShader(matrices.last().pose(), projectionMatrix, shader);
                    VertexBuffer.unbind();
                    matrices.popPose();
                }

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

    private Object[] getObjectDataArray(CelestialObject c) {
        return getObjectDataArray(c, Util.getReplaceMapNormal());
    }

    private Object[] getObjectDataArray(CelestialObject c, HashMap<String, Util.DynamicValue> replaceMap) {

        Object[] dataArray = new Object[13];

        //degrees x 0
        dataArray[0] = ((float) Util.solveEquation(c.degreesX, replaceMap));

        //degrees y 1
        dataArray[1] = ((float) Util.solveEquation(c.degreesY, replaceMap));

        //degrees z 2
        dataArray[2] = ((float) Util.solveEquation(c.degreesZ, replaceMap));

        //pos x 3
        dataArray[3] = ((float) Util.solveEquation(c.posX, replaceMap));

        //pos y 4
        dataArray[4] = ((float) Util.solveEquation(c.posY, replaceMap));

        //pos z 5
        dataArray[5] = ((float) Util.solveEquation(c.posZ, replaceMap));

        //alpha 6
        dataArray[6] = (((float) Util.solveEquation(c.celestialObjectProperties.alpha, replaceMap)));

        //distance 7
        dataArray[7] = ((float) Util.solveEquation(c.distance, replaceMap));

        //scale 8
        dataArray[8] = ((float) Util.solveEquation(c.scale, replaceMap));

        //moon phase 9
        dataArray[9] = ((int) Util.solveEquation(c.celestialObjectProperties.moonPhase, replaceMap));

        ArrayList<Util.VertexPointValue> vertexList = new ArrayList<>();

        if (c.vertexList != null && c.vertexList.size() > 0)
            for (Util.VertexPoint v : c.vertexList)
                vertexList.add(new Util.VertexPointValue(v));

        // vertex list 10
        dataArray[10] = (vertexList);

        // colors 11
        dataArray[11] = (new Vector3f(
                c.celestialObjectProperties.getRed(),
                c.celestialObjectProperties.getGreen(),
                c.celestialObjectProperties.getBlue()));

        //solid colors 12
        if (c.solidColor != null)
            dataArray[12] = (new Vector3f(
                    (c.solidColor.storedColor.getRed()) * (((Vector3f) dataArray[11]).x()),
                    (c.solidColor.storedColor.getGreen()) * (((Vector3f) dataArray[11]).y()),
                    (c.solidColor.storedColor.getBlue()) * (((Vector3f) dataArray[11]).z())));
        else
            dataArray[12] = (null);
        return dataArray;
    }

    // Render sky object
    private void renderSkyObject(BufferBuilder bufferBuilder, PoseStack matrices, Matrix4f matrix4f2, CelestialObject c, Vector3f color, Vector3f colorsSolid, float alpha, float distancePre, float scalePre, int moonPhase, ArrayList<Util.VertexPointValue> vertexList,  HashMap<String, Util.DynamicValue> objectReplaceMap) {
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);

        float distance = (float) (distancePre + c.populateDistanceAdd);

        float scale = (float) (scalePre + c.populateScaleAdd);

        RenderSystem.enableBlend();

        // Set texture
        if (c.texture != null)
            RenderSystem.setShaderTexture(0, c.texture);

        if (c.celestialObjectProperties.ignoreFog)
            FogRenderer.setupNoFog();
        else
            FogRenderer.levelFogColor();

        if (c.celestialObjectProperties.isSolid)
            RenderSystem.defaultBlendFunc();

        if (c.type.equals(CelestialObject.CelestialObjectType.DEFAULT)) {
            RenderSystem.setShader(GameRenderer::getPositionTexShader);
            RenderSystem.setShaderColor(color.x(), color.y(), color.z(), alpha);

            if (c.celestialObjectProperties.hasMoonPhases) {
                int l = (moonPhase % 4);
                int i1 = (moonPhase / 4 % 2);
                float f13 = l / 4.0F;
                float f14 = i1 / 2.0F;
                float f15 = (l + 1) / 4.0F;
                float f16 = (i1 + 1) / 2.0F;
                bufferBuilder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX_COLOR);
                bufferBuilder.vertex(matrix4f2, -scale, -100.0F, (distance < 0 ? scale : -scale)).uv(f15, f16).color(color.x(), color.y(), color.z(), alpha).endVertex();
                bufferBuilder.vertex(matrix4f2, scale, -100.0F, (distance < 0 ? scale : -scale)).uv(f13, f16).color(color.x(), color.y(), color.z(), alpha).endVertex();
                bufferBuilder.vertex(matrix4f2, scale, -100.0F, (distance < 0 ? -scale : scale)).uv(f13, f14).color(color.x(), color.y(), color.z(), alpha).endVertex();
                bufferBuilder.vertex(matrix4f2, -scale, -100.0F, (distance < 0 ? -scale : scale)).uv(f15, f14).color(color.x(), color.y(), color.z(), alpha).endVertex();
            } else if (vertexList.size() > 0) {
                bufferBuilder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX_COLOR);
                for (Util.VertexPointValue v : vertexList) {
                    bufferBuilder.vertex(matrix4f2,
                            (float) v.pointX,
                            (float) v.pointY,
                            (float) v.pointZ
                    ).uv(
                            (float) v.uvX,
                            (float) v.uvY
                    ).color(color.x(), color.y(), color.z(), alpha).endVertex();
                }
            } else {
                bufferBuilder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX_COLOR);
                bufferBuilder.vertex(matrix4f2, -scale, distance, (distance < 0 ? scale : -scale)).uv(0.0F, 0.0F).color(color.x(), color.y(), color.z(), alpha).endVertex();
                bufferBuilder.vertex(matrix4f2, scale, distance, (distance < 0 ? scale : -scale)).uv(1.0F, 0.0F).color(color.x(), color.y(), color.z(), alpha).endVertex();
                bufferBuilder.vertex(matrix4f2, scale, distance, (distance < 0 ? -scale : scale)).uv(1.0F, 1.0F).color(color.x(), color.y(), color.z(), alpha).endVertex();
                bufferBuilder.vertex(matrix4f2, -scale, distance, (distance < 0 ? -scale : scale)).uv(0.0F, 1.0F).color(color.x(), color.y(), color.z(), alpha).endVertex();
            }

            BufferUploader.drawWithShader(bufferBuilder.end());
        } else if (c.type.equals(CelestialObject.CelestialObjectType.COLOR)) {
            RenderSystem.setShader(GameRenderer::getPositionColorShader);
            RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, alpha);
            RenderSystem.disableTexture();

            if (vertexList.size() > 0) {
                bufferBuilder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR);

                for (Util.VertexPointValue v : vertexList) {
                    bufferBuilder.vertex(matrix4f2,
                            (float) v.pointX,
                            (float) v.pointY,
                            (float) v.pointZ
                    ).color(colorsSolid.x() / 255.0F, colorsSolid.y() / 255.0F, colorsSolid.z() / 255.0F, alpha).endVertex();
                }
            } else {
                bufferBuilder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR);
                bufferBuilder.vertex(matrix4f2, -scale, distance, (distance < 0 ? scale : -scale)).color(colorsSolid.x() / 255.0F, colorsSolid.y() / 255.0F, colorsSolid.z() / 255.0F, alpha).endVertex();
                bufferBuilder.vertex(matrix4f2, scale, distance, (distance < 0 ? scale : -scale)).color(colorsSolid.x() / 255.0F, colorsSolid.y() / 255.0F, colorsSolid.z() / 255.0F, alpha).endVertex();
                bufferBuilder.vertex(matrix4f2, scale, distance, (distance < 0 ? -scale : scale)).color(colorsSolid.x() / 255.0F, colorsSolid.y() / 255.0F, colorsSolid.z() / 255.0F, alpha).endVertex();
                bufferBuilder.vertex(matrix4f2, -scale, distance, (distance < 0 ? -scale : scale)).color(colorsSolid.x() / 255.0F, colorsSolid.y() / 255.0F, colorsSolid.z() / 255.0F, alpha).endVertex();
            }

            BufferUploader.drawWithShader(bufferBuilder.end());

            RenderSystem.enableTexture();
        } else if (c.type.equals(CelestialObject.CelestialObjectType.SKYBOX)) {
            matrices.popPose();

            SkyBoxObjectProperties.SkyBoxSideTexture side;
            float size;
            float textureX;
            float textureY;
            float textureScaleX;
            float textureScaleY;

            float uvX;
            float uvY;
            float uvSizeX;
            float uvSizeY;
            float textureSizeX = c.solidColor != null ? 0 : (float) Util.solveEquation(c.skyBoxProperties.textureSizeX, objectReplaceMap);
            float textureSizeY = c.solidColor != null ? 0 : (float) Util.solveEquation(c.skyBoxProperties.textureSizeY, objectReplaceMap);

            for (int l = 0; l < 6; ++l) {
                matrices.pushPose();
                side = c.skyBoxProperties.sides.get(l);
                if (c.solidColor == null) {
                    RenderSystem.setShaderTexture(0, side.texture);
                    RenderSystem.setShader(GameRenderer::getPositionTexColorShader);
                } else {
                    RenderSystem.setShader(GameRenderer::getPositionColorShader);
                }
                if (l == 0) {
                    matrices.mulPose(Vector3f.YP.rotationDegrees(180.0F));
                }
                if (l == 1) {
                    matrices.mulPose(Vector3f.XP.rotationDegrees(90.0F));
                }

                if (l == 2) {
                    matrices.mulPose(Vector3f.XP.rotationDegrees(-90.0F));

                    matrices.mulPose(Vector3f.YP.rotationDegrees(180.0F));
                }

                if (l == 3) {
                    matrices.mulPose(Vector3f.XP.rotationDegrees(180.0F));
                    matrices.mulPose(Vector3f.YP.rotationDegrees(180.0F));
                }

                if (l == 4) {
                    matrices.mulPose(Vector3f.ZP.rotationDegrees(90.0F));
                    matrices.mulPose(Vector3f.YP.rotationDegrees(-90.0F));
                }

                if (l == 5) {
                    matrices.mulPose(Vector3f.ZP.rotationDegrees(-90.0F));
                    matrices.mulPose(Vector3f.YP.rotationDegrees(90.0F));
                }

                size = (float) Util.solveEquation(c.skyBoxProperties.skyBoxSize, objectReplaceMap);

                Matrix4f matrix4f3 = matrices.last().pose();

                if (c.solidColor != null) {
                    RenderSystem.disableTexture();
                    bufferBuilder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR);
                    bufferBuilder.vertex(matrix4f3, -size, size, (size < 0 ? size : -size)).color(colorsSolid.x() / 255.0F, colorsSolid.y() / 255.0F, colorsSolid.z() / 255.0F, alpha).endVertex();
                    bufferBuilder.vertex(matrix4f3, size, size, (size < 0 ? size : -size)).color(colorsSolid.x() / 255.0F, colorsSolid.y() / 255.0F, colorsSolid.z() / 255.0F, alpha).endVertex();
                    bufferBuilder.vertex(matrix4f3, size, size, (size < 0 ? -size : size)).color(colorsSolid.x() / 255.0F, colorsSolid.y() / 255.0F, colorsSolid.z() / 255.0F, alpha).endVertex();
                    bufferBuilder.vertex(matrix4f3, -size, size, (size < 0 ? -size : size)).color(colorsSolid.x() / 255.0F, colorsSolid.y() / 255.0F, colorsSolid.z() / 255.0F, alpha).endVertex();
                    BufferUploader.drawWithShader(bufferBuilder.end());

                    RenderSystem.enableTexture();
                } else {
                    uvX = (float) Util.solveEquation(side.uvX, objectReplaceMap);
                    uvY = (float) Util.solveEquation(side.uvY, objectReplaceMap);
                    uvSizeX = (float) Util.solveEquation(side.uvSizeX, objectReplaceMap);
                    uvSizeY = (float) Util.solveEquation(side.uvSizeY, objectReplaceMap);

                    textureX = (uvX / textureSizeX);
                    textureY = (uvY / textureSizeY);
                    textureScaleX = textureX + (uvSizeX / textureSizeX);
                    textureScaleY = textureY + (uvSizeY / textureSizeY);

                    if (textureX >= 0 && textureY >= 0 && textureScaleX >= 0 && textureScaleY >= 0) {
                        bufferBuilder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX_COLOR);
                        bufferBuilder.vertex(matrix4f3, -size, -size, -size).uv(textureX, textureY).color(color.x(), color.y(), color.z(), alpha).endVertex();
                        bufferBuilder.vertex(matrix4f3, -size, -size, size).uv(textureX, textureScaleY).color(color.x(), color.y(), color.z(), alpha).endVertex();
                        bufferBuilder.vertex(matrix4f3, size, -size, size).uv(textureScaleX, textureScaleY).color(color.x(), color.y(), color.z(), alpha).endVertex();
                        bufferBuilder.vertex(matrix4f3, size, -size, -size).uv(textureScaleX, textureY).color(color.x(), color.y(), color.z(), alpha).endVertex();
                        BufferUploader.drawWithShader(bufferBuilder.end());
                    }
                }
                matrices.popPose();
            }
            matrices.pushPose();
        }

        if (c.celestialObjectProperties.isSolid)
            RenderSystem.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);

        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
    }
}
