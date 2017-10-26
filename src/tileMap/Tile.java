package tileMap;

import java.awt.image.BufferedImage;

public class Tile {

	private BufferedImage image;
	private int type;
	private int tileNum;

	// tile types
	public static final int WALL = 1;
	public static final int GROUND = 0;
	
	// tile nums
	public static final int EARTH = 0;
	public static final int UNUSED = 2;
	public static final int WALL_UL = 15;
	public static final int WALL_U = 16;
	public static final int WALL_UR = 17;
	public static final int WALL_L = 18;
	public static final int WALL_R = 19;
	public static final int WALL_DL = 20;
	public static final int WALL_D = 21;
	public static final int WALL_DR = 22;
	public static final int WALL_ULI = 23;
	public static final int WALL_URI = 24;
	public static final int WALL_DLI = 25;
	public static final int WALL_DRI = 26;
	public static final int ERROR = 27;
	

	public Tile(BufferedImage image, int type) {
		this.image = image;
		this.type = type;
	}

	public BufferedImage getImage() { return image; }
	public int getType() { return type; }
	public int getTileNum() { return tileNum; }
	public void setType(int type) { this.type = type; }

	public boolean isDiggable() {
		return this.getType() == WALL;
	}

	public boolean isGround() {
		return this.getType() != WALL;
	}
}
