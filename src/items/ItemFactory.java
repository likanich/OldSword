package items;

import tileMap.TileMap;

public class ItemFactory {
	private TileMap world;

	public ItemFactory(TileMap world) {
		this.world = world;
	}

	public Item newRock(int depth) {
		Item rock = new Item(world, "rock");
		world.addAtEmptyLocation(rock, depth);
		rock.init();
		return rock;
	}
}
