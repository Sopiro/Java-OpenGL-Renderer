package org.sopiro.game.entities;

import org.joml.*;
import org.sopiro.game.models.*;

public class Entity
{
	private TexturedModel model;
	private Vector3f position;
	private Vector3f rotation;
	private float scale;
	
	private boolean outLined = false;
	
	public Entity(TexturedModel model, Vector3f position, Vector3f rotation, float scale)
	{
		this.model = model;
		this.position = position;
		this.rotation = rotation;
		this.scale = scale;
	}

	public void move(float dx, float dy, float dz)
	{
		position.x += dx;
		position.y += dy;
		position.z += dz;
	}
	
	public void rotate(float dx, float dy, float dz)
	{
		rotation.x += dx;
		rotation.y += dy;
		rotation.z += dz;
	}
	
	public TexturedModel getModel()
	{
		return model;
	}

	public void setModel(TexturedModel model)
	{
		this.model = model;
	}

	public Vector3f getPosition()
	{
		return position;
	}

	public void setPosition(Vector3f position)
	{
		this.position = position;
	}

	public Vector3f getRotation()
	{
		return rotation;
	}

	public void setRoatation(Vector3f rotation)
	{
		this.rotation = rotation;
	}

	public float getScale()
	{
		return scale;
	}

	public void setScale(float scale)
	{
		this.scale = scale;
	}

	public boolean isOutLined()
	{
		return outLined;
	}

	public void setOutLined(boolean outLined)
	{
		this.outLined = outLined;
	}
}
