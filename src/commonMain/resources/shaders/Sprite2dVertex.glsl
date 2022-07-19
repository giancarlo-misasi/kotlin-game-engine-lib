#version 410

precision highp float;

// uniforms
uniform mat4 uMvp;
uniform int uShake;
uniform vec2 uShakeAmount;

// input
layout (location = 0) in vec2 inXy;
layout (location = 1) in vec2 inUv;
layout (location = 2) in float inAlpha;

// output
layout (location = 0) out vec2 outUv;
layout (location = 1) out float outAlpha;

// implementation
void main() {
	vec4 pos = uMvp * vec4(inXy, 0.0, 1.0);
	if (uShake == 1) {
		pos.xy += uShakeAmount;
	}

	gl_Position = pos;
	outUv = inUv;
	outAlpha = inAlpha;
}