package org.sopiro.game.entities;

import java.lang.Math;

import org.joml.*;
import org.lwjgl.glfw.*;
import org.sopiro.game.*;
import org.sopiro.game.utils.*;

public class Camera
{
	private Vector3f position = new Vector3f();
	private Vector3f rotation = new Vector3f();
	
	private Vector3f acceleration = new Vector3f();
	
	private float moveSpeed = 0.3f;
	private float rotSpeed = 0.0015f;
	
	private float cos = 0;
	private float sin = 0;
	
	public Camera(Vector3f position, Vector3f rotation)
	{
		this.position = position;
		this.rotation = rotation;
	}
	
	public Camera()
	{
		position.y = 5;
		rotation.y = 0;//(float)Math.toRadians(180);
	}
	
	public void update()
	{
		float ax = 0;
		float ay = 0;
		float az = 0;
		
		if(Input.isDown(GLFW.GLFW_KEY_W)) az--;
		if(Input.isDown(GLFW.GLFW_KEY_S)) az++;
		if(Input.isDown(GLFW.GLFW_KEY_A)) ax--;
		if(Input.isDown(GLFW.GLFW_KEY_D)) ax++;
		if(Input.isDown(GLFW.GLFW_KEY_SPACE))ay++;
		if(Input.isDown(GLFW.GLFW_KEY_LEFT_SHIFT))ay--;

		if(Input.isCursorBinded())
		{
			Vector2f acc = Input.getCursorAcceleration();
			rotation.y -= acc.x * rotSpeed;
			rotation.x -= acc.y * rotSpeed;
		}
		if (Math.toDegrees(rotation.x) > 89)
			rotation.x = (float) Math.toRadians(89);
		if(Math.toDegrees(rotation.x) < -89)
			rotation.x = (float) Math.toRadians(-89);
		
		cos = (float)Math.cos(rotation.y);
		sin = (float)Math.sin(rotation.y);
		
		acceleration.x += (ax * cos + az * sin) * moveSpeed;
		acceleration.z += (ax * -sin + az * cos) * moveSpeed;
		acceleration.y += ay * moveSpeed;
		
		position = new Vector3f(position.add(acceleration));
		acceleration = new Vector3f(acceleration.mul(0.6f));
	}

	public float getMoveSpeed()
	{
		return moveSpeed;
	}

	public void setMoveSpeed(float moveSpeed)
	{
		this.moveSpeed = moveSpeed;
	}

	public float getRotSpeed()
	{
		return rotSpeed;
	}

	public void setRotSpeed(float rotSpeed)
	{
		this.rotSpeed = rotSpeed;
	}

	public Vector3f getPosition()
	{
		return position;
	}
	
	public Vector3f getRotation()
	{
		return rotation;
	}
	
	public void setPosition(Vector3f position)
	{
		this.position = position;
	}
}
