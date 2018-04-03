package entity;

import java.util.List;

import tileMap.TileMap;

public class CreatureFactory {
	private TileMap world;

	public CreatureFactory(TileMap world) {
		this.world = world;
	}

	public Creature newPlayer(List<String> messages) {
		Creature player = new Creature(world, "Player", 100, 10, 5);
		world.addAtEmptyLocation(player, 0);
		new PlayerAi(player, messages);
		player.init();
		return player;
	}

	public Creature newBat(int depth) {
		Creature bat = new Creature(world, "Летучая мышь", 15, 5, 0);
		world.addAtEmptyLocation(bat, depth);
		new BatAi(bat);
		bat.init();
		return bat;
	}

	public Creature newFungus(int z) {
		// TODO Auto-generated method stub
		Creature fungus = new Creature(world, "Гриб", 10, 0, 0);
		world.addAtEmptyLocation(fungus, z);
		new FungusAi(fungus, this);
		fungus.init();
		return fungus;
	}
}
