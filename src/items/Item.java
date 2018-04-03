package items;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;

import entity.MapObject;
import tileMap.TileMap;

public class Item extends MapObject {
	private BufferedImage sprite;

	private String name;

	public String name() {
		return name;
	}

	public Item(TileMap tm, String name) {
		super(tm);
		this.name = name;
	}

	@Override
	public void init() {
		width = 16;
		height = 16;
		cwidth = 16;
		cheight = 16;
		try {
			sprite = ImageIO.read(getClass().getResourceAsStream("/ItemSprites/" + name + ".png"));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void update(TileMap world) {
	}

	@Override
	public void draw(Graphics2D g) {
		setMapPosition();
		g.drawImage(sprite, (int) (x + xmap - width / 2), (int) (y + ymap - height / 2), null);
	}
}
