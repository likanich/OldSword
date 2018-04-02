package entity;

import java.awt.Graphics2D;

import tileMap.Tile;
import tileMap.TileMap;
import world.Line;
import world.Point;

public class CreatureAi {
	protected Creature creature;

	public CreatureAi(Creature creature) {
		this.creature = creature;
		this.creature.setCreatureAi(this);
	}

	public void onInit() {}

	public void onEnter() {
		getNextPosition();
		creature.checkTileMapCollision();
	}

	public void onUpdate(TileMap world) {}

	public void onDraw(Graphics2D g) {}

	public void onNotify(String format) {
	}
	
	protected void getNextPosition() {

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
		creature.goUp();
		creature.goDown();
	}

	public boolean canSee(int wx, int wy, int wz) {
		if (creature.z != wz)
			return false;

		int xm = (int)creature.x/creature.tileSize;
		
		int ym = (int)creature.y/creature.tileSize;
		//System.err.println("xm=" + xm + " ym=" + ym + " wx=" + wx + " wy=" + wy);
		if ((xm - wy) * (xm - wy) + (ym - wx)*(ym - wx) > creature.visionRadius()*creature.visionRadius())
			return false;
		
		for (Point p : new Line(xm, ym, wy, wx)) {
			//System.err.print(xm);
			if (p.x >= creature.getCols() || p.y >= creature.getRows() || creature.tile(p.y, p.x, wz) != Tile.WALL || creature.tile(p.y, p.x, wz) == Tile.GROUND || p.x == wx && p.y == wy)
				continue;

			return false;
		}

		return true;
	}
}
