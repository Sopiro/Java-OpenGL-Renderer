package org.sopiro.game.utils;

import static org.lwjgl.opengl.GL30.*;

import org.sopiro.game.renderer.*;

public class GLUtills
{
	private static boolean polygonMode = false;
	
	public static void initOpenGLSettings()
	{
		glClearColor(122/255.0f, 188/255.0f, 255/255.0f, 1.0f);
		MasterRenderer.enableCulling();
//		glEnable(GL_FRAMEBUFFER_SRGB); // Gamma Correction
		glEnable(GL_DEPTH_TEST);
		glEnable(GL_STENCIL_TEST);
		glStencilOp(GL_KEEP, GL_KEEP, GL_KEEP);
		glStencilFunc(GL_ALWAYS, 1, 0xff);
//		glEnable(GL_MULTISAMPLE);
	}
	
	public static void togglePolygonMode()
	{
		if(!polygonMode)
		{
			polygonMode = true;
			glPolygonMode(GL_FRONT, GL_LINE);
		}
		else
		{
			polygonMode = false;
			glPolygonMode(GL_FRONT, GL_FILL);
		}
	}
	
	public static void setPolygonMode(boolean mode)
	{
		if(mode && !polygonMode)
			togglePolygonMode();
		
		if(!mode && polygonMode)
			togglePolygonMode();
	}
}
