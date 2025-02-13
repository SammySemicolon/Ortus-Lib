package team.lodestar.lodestone.systems.particle.data.color;

import com.mojang.serialization.*;
import com.mojang.serialization.codecs.*;
import net.minecraft.util.Mth;
import team.lodestar.lodestone.systems.easing.Easing;

import java.awt.*;

public class ColorParticleData {

    public static final Codec<ColorParticleData> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.FLOAT.fieldOf("r1").forGetter(data -> data.r1),
            Codec.FLOAT.fieldOf("g1").forGetter(data -> data.g1),
            Codec.FLOAT.fieldOf("b1").forGetter(data -> data.b1),
            Codec.FLOAT.fieldOf("r2").forGetter(data -> data.r2),
            Codec.FLOAT.fieldOf("g2").forGetter(data -> data.g2),
            Codec.FLOAT.fieldOf("b2").forGetter(data -> data.b2),
            Codec.FLOAT.fieldOf("colorCoefficient").forGetter(data -> data.colorCoefficient),
            Easing.CODEC.fieldOf("colorCurveEasing").forGetter(data -> data.colorCurveEasing),
            Codec.FLOAT.fieldOf("coefficientMultiplier").forGetter(data -> data.coefficientMultiplier)
    ).apply(instance, ColorParticleData::new));

    public final float r1, g1, b1, r2, g2, b2;
    public final float colorCoefficient;
    public final Easing colorCurveEasing;

    public float coefficientMultiplier = 1;

    protected ColorParticleData(float r1, float g1, float b1, float r2, float g2, float b2, float colorCoefficient, Easing colorCurveEasing, float coefficientMultiplier) {
        this(r1, g1, b1, r2, g2, b2, colorCoefficient, colorCurveEasing);
        this.coefficientMultiplier = coefficientMultiplier;
    }

    protected ColorParticleData(float r1, float g1, float b1, float r2, float g2, float b2, float colorCoefficient, Easing colorCurveEasing) {
        this.r1 = r1;
        this.g1 = g1;
        this.b1 = b1;
        this.r2 = r2;
        this.g2 = g2;
        this.b2 = b2;
        this.colorCoefficient = colorCoefficient;
        this.colorCurveEasing = colorCurveEasing;
    }

    public ColorParticleData multiplyCoefficient(float coefficientMultiplier) {
        this.coefficientMultiplier *= coefficientMultiplier;
        return this;
    }

    public ColorParticleData overrideCoefficientMultiplier(float coefficientMultiplier) {
        this.coefficientMultiplier = coefficientMultiplier;
        return this;
    }

    public float getProgress(float age, float lifetime) {
        return Mth.clamp((age * colorCoefficient * coefficientMultiplier) / lifetime, 0, 1);
    }

    public Color getStartingColor() {
        return new Color(r1, g1, b1);
    }

    public Color getEndingColor() {
        return new Color(r2, g2, b2);
    }

    public ColorParticleDataBuilder invert() {
        return create(r2, g2, b2, r1, g1, b1).setCoefficient(colorCoefficient).setEasing(colorCurveEasing);
    }

    public ColorParticleDataBuilder copy() {
        return create(r1, g1, b1, r2, g2, b2).setCoefficient(colorCoefficient).setEasing(colorCurveEasing);
    }

    public static ColorParticleDataBuilder create(float r1, float g1, float b1, float r2, float g2, float b2) {
        return new ColorParticleDataBuilder(r1, g1, b1, r2, g2, b2);
    }

    public static ColorParticleDataBuilder create(float r, float g, float b) {
        return new ColorParticleDataBuilder(r, g, b, r, g, b);
    }

    public static ColorParticleDataBuilder create(Color start, Color end) {
        return create(start.getRed() / 255f, start.getGreen() / 255f, start.getBlue() / 255f, end.getRed() / 255f, end.getGreen() / 255f, end.getBlue() / 255f);
    }

    public static ColorParticleDataBuilder create(Color color) {
        return create(color.getRed() / 255f, color.getGreen() / 255f, color.getBlue() / 255f);
    }
}