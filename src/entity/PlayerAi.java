package entity;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;

import tileMap.TileMap;

public class PlayerAi extends CreatureAi {
	private List<String> messages;

	// animations
	private ArrayList<BufferedImage[]> sprites;
	private final int[] numFrames = { 1, 3, 3, 3, 3, 1 };

	protected Animation animation;
	protected int currentAction;
	protected int previousAction;
	protected boolean facingRight;

	// animation actions
	private static final int IDLE = 0;
	private static final int WALKING_DOWN = 1;
	private static final int WALKING_LEFT = 2;
	private static final int WALKING_RIGHT = 3;
	private static final int WALKING_UP = 4;
	private static final int FIREBALL = 5;

	public PlayerAi(Creature creature, List<String> messages) {
		super(creature);
		this.messages = messages;
	}

	@Override
	public void onInit() {
		creature.width = 32;
		creature.height = 32;
		creature.cwidth = 30;
		creature.cheight = 30;

		creature.moveSpeed = 0.3;
		creature.maxSpeed = 1.6;
		creature.stopSpeed = 0.4;

		creature.fireCost = 200;
		creature.fireBalls = new ArrayList<FireBall>();

		// load sprites
		try {

			BufferedImage spritesheet = ImageIO.read(getClass().getResourceAsStream("/Sprites/Player/player.png"));

			sprites = new ArrayList<BufferedImage[]>();
			for (int i = 0; i < 6; i++) {

				BufferedImage[] bi = new BufferedImage[numFrames[i]];

				for (int j = 0; j < numFrames[i]; j++) {
					bi[j] = spritesheet.getSubimage(j * creature.width, i * creature.height, creature.width,
							creature.height);

				}

				sprites.add(bi);

			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		animation = new Animation();
		currentAction = IDLE;
		animation.setFrames(sprites.get(IDLE));
		animation.setDelay(400);
	}

	@Override
	public void onUpdate(TileMap world) {

		// update position
		getNextPosition();
		creature.checkTileMapCollision();
		creature.setPosition(creature.xtemp, creature.ytemp, creature.z);

		if (currentAction == FIREBALL) {
			if (animation.hasPlayedOnce())
				creature.firing = false;
		}

		// fireball
		creature.fire += 10;
		if (creature.fire > creature.maxFire)
			creature.fire = creature.maxFire;
		if (creature.firing && currentAction != FIREBALL) {
			if (creature.fire > creature.fireCost) {
				creature.fire -= creature.fireCost;
				FireBall fb = new FireBall(world, creature.fireTo, this);
				fb.setPosition(creature.x, creature.y, creature.z);
				creature.fireBalls.add(fb);
			}
		}

		// update fireballs
		for (int i = 0; i < creature.fireBalls.size(); i++) {
			creature.fireBalls.get(i).update();
			if (creature.fireBalls.get(i).shuldRemove()) {
				creature.fireBalls.remove(i);
				i--;
			}
		}

		if (creature.firing) {
			if (currentAction != FIREBALL) {
				currentAction = FIREBALL;
				animation.setFrames(sprites.get(FIREBALL));
				animation.setDelay(100);
				creature.width = 30;
			}
		}

		else if (creature.left) {
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
		} else {
			if (currentAction != IDLE) {
				currentAction = IDLE;
				animation.setFrames(sprites.get(IDLE));
				animation.setDelay(400);
				creature.width = 30;
			}
		}

		if (animation != null)
			animation.update();
	}

	@Override
	public void onDraw(Graphics2D g) {

		creature.setMapPosition();

		// draw fireballs
		for (int i = 0; i < creature.fireBalls.size(); i++) {
			creature.fireBalls.get(i).draw(g);
		}

		// draw player
		if (animation != null) {
			g.drawImage(animation.getImage(), (int) (creature.x + creature.xmap - creature.width / 2),
					(int) (creature.y + creature.ymap - creature.height / 2), null);
		}
	}

	@Override
	public void onNotify(String message) {
		if (messages.size() > 3)
			messages.remove(0);
		messages.add(message);
	}
}
