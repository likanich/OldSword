package world;

import java.awt.Dimension;
import java.awt.Point;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import tileMap.Tile;

/**
 * @version 1
 * @author SorrowComplex
 */
public class DungeonGenerator {

	private final Dimension size;

	private final long seed;
	private final Random rand;

	private int hallsize;
	private int roomsize;

	private int depth;

	/**
	 * Array of tiles in the dungeon
	 *
	 * TODO: Change to an array of bytes TODO: Tileset class to map byte -> tile
	 * type TODO: Method for custom tiles
	 *
	 * Current tile types: " " : Blank space # : Wall P - 3 : Prospect meta-type,
	 * starting point for new features R - 4: Room meta-type, converted to blank
	 * post-generation H - 5: Hall meta-type, converted to blank post-generation E -
	 * 6: Debug type
	 */
	private final int[][][] tiles;

	public int getWidth() {
		return tiles.length;
	}

	public int getHeight() {
		return tiles[0].length;
	}

	public int getDepth() {
		return tiles[0][0].length;
	}

	/**
	 * Constructs a dungeon with the given width, height, and seed
	 * 
	 * @param width
	 * @param height
	 * @param seed
	 */
	public DungeonGenerator(int width, int height, int depth, long seed) {
		this.roomsize = 12;
		this.hallsize = 15;
		this.depth = depth;
		tiles = new int[width][height][depth];
		size = new Dimension(width, height);
		this.seed = seed;
		rand = new Random(this.seed);
	}

	/**
	 * Constructs a dungeon with the given width and height, uses a random seed
	 * 
	 * @param width
	 * @param height
	 */
	public DungeonGenerator(int width, int height, int depth) {
		this(width, height, depth, new Random().nextLong());
	}

	/**
	 * Changes the hall size
	 * 
	 * @param newhallsize
	 */
	public void setHallsize(int newhallsize) {
		this.hallsize = newhallsize;
	}

	/**
	 * Changes the room size
	 * 
	 * @param newroomsize
	 */
	public void setRoomsize(int newroomsize) {
		this.roomsize = newroomsize;
	}

	/**
	 * Generate the dungeon with the current seed/settings
	 */
	public void generate() {
		for (int z = 0; z < depth; z++) {
			// Fill the dungeon with wall tiles
			rectFill(0, 0, z, size.width, size.height, Tile.WALL);
			// Create a starting room
			startingRoom(z);

			// Generate halls/rooms until no prospects remain
			while (hasProspects(z)) {
				hallsGenerate(z);
				roomsGenerate(z);
			}

			// Finalize the dungeon
			cleanUp(z);
		}

		// соединение этажей
		for (int z = 0; z < depth - 1; z++) {
			connectRegionsDown(z);
		}
	}

	/**
	 * Makes a starting room. Override this to customize
	 */
	public void startingRoom(int z) {
		int center = size.width / 2;
		rectFill(center - 2, 1, z, 3, 3, 4);

		placeIfWall(center - 1, 4, z, 3);
	}

	/**
	 * Output the dungeon to the console
	 */
	// public void draw()
	// {
	// for( int y = 0; y < size.height; y++ )
	// {
	// for( int x = 0; x < size.width; x++ )
	// {
	// System.out.print( tiles[x][y] );
	// }
	// System.out.println();
	// }
	//
	// System.out.println( seed );
	// }

	/**
	 * Checks an area non-wall or non-prospect tiles
	 * 
	 * @param x
	 * @param y
	 * @param w
	 * @param h
	 * @return true if the area is suitable for feature generation
	 */
	protected boolean rectCheck(int x, int y, int z, int w, int h) {
		for (int ya = y; ya < y + h; ya++) {
			for (int xa = x; xa < x + w; xa++) {
				int tile;
				try {
					tile = tiles[xa][ya][z];
				} catch (ArrayIndexOutOfBoundsException e) {
					return false;
				}

				if ((3 != tile) && (Tile.WALL != tile)) // TODO: hz
				{
					return false;
				}
			}
		}

		return true;
	}

