package team.lodestar.lodestone.systems.postprocess;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RenderLevelStageEvent;

import java.util.ArrayList;
import java.util.List;

/**
 * Handles world-space post-processing.
 * Based on vanilla {@link net.minecraft.client.renderer.PostChain} system, but allows the shader to access the world depth buffer.
 */
@EventBusSubscriber(value = Dist.CLIENT, bus = EventBusSubscriber.Bus.GAME)
public class PostProcessHandler {
    private static final List<PostProcessor> instances = new ArrayList<>();

    private static boolean didCopyDepth = false;

    /**
     * Add an {@link PostProcessor} for it to be handled automatically.
     * IMPORTANT: processors has to be added in the right order!!!
     * There's no way of getting an instance, so you need to keep the instance yourself.
     */
    public static void addInstance(PostProcessor instance) {
        instances.add(instance);
    }

    public static void render() {
        instances.forEach(PostProcessor::applyPostProcess);
        Minecraft.getInstance().getMainRenderTarget().bindWrite(false);
    }

    public static void copyDepthBuffer() {
        if (didCopyDepth) return;
        instances.forEach(PostProcessor::copyDepthBuffer);
        didCopyDepth = true;
    }

    public static void resize(int width, int height) {
        instances.forEach(i -> i.resize(width, height));
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onWorldRenderLast(RenderLevelStageEvent event) {
        if (event.getStage().equals(RenderLevelStageEvent.Stage.AFTER_PARTICLES)) {
            PostProcessor.viewModelMatrix = RenderSystem.getModelViewMatrix(); // Copy viewModelMatrix from RenderSystem
        }
        if (event.getStage().equals(RenderLevelStageEvent.Stage.AFTER_LEVEL)) {
            copyDepthBuffer(); // copy the depth buffer if the mixin didn't trigger

            render();

            didCopyDepth = false; // reset for next frame
        }
    }
}
