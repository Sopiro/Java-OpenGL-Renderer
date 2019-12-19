package org.sopiro.game.skyBox;

import org.joml.*;
import org.sopiro.game.renderer.shader.*;

public class SkyBoxShader extends ShaderProgram
{
	private final static String vsPath = "./res/shaders/skybox.vs";
	private final static String fsPath = "./res/shaders/skybox.fs";
	
	//uniform locations
	private int viewMatrix;
	private int projectionMatrix;
	private int fogColor;
	private int brightness;
	
	public SkyBoxShader()
	{
		super(vsPath, fsPath);
	}

	protected void getAllUniformLocation()
	{
		viewMatrix = super.getUniformLocation("viewMatrix");
		projectionMatrix = super.getUniformLocation("projectionMatrix");
		fogColor = super.getUniformLocation("fogColor");
		brightness = super.getUniformLocation("brightness");
	}
	
	public void setFogColor(Vector3f fogColor)
	{
		super.setVector3(this.fogColor, fogColor);
	}
	
	public void setViewMatrix(Matrix4f matrix)
	{
		super.setMatrix(viewMatrix, matrix);
	}
	
	public void setProjectionMatrix(Matrix4f matrix)
	{
		super.setMatrix(this.projectionMatrix, matrix);
	}

	public void setBrightness(float brightness)
	{
		super.setFloat(this.brightness, brightness);
	}
}
