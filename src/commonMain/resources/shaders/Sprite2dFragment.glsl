#version 410

precision highp float;

// constants
const float inverse255 = 1.0 / 255.0;
const vec3 colorRetro = inverse255 * vec3(140.0, 173.0, 15.0);

// uniforms
uniform sampler2D uTexture0;
uniform float uAlpha; // alpha override, applies if >= 0
uniform int uEffect; // 1 = sepia, 2 = retro

// input
layout (location = 0) in vec2 inUv;
layout (location = 1) in float inAlpha;

// output
layout (location = 0) out vec4 outColor;

vec3 convert_sepia(vec3 v) {
    return vec3(
        (v.r * .393) + (v.g *.769) + (v.b * .189),
        (v.r * .349) + (v.g *.686) + (v.b * .168),
        (v.r * .272) + (v.g *.534) + (v.b * .131)
    );
}

vec3 convert_retro(vec3 v) {
    return colorRetro * convert_sepia(v);
}

// implementation
void main() {
    // apply texture
    vec4 c = texture2D(uTexture0, inUv);

    // apply alpha
    if (inAlpha >= 0.0) {
        c.a = inAlpha;
    }

    // blend the alpha if override set
    if (uAlpha >= 0.0) {
        c.a *= uAlpha;
    }

    // apply effects
    if (uEffect == 1) {
        c.rgb = convert_sepia(c.rgb);
    } else if (uEffect == 2) {
        c.rgb = convert_retro(c.rgb);
    }

    outColor = c;
}