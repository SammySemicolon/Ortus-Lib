package team.lodestar.lodestone.systems.texture;

import com.mojang.blaze3d.pipeline.RenderTarget;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.platform.TextureUtil;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.neoforged.neoforge.common.NeoForgeConfig;

import static org.lwjgl.opengl.GL30.*;

public class CustomizableTextureTarget extends RenderTarget {
    private final InternalTextureFormat internalFormat;

    public CustomizableTextureTarget(int width, int height, boolean useDepth) {
        this(width, height, useDepth, InternalTextureFormat.RGBA16F);
    }
    public CustomizableTextureTarget(int width, int height, boolean useDepth, InternalTextureFormat internalFormat) {
        super(useDepth);
        this.internalFormat = internalFormat;
        RenderSystem.assertOnRenderThreadOrInit();
        this.resize(width, height, Minecraft.ON_OSX);
    }


    @Override
    public void createBuffers(int width, int height, boolean clearError) {
        RenderSystem.assertOnRenderThreadOrInit();
        int i = RenderSystem.maxSupportedTextureSize();
        if (width > 0 && width <= i && height > 0 && height <= i) {
            this.viewWidth = width;
            this.viewHeight = height;
            this.width = width;
            this.height = height;
            this.frameBufferId = GlStateManager.glGenFramebuffers();
            this.colorTextureId = TextureUtil.generateTextureId();
            if (this.useDepth) {
                this.depthBufferId = TextureUtil.generateTextureId();
                GlStateManager._bindTexture(this.depthBufferId);
                GlStateManager._texParameter(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
                GlStateManager._texParameter(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
                GlStateManager._texParameter(GL_TEXTURE_2D, GL_TEXTURE_COMPARE_MODE, 0);
                GlStateManager._texParameter(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE);
                GlStateManager._texParameter(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE);
                if (!this.isStencilEnabled()) {
                    GlStateManager._texImage2D(GL_TEXTURE_2D, 0, GL_DEPTH_COMPONENT, this.width, this.height, 0, GL_DEPTH_COMPONENT, GL_FLOAT, null);
                } else {
                    GlStateManager._texImage2D(GL_TEXTURE_2D, 0, GL_DEPTH32F_STENCIL8, this.width, this.height, 0, GL_DEPTH_STENCIL, GL_FLOAT_32_UNSIGNED_INT_24_8_REV, null);
                }
            }

            this.setFilterMode(GL_NEAREST, true);
            GlStateManager._bindTexture(this.colorTextureId);
            GlStateManager._texParameter(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE);
            GlStateManager._texParameter(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE);
            //GlStateManager._texImage2D(GL_TEXTURE_2D, 0, GL_RGBA8, this.width, this.height, 0, GL_RGBA, GL_UNSIGNED_BYTE, null);
            //GlStateManager._texImage2D(GL_TEXTURE_2D, 0, GL_RGBA16F, this.width, this.height, 0, GL_RGBA, GL_HALF_FLOAT, null);
            GlStateManager._texImage2D(GL_TEXTURE_2D, 0, this.internalFormat.getGlFormat(), this.width, this.height, 0, GL_RGBA, this.internalFormat.getGlType(), null);
            GlStateManager._glBindFramebuffer(GL_FRAMEBUFFER, this.frameBufferId);
            GlStateManager._glFramebufferTexture2D(GL_FRAMEBUFFER, GL_COLOR_ATTACHMENT0, GL_TEXTURE_2D, this.colorTextureId, 0);
            if (this.useDepth) {
                if (!this.isStencilEnabled()) {
                    GlStateManager._glFramebufferTexture2D(GL_FRAMEBUFFER, GL_DEPTH_ATTACHMENT, GL_TEXTURE_2D, this.depthBufferId, 0);
                } else if (NeoForgeConfig.CLIENT.useCombinedDepthStencilAttachment.get()) {
                    GlStateManager._glFramebufferTexture2D(GL_FRAMEBUFFER, GL_DEPTH_STENCIL_ATTACHMENT, GL_TEXTURE_2D, this.depthBufferId, 0);
                } else {
                    GlStateManager._glFramebufferTexture2D(GL_FRAMEBUFFER, GL_DEPTH_ATTACHMENT, GL_TEXTURE_2D, this.depthBufferId, 0);
                    GlStateManager._glFramebufferTexture2D(GL_FRAMEBUFFER, GL_STENCIL_ATTACHMENT, GL_TEXTURE_2D, this.depthBufferId, 0);
                }
            }

            this.checkStatus();
            this.clear(clearError);
            this.unbindRead();
        } else {
            throw new IllegalArgumentException("Window " + width + "x" + height + " size out of bounds (max. size: " + i + ")");
        }
    }
}
