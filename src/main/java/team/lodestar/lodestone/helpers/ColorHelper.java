package team.lodestar.lodestone.helpers;

import net.minecraft.util.FastColor;
import net.minecraft.util.Mth;
import team.lodestar.lodestone.systems.easing.Easing;

import java.awt.*;
import java.util.List;

@SuppressWarnings("unused")
public class ColorHelper {

    public static Color getColor(int decimal) {
        return new Color(decimal);
    }

    public static void RGBToHSV(Color color, float[] hsv) {
        Color.RGBtoHSB(color.getRed(), color.getGreen(), color.getBlue(), hsv);
    }

    public static int getColor(Color color) {
        return FastColor.ARGB32.color(color.getAlpha(), color.getRed(), color.getGreen(), color.getBlue());
    }

    public static int getColor(int r, int g, int b) {
        return FastColor.ARGB32.color(255, r, g, b);
    }

    public static int getColor(int r, int g, int b, int a) {
        return FastColor.ARGB32.color(a, r, g, b);
    }

    public static int getColor(float r, float g, float b, float a) {
        return FastColor.ARGB32.color((int) (a * 255f), (int) (r * 255f), (int) (g * 255f), (int) (b * 255f));
    }

    public static Color colorLerp(Easing easing, float pct, Color startColor, Color endColor) {
        pct = Mth.clamp(pct, 0, 1);
        int br = startColor.getRed(), bg = startColor.getGreen(), bb = startColor.getBlue();
        int dr = endColor.getRed(), dg = endColor.getGreen(), db = endColor.getBlue();
        float ease = easing.ease(pct, 0, 1, 1);
        int red = (int) Mth.lerp(ease, br, dr);
        int green = (int) Mth.lerp(ease, bg, dg);
        int blue = (int) Mth.lerp(ease, bb, db);
        return new Color(Mth.clamp(red, 0, 255), Mth.clamp(green, 0, 255), Mth.clamp(blue, 0, 255));
    }

    public static Color colorLerp(Easing easing, float pct, float min, float max, Color startColor, Color endColor) {
        pct = Mth.clamp(pct, 0, 1);
        int br = startColor.getRed(), bg = startColor.getGreen(), bb = startColor.getBlue();
        int dr = endColor.getRed(), dg = endColor.getGreen(), db = endColor.getBlue();
        float ease = easing.ease(pct, min, max, 1);
        int red = (int) Mth.lerp(ease, br, dr);
        int green = (int) Mth.lerp(ease, bg, dg);
        int blue = (int) Mth.lerp(ease, bb, db);
        return new Color(Mth.clamp(red, 0, 255), Mth.clamp(green, 0, 255), Mth.clamp(blue, 0, 255));
    }

    public static Color multicolorLerp(Easing easing, float pct, Color... colors) {
        return multicolorLerp(easing, pct, List.of(colors));
    }

    public static Color multicolorLerp(Easing easing, float pct, List<Color> colors) {
        pct = Mth.clamp(pct, 0, 1);
        int colorCount = colors.size() - 1;
        float lerp = easing.ease(pct, 0, 1, 1);
        float colorIndex = colorCount * lerp;
        int index = (int) Mth.clamp(colorIndex, 0, colorCount);
        Color color = colors.get(index);
        Color nextColor = index == colorCount ? color : colors.get(index + 1);
        return ColorHelper.colorLerp(easing, colorIndex - (int) (colorIndex), color, nextColor);
    }

    public static Color multicolorLerp(Easing easing, float pct, float min, float max, Color... colors) {
        return multicolorLerp(easing, pct, min, max, List.of(colors));
    }

    public static Color multicolorLerp(Easing easing, float pct, float min, float max, List<Color> colors) {
        pct = Mth.clamp(pct, 0, 1);
        int colorCount = colors.size() - 1;
        float lerp = easing.ease(pct, 0, 1, 1);
        float colorIndex = colorCount * lerp;
        int index = (int) Mth.clamp(colorIndex, 0, colorCount);
        Color color = colors.get(index);
        Color nextColor = index == colorCount ? color : colors.get(index + 1);
        return ColorHelper.colorLerp(easing, colorIndex - (int) (colorIndex), min, max, nextColor, color);
    }

    public static Color darker(Color color, int times) {
        return darker(color, times, 0.7f);
    }

    public static Color darker(Color color, int power, float factor) {
        float FACTOR = (float) Math.pow(factor, power);
        return new Color(Math.max((int) (color.getRed() * FACTOR), 0),
                Math.max((int) (color.getGreen() * FACTOR), 0),
                Math.max((int) (color.getBlue() * FACTOR), 0),
                color.getAlpha());
    }

    public static Color brighter(Color color, int power) {
        return brighter(color, power, 0.7f);
    }

    public static Color brighter(Color color, int power, float factor) {
        float FACTOR = (float) Math.pow(factor, power);
        int r = color.getRed();
        int g = color.getGreen();
        int b = color.getBlue();
        int alpha = color.getAlpha();

        int i = (int) (1.0 / (1.0 - FACTOR));
        if (r == 0 && g == 0 && b == 0) {
            return new Color(i, i, i, alpha);
        }
        if (r > 0 && r < i) r = i;
        if (g > 0 && g < i) g = i;
        if (b > 0 && b < i) b = i;

        return new Color(Math.min((int) (r / FACTOR), 255),
                Math.min((int) (g / FACTOR), 255),
                Math.min((int) (b / FACTOR), 255),
                alpha);
    }
}