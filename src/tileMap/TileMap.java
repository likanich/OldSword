package tileMap;

import java.awt.Graphics2D;
import java.util.List;
import java.util.Random;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import javax.imageio.ImageIO;

import main.GamePanel;
import world.DungeonGenerator;
import entity.*;

public class TileMap {

	//private final Random rand;

	// positions
	private double x;
	private double y;

	// bounds
	private int xmin;
	private int ymin;
	private int xmax;
	private int ymax;

	private double tween;

	// creatures
	private List<Creature> creatures;

	// map
	private DungeonGenerator world;
	private int tileSize;
	private int numRows;
	private int numCols;
	private int width;
	private int height;

	// tileset
	private BufferedImage tileset;
	private int numTilesAcross;
	private Tile[][] tiles;

	// drawing
	private int rowOffset;
	private int colOffset;
	private int numRowsToDraw;
	private int numColsToDraw;

	public TileMap(int tileSize) {
		this.tileSize = tileSize;
		numRowsToDraw = GamePanel.HEIGHT / tileSize + 2;
		numColsToDraw = GamePanel.WIDTH / tileSize + 2;
		tween = 0.07;
		this.creatures = new ArrayList<Creature>();
		loadMap();
	}

	public void loadTiles(String s) {
		try {
			tileset = ImageIO.read(getClass().getResourceAsStream(s));
			numTilesAcross = tileset.getWidth() / tileSize;
			//numTilesAcross = 1;
			tiles = new Tile[2][numTilesAcross];

			BufferedImage subimage;
			for (int col = 0; col < numTilesAcross; col++) {
				subimage = tileset.getSubimage(col * tileSize, 0, tileSize, tileSize);
				tiles[0][col] = new Tile(subimage, Tile.GROUND);
				subimage = tileset.getSubimage(col * tileSize, tileSize, tileSize, tileSize);
				tiles[1][col] = new Tile(subimage, Tile.WALL);
				//subimage = tileset.getSubimage(col * tileSize, tileSize, tileSize, tileSize);
				//tiles[2][col] = new Tile(subimage, Tile.WALL);
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	public void loadMap() {
		try {
			numCols = 90;
			numRows = 31;
//			world = new WorldBuilder(numRows, numCols)
//					.makeCaves()
//					.build();
			/*
	        Generate a dungeon from random or use a seed:
	        Dungeon d = new Dungeon(50,50);
	        Dungeon d = new Dungeon(50,50,-372208960465762297l);
	        Dungeon d = new Dungeon(50,50,-4966165972393930752l);
	        Dungeon d = new Dungeon(50,50,-5162029599431124909l);
	        Dungeon d = new Dungeon(50,50,-2755630702751861027l);
	        Dungeon d = new Dungeon(50,50,7532888881128992645l);
	        Dungeon d = new Dungeon(50,50,"Hi, Internet!".hashCode());
	        */

	    	world = new DungeonGenerator(numRows,numCols);
	        world.setRoomsize(10);
	        world.setHallsize(8);
	        world.generate();

			width = numCols * tileSize;
			height = numRows * tileSize;

			xmin = GamePanel.WIDTH - width;
			xmax = 0;
			ymin = GamePanel.HEIGHT - height;
			ymax = 0;
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public int getTileSize() { return tileSize; }
	public int getx() { return (int)x; }
	public int gety() { return (int)y; }
	public int getWidth() { return width; }
	public int getHeight() { return height; }

	public int getType(int row, int col) {
		int rc = world.getTile(row, col);
		int r = rc / numTilesAcross;
		int c = rc % numTilesAcross;
		return tiles[r][c].getType();
	}

	public void setPosition(double x, double y) {
		this.x += (x - this.x) * tween;
		this.y += (y - this.y) * tween;

		fixBounds();

		colOffset = (int)-this.x / tileSize;
		rowOffset = (int)-this.y / tileSize;
	}

	private void fixBounds() {
		if (x < xmin) x = xmin;
		if (y < ymin) y = ymin;
		if (x > xmax) x = xmax;
		if (y > ymax) y = ymax;
	}

	public Creature creature(int x, int y){
		for (Creature c : creatures){
			if ((int)c.getx() / tileSize == x / tileSize && (int)c.gety() / tileSize == y / tileSize)
				return c;
		}
		return null;
	}

	public void draw(Graphics2D g) {

		if (tileset != null) { // полнейшее гавно, надо исправить
			numTilesAcross = tileset.getWidth() / tileSize;
			for (int row = rowOffset; row < rowOffset + numRowsToDraw; row++) {

				if (row >= numRows) break;

				for (int col = colOffset; col < colOffset + numColsToDraw; col++) {
					if (col >= numCols) break;
					if (world != null) {
						int rc = world.getTile(row, col);
						int r = rc / numTilesAcross;
						int c = rc % numTilesAcross;

						g.drawImage(tiles[r][c].getImage(), (int)x + col * tileSize, (int)y + row * tileSize, null);
					}

				}
			}
			List<Creature> toDraw = new ArrayList<Creature>(creatures);
			for (Creature creature : toDraw) {
				creature.draw(g);
			}
		}
	}

	public int getNumRows() { return numRows; }
	public int getNumCols() { return numCols; }

	public boolean isGround(int x, int y) {
		return world.getTile(x, y) == Tile.GROUND;
	}

	public void addAtEmptyLocation(Creature creature) {
		int x;
	    int y;

	    do {
	        x = new Random().nextInt(width / tileSize);
	        y = new Random().nextInt(height/ tileSize);
	    }
	    while (!isGround(y, x));

	    creature.setPosition(x * tileSize + tileSize/2, y * tileSize + tileSize/2);
	    creatures.add(creature);
	}

	public void update() {
		List<Creature> toUpdate = new ArrayList<Creature>(creatures);
		for (Creature creature : toUpdate) {
			creature.update(this);
		}
	}

	public void remove(Creature other) {
		creatures.remove(other);
	}
}
