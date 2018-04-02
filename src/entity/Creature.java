package entity;

import tileMap.TileMap;
import tileMap.Tile;

import java.awt.Graphics2D;
import java.util.ArrayList;

/**
 * Базовый класс для всех существ
 *
 * @author Likanich
 *
 */
public class Creature  extends MapObject{

	// AI
	private CreatureAi ai;
	public void setCreatureAi(CreatureAi ai) { this.ai = ai; }

	private String name;
	public String name() { return name; }
	
	// направление движения по лестнице
	protected boolean stairsUp;
	protected boolean stairsDown;

	// характеристики
	protected int health;
	protected int maxHealth;
	protected int fire;
	protected int maxFire;

	private int visionRadius = 20;
	public int visionRadius() { return visionRadius; }
	public boolean canSee(int wx, int wy, int wz) {
		return ai.canSee(wx, wy, wz);
	}

	public int tile(int wx, int wy, int wz) {
		return tileMap.getTile(wx, wy, wz);
	}

	public int getRows() { return tileMap.numRows(); }
	public int getCols() { return tileMap.numCols(); }

	// fireball
	protected boolean firing;
	protected int fireTo;
	protected int fireCost;
	protected ArrayList<FireBall> fireBalls;

	// атака, защита
	private int attackValue;
    public int attackValue() { return attackValue; }
    private int defenseValue;
    public int defenseValue() { return defenseValue; }

    /**
     * Конструктор
     *
     * @param tm		Привязка к миру
     * @param maxHp		Максимальное здоровье
     * @param attack	Величина атаки
     * @param defense	Величина защиты
     */
	public Creature(TileMap tm, String name, int maxHp, int attack, int defense) {
		super(tm);
		tileMap = tm;
		this.name = name;
		this.health = maxHp;
		this.maxHealth = maxHp;
		this.attackValue = attack;
		this.defenseValue = defense;
		fire = maxFire = 2500;
	}

	public int getHealth() { return health; }
	public int getMaxHealth() { return maxHealth; }
	public int getFire() { return fire; }
	public int getMaxFire() { return maxFire; }

	public void setFiring(int i) {
		firing = true;
		fireTo = i;
	}

	public void setStairsDown(boolean b) { stairsDown = b; }
	public void setStaitsUp(boolean b) { stairsUp = b; }

	public void goUp() {
		if (stairsUp) {
			if (tileMap.getTile((int)y/tileSize, (int)x/tileSize, z) == Tile.STAIRS_UP) {
				z--;
				stairsUp = false;
				doAction("поднялся на уровень %d", z+1);
			}
		}
	}

	public void goDown() {
		if (stairsDown) {
			if (tileMap.getTile((int)y/tileSize, (int)x/tileSize, z) == Tile.STAIRS_DOWN) {
				z++;
				stairsDown = false;
				doAction("спустился на уровень %d", z+1);
			}
		}
	}

	public void init() {
		ai.onInit();
	}

	public void update(TileMap world) {
		ai.onUpdate(world);
	}

	public void draw(Graphics2D g) {
		ai.onDraw(g);
	}

	public void attack(Creature other){
        int amount = Math.max(0, attackValue() - other.defenseValue());

        amount = (int)(Math.random() * amount) + 1;

        doAction("нанес %d урона", amount);
        other.modifyHp(-amount);
    }

    public void modifyHp(int amount) {
        health += amount;

        if (health < 1) {
        	doAction("умер");
        	tileMap.remove(this);
        }
    }

    public void notify(String message, Object ... params){
        ai.onNotify(String.format(message, params));
    }

    public void doAction(String message, Object ... params) {
    	int r = 9;
    	for (int ox = -r; ox < r+1; ox++) {
    		for (int oy = -r; oy < r+1; oy++) {
    			if (ox*ox + oy*oy > r*r)
    				continue;
    			Creature other = tileMap.creature((int)x+ox*tileSize, (int)y+oy*tileSize, z);

    			if (other == null) continue;

    			if (other == this)
    				other.notify("Ты " + message + ".", params);
    			else
    				other.notify(String.format("%s %s.", name, message), params);
    		}
    	}
    }
}
















