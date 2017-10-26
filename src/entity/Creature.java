package entity;

import main.GamePanel;
import tileMap.TileMap;
import tileMap.Tile;

import java.awt.Graphics2D;
import java.awt.Rectangle;

public class Creature {

	// tile stuff
	protected TileMap tileMap;
	protected int tileSize;
	protected double xmap;
	protected double ymap;

	// position and vector
	protected double x;
	protected double y;
	protected double dx;
	protected double dy;

	// AI
	private CreatureAi ai;
	public void setCreatureAi(CreatureAi ai) { this.ai = ai; }

	// dimensions
	protected int width;
	protected int height;

	// collision box
	protected int cwidth;
	protected int cheight;

	// collision
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

	// other
	boolean tlc;
	boolean trc;
	boolean blc;
	boolean brc;

	// movement
	protected boolean left;
	protected boolean right;
	protected boolean up;
	protected boolean down;

	// movement attributes
	protected int health;
	protected int maxHealth;
	protected double moveSpeed;
	protected double maxSpeed;
	protected double stopSpeed;
	
	private int attackValue;
    public int attackValue() { return attackValue; }

    private int defenseValue;
    public int defenseValue() { return defenseValue; }

	// constructor
	public Creature(TileMap tm, int maxHp, int attack, int defense) {
		tileMap = tm;
		tileSize = tm.getTileSize();
		this.health = maxHp;
		this.maxHealth = maxHp;
		this.attackValue = attack;
		this.defenseValue = defense;
	}

	public boolean intersects(Creature o) {
		Rectangle r1 = getRectangle();
		Rectangle r2 = o.getRectangle();
		return r1.intersects(r2);
	}

	public Rectangle getRectangle() {
		return new Rectangle(
				(int)x - cwidth,
				(int)y - cheight,
				cwidth,
				cheight
		);
	}

	public int getHealth() { return health; }
	public int getMaxHealth() { return maxHealth; }

	public void calculateCorners(double x, double y) {

		int leftTile = (int)(x - cwidth / 2) / tileSize;
		int rightTile = (int)(x + cwidth / 2 - 1) / tileSize;
		int topTile = (int)(y - cheight / 2) / tileSize;
		int bottomTile = (int)(y + cheight / 2 - 1) / tileSize;

		int tl = tileMap.getType(topTile, leftTile);
		int tr = tileMap.getType(topTile, rightTile);
		int bl = tileMap.getType(bottomTile, leftTile);
		int br = tileMap.getType(bottomTile, rightTile);
		
		//tlc = tileMap.creature(leftTile * tileSize, topTile * tileSize) != null && tileMap.creature(leftTile * tileSize, topTile * tileSize).getMaxHealth() != maxHealth;
		//trc = tileMap.creature(rightTile * tileSize, topTile * tileSize) != null && tileMap.creature(rightTile * tileSize, topTile * tileSize).getMaxHealth() != maxHealth;
		//blc = tileMap.creature(leftTile * tileSize, bottomTile * tileSize) != null && tileMap.creature(leftTile * tileSize, bottomTile * tileSize).getMaxHealth() != maxHealth;
		//brc = tileMap.creature(rightTile * tileSize, bottomTile * tileSize) != null && tileMap.creature(rightTile * tileSize, bottomTile * tileSize).getMaxHealth() != maxHealth;

		topLeft = (tl == Tile.WALL) || tlc;
		topRight = (tr == Tile.WALL) || trc;
		bottomLeft = (bl == Tile.WALL) || blc;
		bottomRight = (br == Tile.WALL) || brc;

	}

	public void checkTileMapCollision() {

		currCol = (int)x / tileSize;
		currRow = (int)y / tileSize;

		xdest = x + dx;
		ydest = y + dy;

		xtemp = x;
		ytemp = y;

		calculateCorners(x, ydest);
		if(dy < 0) {
			if(topLeft || topRight) {
				dy = 0;
				ytemp = currRow * tileSize + cheight / 2;
			}
			else {
				ytemp += dy;
			}
		}
		if(dy > 0) {
			if(bottomLeft || bottomRight) {
				dy = 0;
				ytemp = (currRow + 1) * tileSize - cheight / 2;
			}
			else {
				ytemp += dy;
			}
		}

		calculateCorners(xdest, y);
		if(dx < 0) {
			if(topLeft || bottomLeft) {
				dx = 0;
				xtemp = currCol * tileSize + cwidth / 2;
			}
			else {
				xtemp += dx;
			}
		}
		if(dx > 0) {
			if(topRight || bottomRight) {
				dx = 0;
				xtemp = (currCol + 1) * tileSize - cwidth / 2;
			}
			else {
				xtemp += dx;
			}
		}

		
	}

	public int getx() { return (int)x; }
	public int gety() { return (int)y; }
	public int getWidth() { return width; }
	public int getHeight() { return height; }
	public int getCWidth() { return cwidth; }
	public int getCHeight() { return cheight; }

	public void setPosition(double x, double y) {
		this.x = x;
		this.y = y;
	}
	public void setVector(double dx, double dy) {
		this.dx = dx;
		this.dy = dy;
	}

	public void setMapPosition() {
		xmap = tileMap.getx();
		ymap = tileMap.gety();
	}

	public void setLeft(boolean b) { left = b; }
	public void setRight(boolean b) { right = b; }
	public void setUp(boolean b) { up = b; }
	public void setDown(boolean b) { down = b; }

	public boolean notOnScreen() {
		return x + xmap + width < 0 ||
			x + xmap - width > GamePanel.WIDTH ||
			y + ymap + height < 0 ||
			y + ymap - height > GamePanel.HEIGHT;
	}

	public boolean canEnter(int wx, int wy) {
		return tileMap.isGround(wx, wy);
	}

	public void init() {
		ai.onInit();
	}

	public void update(TileMap world) {
		ai.onUpdate(world);
	}

	public void draw(Graphics2D g) {
		ai.onDraw(g);
	}
	
	public void attack(Creature other){
        int amount = Math.max(0, attackValue() - other.defenseValue());
    
        amount = (int)(Math.random() * amount) + 1;
    
        other.modifyHp(-amount);
    }

    public void modifyHp(int amount) {
        health += amount;
    
        if (health < 1)
         tileMap.remove(this);
    }
    
    public void notify(String message, Object ... params){
        ai.onNotify(String.format(message, params));
    }
}
















