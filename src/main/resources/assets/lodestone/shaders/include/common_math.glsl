#moj_import <fog.glsl>

float linearizeDepth(float depth) {
    float near = 1.0;
    float far = 100.0;
    return (2.0 * near) / (far + near - depth * (far - near));
}

float getDepth(sampler2D DepthBuffer, vec2 uv) {
    return texture(DepthBuffer, uv).r;
}

float getDepthProj(sampler2D DepthBuffer, vec4 uv) {
    return textureProj(DepthBuffer, uv).r;
}

float getDepthFromClipSpace(vec4 clipSpacePosition) {
    return (clipSpacePosition.z / clipSpacePosition.w + 1.0) / 2.0;
}

vec3 getWorldPos(sampler2D DepthBuffer, vec2 texCoord, mat4 invProjMat, mat4 invViewMat, vec3 cameraPos) {
    float z = getDepth(DepthBuffer, texCoord) * 2.0 - 1.0;
    vec4 clipSpacePosition = vec4(texCoord * 2.0 - 1.0, z, 1.0);
    vec4 viewSpacePosition = invProjMat * clipSpacePosition;
    viewSpacePosition /= viewSpacePosition.w;
    vec4 localSpacePosition = invViewMat * viewSpacePosition;
    return cameraPos + localSpacePosition.xyz;
}

vec3 viewSpaceFromDepth(float depth , vec2 texCoord, mat4 invProjMat) {
    float z = depth * 2.0 - 1.0;
    vec4 clipSpacePosition = vec4(texCoord * 2.0 - 1.0, z, 1.0);
    vec4 viewSpacePosition = invProjMat * clipSpacePosition;
    return viewSpacePosition.xyz / viewSpacePosition.w;
}

float applyDepthFade(float sceneDepthView, float pixelDepthView, float intensity) {
    float spacing = pixelDepthView - sceneDepthView;
    float fade = clamp(spacing / intensity, 0.0, 1.0);
    return fade;
}

vec4 projectionUVFromLocalSpace(vec4 position) {
    vec4 projection = position * 0.5;
    projection.xy = vec2(projection.x + projection.w, projection.y + projection.w);
    projection.zw = position.zw;
    return projection;
}

// Lumitransparent color transformation
vec4 transformColor(vec4 initialColor, float lumiTransparent, vec4 vertexColor, vec4 colorModulator) {
    initialColor = lumiTransparent == 1. ? vec4(initialColor.xyz, (0.21 * initialColor.r + 0.71 * initialColor.g + 0.07 * initialColor.b)) : initialColor;
    return initialColor * vertexColor * colorModulator;
}

vec4 applyFog(vec4 initialColor, float fogStart, float fogEnd, vec4 fogColor, float vertexDistance) {
    return linear_fog(vec4(initialColor.rgb, initialColor.a*linear_fog_fade(vertexDistance, fogStart, fogEnd)), vertexDistance, fogStart, fogEnd, vec4(fogColor.rgb, initialColor.r));
}

float fogDistance(vec3 Position, int FogShape) {
    return fog_distance(Position, FogShape);
}

vec4 quantize(vec4 color, float levels) {
    return floor(color*(levels-1)+0.5)/(levels-1);
}

vec3 quantize(vec3 color, float levels) {
    return floor(color*(levels-1)+0.5)/(levels-1);
}
