package game;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;

import javax.swing.JPanel;

import basis.GameFrame;
import basis.KeyHandler;
import basis.MouseListener;
import basis.MouseMotionListener;
import display.Renderer;
import server.Client;
import server.Server;

public class Game extends JPanel implements Runnable {

	/**
	 * Stores the current Version of the SaveFile system, to ensure compatibility.
	 */
	public static final int codeVersion = 0;
	/* public static int loadedVersion = 0; */

	public static GameFrame gameFrame;
	public final static int WIDTH = 1920;
	public final static int HEIGHT = 1080;
	private static final int fps = 256;
	private static final int tick = 128;
	public static final int fpsTickRatio = fps / tick;

	/** Always true boolean for while */
	public static boolean running = false;
	/** Completely halts game-Ticks if true */
	public boolean paused = false;

	/* Threads */
	private Thread GameThread;
	// private Thread ClientThread;
	/**/

	public static Renderer renderer;


	/* Testing */
	/*
	 * Test-Counter for whatever purpose it is used atm is logged in Console every
	 * Sec in Console, just use it
	 */
	/**/
	public static int[] testCounter;

	/** Settings of the game. Currenty fixed, and not loaded. */
	/*
	 * public static Settings settings = new Settings();
	 * public static Settings newSettings;
	 */

	public Game(GameFrame gF) {
		gameFrame = gF;
		this.setPreferredSize(new Dimension(WIDTH, HEIGHT));
		this.setFocusable(true);
		
		this.addKeyListener(new KeyHandler());
		this.addMouseListener(new MouseListener());
		this.addMouseMotionListener(new MouseMotionListener());
		/* this.addMouseWheelListener(new MouseWheelListener()); */
		
		init();
	}

	private void init() {
		testCounter = new int[2];
		// ClientThread = new Thread(client);
		// ClientThread.start();
		// First Spectator to be set
		/* FontHandler.initFont(); */
		/* AudioFilePlayer.playSound(); */
		new Renderer();
		
		GameThread = new Thread(this);
		GameThread.start();

	}

	public void render() {
		repaint();
	}

	public void tick() {

		// Process Inputs in Renderer
		renderer.handleInputs();
		
		if(Server.server != null) {
			Server.processAll();
			Server.world.tick();
		}

		if(Client.world != null) {
			Client.world.tick();
		}

	}

	/**
	 * Draws the white background which overrides the old picture, then calls the
	 * spectator to draw its perspectives
	 */
	@Override
	public void paint(Graphics g) {
		Graphics2D g2d = (Graphics2D) g;
		g2d.setColor(new Color(255, 255, 255));
		g2d.fillRect(0, 0, Game.WIDTH, Game.HEIGHT);
		renderer.draw(g);
	}

	/**
	 * Runs the game. Manages ticks and frames.
	 * Big pile of code, but it works.
	 */
	@Override
	public void run() {
		int tickTime = 0;
		int tickCounter = 0;
		int renderTime = 0;
		int renderCounter = 0;
		running = true;
		long initialTime = System.nanoTime();
		final double timeT = 1000000000 / tick;
		final double timeF = 1000000000 / fps;
		double deltaT = 0, deltaF = 0;
		int frames = 0, ticks = 0;
		long timer = System.currentTimeMillis();
		while (running) {
			if (!paused && running) {
				long currentTime = System.nanoTime();
				deltaT += (currentTime - initialTime) / timeT;
				deltaF += (currentTime - initialTime) / timeF;
				initialTime = currentTime;

				if (deltaT >= 1) {
					tickCounter += 1;
					tickTime -= System.nanoTime();
					tick();
					tickTime += System.nanoTime();
					ticks++;
					deltaT--;
				}

				if (deltaF >= 1) {
					renderCounter += 1;
					renderTime -= System.nanoTime();
					render();
					renderTime += System.nanoTime();
					frames++;
					deltaF--;
				}

				if (System.currentTimeMillis() - timer > 1000) {
					// Test-Counter every sec
					System.out.println("TPS:" + ticks + "(" + tickTime / tickCounter / 1000 + " micS)" + "FPS:"
							+ frames + " (" + renderTime / renderCounter / 1000 + " micS)");
					/*
					 * System.out.println("Game.testCounter: " + (double) testCounter[0] / fps + " "
					 * + (double) testCounter[1] / ticks);
					 */
					tickCounter = 0;
					tickTime = 0;
					renderCounter = 0;
					renderTime = 0;
					testCounter = new int[2];
					frames = 0;
					ticks = 0;
					timer += 1000;
				}
			}
		}
	}

}
