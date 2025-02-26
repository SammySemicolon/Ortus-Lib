package team.lodestar.lodestone.helpers;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static net.minecraft.util.Mth.sqrt;

/**
 * A collection of helper methods for data manipulation
 */
@SuppressWarnings("unused")
public class DataHelper {
    /**
     * Reverses the order of any K collection of T entries
     */
    public static <T, K extends Collection<T>> K reverseOrder(K reversed, Collection<T> items) {
        ArrayList<T> original = new ArrayList<>(items);
        for (int i = items.size() - 1; i >= 0; i--) {
            reversed.add(original.get(i));
        }
        return reversed;
    }

    /**
     * Capitalizes the first character in each word and replaces [regex] with space
     */
    public static String toTitleCase(String givenString, String regex) {
        String[] stringArray = givenString.split(regex);
        StringBuilder stringBuilder = new StringBuilder();
        for (String string : stringArray) {
            stringBuilder.append(Character.toUpperCase(string.charAt(0))).append(string.substring(1)).append(regex);
        }
        return stringBuilder.toString().trim().replaceAll(regex, " ").substring(0, stringBuilder.length() - 1);
    }

    /**
     * returns an integer array of random ints
     *
     * @param count the amount of integers
     * @param range the range the random function uses
     */
    public static int[] nextInts(int count, int range) {
        Random rand = new Random();
        int[] ints = new int[count];
        for (int i = 0; i < count; i++) {
            while (true) {
                int nextInt = rand.nextInt(range);
                if (Arrays.stream(ints).noneMatch(j -> j == nextInt)) {
                    ints[i] = nextInt;
                    break;
                }
            }
        }
        return ints;
    }

    /**
     * returns whether an array of items has any duplicates
     */
    public static <T> boolean hasDuplicate(T[] things) {
        Set<T> thingSet = new HashSet<>();
        return !Arrays.stream(things).allMatch(thingSet::add);
    }

    /**
     * removes an entry from a collection and returns it if removed
     */
    @SuppressWarnings("varargs")
    public static <T> T take(Collection<? extends T> src, T item) {
        src.remove(item);
        return item;
    }

    /**
     * removes all entry from a collection and returns all items removed in a new collection
     */
    @SafeVarargs
    @SuppressWarnings("varargs")
    public static <T> Collection<T> takeAll(Collection<? extends T> src, T... items) {
        List<T> ret = Arrays.asList(items);
        for (T item : items) {
            if (!src.contains(item)) {
                return Collections.emptyList();
            }
        }
        if (!src.removeAll(ret)) {
            return Collections.emptyList();
        }
        return ret;
    }

    /**
     * removes all entry from a collection based off of a predicate and returns all items removed in a new collection
     */
    public static <T> Collection<T> takeAll(Collection<T> src, Predicate<T> pred) {
        List<T> ret = new ArrayList<>();

        Iterator<T> iter = src.iterator();
        while (iter.hasNext()) {
            T item = iter.next();
            if (pred.test(item)) {
                iter.remove();
                ret.add(item);
            }
        }

        if (ret.isEmpty()) {
            return Collections.emptyList();
        }
        return ret;
    }

    /**
     * create a copy of all items in a list that match from another list of items
     */
    @SafeVarargs
    public static <T> Collection<T> getAll(Collection<? extends T> src, T... items) {
        return List.copyOf(getAll(src, t -> Arrays.stream(items).anyMatch(tAgain -> tAgain.getClass().isInstance(t))));
    }

    /**
     * create a copy of all items in a list that match from a predicate
     */
    public static <T> Collection<T> getAll(Collection<T> src, Predicate<T> pred) {
        return src.stream().filter(pred).collect(Collectors.toList());
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

    public static float distSqr(float... a) {
        float d = 0.0F;
        for (float f : a) {
            d += f * f;
        }
        return d;
    }

    public static float distance(float... a) {
        return sqrt(distSqr(a));
    }
}