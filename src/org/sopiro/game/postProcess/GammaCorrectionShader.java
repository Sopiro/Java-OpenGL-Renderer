package org.sopiro.game.postProcess;

import org.sopiro.game.renderer.shader.*;

public class GammaCorrectionShader extends ShaderProgram
{
	private final static String vsPath = "./res/shaders/postProcess.vs";
	private final static String fsPath = "./res/shaders/gamma.fs";
	
	//Uniform Locations
	private int gamma;
	
	public GammaCorrectionShader()
	{
		super(vsPath, fsPath);
	}

	@Override
	protected void getAllUniformLocation()
	{
		gamma = super.getUniformLocation("gamma");
	}
	
	public void setGamma(float gamma)
	{
		super.setFloat(this.gamma, gamma);
	}
}
