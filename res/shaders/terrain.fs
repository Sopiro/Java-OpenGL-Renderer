#version 460 core
#define MAX_POINT_LIGHTS 8

in vec2 tc;
in vec3 surfaceNormal;
in vec3 eyePos;
in vec3 fragPos;
in float visibility;
in vec4 shadowCoords;

out vec4 fragColor;

layout(binding = 0) uniform sampler2D backgroundTexture;
layout(binding = 1) uniform sampler2D rTexture;
layout(binding = 2) uniform sampler2D gTexture;
layout(binding = 3) uniform sampler2D bTexture;
layout(binding = 4) uniform sampler2D blendMap;

layout(binding = 5) uniform sampler2D shadowMap;

struct Light
{
	vec3 pos;
    vec3 ambient;
    vec3 diffuse;
    vec3 specular;
};

struct DirectionalLight
{
	Light base;
	vec3 dir;
};

struct PointLight
{
	Light base;
	vec3 attenuation;
	float range;
};

struct Material {
    vec3 ambient;
    vec3 diffuse;
    vec3 specular;
    float shininess;
}; 

uniform vec3 skyColor;

uniform Material material;
uniform DirectionalLight dirLight;
uniform PointLight pointLights[MAX_POINT_LIGHTS];

uniform int shadowMapSize = 4096;
const int pcfCount = 2;
const float totalPCFTexels = (pcfCount * 2 + 1) * (pcfCount * 2 + 1);


vec3 calcDirLight(DirectionalLight light, vec3 normal, vec3 toEye)
{
	vec3 projCoords = shadowCoords.xyz / shadowCoords.w;
    projCoords = projCoords * 0.5 + 0.5;
   	float shadowFactor = 0.0;
   	float bias = 0.002;

   	float texelSize = 1.0 / shadowMapSize;
   	if(projCoords.z < 1)
   	{
	   	for(int x = -pcfCount; x <= pcfCount; x++)
	   	{
	   		for(int y = -pcfCount; y <= pcfCount; y++)
	   		{
	   			float nearest = texture(shadowMap, projCoords.xy + vec2(x, y) * texelSize).r;
	   			if(nearest < projCoords.z - bias) shadowFactor++;
	   		}
	   	}
	   	shadowFactor /= totalPCFTexels;
   	}

	vec3 ambient = light.base.ambient * material.ambient;
	vec3 unitDir = normalize(light.dir);
	vec3 diffuse = max(dot(normal, - unitDir), 0.0) * light.base.diffuse * material.diffuse;
	vec3 halfway = normalize(-unitDir + toEye);
	float specularFactor = pow(max(dot(normal, halfway), 0.0), material.shininess);
	vec3 specular = specularFactor * material.specular * light.base.specular;

	return ambient + (diffuse + specular) * (1 - shadowFactor);
}

vec3 calcPointLight(PointLight light, vec3 normal, vec3 fragPos, vec3 toEye)
{
	// Range culling
	if(light.range < 0) return vec3(0);
	vec3 toLight = light.base.pos - fragPos;
	vec3 unitToLight = normalize(toLight);
   	float distance = length(toLight); 	
	if(distance > light.range) return vec3(0);

   	//attenuation
   	float attFactor = light.attenuation.x + light.attenuation.y * distance + light.attenuation.z * distance * distance;
   	
   	//ambient
   	vec3 ambient = light.base.ambient * material.ambient;
   	//diffuse
	vec3 diffuse = max(dot(normal, unitToLight), 0.0) * light.base.diffuse * material.diffuse;
	//specular
	vec3 halfway = normalize(unitToLight + toEye);
	float specularFactor = pow(max(dot(normal, halfway), 0.0), material.shininess);
	vec3 specular = specularFactor * material.specular * light.base.specular;
 	
 	return (ambient + diffuse + specular) / attFactor;
}

void main(void)
{
	vec4 blendMapColor = texture(blendMap, tc);

	float backTextureAmount = 1 - (blendMapColor.r + blendMapColor.g + blendMapColor.b);
	vec2 tiledCoords = tc * 40.0;
	vec4 backgroundTextureColor = texture(backgroundTexture, tiledCoords) * backTextureAmount;
	vec4 rTextureColor = texture(rTexture, tiledCoords) * blendMapColor.r;
	vec4 gTextureColor = texture(gTexture, tiledCoords) * blendMapColor.g;
	vec4 bTextureColor = texture(bTexture, tiledCoords) * blendMapColor.b;

	vec4 totalColor = backgroundTextureColor + rTextureColor + gTextureColor + bTextureColor;

   	vec3 unitNormal = normalize(surfaceNormal);
   	vec3 unitToCamera = normalize(eyePos - fragPos);

  	vec3 totalLight = calcDirLight(dirLight, unitNormal, unitToCamera);

   	for(int i = 0; i < MAX_POINT_LIGHTS; i++)
   	{
   		totalLight += calcPointLight(pointLights[i], unitNormal, fragPos, unitToCamera);
   	}

	fragColor = vec4(totalLight, 1.0) * totalColor;
	fragColor = mix(vec4(skyColor, 1.0), fragColor, visibility);
}