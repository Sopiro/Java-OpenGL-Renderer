package org.sopiro.game.postProcess;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL12.*;
import static org.lwjgl.opengl.GL30.*;
import static org.lwjgl.opengl.GL32.*;

import org.sopiro.game.*;

public class MultiSampleFrameBuffer
{
	private int multisampleFbo;
	private int normalFbo;
	private int multiSampleTexture;
	private int normalTexture;
	private int rbo;
	
	private int width;
	private int height;
	
	private static final int DOWN_RESOLUSION = 1;
	private int samples = 4;
	
	public MultiSampleFrameBuffer()
	{
		multisampleFbo = glGenFramebuffers();
		normalFbo = glGenFramebuffers();
		multiSampleTexture = glGenTextures();
		normalTexture = glGenTextures();
		rbo = glGenRenderbuffers();
		
		prepareTexture();
		
		glBindFramebuffer(GL_FRAMEBUFFER, multisampleFbo);
		glFramebufferTexture2D(GL_FRAMEBUFFER, GL_COLOR_ATTACHMENT0, GL_TEXTURE_2D_MULTISAMPLE, multiSampleTexture, 0);
		glFramebufferRenderbuffer(GL_FRAMEBUFFER, GL_DEPTH_STENCIL_ATTACHMENT, GL_RENDERBUFFER, rbo);
		
		glBindFramebuffer(GL_FRAMEBUFFER, normalFbo);
		glFramebufferTexture2D(GL_FRAMEBUFFER, GL_COLOR_ATTACHMENT0, GL_TEXTURE_2D, normalTexture, 0);
		glBindFramebuffer(GL_FRAMEBUFFER, 0);
		
		if(!(glCheckFramebufferStatus(GL_FRAMEBUFFER) == GL_FRAMEBUFFER_COMPLETE))
		{
			System.out.println("Error occurred on creating FrameBuffer");
			System.exit(1);
		}
	}
	
	public void bind()
	{
		glBindFramebuffer(GL_FRAMEBUFFER, multisampleFbo);
		int w = Window.getWindowWidth() / DOWN_RESOLUSION;
		int h = Window.getWindowHeight() / DOWN_RESOLUSION;
		glViewport(0, 0, w, h);
	}
	
	public void unbind()
	{
		resolveToNormalFBO();
		
		glClearColor(1.0f, 0.0f, 1.0f, 1.0f);
		glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT | GL_STENCIL_BUFFER_BIT);
		glViewport(0, 0, Window.getWindowWidth(), Window.getWindowHeight());
		prepareTexture();
	}
	
	private void resolveToNormalFBO()
	{
		//Resolve multi-sample buffer to normal buffer
		glBindFramebuffer(GL_READ_FRAMEBUFFER, multisampleFbo);
		glBindFramebuffer(GL_DRAW_FRAMEBUFFER, normalFbo);
		glBlitFramebuffer(0, 0, width, height, 0, 0, width, height, GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT, GL_NEAREST);
		glBindFramebuffer(GL_FRAMEBUFFER, 0);
	}
	
	private void prepareTexture()
	{
		if(width == Window.getWindowWidth() && height == Window.getWindowHeight())
			return;
		
		width = Window.getWindowWidth() / DOWN_RESOLUSION;
		height = Window.getWindowHeight() / DOWN_RESOLUSION;
		
		glBindTexture(GL_TEXTURE_2D_MULTISAMPLE, multiSampleTexture);
		glTexImage2DMultisample(GL_TEXTURE_2D_MULTISAMPLE, samples, GL_RGBA16F, width, height, true);
		glBindTexture(GL_TEXTURE_2D_MULTISAMPLE, 0);
		
		glBindRenderbuffer(GL_RENDERBUFFER, rbo);
		glRenderbufferStorageMultisample(GL_RENDERBUFFER, samples, GL_DEPTH24_STENCIL8, width, height);
		glBindRenderbuffer(GL_RENDERBUFFER, 0);
		
		glBindTexture(GL_TEXTURE_2D, normalTexture);
		glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA16F, width, height, 0, GL_RGBA, GL_FLOAT, 0);
		glTexParameterf(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
		glTexParameterf(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
		glTexParameterf(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE);
		glTexParameterf(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE);
		glBindTexture(GL_TEXTURE_2D, 0);
	}
	
	public int getScreenTexure()
	{
		return normalTexture;
	}
	
	public void terminate()
	{
		glDeleteFramebuffers(multisampleFbo);;
		glDeleteFramebuffers(normalFbo);;
		glDeleteTextures(multiSampleTexture);
		glDeleteTextures(normalTexture);
		glDeleteRenderbuffers(rbo);
	}
}
