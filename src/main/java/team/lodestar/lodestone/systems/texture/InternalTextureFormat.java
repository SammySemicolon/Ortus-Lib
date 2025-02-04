package team.lodestar.lodestone.systems.texture;

import javax.annotation.Nullable;

import static org.lwjgl.opengl.GL31.*;

public enum InternalTextureFormat {
    RGBA8(GL_RGBA8, GL_UNSIGNED_BYTE),
    RGBA16F(GL_RGBA16F, GL_HALF_FLOAT),
    RGBA32F(GL_RGBA32F, GL_FLOAT),
    RGBA16(GL_RGBA16, GL_UNSIGNED_SHORT),
    RGBA16_SNORM(GL_RGBA16_SNORM, GL_SHORT),
    RGBA8_SNORM(GL_RGBA8_SNORM, GL_BYTE);

    private final int glFormat;
    private final int glType;
    InternalTextureFormat(int glFormat, int glType) {
        this.glFormat = glFormat;
        this.glType = glType;
    }

    public int getGlFormat() {
        return glFormat;
    }

    public int getGlType() {
        return glType;
    }

    public static @Nullable InternalTextureFormat fromString(String format) {
        for (InternalTextureFormat value : values()) {
            if (value.name().equalsIgnoreCase(format)) {
                return value;
            }
        }
        return null;
    }

}
