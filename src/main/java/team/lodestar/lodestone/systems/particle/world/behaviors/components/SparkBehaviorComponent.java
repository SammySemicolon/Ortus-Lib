package team.lodestar.lodestone.systems.particle.world.behaviors.components;

import net.minecraft.world.phys.*;
import team.lodestar.lodestone.systems.particle.data.*;
import team.lodestar.lodestone.systems.particle.world.*;
import team.lodestar.lodestone.systems.particle.world.behaviors.*;

public class SparkBehaviorComponent implements LodestoneBehaviorComponent {

    protected final GenericParticleData lengthData;
    protected Vec3 forcedDirection;
    protected Vec3 cachedDirection;

    public SparkBehaviorComponent(GenericParticleData lengthData) {
        this.lengthData = lengthData;
    }

    public SparkBehaviorComponent() {
        this(null);
    }

    @Override
    public void tick(LodestoneWorldParticle particle) {
        var direction = particle.getParticleSpeed().normalize();
        if (!direction.equals(Vec3.ZERO)) {
            cachedDirection = direction;
        }
    }

    public SparkBehaviorComponent setForcedDirection(Vec3 forcedDirection) {
        this.forcedDirection = forcedDirection;
        return this;
    }

    public GenericParticleData getLengthData(LodestoneWorldParticle particle) {
        return getLengthData() != null ? getLengthData() : particle.scaleData;
    }

    public Vec3 getDirection(LodestoneWorldParticle particle) {
        if (forcedDirection != null) {
            return forcedDirection;
        }
        return getCachedDirection() != null ? getCachedDirection() : particle.getParticleSpeed().normalize();
    }

    @Override
    public LodestoneParticleBehavior getBehaviorType() {
        return LodestoneParticleBehavior.SPARK;
    }

    public GenericParticleData getLengthData() {
        return lengthData;
    }

    public Vec3 getCachedDirection() {
        return cachedDirection;
    }

    public Vec3 sparkStart(Vec3 pos, Vec3 offset) {
        return pos.subtract(offset);
    }

    public Vec3 sparkEnd(Vec3 pos, Vec3 offset) {
        return pos.add(offset);
    }
}