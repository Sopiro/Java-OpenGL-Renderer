#version 460 core

layout(location = 0) in vec3 position;

out vec3 texCoord;

uniform mat4 viewMatrix;
uniform mat4 projectionMatrix;

void main(void)
{
	gl_Position = projectionMatrix * viewMatrix * vec4(position, 1.0);
	gl_Position = gl_Position.xyww;
	texCoord = position;
}