	/**
	 * Fills a rectangular area with a tile
	 * 
	 * @param x
	 * @param y
	 * @param w
	 * @param h
	 * @param tile
	 */
	protected void rectFill(int x, int y, int z, int w, int h, int tile) {
		for (int ya = y; ya < y + h; ya++) {
			for (int xa = x; xa < x + w; xa++) {
				tiles[xa][ya][z] = tile;
			}
		}
	}

	/**
	 * Places a single tile
	 * 
	 * @param x
	 * @param y
	 * @param tile
	 */
	protected void placeTile(int x, int y, int z, int tile) {
		tiles[x][y][z] = tile;
	}

	/**
	 * Replaces a wall with given tile
	 * 
	 * @param x
	 * @param y
	 * @param tile
	 */
	private void placeIfWall(int x, int y, int z, int tile) {
		if (Tile.WALL == tiles[x][y][z]) {
			tiles[x][y][z] = tile;
		}
	}

	/**
	 * Replaces an empty space with given tile
	 * 
	 * @param x
	 * @param y
	 * @param tile
	 */
	private void placeIfEmpty(int x, int y, int z, int tile) {
		if (isEmpty(tiles[x][y][z])) {
			tiles[x][y][z] = tile;
		}
	}

	/**
	 * Determines if the given tile type is considered empty. Empty tiles are either
	 * blank, or a meta-type that becomes blank
	 * 
	 * @param tiles2
	 * @return
	 */
	private boolean isEmpty(int tiles2) {
		if (Tile.UNUSED == tiles2) {
			return true;
		}

		if (tiles2 == 5) {
			return true;
		}

		return tiles2 == 4;
	}

	/**
	 * Scans for prospects
	 * 
	 * @return true if prospect tiles are found
	 */
	private boolean hasProspects(int z) {
		for (int y = 0; y < size.height; y++) {
			for (int x = 0; x < size.width; x++) {
				if (tiles[x][y][z] == 3) {
					return true;
				}
			}
		}

		return false;
	}

	/**
	 * Retrieve a list of prospects
	 * 
	 * @return a list of prospect coords as Points
	 */
	private List<Point> getProspects(int z) {
		ArrayList<Point> prospects = new ArrayList<>();

		for (int y = 0; y < size.height; y++) {
			for (int x = 0; x < size.width; x++) {
				if (tiles[x][y][z] == 3) {
					prospects.add(new Point(x, y));
				}
			}
		}

		return prospects;
	}

	/**
	 * Generates halls stemming from all existing prospects
	 */
	private void hallsGenerate(int z) {
		getProspects(z).stream().forEach((p) -> {
			try {
				hallMake(p.x, p.y, z);
			} catch (ArrayIndexOutOfBoundsException e) {
				tiles[p.x][p.y][z] = Tile.WALL;
			}
		});
	}

	/**
	 * Attempts to make a single hall from the point given
	 * 
	 * @param x
	 * @param y
	 */
	private void hallMake(int x, int y, int z) {
		if (isEmpty(tiles[x - 1][y][z]) && isEmpty(tiles[x + 1][y][z])) {
			tiles[x][y][z] = 5;
			return;
		}

		if (isEmpty(tiles[x][y - 1][z]) && isEmpty(tiles[x][y + 1][z])) {
			tiles[x][y][z] = 5;
			return;
		}

		if (isEmpty(tiles[x - 1][y][z])) {
			hallMakeEastbound(x, y, z, rand.nextInt(this.hallsize));
			return;
		}

		if (isEmpty(tiles[x + 1][y][z])) {
			hallMakeWestbound(x, y, z, rand.nextInt(this.hallsize));
			return;
		}

		if (isEmpty(tiles[x][y - 1][z])) {
			hallMakeSouthbound(x, y, z, rand.nextInt(this.hallsize));
			return;
		}

		if (isEmpty(tiles[x][y + 1][z])) {
			hallMakeNorthbound(x, y, z, rand.nextInt(this.hallsize));
			return;
		}

		tiles[x][y][z] = 4;
	}

