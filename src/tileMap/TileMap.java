package tileMap;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import javax.imageio.ImageIO;

import entity.Creature;
import items.Item;
import main.GamePanel;
import world.DungeonGenerator;

public class TileMap {

	// private final Random rand;

	// positions
	private double x;
	private double y;
	private int z;

	// bounds
	private int xmin;
	private int ymin;
	private int xmax;
	private int ymax;

	private double tween;

	// creatures
	private List<Creature> creatures;
	private List<Item> items;

	// map
	private DungeonGenerator world;
	private int tileSize;
	private int numRows;

	public int numRows() {
		return numRows;
	}

	private int numCols;

	public int numCols() {
		return numCols;
	}

	private int depth;
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
		this.items = new ArrayList<Item>();
		loadMap();
	}

	public void loadTiles(String s) {
		try {
			tileset = ImageIO.read(getClass().getResourceAsStream(s));
			numTilesAcross = tileset.getWidth() / tileSize;
			// numTilesAcross = 1;
			tiles = new Tile[2][numTilesAcross];

			BufferedImage subimage;
			for (int col = 0; col < numTilesAcross; col++) {
				subimage = tileset.getSubimage(col * tileSize, 0, tileSize, tileSize);
				tiles[0][col] = new Tile(subimage, Tile.GROUND);
				subimage = tileset.getSubimage(col * tileSize, tileSize, tileSize, tileSize);
				tiles[1][col] = new Tile(subimage, Tile.WALL);
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void loadMap() {
		try {
			numCols = 90;
			numRows = 31;
			depth = 5;

			world = new DungeonGenerator(numRows, numCols, depth);
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

	public int getTileSize() {
		return tileSize;
	}

	public int getx() {
		return (int) x;
	}

	public int gety() {
		return (int) y;
	}

	public int getWidth() {
		return width;
	}

	public int getHeight() {
		return height;
	}

	public int getType(int row, int col, int depth) {
		int rc = world.getTile(row, col, depth);
		int r = rc / numTilesAcross;
		int c = rc % numTilesAcross;
		return tiles[r][c].getType();
	}

	public int getTile(int row, int col, int depth) {
		return world.getTile(row, col, depth);
	}

	public void setPosition(double x, double y, int z) {
		this.x += (x - this.x) * tween;
		this.y += (y - this.y) * tween;
		this.z = z;

		fixBounds();

		colOffset = (int) -this.x / tileSize;
		rowOffset = (int) -this.y / tileSize;
	}

	private void fixBounds() {
		if (x < xmin)
			x = xmin;
		if (y < ymin)
			y = ymin;
		if (x > xmax)
			x = xmax;
		if (y > ymax)
			y = ymax;
	}

	public Creature creature(int x, int y, int z) {
		for (Creature c : creatures) {
			if (c.getx() / tileSize == x / tileSize && c.gety() / tileSize == y / tileSize && c.getz() == z)
				return c;
		}
		return null;
	}

	public Item item(int x, int y, int z) {
		for (Item i : items) {
			if (i.getx() / tileSize == x / tileSize && i.gety() / tileSize == y / tileSize && i.getz() == z)
				return i;
		}
		return null;
	}

	public void draw(Graphics2D g, Creature player) {

		if (tileset != null) { // полнейшее гавно, надо исправить
			numTilesAcross = tileset.getWidth() / tileSize;
			for (int row = rowOffset; row < rowOffset + numRowsToDraw; row++) {

				if (row >= numRows)
					break;

				for (int col = colOffset; col < colOffset + numColsToDraw; col++) {
					if (col >= numCols)
						break;
					if (world != null) {
						int rc = world.getTile(row, col, player.getz());
						int r = rc / numTilesAcross;
						int c = rc % numTilesAcross;

						g.drawImage(tiles[r][c].getImage(), (int) x + col * tileSize, (int) y + row * tileSize, null);

					}

				}
			}
			List<Item> itemToDraw = new ArrayList<Item>(items);
			for (Item item : itemToDraw) {
				if (item.getz() == z && player.canSee(item.gety() / tileSize, item.getx() / tileSize, player.getz()))
					item.draw(g);
			}
			List<Creature> toDraw = new ArrayList<Creature>(creatures);
			for (Creature creature : toDraw) {
				if (creature.getz() == z
						&& player.canSee(creature.gety() / tileSize, creature.getx() / tileSize, player.getz()))
					creature.draw(g);
			}

		}
	}

	public int getNumRows() {
		return numRows;
	}

	public int getNumCols() {
		return numCols;
	}

	public int getDepth() {
		return depth;
	}

	public boolean isGround(int x, int y, int oz) {
		return world.getTile(x, y, oz) == Tile.GROUND;
	}

	public void addAtEmptyLocation(Creature creature, int oz) {
		int x;
		int y;

		do {
			x = new Random().nextInt(width / tileSize);
			y = new Random().nextInt(height / tileSize);
		} while (!isGround(y, x, oz));

		creature.setPosition(x * tileSize + tileSize / 2, y * tileSize + tileSize / 2, oz);
		creatures.add(creature);
	}

	public void addAtEmptyLocation(Item item, int depth) {
		int x;
		int y;

		do {
			x = new Random().nextInt(width / tileSize);
			y = new Random().nextInt(height / tileSize);
		} while (!isGround(y, x, depth));

		item.setPosition(x * tileSize + tileSize / 2, y * tileSize + tileSize / 2, depth);
		items.add(item);
	}

	public void update(int z) {
		List<Creature> toUpdate = new ArrayList<Creature>(creatures);
		for (Creature creature : toUpdate) {
			if (creature.getz() == z)
				creature.update(this);
		}
	}

	public void remove(Creature other) {
		creatures.remove(other);
	}

	public int getz() {
		return z;
	}

}
