package team.lodestar.lodestone.systems.rendering.trail;

import com.mojang.blaze3d.vertex.*;
import net.minecraft.world.phys.Vec3;
import org.joml.*;

import java.util.ArrayList;
import java.util.List;
import java.util.function.*;
import java.util.stream.Collectors;

public class TrailPointBuilder {

    private final List<TrailPoint> trailPoints = new ArrayList<>();
    private Vec3 origin;
    public final Supplier<Integer> trailLength;

    public TrailPointBuilder(Supplier<Integer> trailLength) {
        this.trailLength = trailLength;
    }

    public static TrailPointBuilder create(int trailLength) {
        return create(() -> trailLength);
    }

    public static TrailPointBuilder create(Supplier<Integer> trailLength) {
        return new TrailPointBuilder(trailLength);
    }

    public List<TrailPoint> getTrailPoints() {
        return trailPoints;
    }

    public TrailPointBuilder addTrailPoint(Vec3 point) {
        return addTrailPoint(new TrailPoint(point, 0));
    }

    public TrailPointBuilder addTrailPoint(TrailPoint point) {
        trailPoints.add(point);
        return setOrigin(point.getPosition());
    }

    public TrailPointBuilder setOrigin(Vec3 origin) {
        this.origin = origin;
        return this;
    }

    public Vec3 getOrigin() {
        return origin;
    }

    public TrailPointBuilder tickTrailPoints() {
        int lifespan = trailLength.get();
        trailPoints.forEach(TrailPoint::tick);
        trailPoints.removeIf(p -> p.getAge() > lifespan);
        return this;
    }

    public TrailPointBuilder run(Consumer<TrailPoint> consumer) {
        trailPoints.forEach(consumer);
        return this;
    }

    public List<Vector4f> build(Matrix4f pose) {
        return trailPoints.stream().map(p -> p.getMatrixPosition(pose)).collect(Collectors.toList());
    }

    public List<Vector4f> build(Matrix4f pose, float partialTicks) {
        return trailPoints.stream().map(p -> p.getInterpolatedMatrixPosition(pose, partialTicks)).collect(Collectors.toList());
    }
}
