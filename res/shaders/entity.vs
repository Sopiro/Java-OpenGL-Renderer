#version 460 core

layout (location = 0) in vec3 position;
layout (location = 1) in vec2 texCoord;
layout (location = 2) in vec3 normal;
layout (location = 3) in vec3 tangent;

out vec2 tc;
out vec3 surfaceNormal;
out vec3 eyePos;
out vec3 fragPos;
out float visibility;
out vec4 shadowCoords;
out float hasNormalMap;
out float hasDepthMap;

out mat3 tbn;

out float t;

uniform mat4 transformationMatrix;
uniform mat4 viewMatrix;
uniform mat4 projectionMatrix;
uniform mat4 lightSpaceMatrix;

uniform bool useFakeLighting;
uniform bool normalMappingUsed;
uniform bool parallaxMappingUsed;

uniform float time;

const float density = 0.002;
const float gradient = 5.0;

void main(void)
{
	vec4 worldPosition = transformationMatrix * vec4(position, 1.0);
	vec4 positionRelativeToCam = viewMatrix * worldPosition;
	mat4 modelViewMatrix = viewMatrix * transformationMatrix;
	gl_Position = projectionMatrix * viewMatrix * worldPosition;

	shadowCoords = lightSpaceMatrix * worldPosition;
	hasNormalMap = normalMappingUsed ? 1 : 0;
	hasDepthMap = parallaxMappingUsed ? 1 : 0;
	
	fragPos = worldPosition.xyz;
	tc = texCoord;
	t = time;

	vec3 actualNormal = normalize(normal);
	if(useFakeLighting) actualNormal = vec3(0,1,0);

	surfaceNormal = (transformationMatrix * vec4(actualNormal, 0.0)).xyz;
	eyePos = (inverse(viewMatrix) * vec4(0,0,0,1)).xyz;

	if(normalMappingUsed)
	{
		vec3 T = normalize(vec3(transformationMatrix * vec4(tangent, 0.0)));
		vec3 N = normalize(surfaceNormal);
		T = normalize(T - dot(T, N) * N);
		vec3 B = cross(N, T);
		mat3 TBN = mat3(T, B, N);
		tbn = TBN;
	}
	
	float distance = length(positionRelativeToCam.xyz);
	visibility = exp(-pow(distance * density, gradient));
	visibility = clamp(visibility, 0.0, 1.0);
	visibility = 1;
}