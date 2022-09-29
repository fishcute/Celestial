package fishcute.celestial.mixin;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import com.mojang.math.Matrix4f;
import com.mojang.math.Quaternion;
import com.mojang.math.Vector3d;
import com.mojang.math.Vector3f;
import fishcute.celestial.sky.CelestialObject;
import fishcute.celestial.sky.CelestialObjectPopulation;
import fishcute.celestial.sky.CelestialRenderInfo;
import fishcute.celestial.sky.CelestialSky;
import fishcute.celestial.util.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.DimensionSpecialEffects;
import net.minecraft.client.renderer.FogRenderer;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
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
import java.util.HashMap;
import java.util.Map;

@Mixin(LevelRenderer.class)
public class LevelRendererMixin {
    @Shadow
    private VertexFormat skyFormat;

    @Shadow
    private VertexBuffer skyBuffer;

    @Shadow
    private ClientLevel level;
    private static Matrix4f setRotation(PoseStack matrices, Quaternion i, Quaternion j, Quaternion k, Vector3d move) {
        matrices.pushPose();

        if (i != null) {
            matrices.mulPose(i);
        }
        if (j != null) {
            matrices.mulPose(j);
        }
        if (k != null) {
            matrices.mulPose(k);
        }

        matrices.translate(move.x, move.y, move.z);

        Matrix4f matrix4f = matrices.last().pose();
        matrices.popPose();

        return matrix4f;
    }

    @Shadow
    private int ticks = 0;

    @Shadow @Final private TextureManager textureManager;

    @Shadow private @Nullable VertexBuffer darkBuffer;

