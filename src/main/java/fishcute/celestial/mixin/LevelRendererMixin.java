package fishcute.celestial.mixin;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import fishcute.celestial.sky.CelestialObject;
import fishcute.celestial.sky.CelestialObjectPopulation;
import fishcute.celestial.sky.CelestialRenderInfo;
import fishcute.celestial.sky.CelestialSky;
import fishcute.celestial.util.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.renderer.vertex.VertexBuffer;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.client.world.DimensionRenderInfo;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Matrix4f;
import net.minecraft.util.math.vector.Quaternion;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.math.vector.Vector3f;
import org.apache.commons.lang3.tuple.MutablePair;
import org.apache.commons.lang3.tuple.MutableTriple;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

@Mixin(WorldRenderer.class)
public class LevelRendererMixin {
    @Shadow
    private VertexFormat skyVertexFormat;
    @Shadow
    private VertexBuffer starVBO;
    @Shadow
    private VertexBuffer sky2VBO;
    @Shadow
    private VertexBuffer skyVBO;

    @Shadow
    private ClientWorld world;
    private static Matrix4f setRotation(MatrixStack matrices, Quaternion i, Quaternion j, Quaternion k, Vector3d move) {
        matrices.push();

        if (i != null) {
            matrices.rotate(i);
        }
        if (j != null) {
            matrices.rotate(j);
        }
        if (k != null) {
            matrices.rotate(k);
        }

        matrices.translate(move.x, move.y, move.z);

        Matrix4f matrix4f = matrices.getLast().getMatrix();
        matrices.pop();

        return matrix4f;
    }

    @Shadow
    private int ticks = 0;

    @Shadow @Final private TextureManager textureManager;

