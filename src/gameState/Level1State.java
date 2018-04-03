package gameState;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.List;

import entity.Creature;
import entity.CreatureFactory;
import items.ItemFactory;
import main.GamePanel;
import tileMap.Background;
import tileMap.StaticImage;
//import tileMap.Background;
import tileMap.TileMap;

public class Level1State extends GameState {

	private List<String> messages;

	private TileMap tileMap;
	private Background bg;
	private StaticImage messagesImage;
	private StaticImage healthBarImage;
	private List<StaticImage> healthImage;
	private Font font;
	private Color color;

	private Creature player;

	private CreatureFactory creatureFactory;
	private ItemFactory itemFactory;

	public Level1State(GameStateManager gsm) {
		this.gsm = gsm;
		color = new Color(128, 0, 0);
		font = new Font("Arial", Font.PLAIN, 12);
		messages = new ArrayList<String>();

		init();
	}

	@Override
	public void init() {
		tileMap = new TileMap(32);
		tileMap.loadTiles("/Tilesets/dungeon.png");

		this.itemFactory = new ItemFactory(tileMap);
		createItems(itemFactory);

		this.creatureFactory = new CreatureFactory(tileMap);
		createCreatures(creatureFactory);

		tileMap.setPosition(player.getx(), player.gety(), player.getz());

		bg = new Background("/Backgrounds/level1.gif", 1);

		messagesImage = new StaticImage("/HUD/messages.gif");
		messagesImage.setPosition(1, GamePanel.HEIGHT - messagesImage.getHeight() - 1);
		font = new Font("/Fonts/BulgariaFantasticaCyr.ttf", Font.PLAIN, 10);
		color = new Color(0, 0, 0);

		healthBarImage = new StaticImage("/HUD/healthBar.gif");
		healthBarImage.setPosition(1, 1);

		healthImage = new ArrayList<StaticImage>(10);

		for (int i = 0; i < 10; i++) {

			healthImage.add(new StaticImage("/HUD/health.gif"));
			healthImage.get(i).setPosition(1 + i * healthImage.get(i).getWidth(), 3);
		}
		messages.add("--Уровень 1--");
	}

	private void createCreatures(CreatureFactory creatureFactory) {
		player = creatureFactory.newPlayer(messages);

		for (int z = 0; z < tileMap.getDepth(); z++) {
			for (int i = 0; i < 8; i++) {
				creatureFactory.newFungus(z);
			}

		}
		for (int i = 0; i < 20; i++) {
			creatureFactory.newBat(0);
		}
	}

	private void createItems(ItemFactory itemFactory) {
		for (int z = 0; z < tileMap.getDepth(); z++) {
			for (int i = 0; i < 8; i++) {
				itemFactory.newRock(z);
			}

		}
	}

	@Override
	public void update() {

		if (player.getHealth() < 1)
			gsm.setState(GameStateManager.LOSESTATE);

		bg.update();
		tileMap.update(player.getz());

		tileMap.setPosition(GamePanel.WIDTH / 2 - player.getx(), GamePanel.HEIGHT / 2 - player.gety(), player.getz());
	}

	@Override
	public void draw(Graphics2D g) {

		// draw bg
		bg.draw(g);

		// draw tilemap
		tileMap.draw(g, player);

		// draw ui
		messagesImage.draw(g);
		/*
		 * healthBarImage.draw(g);
		 *
		 * double health = ((double) player.getHealth() / (double)
		 * player.getMaxHealth()) * 10.0; for (int i = 0; i < (int) health - 1; i++) {
		 * healthImage.get(i).draw(g); }
		 */
		g.drawString(player.getHealth() + "/" + player.getMaxHealth(), 8, 10);
		displayMessages(g, messages);
	}

	private void displayMessages(Graphics2D g, List<String> messages) {

		g.setColor(color);
		g.setFont(font);
		for (int i = 0; i < messages.size(); i++) {
			g.drawString(messages.get(i), 8, GamePanel.HEIGHT - messagesImage.getHeight() + 12 + i * 10);
		}
	}

	@Override
	public void keyPressed(int k) {
		if (k == KeyEvent.VK_A)
			player.setLeft(true);
		if (k == KeyEvent.VK_D)
			player.setRight(true);
		if (k == KeyEvent.VK_W)
			player.setUp(true);
		if (k == KeyEvent.VK_S)
			player.setDown(true);
		if (k == KeyEvent.VK_SHIFT)
			player.setStairsDown(true);
		if (k == KeyEvent.VK_CONTROL)
			player.setStaitsUp(true);
		if (k == KeyEvent.VK_LEFT)
			player.setFiring(1);
		if (k == KeyEvent.VK_RIGHT)
			player.setFiring(2);
		if (k == KeyEvent.VK_UP)
			player.setFiring(3);
		if (k == KeyEvent.VK_DOWN)
			player.setFiring(4);
	}

	@Override
	public void keyReleased(int k) {
		if (k == KeyEvent.VK_A)
			player.setLeft(false);
		if (k == KeyEvent.VK_D)
			player.setRight(false);
		if (k == KeyEvent.VK_W)
			player.setUp(false);
		if (k == KeyEvent.VK_S)
			player.setDown(false);
	}

}
