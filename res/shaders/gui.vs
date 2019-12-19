#version 460 core

layout(location = 0) in vec2 position;

out vec2 texCoord;

uniform mat4 transformationMatrix;

void main(void)
{
	gl_Position = transformationMatrix * vec4(position, 0.0, 1.0);
	texCoord = vec2((position.x + 1) / 2, 1 - (position.y + 1) / 2);
}