	/**
	 * Attempts to make a single eastbound hall from the point given
	 * 
	 * @param x
	 * @param y
	 * @param length
	 *            maximum length of this hall
	 */
	private void hallMakeEastbound(int x, int y, int z, int length) {
		if (rectCheck(x, y - 1, z, length, 3)) {
			rectFill(x, y, z, length, 1, 5);
			placeIfEmpty(x + (length - 1), y, z, 3);

			// TODO: Branches
		} else {
			// TODO: Recursive growth
			tiles[x][y][z] = Tile.WALL;
		}
	}

	/**
	 * Attempts to make a single westbound hall from the point given
	 * 
	 * @param x
	 * @param y
	 * @param length
	 */
	private void hallMakeWestbound(int x, int y, int z, int length) {
		if (rectCheck(x - length, y - 1, z, length, 3)) {
			rectFill(x - (length - 1), y, z, length, 1, 5);
			placeIfEmpty(x - (length - 1), y, z, 3);

			if (length >= 3) {
				int sbranch = rand.nextInt(this.hallsize);
				if (sbranch > 1 && sbranch < length - 1) {
					placeIfEmpty(x - sbranch, y + 1, z, 3);
					hallMakeSouthbound(x - sbranch, y + 1, z, rand.nextInt(hallsize));
				}

				int nbranch = rand.nextInt(this.hallsize);
				if (nbranch > 1 && nbranch < length - 1) {
					placeIfEmpty(x - nbranch, y - 1, z, 3);
					hallMakeNorthbound(x - nbranch, y - 1, z, rand.nextInt(hallsize));
				}
			}

		} else {
			if (length > 0) {
				hallMakeWestbound(x, y, z, length - 1);
			} else {
				tiles[x][y][z] = Tile.WALL;
			}
		}
	}

	/**
	 * Attempts to make a single southbound hall from the point given
	 * 
	 * @param x
	 * @param y
	 * @param length
	 */
	private void hallMakeSouthbound(int x, int y, int z, int length) {
		if (rectCheck(x - 1, y, z, 3, length + 1)) {
			rectFill(x, y, z, 1, length, 5);
			tiles[x][y + (length - 1)][z] = 3;
			// TODO: Branches
		} else {
			// TODO: Recursive growth
			tiles[x][y][z] = Tile.WALL;
		}
	}

	/**
	 * Attempts to make a single northbound hall from the point given
	 * 
	 * @param x
	 * @param y
	 * @param length
	 */
	private void hallMakeNorthbound(int x, int y, int z, int length) {
		if (rectCheck(x - 1, y - length, z, 3, length + 1)) {
			rectFill(x, y - (length - 1), z, 1, length, 5);
			tiles[x][y - (length - 1)][z] = 3;
			// TODO: Branches
		} else {
			// TODO: Recursive growth
			tiles[x][y][z] = Tile.WALL;
		}
	}

	/**
	 * Generates a room from all prospects
	 */
	private void roomsGenerate(int z) {
		getProspects(z).stream().forEach((p) -> {
			try {
				roomMake(p.x, p.y, z);
			} catch (ArrayIndexOutOfBoundsException e) {

			}
		});
	}

	/**
	 * Attempts to make a room from the given point
	 * 
	 * @param x
	 * @param y
	 */
	private void roomMake(int x, int y, int z) {
		if (isEmpty(tiles[x - 1][y][z]) && isEmpty(tiles[x + 1][y][z])) {
			tiles[x][y][z] = 4;
			return;
		}

		if (isEmpty(tiles[x][y - 1][z]) && isEmpty(tiles[x][y + 1][z])) {
			tiles[x][y][z] = 4;
			return;
		}

		int w = 3 + rand.nextInt(this.roomsize);
		int h = 3 + rand.nextInt(this.roomsize);

		if (isEmpty(tiles[x][y - 1][z])) {
			roomMakeSouthbound(x, y, z, w, h);
			return;
		}

		if (isEmpty(tiles[x - 1][y][z])) {
			roomMakeEastbound(x, y, z, w, h);
			return;
		}

		if (isEmpty(tiles[x + 1][y][z])) {
			roomMakeWestbound(x, y, z, w, h);
			return;
		}

		if (isEmpty(tiles[x][y + 1][z])) {
			roomMakeNorthbound(x, y, z, w, h);
			return;
		}

		tiles[x][y][z] = Tile.WALL;
	}

