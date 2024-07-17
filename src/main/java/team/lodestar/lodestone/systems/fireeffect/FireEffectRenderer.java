package team.lodestar.lodestone.systems.fireeffect;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.Material;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import org.joml.Matrix4f;
import team.lodestar.lodestone.config.ClientConfig;
import team.lodestar.lodestone.helpers.VecHelper;


@OnlyIn(Dist.CLIENT)
public abstract class FireEffectRenderer<T extends FireEffectInstance> {

    public boolean canRender(T instance) {
        return true;
    }

    public Material getFirstFlame() {
        return null;
    }

    public Material getSecondFlame() {
        return null;
    }

    public void renderScreen(T instance, Minecraft pMinecraft, PoseStack pPoseStack) {
        pPoseStack.pushPose();
        BufferBuilder bufferbuilder = Tesselator.getInstance().begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX_COLOR);
        RenderSystem.setShader(GameRenderer::getPositionTexColorShader);
        RenderSystem.depthFunc(519);
        RenderSystem.depthMask(false);
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        TextureAtlasSprite textureatlassprite = getFirstFlame().sprite();
        RenderSystem.setShaderTexture(0, textureatlassprite.atlasLocation());
        float f = textureatlassprite.getU0();
        float f1 = textureatlassprite.getU1();
        float f2 = (f + f1) / 2.0F;
        float f3 = textureatlassprite.getV0();
        float f4 = textureatlassprite.getV1();
        float f5 = (f3 + f4) / 2.0F;
        float f6 = textureatlassprite.uvShrinkRatio();
        float f7 = Mth.lerp(f6, f, f2);
        float f8 = Mth.lerp(f6, f1, f2);
        float f9 = Mth.lerp(f6, f3, f5);
        float f10 = Mth.lerp(f6, f4, f5);

        for (int i = 0; i < 2; ++i) {
            pPoseStack.pushPose();
            pPoseStack.translate(((float) (-(i * 2 - 1)) * 0.24F), -0.3F, 0.0D);
            pPoseStack.translate(0, -(ClientConfig.FIRE_OVERLAY_OFFSET.getConfigValue()) * 0.3f, 0);
            pPoseStack.mulPose(VecHelper.Vector3fHelper.rotation((float) (i * 2 - 1) * 10.0F, VecHelper.Vector3fHelper.YP));
            Matrix4f matrix4f = pPoseStack.last().pose();
            bufferbuilder.addVertex(matrix4f, -0.5F, -0.5F, -0.5F).setUv(f8, f10).setColor(1.0F, 1.0F, 1.0F, 0.9F);
            bufferbuilder.addVertex(matrix4f, 0.5F, -0.5F, -0.5F).setUv(f7, f10).setColor(1.0F, 1.0F, 1.0F, 0.9F);
            bufferbuilder.addVertex(matrix4f, 0.5F, 0.5F, -0.5F).setUv(f7, f9).setColor(1.0F, 1.0F, 1.0F, 0.9F);
            bufferbuilder.addVertex(matrix4f, -0.5F, 0.5F, -0.5F).setUv(f8, f9).setColor(1.0F, 1.0F, 1.0F, 0.9F);

            try (MeshData meshData = bufferbuilder.buildOrThrow()) {
                BufferUploader.drawWithShader(meshData);
            }

            pPoseStack.popPose();
        }

        RenderSystem.disableBlend();
        RenderSystem.depthMask(true);
        RenderSystem.depthFunc(515);
        pPoseStack.popPose();
    }

    public void renderWorld(T instance, PoseStack pMatrixStack, MultiBufferSource pBuffer, Camera camera, Entity pEntity) {
        TextureAtlasSprite textureAtlasSprite0 = getFirstFlame().sprite();
        TextureAtlasSprite textureAtlasSprite1 = getSecondFlame().sprite();
        pMatrixStack.pushPose();
        float f = pEntity.getBbWidth() * 1.4F;
        pMatrixStack.scale(f, f, f);
        float f1 = 0.5F;
        float f3 = pEntity.getBbHeight() / f;
        float f4 = 0.0F;
        pMatrixStack.mulPose(VecHelper.Vector3fHelper.rotation(-camera.getYRot(), VecHelper.Vector3fHelper.YP));
        pMatrixStack.translate(0.0D, 0.0D, -0.3F + (float) ((int) f3) * 0.02F);
        float f5 = 0.0F;
        int i = 0;
        VertexConsumer vertexconsumer = pBuffer.getBuffer(Sheets.cutoutBlockSheet());

        for (PoseStack.Pose last = pMatrixStack.last(); f3 > 0.0F; ++i) {
            TextureAtlasSprite finalSprite = i % 2 == 0 ? textureAtlasSprite0 : textureAtlasSprite1;
            float f6 = finalSprite.getU0();
            float f7 = finalSprite.getV0();
            float f8 = finalSprite.getU1();
            float f9 = finalSprite.getV1();
            if (i / 2 % 2 == 0) {
                float f10 = f8;
                f8 = f6;
                f6 = f10;
            }

            fireVertex(last, vertexconsumer, f1 - 0.0F, 0.0F - f4, f5, f8, f9);
            fireVertex(last, vertexconsumer, -f1 - 0.0F, 0.0F - f4, f5, f6, f9);
            fireVertex(last, vertexconsumer, -f1 - 0.0F, 1.4F - f4, f5, f6, f7);
            fireVertex(last, vertexconsumer, f1 - 0.0F, 1.4F - f4, f5, f8, f7);
            f3 -= 0.45F;
            f4 -= 0.45F;
            f1 *= 0.9F;
            f5 += 0.03F;
        }

        pMatrixStack.popPose();
    }

    protected static void fireVertex(PoseStack.Pose pMatrixEntry, VertexConsumer pBuffer, float pX, float pY, float pZ, float pTexU, float pTexV) {
        pBuffer.addVertex(pMatrixEntry.pose(), pX, pY, pZ).setColor(255, 255, 255, 255).setUv(pTexU, pTexV).setOverlay(OverlayTexture.NO_OVERLAY).setLight(240).setNormal(pMatrixEntry, 0.0F, 1.0F, 0.0F);
    }
}