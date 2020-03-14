#version 460 core

in vec2 tc;

uniform sampler2D tex;

out vec4 fragColor;

void main()
{
    fragColor = vec4(texture(tex, tc).rgb, 1.0);
}
