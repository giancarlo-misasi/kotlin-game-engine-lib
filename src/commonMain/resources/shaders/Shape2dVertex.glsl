#version 410

precision highp float;

// uniforms
uniform mat4 uMvp;

// input
layout (location = 0) in vec2 inXy;
layout (location = 1) in vec4 inColor;

// output
layout (location = 0) out vec4 outColor;

// implementation
void main() {
	gl_Position = uMvp * vec4(inXy, 0.0, 1.0);
	outColor = inColor;
}