	/**
	 * Attempts to make a southbound room from the given point
	 * 
	 * @param x
	 * @param y
	 * @param w
	 * @param h
	 */
	private void roomMakeSouthbound(int x, int y, int z, int w, int h) {
		int wc = w / 2;
		int hc = h / 2;

		int xorig = x - wc;
		int yorig = y + 1;

		if (rectCheck(xorig - 1, y, z, w + 1, h + 1)) {
			tiles[x][y][z] = 5;
			rectFill(xorig, yorig, z, w, h, 4);

			// TODO: Fluctuate hall placement
			placeIfWall(xorig + wc, yorig + h, z, 3);
			placeIfWall(xorig - 1, yorig + hc, z, 3);
			placeIfWall(xorig + w, yorig + hc, z, 3);
		} else {
			// TODO: Recursive growth
			tiles[x][y][z] = Tile.WALL;
		}

	}

	/**
	 * Attempts to make an eastbound room from the given point
	 * 
	 * @param x
	 * @param y
	 * @param w
	 * @param h
	 */
	private void roomMakeEastbound(int x, int y, int z, int w, int h) {
		int wc = w / 2;
		int hc = h / 2;

		int xorig = x + 1;
		int yorig = y - hc;

		if (rectCheck(xorig, yorig - 2, z, w + 1, h + 1)) {
			tiles[x][y][z] = 5;
			rectFill(xorig, yorig, z, w, h, 4);

			// TODO: Fluctuate hall placement
			placeIfWall(xorig + wc, yorig - 1, z, 3);
			placeIfWall(xorig + wc, y + hc, z, 3);
			placeIfWall(xorig + w, y, z, 3);
		} else {
			if (w > 3 && h > 3) {
				roomMakeEastbound(x, y, z, w - 1, h - 1);
			} else {
				tiles[x][y][z] = Tile.WALL;
			}
		}
	}

	/**
	 * Attempts to make a westbound room from the given point
	 * 
	 * @param x
	 * @param y
	 * @param w
	 * @param h
	 */
	private void roomMakeWestbound(int x, int y, int z, int w, int h) {

		int hc = h / 2;
		int wc = w / 2;

		int xorig = x - w;
		int yorig = y - hc;

		if (rectCheck(xorig - 1, yorig - 1, z, w + 1, h + 1)) {
			tiles[x][y][z] = 5;
			rectFill(xorig, yorig, z, w, h, 4);

			// TODO: Fluctuate hall placement
			placeIfWall(xorig + wc, yorig - 1, z, 3);
			placeIfWall(xorig + wc, y + hc, z, 3);
			placeIfWall(xorig - 1, y, z, 3);
		} else {
			// TODO: Recursive growth
			tiles[x][y][z] = Tile.WALL;
		}
	}

	/**
	 * Attempts to make a northbound room from the given point
	 * 
	 * @param x
	 * @param y
	 * @param w
	 * @param h
	 */
	private void roomMakeNorthbound(int x, int y, int z, int w, int h) {

		int wc = w / 2;
		int hc = h / 2;

		int xorig = x - wc;
		int yorig = y - h;

		if (rectCheck(xorig - 1, yorig - 1, z, w + 1, h + 1)) {
			tiles[x][y][z] = 5;
			rectFill(xorig, yorig, z, w, h, 4);

			// TODO: Fluctuate hall placement
			placeIfWall(x, yorig - 1, z, 3);
			placeIfWall(xorig - 1, y - hc, z, 3);
			placeIfWall(xorig + w, y - hc, z, 3);
		} else {
			tiles[x][y][z] = Tile.WALL;
		}
	}

