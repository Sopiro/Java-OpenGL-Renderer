package org.sopiro.game;

public class Main
{
	public static final int WIDTH = 1280;
	public static final int HEIGHT = 720;
	private static final String TITLE = "Game Engine";

	private double nsPerUpdate = 1000000000.0 / 144.0;
	private double nsPerFrame = 1000000000.0 / 144.0;

	private boolean running;
	private Window window;
	private GameLoop gameloop;

	private Input inputHandler;

	private void start()
	{
		running = true;
		
		init();
		run();
	}

	private void init()
	{
		inputHandler = new Input();
		window = new Window(WIDTH, HEIGHT, TITLE, inputHandler);

		gameloop = new GameLoop();
		gameloop.init();
	}

	public void run()
	{
		long previousTime = System.nanoTime();
		long passedTime;
		long updateLag = 0;
		long renderLag = 0;

		long frameCounter = System.currentTimeMillis();
		int frames = 0, updates = 0;

		while (running) // Game loop
		{
			long curruntTime = System.nanoTime();
			passedTime = curruntTime - previousTime;
			previousTime = curruntTime;
			updateLag += passedTime;
			renderLag += passedTime;
			
			if (updateLag >= nsPerUpdate)
			{
				updateLag -= nsPerUpdate;
				gameloop.update();  // Game Logics
				inputHandler.update();	// Update Input
				updates++;
			}
			
			if (renderLag >= nsPerFrame)
			{
				renderLag -= nsPerFrame;
				gameloop.render((float)(renderLag / nsPerFrame)); 		// Render Game objects
				window.render();   		// Swap buffer
				frames++;
			}
			
			if (System.currentTimeMillis() - frameCounter >= 1000)
			{
				System.out.println(frames + "fps, " + updates + "ups");
				frameCounter = System.currentTimeMillis();
				updates = 0;
				frames = 0;
			}

			running = !window.isClosed();
		}

		// Terminate game resources
		gameloop.terminate();
		window.terminate();
	}

	public static void main(String[] args)
	{
		new Main().start();
	}
}
