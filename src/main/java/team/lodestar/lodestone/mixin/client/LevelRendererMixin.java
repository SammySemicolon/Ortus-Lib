package team.lodestar.lodestone.mixin.client;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.*;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.*;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.SoundType;
import org.joml.Matrix4f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.Slice;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import team.lodestar.lodestone.handlers.RenderHandler;
import team.lodestar.lodestone.systems.postprocess.PostProcessHandler;
import team.lodestar.lodestone.systems.sound.ExtendedSoundType;

import javax.annotation.Nullable;

@Mixin(LevelRenderer.class)
public class LevelRendererMixin {

    @Shadow
    @Nullable
    private ClientLevel level;
    @Unique
    private SoundType lodestone$blockSoundType;

    @ModifyVariable(method = "levelEvent", slice = @Slice(from = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/Block;stateById(I)Lnet/minecraft/world/level/block/state/BlockState;")), at = @At(value = "INVOKE", target = "Lnet/minecraft/client/multiplayer/ClientLevel;playLocalSound(Lnet/minecraft/core/BlockPos;Lnet/minecraft/sounds/SoundEvent;Lnet/minecraft/sounds/SoundSource;FFZ)V", ordinal = 0), index = 17)
    private SoundType lodestone$GetBreakSound(SoundType type) {
        return this.lodestone$blockSoundType = type;
    }

    @Inject(method = "levelEvent", slice = @Slice(from = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/Block;stateById(I)Lnet/minecraft/world/level/block/state/BlockState;")), at = @At(value = "INVOKE", target = "Lnet/minecraft/client/multiplayer/ClientLevel;playLocalSound(Lnet/minecraft/core/BlockPos;Lnet/minecraft/sounds/SoundEvent;Lnet/minecraft/sounds/SoundSource;FFZ)V", ordinal = 0))
    private void lodestone$CallExtendedBreakSound(int pType, BlockPos pPos, int pData, CallbackInfo ci) {
        if (lodestone$blockSoundType instanceof ExtendedSoundType extendedSoundType) {
            extendedSoundType.onPlayBreakSound(this.level, pPos);
        }
    }

    @Inject(method = "renderLevel", at = @At(value = "INVOKE", target = "net.minecraft.client.renderer.PostChain.process(F)V", ordinal = 1))
    public void lodestone$injectionBeforeTransparencyChainProcess(CallbackInfo ci) {
        PostProcessHandler.copyDepthBuffer();
    }

    @Inject(method = "renderLevel", at = @At(value = "HEAD"))
    private void lodestoneLevelRendererPoseStackGrabber(DeltaTracker pDeltaTracker, boolean pRenderBlockOutline, Camera pCamera, GameRenderer pGameRenderer, LightTexture pLightTexture, Matrix4f pFrustumMatrix, Matrix4f pProjectionMatrix, CallbackInfo ci) {
        RenderHandler.MAIN_PROJ = pProjectionMatrix;
    }
}