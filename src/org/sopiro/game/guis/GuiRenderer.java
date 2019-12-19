package org.sopiro.game.guis;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL13.*;
import static org.lwjgl.opengl.GL30.*;

import java.util.*;

import org.joml.*;
import org.lwjgl.opengl.*;
import org.sopiro.game.*;
import org.sopiro.game.models.*;
import org.sopiro.game.renderer.*;
import org.sopiro.game.utils.*;

public class GuiRenderer
{
	private final RawModel quad;
	private GuiShader shader;
	
	public GuiRenderer(Loader loader)
	{
		float[] positions = {-1 , 1, -1, -1, 1, 1, 1, -1};
		quad = loader.loadVAO(positions, 2);
		shader = new GuiShader();
	}
	
	public void render(List<GuiTexture> guis)
	{
		shader.start();
		glBindVertexArray(quad.getVaoID());
		glEnable(GL_BLEND);
		glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
		glDisable(GL_DEPTH_TEST);
		for(GuiTexture gui: guis)
		{
			glActiveTexture(GL_TEXTURE0);
			glBindTexture(GL13.GL_TEXTURE_2D, gui.getTexture());
			
			Vector2f scale = new Vector2f(gui.getScale());
			
			Matrix4f transformation = Maths.createTransformationMatrix(gui.getPosition(), scale.mul(1, Window.getWindowAspectRatio()));
			shader.setTransformationMatrix(transformation);
			
			glDrawArrays(GL11.GL_TRIANGLE_STRIP, 0, quad.getVertexCount());
		}
		glDisable(GL_BLEND);
		glEnable(GL_DEPTH_TEST);
		glBindVertexArray(0);
		shader.stop();
	}
	
	public void terminate()
	{
		shader.terminate();
	}
}
