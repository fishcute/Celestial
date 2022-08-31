package fishcute.celestial.mixin;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import com.mojang.math.*;
import fishcute.celestial.sky.CelestialObject;
import fishcute.celestial.sky.CelestialObjectPopulation;
import fishcute.celestial.sky.CelestialRenderInfo;
import fishcute.celestial.sky.CelestialSky;
import fishcute.celestial.util.Util;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.*;
import net.minecraft.util.Mth;
import net.minecraft.world.level.material.FogType;
import net.minecraft.world.phys.Vec3;
import org.apache.commons.lang3.tuple.MutablePair;
import org.apache.commons.lang3.tuple.MutableTriple;
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
    private VertexBuffer skyBuffer;
    @Shadow
    private VertexBuffer darkBuffer;
    
    @Shadow
    private ClientLevel level;

    @Shadow
    private void drawStars(BufferBuilder p_109555_) {
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

    @Inject(method = "renderSky", at = @At("HEAD"), cancellable = true)
    private void renderSky(PoseStack matrices, Matrix4f projectionMatrix, float tickDelta, Camera camera, boolean bl, Runnable runnable, CallbackInfo info) {
        // In the midst of chaos, there is also opportunity - Sun Tzu
        if (CelestialSky.doesDimensionHaveCustomSky()) {
            info.cancel();
            runnable.run();
            if (!bl) {
                FogType fogType = camera.getFluidInCamera();
                if (fogType != FogType.POWDER_SNOW && fogType != FogType.LAVA) {
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

                        if (renderInfo.renderType.equals(CelestialRenderInfo.RenderType.SKYBOX)) {
                            RenderSystem.setShaderTexture(0, renderInfo.skyboxTexture);
                            Tesselator tesselator = Tesselator.getInstance();
                            BufferBuilder bufferbuilder = tesselator.getBuilder();

                            for(int l = 0; l < 6; ++l) {
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
                                bufferbuilder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX_COLOR);
                                bufferbuilder.vertex(matrix4f, -100.0F, -100.0F, -100.0F).uv(0.0F, 0.0F).color(255, 255, 255, 255).endVertex();
                                bufferbuilder.vertex(matrix4f, -100.0F, -100.0F, 100.0F).uv(0.0F, 16.0F).color(255, 255, 255, 255).endVertex();
                                bufferbuilder.vertex(matrix4f, 100.0F, -100.0F, 100.0F).uv(16.0F, 16.0F).color(255, 255, 255, 255).endVertex();
                                bufferbuilder.vertex(matrix4f, 100.0F, -100.0F, -100.0F).uv(16.0F, 0.0F).color(255, 255, 255, 255).endVertex();
                                tesselator.end();
                                matrices.popPose();
                            }
                        }

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

                                bufferBuilder.end();
                                BufferUploader.end(bufferBuilder);
                                matrices.popPose();
                            }
                        }
                        float a = level.dimensionType().timeOfDay(level.dayTime()) * 360;

                        Map<String, String> toReplaceMapRotation = Util.getReplaceMapAdd(Map.ofEntries(
                                entry("#skyAngle", a + "")
                        ));

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
                                else if (((CelestialObjectPopulation) c).baseObject.solidColor != null)
                                    renderOrderColor.add(c);
                            }
                            else {
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
                            }
                            else {
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

                        FogRenderer.levelFogColor();

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
        dataArray[9] = ((int) Util.solveEquation(c.celestialObjectProperties.moonPhase, Util.getReplaceMapAdd(
                Map.ofEntries(
                        entry("#moonPhase", this.level.getMoonPhase() + "")
                )
        )));

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

        if (c.texture != null) {
            RenderSystem.setShader(GameRenderer::getPositionTexColorShader);
            RenderSystem.setShaderColor(color.x(), color.y(), color.z(), 1.0f);

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
            } else if (c.vertexList.size() > 0) {
                //Stuff for custom vertex stuff
                //Honestly, don't even ask what's going on here anymore
                bufferBuilder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX_COLOR);
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
                bufferBuilder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX_COLOR);
                bufferBuilder.vertex(matrix4f2, -scale, distance, (distance < 0 ? scale : -scale)).uv(0.0F, 0.0F).color(color.x(), color.y(), color.z(), alpha).endVertex();
                bufferBuilder.vertex(matrix4f2, scale, distance, (distance < 0 ? scale : -scale)).uv(1.0F, 0.0F).color(color.x(), color.y(), color.z(), alpha).endVertex();
                bufferBuilder.vertex(matrix4f2, scale, distance, (distance < 0 ? -scale : scale)).uv(1.0F, 1.0F).color(color.x(), color.y(), color.z(), alpha).endVertex();
                bufferBuilder.vertex(matrix4f2, -scale, distance, (distance < 0 ? -scale : scale)).uv(0.0F, 1.0F).color(color.x(), color.y(), color.z(), alpha).endVertex();
            }
        }
        else if (colorsSolid != null) {
            RenderSystem.setShader(GameRenderer::getPositionColorShader);
            RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0f);

            if (c.vertexList.size() > 0) {
                bufferBuilder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR);

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
                bufferBuilder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR);
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

        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
    }
}
