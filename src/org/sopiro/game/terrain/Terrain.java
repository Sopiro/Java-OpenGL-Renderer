package org.sopiro.game.terrain;

import java.lang.Math;
import java.util.Random;

import org.joml.*;
import org.sopiro.game.models.*;
import org.sopiro.game.renderer.*;
import org.sopiro.game.texture.*;

public class Terrain
{
	private static final float SIZE = 1024;
	private static final int VERTEX_COUNT = 128;

	private Random random = new Random();

	private float worldX;
	private float worldZ;
	private float gridX;
	private float gridZ;
	private RawModel model;
	private TerrainTexturePack texturePack;
	private TerrainTexture blendMap;

	private String seed;
	private float[] heights = new float[VERTEX_COUNT * VERTEX_COUNT];
	private Vector3f[] normal = new Vector3f[VERTEX_COUNT * VERTEX_COUNT];

	public Terrain(int gridX, int gridZ, Loader loader, TerrainTexturePack texture, TerrainTexture blendMap)
	{
		this.texturePack = texture;
		this.blendMap = blendMap;
		this.worldX = gridX * SIZE;
		this.worldZ = gridZ * SIZE;
		this.gridX = gridX * (VERTEX_COUNT - 1);
		this.gridZ = gridZ * (VERTEX_COUNT - 1);
		generateHeightMap();
		generateNormals();
		this.model = generateTerrain(loader);
	}

	private RawModel generateTerrain(Loader loader)
	{
		int count = VERTEX_COUNT * VERTEX_COUNT;
		float[] vertices = new float[count * 3];
		float[] normals = new float[count * 3];
		float[] textureCoords = new float[count * 2];
		int[] indices = new int[6 * (VERTEX_COUNT - 1) * (VERTEX_COUNT - 1)];

		int vertexPointer = 0;
		for (int z = 0; z < VERTEX_COUNT; z++)
		{
			float zz = z + gridZ;
			for (int x = 0; x < VERTEX_COUNT; x++)
			{
				float xx = x + gridX;
				vertices[vertexPointer * 3] = x / (VERTEX_COUNT - 1.0f) * SIZE;
				vertices[vertexPointer * 3 + 1] = heights[x + z * VERTEX_COUNT] = 0;//octavePerlin(xx, zz);
				vertices[vertexPointer * 3 + 2] = z / (VERTEX_COUNT - 1.0f) * SIZE;

				Vector3f n = normal[x + z * VERTEX_COUNT];
				normals[vertexPointer * 3] = 0;
				normals[vertexPointer * 3 + 1] = 1;
				normals[vertexPointer * 3 + 2] = 0;
				
				textureCoords[vertexPointer * 2] = x / (VERTEX_COUNT - 1.0f);
				textureCoords[vertexPointer * 2 + 1] = z / (VERTEX_COUNT - 1.0f);
				vertexPointer++;
			}
		}

		int pointer = 0;
		for (int gz = 0; gz < VERTEX_COUNT - 1; gz++)
		{
			for (int gx = 0; gx < VERTEX_COUNT - 1; gx++)
			{
				int topLeft = (gz * VERTEX_COUNT) + gx;
				int topRight = topLeft + 1;
				int bottomLeft = ((gz + 1) * VERTEX_COUNT) + gx;
				int bottomRight = bottomLeft + 1;

				indices[pointer++] = topLeft;
				indices[pointer++] = bottomLeft;
				indices[pointer++] = topRight;
				indices[pointer++] = topRight;
				indices[pointer++] = bottomLeft;
				indices[pointer++] = bottomRight;
			}
		}
		
		return loader.loadVAO(vertices, textureCoords, normals, indices);
	}

	private void generateHeightMap()
	{
		for (int z = 0; z < VERTEX_COUNT; z++)
		{
			float zz = (this.gridZ + z);
			for (int x = 0; x < VERTEX_COUNT; x++)
			{
				float xx = (this.gridX + x);

				heights[x + z * VERTEX_COUNT] = octavePerlin(xx, zz);
			}
		}
	}

