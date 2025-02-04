#version 150

uniform sampler2D DiffuseSampler;
uniform sampler2D BloomMaskSampler;

in vec2 texCoord;

out vec4 fragColor;

void main() {
    vec4 bloomMask = texture(BloomMaskSampler, texCoord);
    vec4 diffuse = texture(DiffuseSampler, texCoord);
    //fragColor = bloomMask * diffuse;
    fragColor = bloomMask;
}