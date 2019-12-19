#version 460 core

in vec2 texCoord;

uniform sampler2D tex;

out vec4 fragColor;

void main(void)
{
	fragColor = texture(tex, texCoord);
	
	float gamma = 2.2; //Gamma Correction to sRGB
   	fragColor.rgb = pow(fragColor.rgb, vec3(1.0/gamma));
}
