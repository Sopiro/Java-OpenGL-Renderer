#version 460 core

uniform sampler2D tex;

in vec2 texCoord;

out vec4 fragColor;

const float offset = 1.0 / 300.0;

void main(void)
{
	   vec2[] offsets = vec2[](
        vec2(-offset,  offset), // top-left
        vec2( 0.0f,    offset), // top-center
        vec2( offset,  offset), // top-right
        vec2(-offset,  0.0f),   // center-left
        vec2( 0.0f,    0.0f),   // center-center
        vec2( offset,  0.0f),   // center-right
        vec2(-offset, -offset), // bottom-left
        vec2( 0.0f,   -offset), // bottom-center
        vec2( offset, -offset)  // bottom-right    
    );

    float[] kernel = float[](
        -1, -1, -1,
        -1,  9, -1,
        -1, -1, -1
    );

    kernel = float[](
    1.0 / 16, 2.0 / 16, 1.0 / 16,
    2.0 / 16, 4.0 / 16, 2.0 / 16,
    1.0 / 16, 2.0 / 16, 1.0 / 16  
	);

 //    kernel = float[](
 //    0.0, 0.0, 0.0,
 //    1.0/4, 2.0/4, 1.0/4, 
 //    0.0, 0.0, 0.0  
	// );

	// kernel = float[](
 //    -1.0, -1.0, -1.0,
 //    -1.0, 8.0, -1.0,
 //    -1.0, -1.0, -1.0  
	// );

	// kernel = float[](
 //    0.0, -1.0, 0.0,
 //    -1.0, 5.0, -1.0,
 //    0.0, -1.0, 0.0  
	// );
    
	// kernel = float[](
	// 	0, 0, 0,
	// 	0, 1, 0,
	// 	0, 0, 0
	// );

    vec3 sampleTex[9];
    for(int i = 0; i < 9; i++)
    {
        sampleTex[i] = texture(tex, texCoord.st + offsets[i]).rgb;
    }
    vec3 col = vec3(0.0);
    for(int i = 0; i < 9; i++)
        col += sampleTex[i] * kernel[i];

   	fragColor = vec4(col, 1.0);
}