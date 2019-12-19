#version 460 core

layout (location = 0) in vec3 position;
layout (location = 1) in vec2 texCoord;
layout (location = 2) in vec3 normal;

out vec2 tc;
out vec3 surfaceNormal;
out vec3 eyePos;
out vec3 fragPos;
out float visibility;
out vec4 shadowCoords;

uniform mat4 transformationMatrix;
uniform mat4 viewMatrix;
uniform mat4 projectionMatrix;

uniform mat4 lightSpaceMatrix;

const float density = 0.002;
const float gradient = 5.0;

void main(void)
{
	vec4 worldPosition = transformationMatrix * vec4(position, 1.0);
	vec4 positionRelativeToCam = viewMatrix * worldPosition;
	gl_Position = projectionMatrix * viewMatrix * worldPosition;

	shadowCoords = lightSpaceMatrix * worldPosition;

	fragPos = worldPosition.xyz;
	tc = texCoord;

	surfaceNormal = normal;
	eyePos = (inverse(viewMatrix) * vec4(0,0,0,1)).xyz;

	float distance = length(positionRelativeToCam.xyz);
	visibility = exp(-pow(distance*density, gradient));
	visibility = clamp(visibility, 0.0, 1.0);
	visibility = 1;
}