    @Inject(method = "renderSky", at = @At("HEAD"), cancellable = true)
    private void renderSky(PoseStack matrices, float tickDelta, CallbackInfo info) {
        // In the midst of chaos, there is also opportunity - Sun Tzu

        // Oh, and who could forget this famous quote:
        // Java 8 and Minecraft 1.16 mappings are awful - Sun Tzu

        ClientLevel world = Minecraft.getInstance().level;
        Minecraft mc = Minecraft.getInstance();

        if (CelestialSky.doesDimensionHaveCustomSky()) {
            info.cancel();
            if (this.level.effects().skyType() == DimensionSpecialEffects.SkyType.NORMAL) {
                RenderSystem.disableTexture();
                Vec3 vector3d = this.level.getSkyColor(Minecraft.getInstance().gameRenderer.getMainCamera().getBlockPosition(), tickDelta);
                float f = (float) vector3d.x;
                float g = (float) vector3d.y;
                float h = (float) vector3d.z;
                FogRenderer.levelFogColor();
                BufferBuilder bufferBuilder = Tesselator.getInstance().getBuilder();
                RenderSystem.depthMask(false);
                RenderSystem.enableFog();
                RenderSystem.color4f(f, g, h, 1.0F);

                this.skyBuffer.bind();
                this.skyFormat.setupBufferState(0L);
                this.skyBuffer.draw(matrices.last().pose(), 7);
                VertexBuffer.unbind();
                this.skyFormat.clearBufferState();
                RenderSystem.disableFog();
                RenderSystem.disableAlphaTest();
                RenderSystem.enableBlend();
                RenderSystem.defaultBlendFunc();

                CelestialRenderInfo renderInfo = CelestialSky.getDimensionRenderInfo();
                float[] fs = Minecraft.getInstance().level.effects().getSunriseColor(this.level.getTimeOfDay(tickDelta), tickDelta);

                if (renderInfo.renderType.equals(CelestialRenderInfo.RenderType.NORMAL)) {
                    if (fs != null) {
                        RenderSystem.disableTexture();
                        RenderSystem.shadeModel(7425);
                        matrices.pushPose();
                        matrices.mulPose(Vector3f.XP.rotationDegrees(90.0F));
                        float f3 = Mth.sin(this.level.getSunAngle(tickDelta)) < 0.0F ? 180.0F : 0.0F;
                        matrices.mulPose(Vector3f.ZP.rotationDegrees(f3));
                        matrices.mulPose(Vector3f.ZP.rotationDegrees(90.0F));
                        float f4 = fs[0];
                        float f5 = fs[1];
                        float f6 = fs[2];
                        Matrix4f matrix4f = matrices.last().pose();
                        bufferBuilder.begin(6, DefaultVertexFormat.POSITION_COLOR);
                        bufferBuilder.vertex(matrix4f, 0.0F, 100.0F, 0.0F).color(f4, f5, f6, fs[3]).endVertex();

                        for (int j = 0; j <= 16; ++j) {
                            float f7 = (float) j * ((float) Math.PI * 2F) / 16.0F;
                            float f8 = Mth.sin(f7);
                            float f9 = Mth.cos(f7);
                            bufferBuilder.vertex(matrix4f, f8 * 120.0F, f9 * 120.0F, -f9 * 40.0F * fs[3]).color(fs[0], fs[1], fs[2], 0.0F).endVertex();
                        }

                        bufferBuilder.end();
                        BufferUploader.end(bufferBuilder);
                        matrices.popPose();
                        RenderSystem.shadeModel(7424);
                    }
                } else if (renderInfo.renderType.equals(CelestialRenderInfo.RenderType.SKYBOX)) {
                    RenderSystem.enableTexture();
                    this.textureManager.bind(renderInfo.skyboxTexture);
                    Tesselator tessellator = Tesselator.getInstance();
                    BufferBuilder bufferbuilder = tessellator.getBuilder();

                    for (int l = 0; l < 6; ++l) {
                        matrices.pushPose();
                        if (l == 1) {
                            matrices.mulPose(Vector3f.XP.rotationDegrees(90.0F));
                        }

                        if (l == 2) {
                            matrices.mulPose(Vector3f.XP.rotationDegrees(-90.0F));
                        }

                        if (l == 3) {
                            matrices.mulPose(Vector3f.XP.rotationDegrees(180.0F));
                        }

                        if (l == 4) {
                            matrices.mulPose(Vector3f.ZP.rotationDegrees(90.0F));
                        }

                        if (l == 5) {
                            matrices.mulPose(Vector3f.ZP.rotationDegrees(-90.0F));
                        }

                        Matrix4f matrix4f = matrices.last().pose();
                        bufferbuilder.begin(7, DefaultVertexFormat.POSITION_TEX_COLOR);
                        bufferbuilder.vertex(matrix4f, -100.0F, -100.0F, -100.0F).uv(0.0F, 0.0F).color(255, 255, 255, 255).endVertex();
                        bufferbuilder.vertex(matrix4f, -100.0F, -100.0F, 100.0F).uv(0.0F, 16.0F).color(255, 255, 255, 255).endVertex();
                        bufferbuilder.vertex(matrix4f, 100.0F, -100.0F, 100.0F).uv(16.0F, 16.0F).color(255, 255, 255, 255).endVertex();
                        bufferbuilder.vertex(matrix4f, 100.0F, -100.0F, -100.0F).uv(16.0F, 0.0F).color(255, 255, 255, 255).endVertex();
                        tessellator.end();
                        matrices.popPose();
                    }
                }

                float a = this.level.getSunAngle(tickDelta) * 360.0F;

                Map<String, String> toReplaceMapRotation = new HashMap<>();
                toReplaceMapRotation.put("#skyAngle", a + "");
                toReplaceMapRotation = Util.getReplaceMapAdd(toReplaceMapRotation);

                RenderSystem.enableTexture();
                RenderSystem.enableBlend();
                RenderSystem.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);

                RenderSystem.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);

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

                    matrices.pushPose();

                    if (c.isPopulation()) {
                        matrices.mulPose(Vector3f.XP.rotationDegrees((float) Util.solveEquation(((CelestialObjectPopulation) c).baseObject.baseDegreesX, toReplaceMapRotation)));
                        matrices.mulPose(Vector3f.YP.rotationDegrees((float) Util.solveEquation(((CelestialObjectPopulation) c).baseObject.baseDegreesY, toReplaceMapRotation)));
                        matrices.mulPose(Vector3f.ZP.rotationDegrees((float) Util.solveEquation(((CelestialObjectPopulation) c).baseObject.baseDegreesZ, toReplaceMapRotation)));

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
                                    (ArrayList<MutablePair<MutableTriple<Float, Float, Float>, MutablePair<Float, Float>>>) dataArray[10]
                            );
                        }
                    } else {
                        matrices.mulPose(Vector3f.XP.rotationDegrees((float) Util.solveEquation(c.baseDegreesX, toReplaceMapRotation)));
                        matrices.mulPose(Vector3f.YP.rotationDegrees((float) Util.solveEquation(c.baseDegreesY, toReplaceMapRotation)));
                        matrices.mulPose(Vector3f.ZP.rotationDegrees((float) Util.solveEquation(c.baseDegreesZ, toReplaceMapRotation)));

                        Object[] dataArray = getObjectDataArray(c, toReplaceMapRotation);

                        renderSkyObject(bufferBuilder,
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
                                (ArrayList<MutablePair<MutableTriple<Float, Float, Float>, MutablePair<Float, Float>>>) dataArray[10]
                        );
                    }

                    matrices.popPose();
                }

                RenderSystem.enableFog();

                RenderSystem.disableBlend();
                RenderSystem.disableTexture();
                RenderSystem.color4f(0.0F, 0.0F, 0.0F, 1.0F);


                double d = Minecraft.getInstance().player.getEyePosition((float) tickDelta).y - this.level.getLevelData().getHorizonHeight();
                if (d < 0.0) {
                    matrices.pushPose();
                    matrices.translate(0.0, 12.0, 0.0);
                    this.darkBuffer.bind();
                    this.skyFormat.setupBufferState(0L);
                    this.darkBuffer.draw(matrices.last().pose(), 7);
                    VertexBuffer.unbind();
                    this.skyFormat.clearBufferState();
                    matrices.popPose();
                }
                if (this.level.effects().hasGround()) {
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
        toReplaceMap.put("#moonPhase", this.level.getMoonPhase() + "");

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
        dataArray[11] = (new Vector3f(
                (float) ((Util.solveEquation(c.celestialObjectProperties.red, Util.getReplaceMapNormal()))),
                (float) ((Util.solveEquation(c.celestialObjectProperties.green, Util.getReplaceMapNormal()))),
                (float) ((Util.solveEquation(c.celestialObjectProperties.blue, Util.getReplaceMapNormal())))));

        //solid colors 12
        if (c.solidColor != null)
            dataArray[12] = (new Vector3f(
                    (c.solidColor.getRed()) * (((Vector3f) dataArray[11]).x()),
                    (c.solidColor.getGreen()) * (((Vector3f) dataArray[11]).y()),
                    (c.solidColor.getBlue()) * (((Vector3f) dataArray[11]).z())));
        else
            dataArray[12] = (null);
        return dataArray;
    }

    // Render sky object
    private void renderSkyObject(BufferBuilder bufferBuilder, Matrix4f matrix4f2, CelestialObject c, Vector3f color, Vector3f colorsSolid, float alpha, float distancePre, float scalePre, int moonPhase, ArrayList<MutablePair<MutableTriple<Float, Float, Float>, MutablePair<Float, Float>>> vertexList) {
        RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);

        float distance = (float) (distancePre + c.populateDistanceAdd);

        float scale = (float) (scalePre + c.populateScaleAdd);

        RenderSystem.enableBlend();

        // Set texture
        if (c.texture != null)
            this.textureManager.bind(c.texture);

        if (c.celestialObjectProperties.ignoreFog)
            RenderSystem.disableFog();
        else {
            RenderSystem.enableFog();
        }

        if (c.celestialObjectProperties.isSolid)
            RenderSystem.defaultBlendFunc();

        if (c.texture != null) {
            RenderSystem.color4f(color.x(), color.y(), color.z(), alpha);

            if (c.celestialObjectProperties.hasMoonPhases) {
                int l = (moonPhase % 4);
                int i1 = (moonPhase / 4 % 2);
                float f13 = l / 4.0F;
                float f14 = i1 / 2.0F;
                float f15 = (l + 1) / 4.0F;
                float f16 = (i1 + 1) / 2.0F;
                bufferBuilder.begin(7, DefaultVertexFormat.POSITION_TEX_COLOR);
                bufferBuilder.vertex(matrix4f2, -scale, -100.0F, (distance < 0 ? scale : -scale)).uv(f15, f16).color(color.x(), color.y(), color.z(), alpha).endVertex();
                bufferBuilder.vertex(matrix4f2, scale, -100.0F, (distance < 0 ? scale : -scale)).uv(f13, f16).color(color.x(), color.y(), color.z(), alpha).endVertex();
                bufferBuilder.vertex(matrix4f2, scale, -100.0F, (distance < 0 ? -scale : scale)).uv(f13, f14).color(color.x(), color.y(), color.z(), alpha).endVertex();
                bufferBuilder.vertex(matrix4f2, -scale, -100.0F, (distance < 0 ? -scale : scale)).uv(f15, f14).color(color.x(), color.y(), color.z(), alpha).endVertex();
            } else if (c.vertexList.size() > 0) {
                //Stuff for custom vertex stuff
                //Honestly, don't even ask what's going on here anymore
                bufferBuilder.begin(7, DefaultVertexFormat.POSITION_TEX_COLOR);
                for (MutablePair<MutableTriple<Float, Float, Float>, MutablePair<Float, Float>> v : vertexList) {
                    bufferBuilder.vertex(matrix4f2,
                            v.getLeft().getLeft(),
                            v.getLeft().getMiddle(),
                            v.getLeft().getRight()
                    ).uv(
                            v.getRight().getLeft(),
                            v.getRight().getRight()
                    ).color(color.x(), color.y(), color.z(), alpha).endVertex();
                }
            } else {
                bufferBuilder.begin(7, DefaultVertexFormat.POSITION_TEX_COLOR);
                bufferBuilder.vertex(matrix4f2, -scale, distance, (distance < 0 ? scale : -scale)).uv(0.0F, 0.0F).color(color.x(), color.y(), color.z(), alpha).endVertex();
                bufferBuilder.vertex(matrix4f2, scale, distance, (distance < 0 ? scale : -scale)).uv(1.0F, 0.0F).color(color.x(), color.y(), color.z(), alpha).endVertex();
                bufferBuilder.vertex(matrix4f2, scale, distance, (distance < 0 ? -scale : scale)).uv(1.0F, 1.0F).color(color.x(), color.y(), color.z(), alpha).endVertex();
                bufferBuilder.vertex(matrix4f2, -scale, distance, (distance < 0 ? -scale : scale)).uv(0.0F, 1.0F).color(color.x(), color.y(), color.z(), alpha).endVertex();
            }
        }
        else if (colorsSolid != null) {
            RenderSystem.color4f(1.0F, 1.0F, 1.0F, alpha);

            if (c.vertexList.size() > 0) {
                bufferBuilder.begin(7, DefaultVertexFormat.POSITION_COLOR);

                for (MutablePair<MutableTriple<Float, Float, Float>, MutablePair<Float, Float>> v : vertexList) {
                    bufferBuilder.vertex(matrix4f2,
                            v.getLeft().getLeft(),
                            v.getLeft().getMiddle(),
                            v.getLeft().getRight()
                    ).uv(
                            v.getRight().getLeft(),
                            v.getRight().getRight()
                    ).color(color.x(), color.y(), color.z(), alpha).endVertex();
                }
            } else {
                bufferBuilder.begin(7, DefaultVertexFormat.POSITION_COLOR);
                bufferBuilder.vertex(matrix4f2, -scale, distance, (distance < 0 ? scale : -scale)).color(colorsSolid.x() / 255.0F, colorsSolid.y() / 255.0F, colorsSolid.z() / 255.0F, alpha).endVertex();
                bufferBuilder.vertex(matrix4f2, scale, distance, (distance < 0 ? scale : -scale)).color(colorsSolid.x() / 255.0F, colorsSolid.y() / 255.0F, colorsSolid.z() / 255.0F, alpha).endVertex();
                bufferBuilder.vertex(matrix4f2, scale, distance, (distance < 0 ? -scale : scale)).color(colorsSolid.x() / 255.0F, colorsSolid.y() / 255.0F, colorsSolid.z() / 255.0F, alpha).endVertex();
                bufferBuilder.vertex(matrix4f2, -scale, distance, (distance < 0 ? -scale : scale)).color(colorsSolid.x() / 255.0F, colorsSolid.y() / 255.0F, colorsSolid.z() / 255.0F, alpha).endVertex();
            }

            RenderSystem.enableTexture();
        }

        bufferBuilder.end();
        BufferUploader.end(bufferBuilder);

        if (c.celestialObjectProperties.isSolid)
            RenderSystem.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);

        RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
    }
}