    @Inject(method = "renderSky(Lcom/mojang/blaze3d/matrix/MatrixStack;F)V", at = @At("HEAD"), cancellable = true)
    private void renderSky(MatrixStack matrices, float tickDelta, CallbackInfo info) {
        // In the midst of chaos, there is also opportunity - Sun Tzu
        
        // Oh, and who could forget this famous quote:
        // Java 8 and Minecraft 1.16 mappings are awful - Sun Tzu

        ClientWorld world = Minecraft.getInstance().world;
        Minecraft mc = Minecraft.getInstance();
        net.minecraftforge.client.ISkyRenderHandler renderHandler = world.getDimensionRenderInfo().getSkyRenderHandler();
        if (renderHandler != null) {
            renderHandler.render(ticks, tickDelta, matrices, world, mc);
            info.cancel();
        }

        if (CelestialSky.doesDimensionHaveCustomSky()) {
            info.cancel();
            if (mc.world.getDimensionRenderInfo().getFogType() == DimensionRenderInfo.FogType.NORMAL) {
                RenderSystem.disableTexture();
                Vector3d vector3d = this.world.getSkyColor(Minecraft.getInstance().gameRenderer.getActiveRenderInfo().getBlockPos(), tickDelta);
                float f = (float) vector3d.x;
                float g = (float) vector3d.y;
                float h = (float) vector3d.z;
                FogRenderer.applyFog();
                BufferBuilder bufferBuilder = Tessellator.getInstance().getBuffer();
                RenderSystem.depthMask(false);
                RenderSystem.enableFog();
                RenderSystem.color4f(f, g, h, 1.0F);

                this.skyVBO.bindBuffer();
                this.skyVertexFormat.setupBufferState(0L);
                this.skyVBO.draw(matrices.getLast().getMatrix(), 7);
                VertexBuffer.unbindBuffer();
                this.skyVertexFormat.clearBufferState();
                RenderSystem.disableFog();
                RenderSystem.disableAlphaTest();
                RenderSystem.enableBlend();
                RenderSystem.defaultBlendFunc();

                CelestialRenderInfo renderInfo = CelestialSky.getDimensionRenderInfo();
                float[] fs = this.world.getDimensionRenderInfo().func_230492_a_(this.world.func_242415_f(tickDelta), tickDelta);

                if (renderInfo.renderType.equals(CelestialRenderInfo.RenderType.NORMAL)) {
                    if (fs != null) {
                        RenderSystem.disableTexture();
                        RenderSystem.shadeModel(7425);
                        matrices.push();
                        matrices.rotate(Vector3f.XP.rotationDegrees(90.0F));
                        float f3 = MathHelper.sin(world.getCelestialAngleRadians(tickDelta)) < 0.0F ? 180.0F : 0.0F;
                        matrices.rotate(Vector3f.ZP.rotationDegrees(f3));
                        matrices.rotate(Vector3f.ZP.rotationDegrees(90.0F));
                        float f4 = fs[0];
                        float f5 = fs[1];
                        float f6 = fs[2];
                        Matrix4f matrix4f = matrices.getLast().getMatrix();
                        bufferBuilder.begin(6, DefaultVertexFormats.POSITION_COLOR);
                        bufferBuilder.pos(matrix4f, 0.0F, 100.0F, 0.0F).color(f4, f5, f6, fs[3]).endVertex();

                        for (int j = 0; j <= 16; ++j) {
                            float f7 = (float) j * ((float) Math.PI * 2F) / 16.0F;
                            float f8 = MathHelper.sin(f7);
                            float f9 = MathHelper.cos(f7);
                            bufferBuilder.pos(matrix4f, f8 * 120.0F, f9 * 120.0F, -f9 * 40.0F * fs[3]).color(fs[0], fs[1], fs[2], 0.0F).endVertex();
                        }

                        bufferBuilder.finishDrawing();
                        WorldVertexBufferUploader.draw(bufferBuilder);
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
                            matrices.rotate(Vector3f.XP.rotationDegrees(90.0F));
                        }

                        if (l == 2) {
                            matrices.rotate(Vector3f.XP.rotationDegrees(-90.0F));
                        }

                        if (l == 3) {
                            matrices.rotate(Vector3f.XP.rotationDegrees(180.0F));
                        }

                        if (l == 4) {
                            matrices.rotate(Vector3f.ZP.rotationDegrees(90.0F));
                        }

                        if (l == 5) {
                            matrices.rotate(Vector3f.ZP.rotationDegrees(-90.0F));
                        }

                        Matrix4f matrix4f = matrices.getLast().getMatrix();
                        bufferbuilder.begin(7, DefaultVertexFormats.POSITION_TEX_COLOR);
                        bufferbuilder.pos(matrix4f, -100.0F, -100.0F, -100.0F).tex(0.0F, 0.0F).color(255, 255, 255, 255).endVertex();
                        bufferbuilder.pos(matrix4f, -100.0F, -100.0F, 100.0F).tex(0.0F, 16.0F).color(255, 255, 255, 255).endVertex();
                        bufferbuilder.pos(matrix4f, 100.0F, -100.0F, 100.0F).tex(16.0F, 16.0F).color(255, 255, 255, 255).endVertex();
                        bufferbuilder.pos(matrix4f, 100.0F, -100.0F, -100.0F).tex(16.0F, 0.0F).color(255, 255, 255, 255).endVertex();
                        tessellator.draw();
                        matrices.pop();
                    }
                }

                float a = this.world.func_242415_f(tickDelta) * 360.0F;

                Map<String, String> toReplaceMapRotation = new HashMap<>();
                toReplaceMapRotation.put("#skyAngle", a + "");
                toReplaceMapRotation = Util.getReplaceMapAdd(toReplaceMapRotation);

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
                        matrices.rotate(Vector3f.XP.rotationDegrees((float) Util.solveEquation(((CelestialObjectPopulation) c).baseObject.baseDegreesX, toReplaceMapRotation)));
                        matrices.rotate(Vector3f.YP.rotationDegrees((float) Util.solveEquation(((CelestialObjectPopulation) c).baseObject.baseDegreesY, toReplaceMapRotation)));
                        matrices.rotate(Vector3f.ZP.rotationDegrees((float) Util.solveEquation(((CelestialObjectPopulation) c).baseObject.baseDegreesZ, toReplaceMapRotation)));

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
                        matrices.rotate(Vector3f.XP.rotationDegrees((float) Util.solveEquation(c.baseDegreesX, toReplaceMapRotation)));
                        matrices.rotate(Vector3f.YP.rotationDegrees((float) Util.solveEquation(c.baseDegreesY, toReplaceMapRotation)));
                        matrices.rotate(Vector3f.ZP.rotationDegrees((float) Util.solveEquation(c.baseDegreesZ, toReplaceMapRotation)));

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

                    matrices.pop();
                }

                FogRenderer.applyFog();

                RenderSystem.disableBlend();
                RenderSystem.disableTexture();
                RenderSystem.color4f(0.0F, 0.0F, 0.0F, 1.0F);


                double d0 = Minecraft.getInstance().player.getEyePosition(tickDelta).y - this.world.getWorldInfo().getVoidFogHeight();
                if (d0 < 0.0D) {
                    matrices.push();
                    matrices.translate(0.0D, 12.0D, 0.0D);
                    this.sky2VBO.bindBuffer();
                    this.skyVertexFormat.setupBufferState(0L);
                    this.sky2VBO.draw(matrices.getLast().getMatrix(), 7);
                    VertexBuffer.unbindBuffer();
                    this.skyVertexFormat.clearBufferState();
                    matrices.pop();
                }

                if (this.world.getDimensionRenderInfo().func_239216_b_()) {
                    RenderSystem.color3f(f * 0.2F + 0.04F, g * 0.2F + 0.04F, h * 0.6F + 0.1F);
                } else {
                    RenderSystem.color3f(f, g, h);
                }

                RenderSystem.enableTexture();
                RenderSystem.depthMask(true);
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
        dataArray[11] = (new Vector3f(
                (float) ((Util.solveEquation(c.celestialObjectProperties.red, Util.getReplaceMapNormal()))),
                (float) ((Util.solveEquation(c.celestialObjectProperties.green, Util.getReplaceMapNormal()))),
                (float) ((Util.solveEquation(c.celestialObjectProperties.blue, Util.getReplaceMapNormal())))));

        //solid colors 12
        if (c.solidColor != null)
            dataArray[12] = (new Vector3f(
                    (c.solidColor.getRed()) * (((Vector3f) dataArray[11]).getX()),
                    (c.solidColor.getGreen()) * (((Vector3f) dataArray[11]).getY()),
                    (c.solidColor.getBlue()) * (((Vector3f) dataArray[11]).getZ())));
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
            this.textureManager.bindTexture(c.texture);

        if (c.celestialObjectProperties.ignoreFog)
            RenderSystem.disableFog();
        else {
            FogRenderer.applyFog();
        }

        if (c.celestialObjectProperties.isSolid)
            RenderSystem.defaultBlendFunc();

        if (c.texture != null) {
            RenderSystem.color4f(color.getX(), color.getY(), color.getZ(), alpha);

            if (c.celestialObjectProperties.hasMoonPhases) {
                int l = (moonPhase % 4);
                int i1 = (moonPhase / 4 % 2);
                float f13 = l / 4.0F;
                float f14 = i1 / 2.0F;
                float f15 = (l + 1) / 4.0F;
                float f16 = (i1 + 1) / 2.0F;
                bufferBuilder.begin(7, DefaultVertexFormats.POSITION_TEX_COLOR);
                bufferBuilder.pos(matrix4f2, -scale, -100.0F, (distance < 0 ? scale : -scale)).tex(f15, f16).color(color.getX(), color.getY(), color.getZ(), alpha).endVertex();
                bufferBuilder.pos(matrix4f2, scale, -100.0F, (distance < 0 ? scale : -scale)).tex(f13, f16).color(color.getX(), color.getY(), color.getZ(), alpha).endVertex();
                bufferBuilder.pos(matrix4f2, scale, -100.0F, (distance < 0 ? -scale : scale)).tex(f13, f14).color(color.getX(), color.getY(), color.getZ(), alpha).endVertex();
                bufferBuilder.pos(matrix4f2, -scale, -100.0F, (distance < 0 ? -scale : scale)).tex(f15, f14).color(color.getX(), color.getY(), color.getZ(), alpha).endVertex();
            } else if (c.vertexList.size() > 0) {
                //Stuff for custom vertex stuff
                //Honestly, don't even ask what's going on here anymore
                bufferBuilder.begin(7, DefaultVertexFormats.POSITION_TEX_COLOR);
                for (MutablePair<MutableTriple<Float, Float, Float>, MutablePair<Float, Float>> v : vertexList) {
                    bufferBuilder.pos(matrix4f2,
                            v.getLeft().getLeft(),
                            v.getLeft().getMiddle(),
                            v.getLeft().getRight()
                    ).tex(
                            v.getRight().getLeft(),
                            v.getRight().getRight()
                    ).color(color.getX(), color.getY(), color.getZ(), alpha).endVertex();
                }
            } else {
                bufferBuilder.begin(7, DefaultVertexFormats.POSITION_TEX_COLOR);
                bufferBuilder.pos(matrix4f2, -scale, distance, (distance < 0 ? scale : -scale)).tex(0.0F, 0.0F).color(color.getX(), color.getY(), color.getZ(), alpha).endVertex();
                bufferBuilder.pos(matrix4f2, scale, distance, (distance < 0 ? scale : -scale)).tex(1.0F, 0.0F).color(color.getX(), color.getY(), color.getZ(), alpha).endVertex();
                bufferBuilder.pos(matrix4f2, scale, distance, (distance < 0 ? -scale : scale)).tex(1.0F, 1.0F).color(color.getX(), color.getY(), color.getZ(), alpha).endVertex();
                bufferBuilder.pos(matrix4f2, -scale, distance, (distance < 0 ? -scale : scale)).tex(0.0F, 1.0F).color(color.getX(), color.getY(), color.getZ(), alpha).endVertex();
            }
        }
        else if (colorsSolid != null) {
            RenderSystem.color4f(1.0F, 1.0F, 1.0F, alpha);

            if (c.vertexList.size() > 0) {
                bufferBuilder.begin(7, DefaultVertexFormats.POSITION_COLOR);

                for (MutablePair<MutableTriple<Float, Float, Float>, MutablePair<Float, Float>> v : vertexList) {
                    bufferBuilder.pos(matrix4f2,
                            v.getLeft().getLeft(),
                            v.getLeft().getMiddle(),
                            v.getLeft().getRight()
                    ).tex(
                            v.getRight().getLeft(),
                            v.getRight().getRight()
                    ).color(color.getX(), color.getY(), color.getZ(), alpha).endVertex();
                }
            } else {
                bufferBuilder.begin(7, DefaultVertexFormats.POSITION_COLOR);
                bufferBuilder.pos(matrix4f2, -scale, distance, (distance < 0 ? scale : -scale)).color(colorsSolid.getX() / 255.0F, colorsSolid.getY() / 255.0F, colorsSolid.getZ() / 255.0F, alpha).endVertex();
                bufferBuilder.pos(matrix4f2, scale, distance, (distance < 0 ? scale : -scale)).color(colorsSolid.getX() / 255.0F, colorsSolid.getY() / 255.0F, colorsSolid.getZ() / 255.0F, alpha).endVertex();
                bufferBuilder.pos(matrix4f2, scale, distance, (distance < 0 ? -scale : scale)).color(colorsSolid.getX() / 255.0F, colorsSolid.getY() / 255.0F, colorsSolid.getZ() / 255.0F, alpha).endVertex();
                bufferBuilder.pos(matrix4f2, -scale, distance, (distance < 0 ? -scale : scale)).color(colorsSolid.getX() / 255.0F, colorsSolid.getY() / 255.0F, colorsSolid.getZ() / 255.0F, alpha).endVertex();
            }

            RenderSystem.enableTexture();
        }

        bufferBuilder.finishDrawing();
        WorldVertexBufferUploader.draw(bufferBuilder);

        if (c.celestialObjectProperties.isSolid)
            RenderSystem.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);

        RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
    }
}
