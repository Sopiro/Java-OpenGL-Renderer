package org.sopiro.game.models;

import org.sopiro.game.texture.*;

public class TexturedModel
{
	private RawModel rawModel;
	private Material texture;

	public TexturedModel(RawModel model, Material texture)
	{
		this.rawModel = model;
		this.texture = texture;
	}

	public RawModel getRawModel()
	{
		return rawModel;
	}

	public Material getMaterial()
	{
		return texture;
	}
}
