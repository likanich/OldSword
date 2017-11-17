package gameState;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;

import main.GamePanel;
import tileMap.Background;
import tileMap.StaticImage;

public class MenuState extends GameState{
	private final String TITLE = "Old Sword"; 
	
	private Background bg;
	private StaticImage menuImage;

	private int currentChoise = 0;
	private String[] options = {
			"Новая игра",
			"Настройка",
			"Выход"
	};

	private Color titleColor;
	private Font titleFont;

	private Font font;

	public MenuState(GameStateManager gsm) {
		this.gsm = gsm;

		try {
			bg = new Background("/Backgrounds/menubg.png", 1);
			//bg.setVector(-0.1, 0);
			menuImage = new StaticImage("/HUD/menuImage.gif");
			

			titleColor = new Color(128, 0, 0);
			titleFont = new Font("Century Gothic", Font.PLAIN, 28);
			font = new Font("Arial", Font.PLAIN, 12);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void init() {}
	public void update() {
		bg.update();
	}
	public void draw(Graphics2D g) {
		// draw bg
		bg.draw(g);
		
		// draw image
		int x = GamePanel.WIDTH / 2 - menuImage.getWidth() / 2;
		menuImage.setPosition(x, 20);
		menuImage.draw(g);

		// draw title
		g.setColor(titleColor);
		g.setFont(titleFont);
		FontMetrics fm = g.getFontMetrics();
		int xCent = ((GamePanel.WIDTH - fm.stringWidth(TITLE)) / 2);
		g.drawString(TITLE, xCent, 70);

		//draw menu options
		g.setFont(font);
		for (int i = 0; i < options.length; i++) {
			if (i == currentChoise) {
				g.setColor(Color.BLACK);
			} else {
				g.setColor(Color.RED);
			}
			g.drawString(options[i], 165, 120 + i * 15);
		}
	}

	private void select() {
		if (currentChoise == 0) {
			// start
			gsm.setState(GameStateManager.LEVEL1STATE);
		}
		if (currentChoise == 2) {
			//exit
			System.exit(0);
		}
	}

	public void keyPressed(int k) {
		if (k == KeyEvent.VK_ENTER) {
			select();
		}
		if (k == KeyEvent.VK_UP) {
			currentChoise--;
			if (currentChoise == -1) {
				currentChoise = options.length -1;
			}
		}
		if (k == KeyEvent.VK_DOWN) {
			currentChoise++;
			if (currentChoise == options.length) {
				currentChoise = 0;
			}
		}
	}
	public void keyReleased(int k) {}
}
