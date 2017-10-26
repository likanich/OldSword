package gameState;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import entity.Creature;
import entity.CreatureFactory;
import main.GamePanel;
import tileMap.Background;
//import tileMap.Background;
import tileMap.TileMap;

public class Level1State extends GameState{

	private List<String> messages;
	
	private TileMap tileMap;
	private Background bg;
	private Font font;
	private Color color;

	private Creature player;

	private CreatureFactory creatureFactory;

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
		//tileMap.loadMap();
		//funguses = new Fungus[8];
		this.creatureFactory = new CreatureFactory(tileMap);
		createCreatures(creatureFactory);
		tileMap.setPosition(player.getx(), player.gety());
		bg = new Background("/Backgrounds/level1.gif", 1);
	}

	private void createCreatures(CreatureFactory creatureFactory) {
		player = creatureFactory.newPlayer(messages);
		for (int i = 0; i < 8; i++) {
			creatureFactory.newFungus();
		}
	}

	@Override
	public void update() {

		bg.update();
		tileMap.update();

		tileMap.setPosition(GamePanel.WIDTH / 2 - player.getx(), GamePanel.HEIGHT / 2 - player.gety());
	}

	@Override
	public void draw(Graphics2D g) {

		//draw bg
		bg.draw(g);

		// draw tilemap
		tileMap.draw(g);
		
		// draw ui
		String stats = String.format(" %3d/%3d hp", player.getHealth(), player.getMaxHealth());
		g.setColor(color);
		g.setFont(font);
		g.drawString(stats, 20, 20);
	}
	
	private void displayMessages(Graphics2D g, List<String> messages) {
		int top = tileMap.getHeight() - messages.size();
		for (int i = 0; i < messages.size(); i++){
			g.drawString(messages.get(i), top + i, 20);
		}
		//messages.clear();
	}

	@Override
	public void keyPressed(int k) {
		if (k == KeyEvent.VK_LEFT) {
			player.setLeft(true);
		}
		if (k == KeyEvent.VK_RIGHT) player.setRight(true);
		if (k == KeyEvent.VK_UP) player.setUp(true);
		if (k == KeyEvent.VK_DOWN) player.setDown(true);
		if (k == KeyEvent.VK_ESCAPE) gsm.setState(GameStateManager.LOSESTATE);
		if (k == KeyEvent.VK_ENTER) gsm.setState(GameStateManager.WINSTATE);
	}

	@Override
	public void keyReleased(int k) {
		if (k == KeyEvent.VK_LEFT) {
			player.setLeft(false);
		}
		if (k == KeyEvent.VK_RIGHT) player.setRight(false);
		if (k == KeyEvent.VK_UP) player.setUp(false);
		if (k == KeyEvent.VK_DOWN) player.setDown(false);

	}

}
