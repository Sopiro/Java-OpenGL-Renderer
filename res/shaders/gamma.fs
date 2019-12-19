#version 460 core

uniform sampler2D tex;
uniform float gamma = 2.2;

in vec2 texCoord;

out vec4 fragColor;

void main(void)
{
	fragColor = texture(tex, texCoord);

    //Gamma Correction to sRGB
   	fragColor.rgb = pow(fragColor.rgb, vec3(1.0 / gamma));
}