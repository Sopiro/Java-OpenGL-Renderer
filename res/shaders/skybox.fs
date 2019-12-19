#version 460 core

in vec3 texCoord;

out vec4 fragColor;

uniform samplerCube cubeMap;
uniform vec3 fogColor;
uniform float brightness;

const float lowerLimit = 0.0;
const float upperLimit = 0.1;

void main(void)
{
	vec4 skyBoxColor = texture(cubeMap, texCoord);

	float factor = (texCoord.y - lowerLimit) / (upperLimit - lowerLimit);
	factor = clamp(factor, 0.0, 1.0);
	fragColor = mix(vec4(fogColor, 1.0), skyBoxColor, factor);
	fragColor.xyz *= brightness;
}