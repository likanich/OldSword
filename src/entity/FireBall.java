package entity;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import javax.imageio.ImageIO;

import tileMap.TileMap;

public class FireBall extends MapObject{
	
	private boolean hit;
	private boolean remove;
	private BufferedImage[] sprites;
	private BufferedImage[] hitSprites;
	private PlayerAi player;
	
	private Animation animation;
	
	public FireBall(TileMap tm, int to, PlayerAi player) {
		super(tm);
		
		moveSpeed = 3.8;
		if(to == 1) {
			dx = -moveSpeed;
			dy = 0;
		}
		else if(to == 2) {
			dx = moveSpeed;
			dy = 0;
		}
		else if(to == 3) {
			dx = 0;
			dy = -moveSpeed;
		}
		else if(to == 4) {
			dx = 0;
			dy = moveSpeed;
		}
				
		width = 30;
		height = 30;
		cwidth = 14;
		cheight = 14;
		
		// load sprites
		try {
			BufferedImage spritesheet = ImageIO.read(getClass().getResourceAsStream("/Sprites/Player/fireball.gif"));
			
			sprites = new BufferedImage[4];
			for(int i = 0; i < sprites.length; i++) {
				sprites[i] = spritesheet.getSubimage(i * width, 0, width, height);
			}
			
			hitSprites = new BufferedImage[4];
			for(int i = 0; i < hitSprites.length; i++) {
				hitSprites[i] = spritesheet.getSubimage(i * width, height, width, height);
			}
			
			animation = new Animation();
			animation.setFrames(sprites);
			animation.setDelay(70);
		}
		catch(Exception e) {
			e.printStackTrace();
		}
		this.player = player;
	}
	
	public void setHit() {
		if(hit) return;
		hit = true;
		animation.setFrames(hitSprites);
		animation.setDelay(70);
		dx = 0;
	}
	
	public boolean shuldRemove() { return remove; }
	
	public void update() {
		checkTileMapCollision();
		Creature other = tileMap.creature((int)xtemp, (int)ytemp, z);
		if (!hit && other != null && other.maxHealth != player.creature.maxHealth && intersects(other)) {
			player.creature.attack(other);
			setHit();
		}
		setPosition(xtemp, ytemp, z);
		
		if(dx == 0 && dy == 0 && !hit) {
			setHit();
		}
		
		animation.update();
		
		if(hit && animation.hasPlayedOnce()) {
			remove = true;
		}
	}
	
	public void draw(Graphics2D g) {
		setMapPosition();
		// draw 
		if (animation != null) { g.drawImage(animation.getImage(),(int)(x + xmap - width / 2),(int)(y + ymap - height / 2),null);
		}
	}
}
