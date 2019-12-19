package org.sopiro.game.postProcess;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL13.*;
import static org.lwjgl.opengl.GL30.*;

import org.sopiro.game.models.*;
import org.sopiro.game.renderer.*;
import org.sopiro.game.utils.*;

public class QuadRenderer
{
	private float[] positions = { -1, 1, -1, -1, 1, 1, 1, -1 };
	private RawModel vao;
	
	public QuadRenderer(Loader loader)
	{
		this(loader, 1);
	}
	
	public QuadRenderer(Loader loader, float scale)
	{
		Maths.mul(positions, scale);
		vao = loader.loadVAO(positions, 2);
	}
	
	public void render(int texture)
	{
		glDisable(GL_DEPTH_TEST);
		glActiveTexture(GL_TEXTURE0);
		glBindTexture(GL_TEXTURE_2D, texture);
		
		glBindVertexArray(vao.getVaoID());
		glDrawArrays(GL_TRIANGLE_STRIP, 0, vao.getVertexCount());
		glBindVertexArray(0);
		glEnable(GL_DEPTH_TEST);
	}
}
