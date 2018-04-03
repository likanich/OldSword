package entity;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Random;

import javax.imageio.ImageIO;

import tileMap.TileMap;

public class BatAi extends CreatureAi {

	// animations
	private ArrayList<BufferedImage[]> sprites;
	private final int[] numFrames = { 3, 3, 3, 3 };
	private int nextGo = 0;

	private static final int WALKING_DOWN = 0;
	private static final int WALKING_LEFT = 1;
	private static final int WALKING_RIGHT = 2;
	private static final int WALKING_UP = 3;

	protected Animation animation;
	protected int currentAction;
	protected int previousAction;

	public BatAi(Creature creature) {
		super(creature);
	}

	@Override
	public void onInit() {
		creature.width = 32;
		creature.height = 24;
		creature.cwidth = 30;
		creature.cheight = 20;

		creature.moveSpeed = 0.2;
		creature.maxSpeed = 0.8;
		creature.stopSpeed = 0.4;

		creature.health = creature.maxHealth = 15;

		// load sprites
		try {

			BufferedImage spritesheet = ImageIO.read(getClass().getResourceAsStream("/Sprites/Enemies/bat.gif"));

			sprites = new ArrayList<BufferedImage[]>();
			for (int i = 0; i < 4; i++) {

				BufferedImage[] bi = new BufferedImage[numFrames[i]];

				for (int j = 0; j < numFrames[i]; j++) {
					bi[j] = spritesheet.getSubimage(j * creature.width, i * creature.height, creature.width,
							creature.height);

				}

				sprites.add(bi);

			}
			creature.setDown(true);

		} catch (Exception e) {
			e.printStackTrace();
		}

		animation = new Animation();
		currentAction = WALKING_DOWN;
		animation.setFrames(sprites.get(WALKING_DOWN));
		animation.setDelay(400);
	}

	private void wander() {

		Random generator = new Random();

		if (nextGo >= generator.nextInt(50) + 30) {
			int goTo = generator.nextInt(4);

			switch (goTo) {
			case 0:
				creature.setDown(true);
				creature.setUp(false);
				break;
			case 1:
				creature.setLeft(true);
				creature.setRight(false);
				break;
			case 2:
				creature.setLeft(false);
				creature.setRight(true);
				break;
			case 3:
				creature.setDown(false);
				creature.setUp(true);
				break;

			default:
				break;
			}
			nextGo = 0;
		}
	}

	@Override
	public void onUpdate(TileMap world) {

		nextGo++;
		wander();

		// update position
		getNextPosition();
		creature.checkTileMapCollision();
		creature.setPosition(creature.xtemp, creature.ytemp, creature.z);

		Creature other = world.creature((int) creature.xtemp - creature.cwidth / 2, (int) creature.ytemp, creature.z);
		if (other != null && creature.name() != other.name() && creature.intersects(other)) {
			creature.attack(other);
		}
		if (creature.left) {
			if (currentAction != WALKING_LEFT) {
				currentAction = WALKING_LEFT;
				animation.setFrames(sprites.get(WALKING_LEFT));
				animation.setDelay(100);
				creature.width = 30;
			}

		}

		else if (creature.right) {
			if (currentAction != WALKING_RIGHT) {
				currentAction = WALKING_RIGHT;
				animation.setFrames(sprites.get(WALKING_RIGHT));
				animation.setDelay(100);
				creature.width = 30;
			}
		} else if (creature.up) {
			if (currentAction != WALKING_UP) {
				currentAction = WALKING_UP;
				animation.setFrames(sprites.get(WALKING_UP));
				animation.setDelay(100);
				creature.width = 30;
			}
		} else if (creature.down) {
			if (currentAction != WALKING_DOWN) {
				currentAction = WALKING_DOWN;
				animation.setFrames(sprites.get(WALKING_DOWN));
				animation.setDelay(100);
				creature.width = 30;
			}
		}

		if (animation != null)
			animation.update();
	}

	@Override
	public void onDraw(Graphics2D g) {

		creature.setMapPosition();
		// draw
		if (animation != null) {
			g.drawImage(animation.getImage(), (int) (creature.x + creature.xmap - creature.width / 2),
					(int) (creature.y + creature.ymap - creature.height / 2), null);
		}
	}
}
