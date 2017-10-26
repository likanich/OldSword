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
	private final int[] numFrames = {
			1, 3, 3, 3, 3
	};

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

	public PlayerAi(Creature creature, List<String> messages) {
		super(creature);
		this.messages = messages;
	}

	public void onInit() {
		creature.width = 32;
		creature.height = 32;
		creature.cwidth = 30;
		creature.cheight = 30;

		creature.moveSpeed = 0.3;
		creature.maxSpeed = 1.6;
		creature.stopSpeed = 0.4;

		// load sprites
		try {

			BufferedImage spritesheet = ImageIO.read(
					getClass().getResourceAsStream(
							"/Sprites/Player/player.png"
							)
					);

			sprites = new ArrayList<BufferedImage[]>();
			for(int i = 0; i < 5; i++) {

				BufferedImage[] bi =
						new BufferedImage[numFrames[i]];

				for(int j = 0; j < numFrames[i]; j++) {
					bi[j] = spritesheet.getSubimage(
							j * creature.width,
							i * creature.height,
							creature.width,
							creature.height
							);

				}

				sprites.add(bi);

			}

		}
		catch(Exception e) {
			e.printStackTrace();
		}

		animation = new Animation();
		currentAction = IDLE;
		animation.setFrames(sprites.get(IDLE));
		animation.setDelay(400);
	}

	private void getNextPosition() {

		// movement
		if(creature.left) {
			creature.dx -= creature.moveSpeed;
			if(creature.dx < -creature.maxSpeed) {
				creature.dx = -creature.maxSpeed;
			}
		}
		else if(creature.right) {
			creature.dx += creature.moveSpeed;
			if(creature.dx > creature.maxSpeed) {
				creature.dx = creature.maxSpeed;
			}
		}
		else {
			if(creature.dx > 0) {
				creature.dx -= creature.stopSpeed;
				if(creature.dx < 0) {
					creature.dx = 0;
				}
			}
			else if(creature.dx < 0) {
				creature.dx += creature.stopSpeed;
				if(creature.dx > 0) {
					creature.dx = 0;
				}
			}
		}
		if(creature.up) {
			creature.dy -= creature.moveSpeed;
			if(creature.dy < -creature.maxSpeed) {
				creature.dy = -creature.maxSpeed;
			}
		}
		else if(creature.down) {
			creature.dy += creature.moveSpeed;
			if(creature.dy > creature.maxSpeed) {
				creature.dy = creature.maxSpeed;
			}
		}
		else {
			if(creature.dy > 0) {
				creature.dy -= creature.stopSpeed;
				if(creature.dy < 0) {
					creature.dy = 0;
				}
			}
			else if(creature.dy < 0) {
				creature.dy += creature.stopSpeed;
				if(creature.dy > 0) {
					creature.dy = 0;
				}
			}
		}
	}

	public void onUpdate(TileMap world) {

		// update position
		getNextPosition();
		creature.checkTileMapCollision();
		creature.setPosition(creature.xtemp, creature.ytemp);

		if(creature.left) {
			if(currentAction != WALKING_LEFT) {
				currentAction = WALKING_LEFT;
				animation.setFrames(sprites.get(WALKING_LEFT));
				animation.setDelay(100);
				creature.width = 30;
			}
			Creature other = world.creature((int)creature.xtemp - creature.cwidth/2, (int)creature.ytemp);
			if (other != null && creature.maxHealth != other.maxHealth && creature.intersects(other)) {
				creature.attack(other);
				creature.dx = 0;
			}
		}

		else if(creature.right) {
			if(currentAction != WALKING_RIGHT) {
				currentAction = WALKING_RIGHT;
				animation.setFrames(sprites.get(WALKING_RIGHT));
				animation.setDelay(100);
				creature.width = 30;
			}
		}
		else if(creature.up) {
			if(currentAction != WALKING_UP) {
				currentAction = WALKING_UP;
				animation.setFrames(sprites.get(WALKING_UP));
				animation.setDelay(100);
				creature.width = 30;
			}
		}
		else if(creature.down) {
			if(currentAction != WALKING_DOWN) {
				currentAction = WALKING_DOWN;
				animation.setFrames(sprites.get(WALKING_DOWN));
				animation.setDelay(100);
				creature.width = 30;
			}
		}
		else {
			if(currentAction != IDLE) {
				currentAction = IDLE;
				animation.setFrames(sprites.get(IDLE));
				animation.setDelay(400);
				creature.width = 30;
			}
		}

		if (animation != null) animation.update();
	}

	public void onDraw(Graphics2D g) {

		creature.setMapPosition();
		// draw player
		if (animation != null) { g.drawImage(animation.getImage(),(int)(creature.x + creature.xmap - creature.width / 2),(int)(creature.y + creature.ymap - creature.height / 2),null);
		}
	}
	
	public void onNotify(String message){
        messages.add(message);
    }
}
