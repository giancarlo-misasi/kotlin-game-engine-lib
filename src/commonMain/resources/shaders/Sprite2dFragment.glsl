#version 410

precision highp float;

// constants
const vec3 colorRetro = (1.0 / 255.0) * vec3(140.0, 173.0, 15.0);

// uniforms
uniform sampler2D uTexture0;
uniform float uAlpha = -1.0;
uniform int uSepia = 0;
uniform int uRetro = 0;
uniform int uInvert = 0;

// input
layout (location = 0) in vec2 inUv;
layout (location = 1) in float inAlpha;

// output
layout (location = 0) out vec4 outColor;

vec3 sepia(vec3 v) {
    return vec3(
        (v.r * .393) + (v.g *.769) + (v.b * .189),
        (v.r * .349) + (v.g *.686) + (v.b * .168),
        (v.r * .272) + (v.g *.534) + (v.b * .131)
    );
}

vec3 retro(vec3 v) {
    return colorRetro * sepia(v);
}

vec3 invert(vec3 v) {
    return 1 - v;
}

// implementation
void main() {
    // apply texture
    vec4 c = texture2D(uTexture0, inUv);

    // apply alpha (and blend the override if set)
    if (inAlpha >= 0.0) {
        c.a = inAlpha;
    }
    if (uAlpha >= 0.0) {
        c.a *= uAlpha;
    }

    // apply effects
    if (uSepia == 1) {
        c.rgb = sepia(c.rgb);
    }
    if (uRetro == 1) {
        c.rgb = retro(c.rgb);
    }
    if (uInvert == 1) {
        c.rgb = invert(c.rgb);
    }

    outColor = c;
}