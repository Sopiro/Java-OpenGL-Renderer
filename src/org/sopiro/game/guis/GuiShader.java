package org.sopiro.game.guis;

import org.joml.*;
import org.sopiro.game.renderer.shader.*;

public class GuiShader extends ShaderProgram
{
	private final static String vsPath = "./res/shaders/gui.vs";
	private final static String fsPath = "./res/shaders/gui.fs";
	
	//Uniform locations
	private int transformationMatrix;
	
	public GuiShader()
	{
		super(vsPath, fsPath);
	}

	@Override
	protected void getAllUniformLocation()
	{
		transformationMatrix = super.getUniformLocation("transformationMatrix");
	}
	
	public void setTransformationMatrix(Matrix4f matrix)
	{
		super.setMatrix(this.transformationMatrix, matrix);
	}

}
