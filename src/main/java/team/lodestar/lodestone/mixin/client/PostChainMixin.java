package team.lodestar.lodestone.mixin.client;

import com.google.gson.JsonObject;
import com.llamalad7.mixinextras.sugar.Local;
import com.mojang.blaze3d.pipeline.RenderTarget;
import net.minecraft.client.renderer.PostChain;
import net.minecraft.util.GsonHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import team.lodestar.lodestone.LodestoneLib;
import team.lodestar.lodestone.systems.texture.CustomizableTextureTarget;
import team.lodestar.lodestone.systems.texture.InternalTextureFormat;

import java.util.List;
import java.util.Map;

@Mixin(PostChain.class)
public class PostChainMixin {
    @Redirect(method = "parseTargetNode",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/renderer/PostChain;addTempTarget(Ljava/lang/String;II)V"
            )
    )
    public void addAdditionalTargetNodeSpecifications(PostChain postChain, String name, int width, int height, @Local JsonObject jsonObject) {
        if (jsonObject.has("format")) {
            String formatString = GsonHelper.getAsString(jsonObject, "format");
            InternalTextureFormat format = InternalTextureFormat.fromString(formatString);
            if (format != null) {
                addTempTargetWithFormat(name, width, height, format);
            } else {
                postChain.addTempTarget(name, width, height);
            }
        } else {
            postChain.addTempTarget(name, width, height);
        }
    }

    private void addTempTargetWithFormat(String name, int width, int height, InternalTextureFormat internalFormat) {
        RenderTarget rendertarget = new CustomizableTextureTarget(width, height, true, internalFormat);
        rendertarget.setClearColor(0.0F, 0.0F, 0.0F, 0.0F);
        if (this.screenTarget.isStencilEnabled()) {
            rendertarget.enableStencil();
        }

        this.customRenderTargets.put(name, rendertarget);
        if (width == this.screenWidth && height == this.screenHeight) {
            this.fullSizedTargets.add(rendertarget);
        }
    }

    @Shadow
    private RenderTarget screenTarget;
    @Shadow
    private int screenWidth;
    @Shadow
    private int screenHeight;
    @Shadow
    private Map<String, RenderTarget> customRenderTargets;
    @Shadow
    private List<RenderTarget> fullSizedTargets;

}
