package org.sopiro.game.postProcess;

import static org.lwjgl.opengl.GL30.*;

import org.sopiro.game.*;

public class FrameBuffer
{
	private int fbo;
	private int texture;
	
	private int width;
	private int height;
	
	public FrameBuffer()
	{
		fbo = glGenFramebuffers();
		texture = glGenTextures();

		this.width = Window.getWindowWidth();
		this.height = Window.getWindowHeight();
		
		prepareTexture();
		
		glBindFramebuffer(GL_FRAMEBUFFER, fbo);
		glFramebufferTexture2D(GL_FRAMEBUFFER, GL_COLOR_ATTACHMENT0, GL_TEXTURE_2D, texture, 0);
		glDrawBuffers(GL_COLOR_ATTACHMENT0);
		glBindFramebuffer(GL_FRAMEBUFFER, 0);
		
		if(!(glCheckFramebufferStatus(GL_FRAMEBUFFER) == GL_FRAMEBUFFER_COMPLETE))
		{
			System.out.println("Error occured on creating FrameBuffer");
			System.exit(1);
		}
	}
	
	private void prepareTexture()
	{
		glBindTexture(GL_TEXTURE_2D, texture);
		glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, width, height, 0, GL_RGBA, GL_UNSIGNED_BYTE, 0);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE);
		glBindTexture(GL_TEXTURE_2D, 0);
	}
	
	public int getTexture()
	{
		return texture;
	}
	
	public void bind()
	{
		glBindFramebuffer(GL_FRAMEBUFFER, fbo);
		
		if(width == Window.getWindowWidth() && height == Window.getWindowHeight())
			return;
		else
		{
			width = Window.getWindowWidth();
			height = Window.getWindowHeight();
			prepareTexture();
		}
	}
	
	public void unbind()
	{
		glBindFramebuffer(GL_FRAMEBUFFER, 0);
	}
	
	public void terminate()
	{
		glDeleteFramebuffers(fbo);
		glDeleteTextures(texture);
	}
}