	private void generateNormals()
	{
//		for (int z = 0; z < VERTEX_COUNT; z++)
//		{
//			for (int x = 0; x < VERTEX_COUNT; x++)
//			{
//				Vector3f dx = new Vector3f(x, getHeight(x, z), z).sub(new Vector3f(x - 1, getHeight(x - 1, z), z));
//				Vector3f dz = new Vector3f(x, getHeight(x, z), z).sub(new Vector3f(x, getHeight(x, z - 1), z - 1));
//				
//				Vector3f n = dx.cross(dz);
//				n.normalize();
//				
//				normal[x + z * VERTEX_COUNT] = n;
//			}
//		}
		for (int z = 0; z < VERTEX_COUNT; z++)
		{
			for (int x = 0; x < VERTEX_COUNT; x++)
			{
				float r = getHeight(x - 1, z);
				float l = getHeight(x + 1, z);
				float b = getHeight(x, z - 1);
				float t = getHeight(x, z + 1);
				
				Vector3f n = new Vector3f(l - r, 2.0f, b - t);
				n.normalize();
				
				normal[x + z * VERTEX_COUNT] = n;
			}
		}
	}

	public float getHeight(int x, int z)
	{
		if (x < 0 || z < 0 || x >= VERTEX_COUNT || z >= VERTEX_COUNT)
		{
			float xx = (this.gridX + x);
			float zz = (this.gridZ + z);
			return octavePerlin(xx, zz);
		}

		return heights[x + z * VERTEX_COUNT];
	}

	private float octavePerlin(float x, float z)
	{
		float res = 0;
		float f = VERTEX_COUNT / 4;
		float a = 64;
		for (int i = 0; i < 3; i++)
		{
			res += perlin(x, z, f, a);
			f /= 2;
			a /= 2;
		}
		
		return res;
	}

	private float perlin(float x, float z, float frequency, float amplitude)
	{
		x /= frequency;
		z /= frequency;

		int ix = (int) Math.floor(x);
		int iy = (int) Math.floor(z);
		float fx = x - ix;
		float fy = z - iy;

		Vector2f s = new Vector2f(x, z);

		Vector2f v00 = getGradient(ix, iy);
		Vector2f v10 = getGradient(ix + 1, iy);
		Vector2f v01 = getGradient(ix, iy + 1);
		Vector2f v11 = getGradient(ix + 1, iy + 1);

		Vector2f p00 = new Vector2f(ix, iy);
		Vector2f p10 = new Vector2f(ix + 1, iy);
		Vector2f p01 = new Vector2f(ix, iy + 1);
		Vector2f p11 = new Vector2f(ix + 1, iy + 1);

		float h00 = p00.sub(s).dot(v00);
		float h10 = p10.sub(s).dot(v10);
		float h01 = p01.sub(s).dot(v01);
		float h11 = p11.sub(s).dot(v11);

		float r0 = lerp(h00, h10, fade(fx));
		float r1 = lerp(h01, h11, fade(fx));

		return lerp(r0, r1, fade(fy)) * amplitude;
	}

	private float fade(float t)
	{
		return t * t * t * (t * (t * 6 - 15) + 10);
	}

	private float lerp(float x0, float x1, float percent)
	{
		return x1 * percent + x0 * (1 - percent);
	}

	private Vector2f getGradient(int x, int z)
	{
		random.setSeed((seed + x + z).hashCode());

		return new Vector2f((float) random.nextGaussian() * 2 - 1, (float) random.nextGaussian() * 2 - 1).normalize();
	}

	public void setSeed(String seed)
	{
		this.seed = seed;
	}

	public String getSeed()
	{
		return seed;
	}

	public float getWorldX()
	{
		return worldX;
	}

	public float getWorldZ()
	{
		return worldZ;
	}
	
	public float getGridX()
	{
		return gridX;
	}
	
	public float getGridZ()
	{
		return gridZ;
	}

	public RawModel getModel()
	{
		return model;
	}

	public TerrainTexturePack getTexturePack()
	{
		return texturePack;
	}

	public TerrainTexture getBlendMap()
	{
		return blendMap;
	}
}
