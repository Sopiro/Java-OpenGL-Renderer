package org.sopiro.game;

import static org.lwjgl.glfw.GLFW.*;

import org.joml.*;
import org.lwjgl.glfw.*;

public class Input extends GLFWKeyCallback
{
	private static Vector2f lastCursorPos = new Vector2f();
	private static Vector2f currentCursorPos = new Vector2f();
	
	private static boolean[] lastKeys = new boolean[512];
	private static boolean[] currentKeys = new boolean[512];

	private static boolean cursorBind = false;
	
	public void invoke(long window, int key, int scancode, int action, int mods)
	{
		currentKeys[key] = action == GLFW_PRESS || action == GLFW_REPEAT;
	}
	
	public static boolean isPressed(int key)
	{
		return currentKeys[key] && !lastKeys[key];
	}
	
	public static  boolean isDown(int key)
	{
		return currentKeys[key];
	}
	
	public static  boolean isUp(int key)
	{
		return !currentKeys[key];
	}
	
	public void update()
	{
		lastCursorPos = currentCursorPos;
		currentCursorPos = getCursorPos();
		
		if(isPressed(GLFW_KEY_ESCAPE))
			if(cursorBind)
				unbindCursor();
			else
				bindCursor();
		
		if(isPressed(GLFW_KEY_F11))
			Window.toggleFullScreen();
		
		for(int i = 0; i < currentKeys.length; i++)
			lastKeys[i] = currentKeys[i];
	}

	public static Vector2f getCursorPos()
	{
		double[] xpos = new double[1];
		double[] ypos = new double[1];

		glfwGetCursorPos(Window.window, xpos, ypos);

		return new Vector2f((float) xpos[0], (float) ypos[0]);
	}

	public static Vector2f getCursorAcceleration()
	{
		return new Vector2f(currentCursorPos).sub(lastCursorPos);
	}

	public static void bindCursor()
	{
		cursorBind = true;
		glfwSetInputMode(Window.window, GLFW_CURSOR, GLFW_CURSOR_DISABLED);
	}
	
	public static void unbindCursor()
	{
		cursorBind = false;
		glfwSetInputMode(Window.window, GLFW_CURSOR, GLFW_CURSOR_NORMAL);
		glfwSetCursorPos(Window.window, Window.getWindowWidth() / 2, Window.getWindowHeight() / 2);
	}
	
	public static boolean isCursorBinded()
	{
		return cursorBind;
	}
}
