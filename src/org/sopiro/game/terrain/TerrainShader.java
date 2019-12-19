package org.sopiro.game.terrain;

import java.util.*;

import org.joml.*;
import org.sopiro.game.entities.*;
import org.sopiro.game.entities.light.*;
import org.sopiro.game.renderer.shader.*;
import org.sopiro.game.texture.*;
import org.sopiro.game.utils.*;

public class TerrainShader extends ShaderProgram
{
	private final static String vsPath = "./res/shaders/terrain.vs";
	private final static String fsPath = "./res/shaders/terrain.fs";

	// Uniform locations
	private int transformationMatrix;
	private int viewMatrix;
	private int projectionMatrix;
	private int skyColor;

	private int materialAmbient;
	private int materialDiffuse;
	private int materialSpecular;
	private int materialShininess;

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
	
	private int lightSpaceMatrix;
	private int shadowMapSize;
	
	public TerrainShader()
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
		
		materialAmbient = super.getUniformLocation("material.ambient");
		materialDiffuse = super.getUniformLocation("material.diffuse");
		materialSpecular = super.getUniformLocation("material.specular");
		materialShininess = super.getUniformLocation("material.shininess");

		dirLightDir      = super.getUniformLocation("dirLight.dir");
		dirLightAmbient  = super.getUniformLocation("dirLight.base.ambient");
		dirLightDiffuse  = super.getUniformLocation("dirLight.base.diffuse");
		dirLightSpecular = super.getUniformLocation("dirLight.base.specular");
		
		pLightPosition = new int[PointLight.MAX_POINT_LIGHTS];
		pLightAmbient = new int[PointLight.MAX_POINT_LIGHTS];
		pLightDiffuse = new int[PointLight.MAX_POINT_LIGHTS];
		pLightSpecular = new int[PointLight.MAX_POINT_LIGHTS];
		pLightAttenuation = new int[PointLight.MAX_POINT_LIGHTS];
		pLightRange = new int[PointLight.MAX_POINT_LIGHTS];
		
		lightSpaceMatrix = super.getUniformLocation("lightSpaceMatrix");
		shadowMapSize = super.getUniformLocation("shadowMapSize");
		
		for (int i = 0; i < PointLight.MAX_POINT_LIGHTS; i++)
		{
			pLightPosition[i] 	 = super.getUniformLocation("pointLights[" + i + "].base.pos");
			pLightAmbient[i] 	 = super.getUniformLocation("pointLights[" + i + "].base.ambient");
			pLightDiffuse[i] 	 = super.getUniformLocation("pointLights[" + i + "].base.diffuse");
			pLightSpecular[i] 	 = super.getUniformLocation("pointLights[" + i + "].base.specular");
			pLightAttenuation[i] = super.getUniformLocation("pointLights[" + i + "].attenuation");
			pLightRange[i] 		 = super.getUniformLocation("pointLights[" + i + "].range");
		}
	}

	public void setSkyColor(Vector3f skyColor)
	{
		super.setVector3(this.skyColor, skyColor);
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
	
	public void setMaterial(TerrainTexturePack material)
	{
		super.setVector3(materialAmbient, material.getAmbient());
		super.setVector3(materialDiffuse, material.getDiffuse());
		super.setVector3(materialSpecular, material.getSpecular());
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
}
