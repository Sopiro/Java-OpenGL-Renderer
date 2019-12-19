package org.sopiro.game.entities.light;

import org.joml.*;

public class PointLight extends Light
{
	public static final int MAX_POINT_LIGHTS = 8;
	
	private Vector3f attenuation;
	private float range;
	
	public PointLight(Vector3f position, Vector3f color, Vector3f attenuation, float range)
	{
		this(position, new Vector3f(1), color, new Vector3f(1), attenuation, range);
	}
	
	public PointLight(Vector3f position, Vector3f ambient, Vector3f diffuse, Vector3f specular, Vector3f attenuation, float range)
	{
		super(position, ambient, diffuse, specular);
		this.attenuation = attenuation;
		this.range = range;
	}
	
	public Vector3f getAttenuation()
	{
		return attenuation;
	}
	
	public float getRange()
	{
		return range;
	}
}
