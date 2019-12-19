package org.sopiro.game.texture;

import org.joml.*;

public class Material
{
	private Texture diffuseMap;
	private Texture spcularMap;
	private Texture normalMap; // For Normal Mapping
	private Texture depthMap; // For Parallax Mapping

	private Vector3f ambient;
	private Vector3f diffuse;
	private Vector3f specular;
	private float shininess;

	private boolean hasTransparency = false;
	private boolean useFakeLighting = false;
	private boolean hasNormalMap = false;
	private boolean hasDepthMap = false;

	public Material(Texture texture)
	{
		this(texture, new Vector3f(1), new Vector3f(1), new Vector3f(0), 10);
	}

	public Material(Texture diffuseMap, Vector3f ambient, Vector3f diffuse, Vector3f specular, float shininess)
	{
		this.diffuseMap = diffuseMap;
		this.ambient = ambient;
		this.diffuse = diffuse;
		this.specular = specular;
		this.shininess = shininess;
	}

	public Material(Texture diffuseMap, Texture specularMap, float shininess)
	{
		this(diffuseMap, new Vector3f(1.0f), new Vector3f(1.0f), new Vector3f(0.0f), shininess);
		this.spcularMap = specularMap;
	}

	public Material(Texture diffuseMap, Texture specularMap, float shininess, Texture normalMap)
	{
		this(diffuseMap, specularMap, shininess);
		this.normalMap = normalMap;
		this.hasNormalMap = true;
	}
	
	public Material(Texture diffuseMap, Texture specularMap, float shininess, Texture normalMap, Texture depthMap)
	{
		this(diffuseMap, specularMap, shininess, normalMap);
		this.depthMap = depthMap;
		this.hasDepthMap = true;
	}

	public boolean isUseFakeLighting()
	{
		return useFakeLighting;
	}

	public void setUseFakeLighting(boolean useFakeLighting)
	{
		this.useFakeLighting = useFakeLighting;
	}

	public boolean isHasTransparency()
	{
		return hasTransparency;
	}

	public void setHasTransparency(boolean hasTransparency)
	{
		this.hasTransparency = hasTransparency;
	}

	public Vector3f getAmbient()
	{
		return ambient;
	}

	public void setAmbient(Vector3f ambient)
	{
		this.ambient = ambient;
	}

	public Vector3f getDiffuse()
	{
		return diffuse;
	}

	public void setDiffuse(Vector3f diffuse)
	{
		this.diffuse = diffuse;
	}

	public Vector3f getSpecular()
	{
		return specular;
	}

	public void setSpecular(Vector3f specular)
	{
		this.specular = specular;
	}

	public float getShininess()
	{
		return shininess;
	}

	public void setShininess(float shininess)
	{
		this.shininess = shininess;
	}

	public Texture getDiffuseMap()
	{
		return diffuseMap;
	}

	public Texture getSpecularMap()
	{
		return spcularMap;
	}

	public boolean isHasNormalMap()
	{
		return hasNormalMap;
	}

	public Texture getNormalMap()
	{
		return normalMap;
	}

	public boolean isHasDepthMap()
	{
		return hasDepthMap;
	}

	public Texture getDepthMap()
	{
		return depthMap;
	}
}
