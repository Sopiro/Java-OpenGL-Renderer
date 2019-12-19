#version 460 core

layout (location = 0) in vec3 position;
layout (location = 1) in vec2 texCoord;

out vec2 tc;

uniform mat4 m;
uniform mat4 vp;

void main()
{
	tc = texCoord;
    gl_Position = vp * m * vec4(position, 1.0);
}  