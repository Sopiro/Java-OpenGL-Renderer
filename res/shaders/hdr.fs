#version 460 core

in vec2 texCoord;

uniform sampler2D tex;
uniform float exposure = 1.0;

out vec4 fragColor;

void main()
{
    vec3 col = texture(tex, texCoord).xyz;

	col = vec3(1.0) - exp(-col * exposure);

    fragColor = vec4(col, 1.0);
} 