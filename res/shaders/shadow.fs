#version 460 core

uniform sampler2D tex;

in vec2 tc;

out vec4 fragColor;

void main()
{
	float alpha = texture(tex, tc).a;
	if(alpha < 0.5) discard;
	fragColor = vec4(1.0);
}  