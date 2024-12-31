package team.lodestar.lodestone.helpers;

import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.joml.AxisAngle4f;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import org.joml.Vector4f;

import java.util.ArrayList;

/**
 * A collection of methods designed to simplify and unify the use of vectors
 */
@SuppressWarnings("unused")
public class VecHelper {
    //TODO: re-implement all NECESSARY functions into the new vecHelper method and remove ones that are not required.
    public static final Vec3 CENTER_OF_ORIGIN = new Vec3(.5, .5, .5);

    /**
     * A method that returns a position on the perimeter of a circle around a given Vec3 position
     *
     * @param pos      - Defines the center of the circle
     * @param distance - Defines the radius of your circle
     * @param current  - Defines the current point we are calculating the position for on the circle
     * @param total    - Defines the total amount of points in the circle
     */
    public static Vec3 radialOffset(Vec3 pos, float distance, float current, float total) {
        double angle = current / total * (Math.PI * 2);
        double dx2 = (distance * Math.cos(angle));
        double dz2 = (distance * Math.sin(angle));

        Vec3 vector = new Vec3(dx2, 0, dz2);
        double x = vector.x * distance;
        double z = vector.z * distance;
        return pos.add(new Vec3(x, 0, z));
    }

    /**
     * A method that returns an array list of positions on the perimeter of a circle around a given Vec3 position.
     * These positions constantly rotate around the center of the circle based on gameTime
     *
     * @param pos      - Defines the center of the circle
     * @param distance - Defines the radius of your circle
     * @param total    - Defines the total amount of points in the circle
     * @param gameTime - Defines the current game time value
     * @param time     - Defines the total time for one position to complete a full rotation cycle
     */
    public static ArrayList<Vec3> rotatingRadialOffsets(Vec3 pos, float distance, float total, float gameTime, float time) {
        return rotatingRadialOffsets(pos, distance, distance, total, gameTime, time);
    }

    /**
     * A method that returns an array list of positions on the perimeter of a sphere around a given Vec3 position.
     * These positions constantly rotate around the center of the circle based on gameTime.
     */
    public static ArrayList<Vec3> rotatingRadialOffsets(Vec3 pos, float distanceX, float distanceZ, float total, float gameTime, float time) {
        ArrayList<Vec3> positions = new ArrayList<>();
        for (int i = 0; i < total; i++) {
            positions.add(rotatingRadialOffset(pos, distanceX, distanceZ, i, total, gameTime, time));
        }
        return positions;
    }

    /**
     * A method that returns a single position on the perimeter of a circle around a given Vec3 position.
     * These positions constantly rotate around the center of the circle based on gameTime
     */
    public static Vec3 rotatingRadialOffset(Vec3 pos, float distance, float current, float total, long gameTime, float time) {
        return rotatingRadialOffset(pos, distance, distance, current, total, gameTime, time);
    }

    /**
     * A method that returns a single position on the perimeter of a circle around a given Vec3 position.
     * These positions constantly rotate around the center of the circle based on gameTime
     */
    public static Vec3 rotatingRadialOffset(Vec3 pos, float distanceX, float distanceZ, float current, float total, float gameTime, float time) {
        double angle = current / total * (Math.PI * 2);
        angle += ((gameTime % time) / time) * (Math.PI * 2);
        double dx2 = (distanceX * Math.cos(angle));
        double dz2 = (distanceZ * Math.sin(angle));

        Vec3 vector2f = new Vec3(dx2, 0, dz2);
        double x = vector2f.x * distanceX;
        double z = vector2f.z * distanceZ;
        return pos.add(x, 0, z);
    }

    public static ArrayList<Vec3> blockOutlinePositions(Level level, BlockPos pos) {
        ArrayList<Vec3> arrayList = new ArrayList<>();
        double d0 = 0.5625D;
        var random = level.random;
        for (Direction direction : Direction.values()) {
            BlockPos blockpos = pos.relative(direction);
            if (!level.getBlockState(blockpos).isSolidRender(level, blockpos)) {
                Direction.Axis direction$axis = direction.getAxis();
                double d1 = direction$axis == Direction.Axis.X ? 0.5D + d0 * (double) direction.getStepX() : (double) random.nextFloat();
                double d2 = direction$axis == Direction.Axis.Y ? 0.5D + d0 * (double) direction.getStepY() : (double) random.nextFloat();
                double d3 = direction$axis == Direction.Axis.Z ? 0.5D + d0 * (double) direction.getStepZ() : (double) random.nextFloat();
                arrayList.add(new Vec3((double) pos.getX() + d1, (double) pos.getY() + d2, (double) pos.getZ() + d3));
            }
        }
        return arrayList;
    }

    public static Vec3 getCenterOf(Vec3i pos) {
        if (pos.equals(Vec3i.ZERO))
            return CENTER_OF_ORIGIN;
        return Vec3.atLowerCornerOf(pos).add(.5f, .5f, .5f);
    }

