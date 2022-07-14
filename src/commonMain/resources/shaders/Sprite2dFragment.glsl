#version 410

precision highp float;

// constants
const float inverse255 = 1.0 / 255.0;
const vec3 colorRetro = inverse255 * vec3(140.0, 173.0, 15.0);
const float fxaaReduceMin = 1.0 / 128.0;
const float fxaaReduceMul = 1.0 / 8.0;
const float fxaaSpanMax = 8.0;

// uniforms
uniform vec2 uResolution; // screen resolution
uniform sampler2D uTexture0; // bound texture
uniform int uFxaa = 0; // 1 = enabled
uniform float uAlpha = -1.0;// alpha override, applies if >= 0
uniform int uEffect = 0;// 1 = sepia, 2 = retro

// input
layout (location = 0) in vec2 inUv;
layout (location = 1) in float inAlpha;

// output
layout (location = 0) out vec4 outColor;

// effects
// https://github.com/mattdesl/glsl-fxaa/blob/master/LICENSE.md
vec4 fxaa(sampler2D tex, vec2 fragCoord, vec2 resolution) {
    mediump vec2 inverseVP = vec2(1.0 / resolution.x, 1.0 / resolution.y);
    mediump vec2 v_rgbNW = (fragCoord + vec2(-1.0, -1.0)) * inverseVP;
    mediump vec2 v_rgbNE = (fragCoord + vec2(1.0, -1.0)) * inverseVP;
    mediump vec2 v_rgbSW = (fragCoord + vec2(-1.0, 1.0)) * inverseVP;
    mediump vec2 v_rgbSE = (fragCoord + vec2(1.0, 1.0)) * inverseVP;
    mediump vec2 v_rgbM = vec2(fragCoord * inverseVP);
    vec3 rgbNW = texture2D(tex, v_rgbNW).xyz;
    vec3 rgbNE = texture2D(tex, v_rgbNE).xyz;
    vec3 rgbSW = texture2D(tex, v_rgbSW).xyz;
    vec3 rgbSE = texture2D(tex, v_rgbSE).xyz;
    vec4 texColor = texture2D(tex, v_rgbM);
    vec3 rgbM  = texColor.xyz;
    vec3 luma = vec3(0.299, 0.587, 0.114);
    float lumaNW = dot(rgbNW, luma);
    float lumaNE = dot(rgbNE, luma);
    float lumaSW = dot(rgbSW, luma);
    float lumaSE = dot(rgbSE, luma);
    float lumaM  = dot(rgbM, luma);
    float lumaMin = min(lumaM, min(min(lumaNW, lumaNE), min(lumaSW, lumaSE)));
    float lumaMax = max(lumaM, max(max(lumaNW, lumaNE), max(lumaSW, lumaSE)));

    mediump vec2 dir;
    dir.x = -((lumaNW + lumaNE) - (lumaSW + lumaSE));
    dir.y =  ((lumaNW + lumaSW) - (lumaNE + lumaSE));

    float dirReduce = max((lumaNW + lumaNE + lumaSW + lumaSE) * (0.25 * fxaaReduceMul), fxaaReduceMin);

    float rcpDirMin = 1.0 / (min(abs(dir.x), abs(dir.y)) + dirReduce);
    dir = min(vec2(fxaaSpanMax, fxaaSpanMax), max(vec2(-fxaaSpanMax, -fxaaSpanMax), dir * rcpDirMin)) * inverseVP;

    vec3 rgbA = 0.5 * (
        texture2D(tex, fragCoord * inverseVP + dir * (1.0 / 3.0 - 0.5)).xyz +
        texture2D(tex, fragCoord * inverseVP + dir * (2.0 / 3.0 - 0.5)).xyz
    );
    vec3 rgbB = rgbA * 0.5 + 0.25 * (
        texture2D(tex, fragCoord * inverseVP + dir * -0.5).xyz +
        texture2D(tex, fragCoord * inverseVP + dir * 0.5).xyz
    );
    float lumaB = dot(rgbB, luma);

    vec4 color;
    if ((lumaB < lumaMin) || (lumaB > lumaMax)) {
        color = vec4(rgbA, texColor.a);
    } else {
        color = vec4(rgbB, texColor.a);
    }

    return color;
}

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
    if (uFxaa == 1) {
        c = fxaa(uTexture0, uResolution * inUv, uResolution);
    }

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