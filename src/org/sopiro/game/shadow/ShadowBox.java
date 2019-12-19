package org.sopiro.game.shadow;

import java.lang.Math;

import org.joml.*;
import org.sopiro.game.*;
import org.sopiro.game.entities.*;
import org.sopiro.game.renderer.*;
import org.sopiro.game.utils.*;

public class ShadowBox
{
	private float distance;
	private float offset;

	private float minX, maxX, minY, maxY, minZ, maxZ;

	public ShadowBox(float distance, float offset)
	{
		this.distance = distance;
		this.offset = offset;
	}
	
	public void update(Matrix4f lightSightMatrix, Camera camera)
	{
		Vector4f[] frustumPoints = new Vector4f[5];
		frustumPoints[0] = new Vector4f(0, 0, offset, 1);

		float angle = MasterRenderer.FOV / 2.0f;
		float h = (float) Math.tan(angle) * distance;
		float w = h * Window.getWindowAspectRatio();

		frustumPoints[1] = new Vector4f(w, h, -distance, 1);
		frustumPoints[2] = new Vector4f(-w, h, -distance, 1);
		frustumPoints[3] = new Vector4f(-w, -h, -distance, 1);
		frustumPoints[4] = new Vector4f(w, -h, -distance, 1);

		Matrix4f viewMatrix = Maths.createViewMatrix(camera);
		Matrix4f invertedViewMatrix = Maths.invert(viewMatrix);

		for (int i = 0; i < 5; i++)
			frustumPoints[i] = Maths.mul(lightSightMatrix, Maths.mul(invertedViewMatrix, frustumPoints[i])).mul(1, 1, -1, 1);

		minX = frustumPoints[0].x;
		maxX = frustumPoints[0].x;
		minY = frustumPoints[0].y;
		maxY = frustumPoints[0].y;
		minZ = frustumPoints[0].z;
		maxZ = frustumPoints[0].z;

		for (int i = 1; i < 5; i++)
		{
			Vector4f p = frustumPoints[i];
			
			if (p.x < minX)
				minX = p.x;
			else if (p.x > maxX)
				maxX = p.x;
			if (p.y < minY)
				minY = p.y;
			else if (p.y > maxY)
				maxY = p.y;
			if (p.z < minZ)
				minZ = p.z;
			else if (p.z > maxZ)
				maxZ = p.z;
		}
	}

	public float getMinX()
	{
		return minX;
	}

	public float getMaxX()
	{
		return maxX;
	}

	public float getMinY()
	{
		return minY;
	}

	public float getMaxY()
	{
		return maxY;
	}

	public float getMinZ()
	{
		return minZ;
	}

	public float getMaxZ()
	{
		return maxZ;
	}
	
	public void setDistance(float distance)
	{
		this.distance = distance;
	}
	
	public void setOffset(float offset)
	{
		this.offset = offset;
	}
}
