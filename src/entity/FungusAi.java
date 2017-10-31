package entity;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import javax.imageio.ImageIO;

import tileMap.TileMap;

public class FungusAi extends CreatureAi{
	private CreatureFactory factory;
	private int spreadcount;
	// animations
	private ArrayList<BufferedImage[]> sprites;
	private final int numFrames = 2;

	// animation actions
	private static final int IDLE = 0;

	protected Animation animation;
	protected int currentAction;
	protected int previousAction;
	protected boolean facingRight;

	public FungusAi( Creature creature, CreatureFactory factory) {
		super(creature);
		this.factory = factory;
	}

	public void onUpdate(TileMap world){
		if (spreadcount < 5 && Math.random() < 0.002)
			spread(world);

		if (animation != null) {
			animation.update();
		}
	}

	private void spread(TileMap world){
		int x = creature.getx() / world.getTileSize() + (int)(Math.random() * 11) - 5;
		int y = creature.gety() / world.getTileSize() + (int)(Math.random() * 11) - 5;

		if (x < 0 || y < 0 || x >= world.getNumCols() || y >= world.getNumRows() || !creature.canEnter(y, x))
			return;
		x = x * world.getTileSize() + world.getTileSize() / 2;
		y = y * world.getTileSize() + world.getTileSize() / 2;
		if (world.creature(x, y) == null) {
			Creature child = factory.newFungus();
			child.x = x;
			child.y = y;
			spreadcount++;
			creature.doAction("spawn a child");
		}
	}

	public void onInit() {
		creature.width = 31;
		creature.height = 38;
		creature.cwidth = 31;
		creature.cheight = 30;

		creature.health = creature.maxHealth = 5;

		// load sprites
		try {

			BufferedImage spritesheet = ImageIO.read(
				getClass().getResourceAsStream(
					"/Sprites/Enemies/fungus.gif"
				)
			);

			sprites = new ArrayList<BufferedImage[]>();
			BufferedImage[] bi = new BufferedImage[numFrames];

			for(int j = 0; j < numFrames; j++) {
				bi[j] = spritesheet.getSubimage(j * creature.width,0,creature.width,creature.height);

			}
			sprites.add(bi);

		}
		catch(Exception e) {
			e.printStackTrace();
		}

		animation = new Animation();
		currentAction = IDLE;
		animation.setFrames(sprites.get(IDLE));
		animation.setDelay(400);

	}

	public void onDraw(Graphics2D g) {

		creature.setMapPosition();
		// draw
		if (animation != null) {
			g.drawImage(animation.getImage(),(int)(creature.x + creature.xmap - creature.width / 2),(int)(creature.y + creature.ymap - creature.height / 2),null);
		}
	}
}
