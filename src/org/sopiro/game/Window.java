package org.sopiro.game;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.system.MemoryUtil.*;

import org.lwjgl.glfw.*;
import org.lwjgl.opengl.*;
import org.sopiro.game.*;

public class Window
{
    public static long window;
    private static boolean isFullScreen = false;

    private static int WINDOW_WIDTH;
    private static int WINDOW_HEIGHT;

    private static int MONITOR_WIDTH;
    private static int MONITOR_HEIGHT;
    private static int MONITOR_REFRESH_RATE;

    public Window(int width, int height, String title, Input inputCallback)
    {
        WINDOW_WIDTH = width;
        WINDOW_HEIGHT = height;

        GLFWErrorCallback.createPrint(System.err).set();

        if (!glfwInit())
        {
            System.err.println("GLFW initialization error");
            System.exit(-1);
        }

        glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE);
        glfwWindowHint(GLFW_RESIZABLE, GLFW_TRUE);
//		glfwWindowHint(GLFW_SAMPLES, 4);

        window = glfwCreateWindow(WINDOW_WIDTH, WINDOW_HEIGHT, title, NULL, NULL); // Put
        // forth
        // argument
        // as
        // glfwGetPrimaryMonitor()
        // if
        // you
        // want
        // full-screen
        // mode
        if (window == NULL)
        {
            System.err.println("Failed to create Window");
            System.exit(-1);
        }
        GLFWVidMode videoMode = glfwGetVideoMode(glfwGetPrimaryMonitor());
        MONITOR_WIDTH = videoMode.width();
        MONITOR_HEIGHT = videoMode.height();
        MONITOR_REFRESH_RATE = videoMode.refreshRate();

        // System.out.println(MONITOR_WIDTH + ", " + MONITOR_HEIGHT + ", " +
        // MONITOR_REFRESH_RATE);

        glfwSetWindowMonitor(window, NULL, 0, 0, width, height, MONITOR_REFRESH_RATE);
        glfwSetWindowPos(window, (MONITOR_WIDTH - WINDOW_WIDTH) / 2, (MONITOR_HEIGHT - WINDOW_HEIGHT) / 2);

        glfwSetKeyCallback(window, inputCallback);

        glfwMakeContextCurrent(window);
        GL.createCapabilities(); // Create context in current thread;

        glfwSetFramebufferSizeCallback(window, (s, w, h) ->
        {
            GL11.glViewport(0, 0, w, h);
            WINDOW_WIDTH = w;
            WINDOW_HEIGHT = h;
        }); // Update viewport when window resized

        glfwSwapInterval(0);

        glfwShowWindow(window);
    }

    public void render()
    {
        glfwSwapBuffers(window);
        glfwPollEvents();
    }

    public void terminate()
    {
        Callbacks.glfwFreeCallbacks(window);
        glfwDestroyWindow(window);
        glfwTerminate();
        glfwSetErrorCallback(null).free();
    }

    public boolean isClosed()
    {
        return glfwWindowShouldClose(window);
    }

    public static void toggleFullScreen()
    {
        if (!isFullScreen)
        {
            glfwSetWindowMonitor(window, glfwGetPrimaryMonitor(), 0, 0, MONITOR_WIDTH, MONITOR_HEIGHT, MONITOR_REFRESH_RATE);
            glfwSetWindowPos(window, (MONITOR_WIDTH - WINDOW_WIDTH) / 2, (MONITOR_HEIGHT - WINDOW_HEIGHT) / 2);
        } else
        {
            glfwSetWindowMonitor(window, NULL, 0, 0, Main.WIDTH, Main.HEIGHT, MONITOR_REFRESH_RATE);
            glfwSetWindowPos(window, (MONITOR_WIDTH - WINDOW_WIDTH) / 2, (MONITOR_HEIGHT - WINDOW_HEIGHT) / 2);
        }

        glfwSwapInterval(1);
        isFullScreen = !isFullScreen;
    }

    public static int getWindowWidth()
    {
        return WINDOW_WIDTH;
    }

    public static int getWindowHeight()
    {
        return WINDOW_HEIGHT;
    }

    public static float getWindowAspectRatio()
    {
        return (float) WINDOW_WIDTH / WINDOW_HEIGHT;
    }
}
