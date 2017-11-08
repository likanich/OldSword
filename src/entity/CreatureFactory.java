package entity;

import java.util.List;

import tileMap.TileMap;

public class CreatureFactory {
	private TileMap world;

	public CreatureFactory(TileMap world) {
		this.world = world;
	}

	public Creature newPlayer(List<String> messages) {
		Creature player = new Creature(world, 100, 20, 5);
		world.addAtEmptyLocation(player, 0);
		new PlayerAi(player, messages);
		player.init();
		return player;
	}

	public Creature newFungus(int z) {
		// TODO Auto-generated method stub
		Creature fungus = new Creature(world, 10, 0, 0);
		world.addAtEmptyLocation(fungus, z);
		new FungusAi(fungus, this);
		fungus.init();
		return fungus;
	}
}
