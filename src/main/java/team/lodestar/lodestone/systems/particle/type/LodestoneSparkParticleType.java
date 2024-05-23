package team.lodestar.lodestone.systems.particle.type;

import com.mojang.serialization.Codec;
import net.fabricmc.fabric.impl.client.particle.FabricSpriteProviderImpl;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.core.particles.ParticleType;
import team.lodestar.lodestone.systems.particle.options.SparkParticleOptions;
import team.lodestar.lodestone.systems.particle.world.SparkParticle;

import org.jetbrains.annotations.Nullable;

public class LodestoneSparkParticleType extends ParticleType<SparkParticleOptions> {
    public LodestoneSparkParticleType() {
        super(false, SparkParticleOptions.DESERIALIZER);
    }


    @Override
    public Codec<SparkParticleOptions> codec() {
        return SparkParticleOptions.sparkCodec(this);
    }

    public static class Factory implements ParticleProvider<SparkParticleOptions> {
        private final SpriteSet sprite;

        public Factory(SpriteSet sprite) {
            this.sprite = sprite;
        }

        @Nullable
        @Override
        public Particle createParticle(SparkParticleOptions data, ClientLevel world, double x, double y, double z, double mx, double my, double mz) {
            return new SparkParticle(world, data, (FabricSpriteProviderImpl) sprite, x, y, z, mx, my, mz);
        }
    }
}