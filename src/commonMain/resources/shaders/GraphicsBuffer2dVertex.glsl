#version 100

precision highp float;

uniform mat4 uMvp;

attribute vec2 aXy;
attribute vec3 aUvOrRgb;
attribute float aAlpha;

varying vec3 vUvOrRgb;
varying float vAlpha;

void main()
{
	gl_Position = uMvp * vec4(aXy, 0.0, 1.0);

	vUvOrRgb = aUvOrRgb;
	vAlpha = aAlpha;
}