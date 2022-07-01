#version 100

precision highp float;

// Texture samplers
// 0 - General
// 1 - Unused
// 2 - Unused
// 3 - Unused
uniform sampler2D uTexture0;
uniform sampler2D uTexture1;
uniform sampler2D uTexture2;
uniform sampler2D uTexture3;

// For coloring effects
uniform int uEffect;

// Texture coordinates
varying vec3 vUvOrRgb;

// Alpha values for transparency
varying float vAlpha;

vec3 convert_sepia(vec3 v) {
	return vec3((v.r * .393) + (v.g *.769) + (v.b * .189)
	, (v.r * .349) + (v.g *.686) + (v.b * .168)
	, (v.r * .272) + (v.g *.534) + (v.b * .131));
}

vec3 convert_retro(vec3 v) {
	return (1.0 / 255.0) * vec3(140.0, 173.0, 15.0) * convert_sepia(v);
}

void main() {
	vec4 c = vec4(vUvOrRgb, 1.0);
	if (vUvOrRgb.z < 0.0) {
		c = texture2D(uTexture0, vUvOrRgb.xy);
	}

	if (vAlpha >= 0.0) {
		c.a = vAlpha;
	}

	if (uEffect == 1) {
		c.rgb = convert_retro(c.rgb);
	}  else if (uEffect == 2) {
		c.rgb = convert_sepia(c.rgb);
	}

	gl_FragColor = c;
}