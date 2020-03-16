#version 460 core

#define MAX_BONE 50

layout (location = 0) in vec3 position;
layout (location = 1) in vec2 texCoord;
layout (location = 2) in vec3 normal;
layout (location = 3) in vec3 tangent;
layout (location = 4) in vec3 bone_id;
layout (location = 5) in vec3 weight;

uniform mat4 transformationMatrix;
uniform mat4 viewMatrix;
uniform mat4 projectionMatrix;

uniform mat4 bones[MAX_BONE];

out vec2 tc;
out vec3 normalVector;

void main()
{
    vec3 normalizedWeight = normalize(weight);

    mat4 boneTransformation =
    bones[uint(bone_id.x)] * normalizedWeight.x +
    bones[uint(bone_id.y)] * normalizedWeight.y +
    bones[uint(bone_id.z)] * normalizedWeight.z;

    vec4 worldPosition = transformationMatrix * boneTransformation * vec4(position, 1.0);
    normalVector = (transformationMatrix * vec4(normalVector, 1)).xyz;
    gl_Position = projectionMatrix * viewMatrix * worldPosition;

    tc = texCoord;
}
