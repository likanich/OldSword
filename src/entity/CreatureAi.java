package entity;

import java.awt.Graphics2D;

import tileMap.TileMap;

public class CreatureAi {
	protected Creature creature;

	public CreatureAi(Creature creature) {
		this.creature = creature;
		this.creature.setCreatureAi(this);
	}

	public void onInit() {}

	public void onEnter(int x, int y) { }

	public void onUpdate(TileMap world) {}

	public void onDraw(Graphics2D g) {}

	public void onNotify(String format) {
		// TODO Auto-generated method stub
		
	}
}
