package org.sopiro.game.entities;

import java.util.*;

import org.joml.*;
import org.sopiro.game.entities.light.*;
import org.sopiro.game.renderer.shader.*;
import org.sopiro.game.texture.*;
import org.sopiro.game.utils.*;

public class EntityShader extends ShaderProgram
{
	private final static String vsPath = "./res/shaders/entity.vs";
	private final static String fsPath = "./res/shaders/entity.fs";

	// Uniform locations
	private int transformationMatrix;
	private int viewMatrix;
	private int projectionMatrix;
	private int skyColor;
	private int outlined;

	private int useFakeLighting;
	private int materialDiffuseMap;
	private int materialSpecularMap;
	private int materialShininess;
	private int materialNormalMap;
	private int materialDepthMap;

	private int dirLightDir;
	private int dirLightAmbient;
	private int dirLightDiffuse;
	private int dirLightSpecular;
	
	private int pLightPosition[];
	private int pLightAmbient[];
	private int pLightDiffuse[];
	private int pLightSpecular[];
	private int pLightAttenuation[];
	private int pLightRange[];
	
	private int shadowMap;
	private int lightSpaceMatrix;
	private int shadowMapSize;
	
	private int normalMappingUsed;
	private int parallaxMappingUsed;
	
	private int time;
	
	public EntityShader()
	{
		super(vsPath, fsPath);
	}

	@Override
	protected void getAllUniformLocation()
	{
		transformationMatrix = super.getUniformLocation("transformationMatrix");
		viewMatrix = super.getUniformLocation("viewMatrix");
		projectionMatrix = super.getUniformLocation("projectionMatrix");
		skyColor = super.getUniformLocation("skyColor");
		outlined = super.getUniformLocation("outlined");

		useFakeLighting = super.getUniformLocation("useFakeLighting");
		materialDiffuseMap = super.getUniformLocation("material.diffuseMap");
		materialSpecularMap = super.getUniformLocation("material.specularMap");
		materialShininess = super.getUniformLocation("material.shininess");
		materialNormalMap = super.getUniformLocation("material.normalMap");
		materialDepthMap = super.getUniformLocation("material.depthMap");

		dirLightDir      = super.getUniformLocation("directionalLight.dir");
		dirLightAmbient  = super.getUniformLocation("directionalLight.base.ambient");
		dirLightDiffuse  = super.getUniformLocation("directionalLight.base.diffuse");
		dirLightSpecular = super.getUniformLocation("directionalLight.base.specular");
		
		pLightPosition = new int[PointLight.MAX_POINT_LIGHTS];
		pLightAmbient = new int[PointLight.MAX_POINT_LIGHTS];
		pLightDiffuse = new int[PointLight.MAX_POINT_LIGHTS];
		pLightSpecular = new int[PointLight.MAX_POINT_LIGHTS];
		pLightAttenuation = new int[PointLight.MAX_POINT_LIGHTS];
		pLightRange = new int[PointLight.MAX_POINT_LIGHTS];

		for (int i = 0; i < PointLight.MAX_POINT_LIGHTS; i++)
		{
			pLightPosition[i] 	 = super.getUniformLocation("pointLights[" + i + "].base.pos");
			pLightAmbient[i] 	 = super.getUniformLocation("pointLights[" + i + "].base.ambient");
			pLightDiffuse[i] 	 = super.getUniformLocation("pointLights[" + i + "].base.diffuse");
			pLightSpecular[i] 	 = super.getUniformLocation("pointLights[" + i + "].base.specular");
			pLightAttenuation[i] = super.getUniformLocation("pointLights[" + i + "].attenuation");
			pLightRange[i]		 = super.getUniformLocation("pointLights[" + i + "].range");
		}
		
		lightSpaceMatrix = super.getUniformLocation("lightSpaceMatrix");
		shadowMapSize = super.getUniformLocation("shadowMapSize");
		shadowMap = super.getUniformLocation("shadowMap");
		
		normalMappingUsed = super.getUniformLocation("normalMappingUsed");
		parallaxMappingUsed = super.getUniformLocation("parallaxMappingUsed");
		
		time = super.getUniformLocation("time");
		
		start();
		super.setInt(materialDiffuseMap, 0);
		super.setInt(materialSpecularMap, 1);
		super.setInt(shadowMap, 5);
		super.setInt(materialNormalMap, 6);
		super.setInt(materialDepthMap, 7);
		stop();
	}

