#version 150

uniform sampler2D Sampler0;
uniform float GameTime;
uniform vec4 ColorModulator;

in vec2 texCoord0;
in vec4 vertexColor;
out vec4 fragColor;

void main() {
    vec2 p = texCoord0 * 2.0 - 1.0;
    float dist = length(p);
    float coreSize = 0.40;

    // Render only the black core; everything outside is transparent.
    // Use derivative-based edge smoothing for cleaner visuals at varying scales.
    float edgeAA = max(0.002, fwidth(dist) * 1.25);
    float alpha = 1.0 - smoothstep(coreSize - edgeAA, coreSize + edgeAA, dist);
    if (alpha < 0.01 || dist > 1.0) {
        discard;
    }

    fragColor = vec4(0.0, 0.0, 0.0, alpha) * vertexColor * ColorModulator;
}
