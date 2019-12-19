#version 460 core
#define MAX_POINT_LIGHTS 8

in vec2 tc;
in vec3 surfaceNormal;
in vec3 eyePos;
in vec3 fragPos;
in float visibility;
in vec4 shadowCoords;
in float hasNormalMap;
in float hasDepthMap;

in mat3 tbn;

in float t;

uniform sampler2D shadowMap;

out vec4 fragColor;

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

struct Material
{
    sampler2D diffuseMap;
    sampler2D specularMap;
    sampler2D normalMap;
    sampler2D depthMap;
    float shininess;
}; 

uniform vec3 skyColor;
uniform bool outlined;

uniform Material material;
uniform DirectionalLight directionalLight;
uniform PointLight pointLights[MAX_POINT_LIGHTS];

uniform int shadowMapSize = 4096;
const int pcfCount = 2;
const float totalPCFTexels = (pcfCount * 2 + 1) * (pcfCount * 2 + 1);

uniform float heightScale = 0.03;

vec2 texCoords;

vec3 calcDirLight(DirectionalLight light, vec3 normal, vec3 toEye)
{
   vec3 diffuseColor = vec3(texture(material.diffuseMap, texCoords));
   vec3 specularColor = vec3(texture(material.specularMap, texCoords));

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

   vec3 unitDir = normalize(light.dir);
   vec3 ambient = light.base.ambient * diffuseColor;
   vec3 diffuse = max(dot(normal, -unitDir), 0.0) * light.base.diffuse * diffuseColor;
   vec3 halfway = normalize(-unitDir + toEye);
   float specularFactor = pow(max(dot(normal, halfway), 0.0), material.shininess);
   vec3 specular = specularFactor * specularColor * light.base.specular;

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

  vec3 diffuseColor = vec3(texture(material.diffuseMap, texCoords));
  vec3 specularColor = vec3(texture(material.specularMap, texCoords));

  //attenuation
  float attFactor = light.attenuation.x + light.attenuation.y * distance + light.attenuation.z * distance * distance;
  //ambient
  vec3 ambient = light.base.ambient * diffuseColor;
  //diffuse
  vec3 diffuse = max(dot(normal, unitToLight), 0.0) * light.base.diffuse * diffuseColor;
  //specular
  vec3 halfway = normalize(unitToLight + toEye);
  float specularFactor = pow(max(dot(normal, halfway), 0.0), material.shininess);
  vec3 specular = specularFactor * specularColor * light.base.specular;
  
  return (ambient + diffuse + specular) / attFactor;
}

vec2 parallaxMapping(vec2 texCoord, vec3 fragToEye)
{
    float height = texture(material.depthMap, texCoord).r;
    vec2 p = (fragToEye.xy / fragToEye.z) * (height * heightScale);
    p.y *= -1;
    return texCoord - p;
}

vec2 parallaxOcculusionMapping(vec2 texCoords, vec3 viewDir)
{ 
    // number of depth layers
    const float minLayers = 8;
    const float maxLayers = 32;
    float numLayers = mix(maxLayers, minLayers, abs(dot(vec3(0.0, 0.0, 1.0), viewDir)));  
    // calculate the size of each layer
    float layerDepth = 1.0 / numLayers;
    // depth of current layer
    float currentLayerDepth = 0.0;
    // the amount to shift the texture coordinates per layer (from vector P)
    vec2 P = viewDir.xy / viewDir.z * heightScale;
    P.y *= -1;
    vec2 deltaTexCoords = P / numLayers;
  
    // get initial values
    vec2  currentTexCoords     = texCoords;
    float currentDepthMapValue = texture(material.depthMap, currentTexCoords).r;
      
    while(currentLayerDepth < currentDepthMapValue)
    {
        // shift texture coordinates along direction of P
        currentTexCoords -= deltaTexCoords;
        // get depthmap value at current texture coordinates
        currentDepthMapValue = texture(material.depthMap, currentTexCoords).r;  
        // get depth of next layer
        currentLayerDepth += layerDepth;  
    }
    
    // get texture coordinates before collision (reverse operations)
    vec2 prevTexCoords = currentTexCoords + deltaTexCoords;

    // get depth after and before collision for linear interpolation
    float afterDepth  = currentDepthMapValue - currentLayerDepth;
    float beforeDepth = texture(material.depthMap, prevTexCoords).r - currentLayerDepth + layerDepth;
 
    // interpolation of texture coordinates
    float weight = afterDepth / (afterDepth - beforeDepth);
    vec2 finalTexCoords = prevTexCoords * weight + currentTexCoords * (1.0 - weight);

    return finalTexCoords;
}

void main(void)
{
   if(outlined)
   {
      fragColor = vec4(1.0, 0.0, 0.0, 1.0);
      return;
   }
   
   float alpha = texture(material.diffuseMap, tc).a;
   if(alpha < 0.5) discard;

   vec3 unitToCamera = normalize(eyePos - fragPos);
   
   texCoords = tc;
   if(hasDepthMap > 0.5)
   {
      texCoords = parallaxOcculusionMapping(texCoords, transpose(tbn) * unitToCamera);
      if(texCoords.x < 0 || texCoords.y < 0 || texCoords.x > 1 || texCoords.y > 1)
         discard;
   }

   vec3 unitNormal = normalize(surfaceNormal);
   if(hasNormalMap > 0.5)
   {
      unitNormal = texture(material.normalMap, texCoords).rgb;
      unitNormal = tbn * normalize(unitNormal * 2 - 1);
   }

   vec3 totalLight = calcDirLight(directionalLight, unitNormal, unitToCamera);

   for(int i = 0; i < MAX_POINT_LIGHTS; i++)
   {
      totalLight += calcPointLight(pointLights[i], unitNormal, fragPos, unitToCamera);
   }

   fragColor = vec4(totalLight, 1.0);
   fragColor = mix(vec4(skyColor, 1.0), fragColor, visibility);
   //fragColor = vec4(1,0,0,1);
}
