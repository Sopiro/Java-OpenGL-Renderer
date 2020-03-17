#version 460 core

in vec2 tc;
in vec3 n;

uniform sampler2D tex;

out vec4 fragColor;

const vec3 lightDir = vec3(-1, -1, -1);

void main()
{
    vec3 unitDir = normalize(lightDir);
    float diffuse = max(dot(n, -unitDir), 0.3) * 0.2;
    fragColor = diffuse * vec4(texture(tex, tc).rgb, 1.0);
}
