package entity;

import java.awt.Graphics2D;
import java.awt.Rectangle;

import main.GamePanel;
import tileMap.Tile;
import tileMap.TileMap;

/**
 * ������� ����� ��� ���� �������
 *
 * @author Likanich
 *
 */
public class MapObject {

	// tile stuff
	protected TileMap tileMap;
	protected int tileSize;
	protected double xmap;
	protected double ymap;
	protected int zmap;

	// ������� � �����������
	protected double x;
	protected double y;
	protected int z;
	protected double dx;
	protected double dy;

	// �������
	protected int width;
	protected int height;

	// ������� ������������
	protected int cwidth;
	protected int cheight;

	// ������������
	protected int currRow;
	protected int currCol;
	protected double xdest;
	protected double ydest;
	protected double xtemp;
	protected double ytemp;
	protected boolean topLeft;
	protected boolean topRight;
	protected boolean bottomLeft;
	protected boolean bottomRight;

	// ������
	boolean tlc;
	boolean trc;
	boolean blc;
	boolean brc;

	// ����������� ��������
	protected boolean left;
	protected boolean right;
	protected boolean up;
	protected boolean down;

	// ��������������
	protected double moveSpeed;
	protected double maxSpeed;
	protected double stopSpeed;

	/**
	 * �����������
	 *
	 * @param tm
	 *            �������� � ����
	 */
	public MapObject(TileMap tm) {
		tileMap = tm;
		tileSize = tm.getTileSize();
	}

	public boolean intersects(MapObject o) {
		Rectangle r1 = getRectangle();
		Rectangle r2 = o.getRectangle();
		return r1.intersects(r2);
	}

	public Rectangle getRectangle() {
		return new Rectangle((int) x - cwidth, (int) y - cheight, cwidth, cheight);
	}

	public void calculateCorners(double x, double y) {

		int leftTile = (int) (x - cwidth / 2) / tileSize;
		int rightTile = (int) (x + cwidth / 2 - 1) / tileSize;
		int topTile = (int) (y - cheight / 2) / tileSize;
		int bottomTile = (int) (y + cheight / 2 - 1) / tileSize;

		int tl = tileMap.getType(topTile, leftTile, z);
		int tr = tileMap.getType(topTile, rightTile, z);
		int bl = tileMap.getType(bottomTile, leftTile, z);
		int br = tileMap.getType(bottomTile, rightTile, z);

		topLeft = (tl == Tile.WALL);
		topRight = (tr == Tile.WALL);
		bottomLeft = (bl == Tile.WALL);
		bottomRight = (br == Tile.WALL);
	}

	public void checkTileMapCollision() {

		currCol = (int) x / tileSize;
		currRow = (int) y / tileSize;

		xdest = x + dx;
		ydest = y + dy;

		xtemp = x;
		ytemp = y;

		calculateCorners(x, ydest);
		if (dy < 0) {
			if (topLeft || topRight) {
				dy = 0;
				ytemp = currRow * tileSize + cheight / 2;
			} else {
				ytemp += dy;
			}
		}
		if (dy > 0) {
			if (bottomLeft || bottomRight) {
				dy = 0;
				ytemp = (currRow + 1) * tileSize - cheight / 2;
			} else {
				ytemp += dy;
			}
		}

		calculateCorners(xdest, y);
		if (dx < 0) {
			if (topLeft || bottomLeft) {
				dx = 0;
				xtemp = currCol * tileSize + cwidth / 2;
			} else {
				xtemp += dx;
			}
		}
		if (dx > 0) {
			if (topRight || bottomRight) {
				dx = 0;
				xtemp = (currCol + 1) * tileSize - cwidth / 2;
			} else {
				xtemp += dx;
			}
		}

	}

	public int getx() {
		return (int) x;
	}

	public int gety() {
		return (int) y;
	}

	public int getz() {
		return z;
	}

	public int getWidth() {
		return width;
	}

	public int getHeight() {
		return height;
	}

	public int getCWidth() {
		return cwidth;
	}

	public int getCHeight() {
		return cheight;
	}

	public void setPosition(double x, double y, int z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}

	public void setVector(double dx, double dy) {
		this.dx = dx;
		this.dy = dy;
	}

	public void setMapPosition() {
		xmap = tileMap.getx();
		ymap = tileMap.gety();
		zmap = tileMap.getz();
	}

	public void setLeft(boolean b) {
		left = b;
	}

	public void setRight(boolean b) {
		right = b;
	}

	public void setUp(boolean b) {
		up = b;
	}

	public void setDown(boolean b) {
		down = b;
	}

	public boolean notOnScreen() {
		return x + xmap - width < 0 || x + xmap + width > GamePanel.WIDTH || y + ymap - height < 0
				|| y + ymap + height > GamePanel.HEIGHT;
	}

	public boolean canEnter(int wx, int wy, int wz) {
		return tileMap.isGround(wx, wy, wz);
	}

	public void init() {
	}

	public void update(TileMap world) {
	}

	public void draw(Graphics2D g) {
	}
}
