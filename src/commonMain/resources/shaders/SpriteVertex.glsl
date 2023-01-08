#version 410

precision highp float;

// uniforms
uniform mat4 uModelViewProjection;
uniform vec2 uTranslation = vec2(0, 0);

// input
layout (location = 0) in vec2 inXy;
layout (location = 1) in vec2 inUv;
layout (location = 2) in int inColor;
layout (location = 3) in int inMasks;

// output
layout (location = 0) out vec2 outUv;
layout (location = 1) flat out int outColor;
layout (location = 2) flat out int outMasks;

// implementation
void main() {
	gl_Position = uModelViewProjection * vec4(inXy + uTranslation, 0, 1);

	outUv = inUv;
	outColor = inColor;
	outMasks = inMasks;
}