package team.lodestar.lodestone.systems.rendering.trail;

import net.minecraft.world.phys.Vec3;
import org.joml.*;

public class TrailPoint {
    private Vec3 oldPosition;
    private Vec3 position;
    private int age;

    public TrailPoint(Vec3 position, int age) {
        this.position = position;
        this.age = age;
    }

    public TrailPoint(Vec3 position) {
        this(position, 0);
    }

    public Vector4f getMatrixPosition(Matrix4f pose) {
        return getMatrixPosition(getPosition(), pose);
    }

    public Vector4f getInterpolatedMatrixPosition(Matrix4f pose, float partialTicks) {
        return getMatrixPosition(getInterpolatedPosition(partialTicks), pose);
    }

    public Vec3 getPosition() {
        return position;
    }

    public Vec3 getInterpolatedPosition(float partialTicks) {
        if (oldPosition == null) {
            return position;
        }
        return oldPosition.lerp(position, partialTicks);
    }

    public void setPosition(Vec3 position) {
        this.oldPosition = this.position;
        this.position = position;
    }

    public void move(Vec3 offset) {
        setPosition(getPosition().add(offset));
    }

    public int getAge() {
        return age;
    }

    public void tick() {
        age++;
    }

    public static Vector4f getMatrixPosition(Vec3 position, Matrix4f pose) {
        return new Vector4f((float) position.x, (float) position.y, (float) position.z, 1.0f).mul(pose);
    }
}