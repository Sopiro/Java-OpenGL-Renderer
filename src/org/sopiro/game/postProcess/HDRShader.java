package org.sopiro.game.postProcess;

import org.joml.*;
import org.sopiro.game.renderer.shader.*;

public class HDRShader extends ShaderProgram
{
	private final static String vsPath = "./res/shaders/postProcess.vs";
	private final static String fsPath = "./res/shaders/hdr.fs";
	
	// Uniform Locations
	private int transformationMatrix;
	private int exposure;
	
	public HDRShader()
	{
		super(vsPath, fsPath);
	}

	@Override
	protected void getAllUniformLocation()
	{
		transformationMatrix = super.getUniformLocation("transformationMatrix");
		exposure = super.getUniformLocation("exposure");
	}
	
	public void setTransformationMatrix(Matrix4f transformationMatrix)
	{
		super.setMatrix(this.transformationMatrix, transformationMatrix);
	}
	
	public void setExposure(float exposure)
	{
		super.setFloat(this.exposure, exposure);
	}
}
