package org.sopiro.game.models;

public class RawModel
{
	private int vaoID;
	private int vertexCount;
	
	private boolean hasNormalMap;

	public RawModel(int vaoId, int vertexCount)
	{
		this(vaoId, vertexCount, false);
	}
	
	public RawModel(int vaoId, int vertexCount, boolean hasNormalMap)
	{
		this.vaoID = vaoId;
		this.vertexCount = vertexCount;
		this.hasNormalMap = hasNormalMap;
	}
	
	public int getVaoID()
	{
		return vaoID;
	}

	public int getVertexCount()
	{
		return vertexCount;
	}
	
	public boolean isHasNormalMap()
	{
		return hasNormalMap;
	}
}
