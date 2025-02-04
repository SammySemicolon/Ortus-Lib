package team.lodestar.lodestone.systems.particle.render_types;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureManager;
import org.lwjgl.opengl.GL11;
import team.lodestar.lodestone.registry.client.*;

import java.util.function.Supplier;

public interface LodestoneScreenParticleRenderType {
    LodestoneScreenParticleRenderType ADDITIVE = new LodestoneScreenParticleRenderType() {
        @Override
        public BufferBuilder begin(Tesselator tesselator, TextureManager manager) {
            RenderSystem.enableBlend();
            RenderSystem.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE);
            RenderSystem.setShader(LodestoneShaders.SCREEN_PARTICLE.getInstance());
            RenderSystem.setShaderTexture(0, TextureAtlas.LOCATION_PARTICLES);
            return tesselator.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX_COLOR);
        }

        @Override
        public void end(BufferBuilder builder) {
            MeshData meshdata = builder.build();
            if (meshdata != null) {
                BufferUploader.drawWithShader(meshdata);
            }
            RenderSystem.disableBlend();
            RenderSystem.defaultBlendFunc();
        }
    };
    LodestoneScreenParticleRenderType TRANSPARENT = new LodestoneScreenParticleRenderType() {
        @Override
        public BufferBuilder begin(Tesselator tesselator, TextureManager manager) {
            RenderSystem.enableBlend();
            RenderSystem.setShader(LodestoneShaders.SCREEN_PARTICLE.getInstance());
            RenderSystem.setShaderTexture(0, TextureAtlas.LOCATION_PARTICLES);
            RenderSystem.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
            return tesselator.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX_COLOR);
        }

        @Override
        public void end(BufferBuilder builder) {
            MeshData meshdata = builder.build();
            if (meshdata != null) {
                BufferUploader.drawWithShader(meshdata);
            }
            RenderSystem.disableBlend();
            RenderSystem.defaultBlendFunc();
        }
    };

    LodestoneScreenParticleRenderType LUMITRANSPARENT = new LodestoneScreenParticleRenderType() {
        @Override
        public BufferBuilder begin(Tesselator tesselator, TextureManager manager) {
            RenderSystem.enableBlend();
            Supplier<ShaderInstance> instance = LodestoneShaders.SCREEN_PARTICLE.getInstance();
            RenderSystem.setShader(instance);
            instance.get().safeGetUniform("LumiTransparency").set(1f);
            RenderSystem.setShaderTexture(0, TextureAtlas.LOCATION_PARTICLES);
            RenderSystem.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
            return tesselator.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX_COLOR);
        }

        @Override
        public void end(BufferBuilder builder) {
            MeshData meshdata = builder.build();
            if (meshdata != null) {
                BufferUploader.drawWithShader(meshdata);
            }
            RenderSystem.disableBlend();
            RenderSystem.defaultBlendFunc();

            Supplier<ShaderInstance> instance = LodestoneShaders.SCREEN_PARTICLE.getInstance();
            instance.get().safeGetUniform("LumiTransparency").set(0f);
        }
    };

    BufferBuilder begin(Tesselator tesselator, TextureManager pTextureManager);

    void end(BufferBuilder builder);
}