	/**
	 * Performs a series of clean up tasks.
	 *
	 * - Removes leftover prospects - Fixes issues with halls - Puts doors where
	 * needed - Removes meta tiles
	 */
	private void cleanUp(int z) {
		getProspects(z).stream().forEach((p) -> {
			tiles[p.x][p.y][z] = Tile.WALL;
		});

		fixOneBlockDeadEnds(z);
		// fixOneBlockHalls();

		// makeDoors();

		removeDeadEnds(z);

		removeMeta(z);

		removeEmptyWalls(z);

		changeWalls(z);
	}

	private void removeEmptyWalls(int z) {
		for (int x = 0; x < size.width; x++) {
			for (int y = 0; y < size.height; y++) {
				int tile = tiles[x][y][z];
				if (tile == Tile.WALL) {
					int check = 0;
					if (x == 0)
						check++;
					else if (tiles[x - 1][y][z] == Tile.WALL || tiles[x - 1][y][z] == Tile.UNUSED)
						check++;

					if (x == 0 || y == 0)
						check++;
					else if (tiles[x - 1][y - 1][z] == Tile.WALL || tiles[x - 1][y - 1][z] == Tile.UNUSED)
						check++;

					if (x == 0 || y == size.height - 1)
						check++;
					else if (tiles[x - 1][y + 1][z] == Tile.WALL || tiles[x - 1][y + 1][z] == Tile.UNUSED)
						check++;

					if (y == 0)
						check++;
					else if (tiles[x][y - 1][z] == Tile.WALL || tiles[x][y - 1][z] == Tile.UNUSED)
						check++;

					if (y == 0 || x == size.width - 1)
						check++;
					else if (tiles[x + 1][y - 1][z] == Tile.WALL || tiles[x + 1][y - 1][z] == Tile.UNUSED)
						check++;

					if (y == size.height - 1)
						check++;
					else if (tiles[x][y + 1][z] == Tile.WALL || tiles[x][y + 1][z] == Tile.UNUSED)
						check++;

					if (x == size.width - 1)
						check++;
					else if (tiles[x + 1][y][z] == Tile.WALL || tiles[x + 1][y][z] == Tile.UNUSED)
						check++;

					if (y == size.height - 1 || x == size.width - 1)
						check++;
					else if (tiles[x + 1][y + 1][z] == Tile.WALL || tiles[x + 1][y + 1][z] == Tile.UNUSED)
						check++;
					if (check >= 8) {
						tiles[x][y][z] = Tile.UNUSED;
					}
				}
			}
		}
	}

	private void removeDeadEnds(int z) {
		{
			boolean flag;
			do {
				flag = false;
				for (int x = 0; x < size.width; x++) {
					for (int y = 0; y < size.height; y++) {
						int tile = tiles[x][y][z];
						if (tile == 5) {
							int check = 0;

							if (tiles[x - 1][y][z] == Tile.WALL)
								check++;
							if (tiles[x + 1][y][z] == Tile.WALL)
								check++;
							if (tiles[x][y + 1][z] == Tile.WALL)
								check++;
							if (tiles[x][y - 1][z] == Tile.WALL)
								check++;
							if (check == 3) {
								tiles[x][y][z] = Tile.WALL;
								flag = true;
							}
						}
					}
				}
			} while (flag);
		}
	}

	/**
	 * Removes one-block halls that don't lead to rooms
	 */
	private void fixOneBlockDeadEnds(int z) {
		for (int x = 0; x < size.width; x++)
			for (int y = 0; y < size.height; y++) {
				int tile = tiles[x][y][z];
				if (tile == 5) {
					// Westbound
					if (tiles[x + 1][y][z] == 4 && tiles[x - 1][y][z] == Tile.WALL) {
						tiles[x][y][z] = Tile.WALL;
					}

					// Eastbound
					if (tiles[x - 1][y][z] == 4 && tiles[x + 1][y][z] == Tile.WALL) {
						tiles[x][y][z] = Tile.WALL;
					}

					// Southbound
					if (tiles[x][y - 1][z] == 4 && tiles[x][y + 1][z] == Tile.WALL) {
						tiles[x][y][z] = Tile.WALL;
					}

					// Northbound
					if (tiles[x][y + 1][z] == 4 && tiles[x][y - 1][z] == Tile.WALL) {
						tiles[x][y][z] = Tile.WALL;
					}
				}
			}
	}

