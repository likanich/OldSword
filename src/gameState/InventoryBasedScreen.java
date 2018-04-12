package gameState;

import java.awt.Graphics2D;

import entity.Creature;
import items.Item;

public abstract class InventoryBasedScreen extends GameState {

	protected Creature player;
	private String letters;

	protected abstract String getVerb();

	protected abstract boolean isAcceptable(Item item);

	protected abstract GameState use(Item item);

	public InventoryBasedScreen(Creature player) {
		this.player = player;
		this.letters = "abcdefghijklmnopqrstuvwxyz";
	}

	@Override
	public void init() {
		// TODO Auto-generated method stub

	}

	@Override
	public void update() {
		// TODO Auto-generated method stub

	}

	@Override
	public void draw(Graphics2D g) {
		// TODO Auto-generated method stub

	}

	@Override
	public void keyPressed(int k) {
		// TODO Auto-generated method stub

	}

	@Override
	public void keyReleased(int k) {

	}

}
