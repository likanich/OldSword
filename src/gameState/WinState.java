package gameState;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;

import main.GamePanel;
import tileMap.Background;

public class WinState extends GameState{
	private Background bg;
	private Color color;
	private Font font;
	
	public WinState(GameStateManager gsm) {
		this.gsm = gsm;
		
		try {
			bg = new Background("/Backgrounds/losebg.png", 1);
			
			color = new Color(128, 0, 0);
			font = new Font("Century Gothic", Font.PLAIN, 28);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public void init() {}

	@Override
	public void update() {}

	@Override
	public void draw(Graphics2D g) {
		// draw bg
		bg.draw(g);
		
		// draw title
		g.setColor(color);
		g.setFont(font);
		String text = "Вы победили!";
		FontMetrics fm = g.getFontMetrics();
		int xCent = ((GamePanel.WIDTH - fm.stringWidth(text)) / 2);
		g.drawString(text, xCent, 70);
	}

	@Override
	public void keyPressed(int k) {
		if (k == KeyEvent.VK_ENTER) {
			gsm.setState(GameStateManager.MENUSTATE);
		}
	}

	@Override
	public void keyReleased(int k) {}

}
