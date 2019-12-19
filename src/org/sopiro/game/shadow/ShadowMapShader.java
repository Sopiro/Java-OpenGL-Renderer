package org.sopiro.game.shadow;

import org.joml.*;
import org.sopiro.game.renderer.shader.*;

public class ShadowMapShader extends ShaderProgram
{
	private final static String vsPath = "./res/shaders/shadow.vs";
	private final static String fsPath = "./res/shaders/shadow.fs";
	
	// Uniform Locations
	private int m;
	private int vp;
	
	public ShadowMapShader()
	{
		super(vsPath, fsPath);
	}

	protected void getAllUniformLocation()
	{
		m = super.getUniformLocation("m");
		vp = super.getUniformLocation("vp");
	}

	public void setVPMatrix(Matrix4f matrix)
	{
		super.setMatrix(this.vp, matrix);
	}
	
	public void setMMatrix(Matrix4f matrix)
	{
		super.setMatrix(this.m, matrix);
	}
}
