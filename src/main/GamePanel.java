package main;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.image.BufferedImage;

import javax.swing.JPanel;

import gameState.GameStateManager;

public class GamePanel extends JPanel implements Runnable, KeyListener{

	/**
	 *
	 */
	private static final long serialVersionUID = 5759218091147851942L;
	// dimensions
	public static final int WIDTH = 640;
	public static final int HEIGHT = 360;
	public static final int SCALE = 2;

	// game thread
	private Thread thread;
	private boolean running;
	private int FPS = 60;
	private long targetTime = 1000 / FPS;

	// image
	private BufferedImage image;
	private Graphics2D g;

	// game state manager
	private GameStateManager gsm;

	public GamePanel() {
		super();
		setPreferredSize(new Dimension(WIDTH * SCALE, HEIGHT * SCALE));
		setFocusable(true);
		requestFocus();
	}

	public void addNotify() {
		super.addNotify();
		if (thread == null) {
			thread = new Thread(this);
			addKeyListener(this);
			thread.start();
		}
	}

	private void init() {
		image = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_RGB);
		g = (Graphics2D) image.getGraphics();

		running = true;

		gsm = new GameStateManager();
	}

	public void run() {
		init();

		long start;
		long elapsed;
		long wait;

		// game loop
		while (running) {
			start = System.nanoTime();

			update();
			draw();
			drawToScreen();

			elapsed = System.nanoTime() - start;

			wait = targetTime - elapsed / 1000000;

			if (wait < 0) wait = 5;
			try {
				Thread.sleep(wait);
			}
			catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	private void update() {
		gsm.update();
	}

	private void draw() {
		gsm.draw(g);
	}

	private void drawToScreen() {
		Graphics g2 = getGraphics();
		g2.drawImage(image, 0, 0, WIDTH * SCALE, HEIGHT * SCALE, null);
	}

	public void keyTyped(KeyEvent e) {}
	public void keyPressed(KeyEvent e) {
		gsm.keyPressed(e.getKeyCode());
	}

	public void keyReleased(KeyEvent e) {
		gsm.keyReleased(e.getKeyCode());
	}


}