	public void setSkyColor(Vector3f skyColor)
	{
		super.setVector3(this.skyColor, skyColor);
	}

	public void setUseFakeLighting(boolean useFakeLighting)
	{
		super.setBoolean(this.useFakeLighting, useFakeLighting);
	}

	public void setLights(List<Light> lights)
	{
		List<PointLight> pLights = new ArrayList<PointLight>();
		
		for(Light l : lights)
		{
			if (l instanceof DirectionalLight)
			{
				setDirectionalLight((DirectionalLight)l);
				continue;
			}
			if (l instanceof PointLight)
			{
				pLights.add((PointLight) l);
				continue;
			}
		}
		
		setPointLights(pLights);
	}
	
	public void setPointLights(List<PointLight> lights)
	{
		for (int i = 0; i < PointLight.MAX_POINT_LIGHTS; i++)
		{
			if (i < lights.size())
			{
				super.setVector3(pLightPosition[i], lights.get(i).getPosition());
				super.setVector3(pLightAmbient[i], lights.get(i).getAmbient());
				super.setVector3(pLightDiffuse[i], lights.get(i).getDiffuse());
				super.setVector3(pLightSpecular[i], lights.get(i).getSpecular());
				super.setVector3(pLightAttenuation[i], lights.get(i).getAttenuation());
				super.setFloat(pLightRange[i], lights.get(i).getRange());
			} else
			{
				super.setVector3(pLightPosition[i], new Vector3f(0));
				super.setVector3(pLightAmbient[i], new Vector3f(0));
				super.setVector3(pLightDiffuse[i], new Vector3f(0));
				super.setVector3(pLightSpecular[i], new Vector3f(0));
				super.setVector3(pLightAttenuation[i], new Vector3f(-1));
				super.setFloat(pLightRange[i], -1);
			}
		}
	}
	
	public void setDirectionalLight(DirectionalLight light)
	{
		super.setVector3(dirLightDir, light.getDirection());
		super.setVector3(dirLightAmbient, light.getAmbient());
		super.setVector3(dirLightDiffuse, light.getDiffuse());
		super.setVector3(dirLightSpecular, light.getSpecular());
	}

	public void setTransformationMatrix(Matrix4f matrix)
	{
		super.setMatrix(this.transformationMatrix, matrix);
	}

	public void setViewMatrix(Camera camera)
	{
		super.setMatrix(viewMatrix, Maths.createViewMatrix(camera));
	}

	public void setProjectionMatrix(Matrix4f matrix)
	{
		super.setMatrix(this.projectionMatrix, matrix);
	}

	public void setOutlined(boolean outlined)
	{
		super.setBoolean(this.outlined, outlined);
	}

	public void setMaterialVariables(Material material)
	{
		super.setFloat(materialShininess, material.getShininess());
	}
	
	public void setLightSpaceMatrix(Matrix4f lightSpaceMatrix)
	{
		super.setMatrix(this.lightSpaceMatrix, lightSpaceMatrix);
	}
	
	public void setShadowMapSize(int shadowMapSize)
	{
		super.setInt(this.shadowMapSize, shadowMapSize);
	}
	
	public void setNormalMappingUsed(boolean used)
	{
		super.setBoolean(this.normalMappingUsed, used);
	}
	
	
	public void setParallaxMappingUsed(boolean used)
	{
		super.setBoolean(this.parallaxMappingUsed, used);
	}
	
	public void updateTime(float time)
	{
		super.setFloat(this.time, time);
	}
}