	private void changeWalls(int z) {
		for (int x = 0; x < size.width; x++)
			for (int y = 0; y < size.height; y++) {
				if (tiles[x][y][z] == Tile.WALL || tiles[x][y][z] == Tile.ERROR) {
					String string = "";
					if (x < size.width - 1 && y < size.height - 1 && tiles[x + 1][y + 1][z] == Tile.EARTH)
						string += "UL";
					if (x < size.width - 1 && tiles[x + 1][y][z] == Tile.EARTH)
						string += "UU";
					if (x < size.width - 1 && y != 0 && tiles[x + 1][y - 1][z] == Tile.EARTH)
						string += "UR";
					if (y < size.height - 1 && tiles[x][y + 1][z] == Tile.EARTH)
						string += "LL";
					if (y != 0 && tiles[x][y - 1][z] == Tile.EARTH)
						string += "RR";
					if (x != 0 && y < size.height - 1 && tiles[x - 1][y + 1][z] == Tile.EARTH)
						string += "DL";
					if (x != 0 && tiles[x - 1][y][z] == Tile.EARTH)
						string += "DD";
					if (x != 0 && y != 0 && tiles[x - 1][y - 1][z] == Tile.EARTH)
						string += "DR";

					if (string.equals("UL"))
						tiles[x][y][z] = Tile.WALL_UL;
					else if (string.equals("UR"))
						tiles[x][y][z] = Tile.WALL_UR;
					else if (string.equals("DL"))
						tiles[x][y][z] = Tile.WALL_DL;
					else if (string.equals("DR"))
						tiles[x][y][z] = Tile.WALL_DR;
					else if (string.equals("URRRDR") || string.equals("RRDR") || string.equals("URRR")
							|| string.equals("RR"))
						tiles[x][y][z] = Tile.WALL_R;
					else if (string.equals("ULLLDL") || string.equals("LLDL") || string.equals("ULLL")
							|| string.equals("LL"))
						tiles[x][y][z] = Tile.WALL_L;
					else if (string.equals("ULUUUR") || string.equals("UUUR") || string.equals("ULUU")
							|| string.equals("UU"))
						tiles[x][y][z] = Tile.WALL_U;
					else if (string.equals("DLDDDR") || string.equals("DDDR") || string.equals("DLDD")
							|| string.equals("DD"))
						tiles[x][y][z] = Tile.WALL_D;
					else if (string.equals("ULUUURLLDL") || string.equals("ULUULLDL") || string.equals("ULUUURLL")
							|| string.equals("UULL"))
						tiles[x][y][z] = Tile.WALL_ULI;
					else if (string.equals("ULUUURRRDR") || string.equals("UUURRRDR") || string.equals("ULUUURRR")
							|| string.equals("UURR"))
						tiles[x][y][z] = Tile.WALL_URI;
					else if (string.equals("ULLLDLDDDR") || string.equals("LLDLDDDR") || string.equals("ULLLDLDD")
							|| string.equals("LLDD"))
						tiles[x][y][z] = Tile.WALL_DLI;
					else if (string.equals("URRRDLDDDR") || string.equals("RRDLDDDR") || string.equals("URRRDDDR")
							|| string.equals("RRDD"))
						tiles[x][y][z] = Tile.WALL_DRI;

					else if (string.equals("ULUUURLLDLDDDR") || string.equals("ULUUURRRDLDDDR")
							|| string.equals("ULURLLRRDLDDDR") || string.equals("ULUUURLLRRDLDR")
							|| string.equals("ULUULLDLDDDR") || string.equals("UUURRRDLDDDR")
							|| string.equals("URLLRRDLDDDR") || string.equals("ULUUURLLRRDR")
							|| string.equals("ULUUURLLDLDD") || string.equals("ULUUURRRDDDR")
							|| string.equals("ULLLRRDLDDDR") || string.equals("ULUUURLLRRDL")
							|| string.equals("ULUULLDLDD") || string.equals("UUURRRDDDR") || string.equals("LLRRDLDDDR")
							|| string.equals("ULUUURLLRR")) {
						tiles[x][y][z] = Tile.EARTH;
						x = 0;
						y = 0;
					}

					else
						tiles[x][y][z] = Tile.ERROR;
				}
			}
	}

