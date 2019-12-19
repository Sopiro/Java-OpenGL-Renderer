package org.sopiro.game.postProcess;

import org.sopiro.game.renderer.shader.*;

public class GaussianBlurShader extends ShaderProgram
{
	private final static String vsPath = "./res/shaders/postProcess.vs";
	private final static String fsPath = "./res/shaders/gaussianBlur.fs";
	
	public GaussianBlurShader()
	{
		super(vsPath, fsPath);
	}

	@Override
	protected void getAllUniformLocation()
	{
		// TODO Auto-generated method stub
		
	}
	
}
