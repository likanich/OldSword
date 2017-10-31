package tileMap;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import javax.imageio.ImageIO;

import main.GamePanel;

public class StaticImage {
	private BufferedImage image;

	private double x;
	private double y;
	
	public StaticImage(String s) {
		try {
			image = ImageIO.read(getClass().getResourceAsStream(s));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void setPosition(double x, double y) {
		this.x = (x) % GamePanel.WIDTH;
		this.y = (y) % GamePanel.HEIGHT;
	}

	public void update() {
	}

	public void draw(Graphics2D g) {
		g.drawImage(image, (int) x, (int) y, null);
	}
	
	public int getWidth() {
		return image.getWidth();
	}
	
	public int getHeight() {
		return image.getHeight();
	}
}
