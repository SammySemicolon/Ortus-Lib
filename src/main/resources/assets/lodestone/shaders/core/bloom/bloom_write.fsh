#version 150

uniform vec4 ColorModulator;
uniform float FogStart;
uniform float FogEnd;
uniform vec4 FogColor;

in float vertexDistance;
in vec4 vertexColor;

out vec4 fragColor;

void main() {
    float brightness = 5.0f;
    vec3 color = vec3(0.3, 0.3, 1.0);
    vec3 bloomColor = color * brightness;
    fragColor = vec4(bloomColor, 1.0);
}