	/**
	 * Finds one-block halls that connect rooms and converts them to doors
	 */
	// private void fixOneBlockHalls()
	// {
	// for( int x = 0; x < size.width; x++ )
	// for( int y = 0; y < size.height; y++ )
	// {
	// String tile = tiles[x][y];
	// if( tile == 5 )
	// {
	// // Westbound
	// if( 4.equals(tiles[x+1][y]) && 4.equals(tiles[x-1][y]) )
	// {
	// tiles[x][y] = "D";
	// }
	//
	// // Eastbound
	// if( 4.equals(tiles[x-1][y]) && 4.equals(tiles[x+1][y]) )
	// {
	// tiles[x][y] = "D";
	// }
	//
	// // Southbound
	// if( 4.equals(tiles[x][y-1]) && 4.equals(tiles[x][y+1]) )
	// {
	// tiles[x][y] = "D";
	// }
	//
	// // Northbound
	// if( 4.equals(tiles[x][y+1]) && 4.equals(tiles[x][y-1]) )
	// {
	// tiles[x][y] = "D";
	// }
	// }
	// }
	// }

	/**
	 * Places doors between rooms and halls
	 */
	// private void makeDoors()
	// {
	// for( int x = 0; x < size.width; x++ )
	// for( int y = 0; y < size.height; y++ )
	// {
	// String tile = tiles[x][y];
	// if( 5.equals(tile) )
	// {
	// // Southbound
	// if( 4.equals(tiles[x][y-1]) && 5.equals(tiles[x][y+1]) )
	// {
	// tiles[x][y] = "D";
	// }
	//
	// // Northbound
	// if( 4.equals(tiles[x][y+1]) && 5.equals(tiles[x][y-1]) )
	// {
	// tiles[x][y] = "D";
	// }
	//
	// // Westbound
	// if( 4.equals(tiles[x+1][y]) && 5.equals(tiles[x-1][y]) )
	// {
	// tiles[x][y] = "D";
	// }
	//
	// // Eastbound
	// if( 4.equals(tiles[x-1][y]) && 5.equals(tiles[x+1][y]) )
	// {
	// tiles[x][y] = "D";
	// }
	// }
	// }
	// }

	/**
	 * Converts existing hall/room meta tiles to open spaces
	 */
	private void removeMeta(int z) {
		for (int x = 0; x < size.width; x++)
			for (int y = 0; y < size.height; y++) {
				int tile = tiles[x][y][z];
				if (tile == 5 || tile == 4) {
					tiles[x][y][z] = Tile.EARTH;
				}
			}
	}

	public int getTile(int x, int y, int oz) {
		if (x >= tiles.length || y > tiles[x].length)
			return Tile.WALL;
		return tiles[x][y][oz];
	}

	private void connectRegionsDown(int z) {
		ArrayList<world.Point> candidates = new ArrayList<world.Point>();

		for (int x = 0; x < size.width; x++) {
			for (int y = 0; y < size.height; y++) {
				if (tiles[x][y][z] == Tile.EARTH && tiles[x][y][z + 1] == Tile.EARTH) {
					candidates.add(new world.Point(x, y, z));
				}
			}
		}

		Collections.shuffle(candidates);
		world.Point p = candidates.remove(0);
		tiles[p.x][p.y][z] = Tile.STAIRS_DOWN;
		tiles[p.x][p.y][z + 1] = Tile.STAIRS_UP;
	}
}