    public static Vec3 axisAlignedPlaneOf(Vec3 vec) {
        vec = vec.normalize();
        return new Vec3(1, 1, 1).subtract(Math.abs(vec.x), Math.abs(vec.y), Math.abs(vec.z));
    }

    public static Vec3 rotate(Vec3 vec, double deg, Direction.Axis axis) {
        if (deg == 0)
            return vec;
        if (vec == Vec3.ZERO)
            return vec;

        float angle = (float) (deg / 180f * Math.PI);
        double sin = Mth.sin(angle);
        double cos = Mth.cos(angle);
        double x = vec.x;
        double y = vec.y;
        double z = vec.z;

        if (axis == Direction.Axis.X)
            return new Vec3(x, y * cos - z * sin, z * cos + y * sin);
        if (axis == Direction.Axis.Y)
            return new Vec3(x * cos + z * sin, y, z * cos - x * sin);
        if (axis == Direction.Axis.Z)
            return new Vec3(x * cos - y * sin, y * cos + x * sin, z);
        return vec;
    }

    // https://forums.minecraftforge.net/topic/88562-116solved-3d-to-2d-conversion/?do=findComment&comment=413573 slightly modified
    public static Vec3 projectToPlayerView(Vec3 target, float partialTicks) {
        /*
         * The (centered) location on the screen of the given 3d point in the world.
         * Result is (dist right of center screen, dist up from center screen, if < 0, then in front of view plane)
         */
        Camera ari = Minecraft.getInstance().gameRenderer.getMainCamera();
        Vec3 camera_pos = ari.getPosition();
        Quaternionf camera_rotation_conj = new Quaternionf(ari.rotation());
        camera_rotation_conj.conjugate();

        Vector3f result3f = new Vector3f((float) (camera_pos.x - target.x), (float) (camera_pos.y - target.y),
                (float) (camera_pos.z - target.z));
        result3f.rotate(camera_rotation_conj);

        // ----- compensate for view bobbing (if active) -----
        // the following code adapted from GameRenderer::applyBobbing (to invert it)
        Minecraft mc = Minecraft.getInstance();
        if (mc.options.bobView().get()) {
            Entity renderViewEntity = mc.getCameraEntity();
            if (renderViewEntity instanceof Player player) {
                float distwalked_modified = player.walkDist;

                float f = distwalked_modified - player.walkDistO;
                float f1 = -(distwalked_modified + f * partialTicks);
                float f2 = Mth.lerp(partialTicks, player.oBob, player.bob);
                Quaternionf q2 = new Quaternionf(new AxisAngle4f(Math.abs(Mth.cos(f1 * (float) Math.PI - 0.2F) * f2) * 5.0F, Vector3fHelper.XP));
                q2.conjugate();
                result3f.rotate(q2);

                Quaternionf q1 = new Quaternionf(new AxisAngle4f(Math.abs(Mth.sin(f1 * (float) Math.PI) * f2) * 3.0F, Vector3fHelper.ZP));
                q1.conjugate();
                result3f.rotate(q1);

                Vector3f bob_translation = new Vector3f((Mth.sin(f1 * (float) Math.PI) * f2 * 0.5F), (-Math.abs(Mth.cos(f1 * (float) Math.PI) * f2)), 0.0f);
                result3f.add(new Vector3f(bob_translation.x(), -bob_translation.y(), bob_translation.z()));
            }
        }

        // ----- adjust for fov -----
        float fov = (float) mc.gameRenderer.getFov(ari, partialTicks, true);

        float half_height = (float) mc.getWindow()
                .getGuiScaledHeight() / 2;
        float scale_factor = half_height / (result3f.z() * (float) Math.tan(Math.toRadians(fov / 2)));
        return new Vec3(-result3f.x() * scale_factor, result3f.y() * scale_factor, result3f.z());
    }

    public static class Vector3fHelper {
        public static Vector3f XP = new Vector3f(1.0F, 0.0F, 0.0F);
        public static Vector3f YP = new Vector3f(0.0F, 1.0F, 0.0F);
        public static Vector3f ZP = new Vector3f(0.0F, 0.0F, 1.0F);
        public static Vector3f XN = new Vector3f(-1.0F, 0.0F, 0.0F);
        public static Vector3f YN = new Vector3f(0.0F, -1.0F, 0.0F);
        public static Vector3f ZN = new Vector3f(0.0F, 0.0F, -1.0F);

        public static Quaternionf rotation(float rotation, Vector3f axis) {
            return new Quaternionf(new AxisAngle4f(rotation, axis));
        }
    }

    public static class Vector4fHelper {
        public static void perspectiveDivide(Vector4f v) {
            v.div(v.w);
        }
    }

}