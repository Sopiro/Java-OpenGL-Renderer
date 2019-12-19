package org.sopiro.game.renderer.shader;

import static org.lwjgl.opengl.GL20.*;

import java.io.*;
import java.util.*;

import org.joml.*;

public abstract class ShaderProgram
{
	private int program;
	private int vertexShader;
	private int fragmentShader;

	public ShaderProgram(String vsPath, String fsPath)
	{
		vertexShader = loadShader(vsPath, GL_VERTEX_SHADER);
		fragmentShader = loadShader(fsPath, GL_FRAGMENT_SHADER);
		
		program = glCreateProgram();
		
		glAttachShader(program, vertexShader);
		glAttachShader(program, fragmentShader);

		glLinkProgram(program);
		checkProgram();
		
		glValidateProgram(program);
		if(glGetProgrami(program, GL_VALIDATE_STATUS) != 1)
		{
			System.out.println(glGetShaderInfoLog(program));
			System.exit(1);
		}
		
		// It's ok because we dose it after attaching and linking. 
		glDeleteShader(vertexShader); 
		glDeleteShader(fragmentShader);

		getAllUniformLocation();
	}
	
	protected abstract void getAllUniformLocation();
	
	protected int getUniformLocation(String uniformName)
	{
		return glGetUniformLocation(program, uniformName);
	}
	
	protected void setInt(int unifromLocation, int value)
	{
		glUniform1i(unifromLocation, value);
	}
	
	protected void setFloat(int uniformLocation, float value)
	{
		glUniform1f(uniformLocation, value);
	}
	
	protected void setVector3(int uniformLocation, Vector3f v)
	{
		glUniform3f(uniformLocation, v.x, v.y, v.z);
	}
	
	protected void setVector4(int uniformLocation, Vector4f v)
	{
		glUniform4f(uniformLocation, v.x, v.y, v.z, v.w);
	}

	protected void setBoolean(int uniformLocation, boolean value)
	{
		glUniform1i(uniformLocation, value ? GL_TRUE : GL_FALSE);
	}
	
	protected void setMatrix(int uniformLocation, Matrix4f matrix)
	{
		float[] a = new float[16];
		matrix.get(a);
		glUniformMatrix4fv(uniformLocation, false, a);
	}
	
	private int loadShader(String path, int type)
	{
		String src = "";

		try
		{
			Scanner s = new Scanner(new File(path));
			
			while (s.hasNext())
				src += "\n" + s.nextLine();
			
			s.close();
			
		} catch (FileNotFoundException e)
		{
			e.printStackTrace();
		}
		
		int shaderID = glCreateShader(type);
		glShaderSource(shaderID, src);
		glCompileShader(shaderID);
		checkShader(shaderID, type);
		
		return shaderID;
	}

	public void start()
	{
		glUseProgram(program);
	}
	
	public void stop()
	{
		glUseProgram(0);
	}
	
	public void terminate()
	{
		stop();
		glDeleteProgram(program);
	}

	private void checkShader(int shader, int type)
	{
		int status = glGetShaderi(shader, GL_COMPILE_STATUS);
		if(status != GL_TRUE)
		{
			System.err.println(type == GL_VERTEX_SHADER ? "Error Occur on VertexShader" : "Error Occur on FragmentShader : " + this.getClass().getName());
			throw new RuntimeException(glGetShaderInfoLog(shader));
		}
	}
	
	private void checkProgram()
	{
		int status = glGetProgrami(program, GL_LINK_STATUS);
		if(status != GL_TRUE)
		{
			throw new RuntimeException(glGetProgramInfoLog(program));
		}
	}
}
