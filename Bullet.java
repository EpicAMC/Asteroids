/*
Program: AsteroidsChen
File: Bullet.java
Author: Andrew Chen 
Purpose: Implements the movement and defines the graphics of the bullets
*/
import java.util.*;
import java.awt.geom.*;
import java.awt.*;

public class Bullet {
	public static ArrayList<Bullet> bullets = new ArrayList<Bullet>();
	private static ArrayList<Bullet> killQueue = new ArrayList<Bullet>();
	
	private static long lastReload = 0;

	private long lastUpdate;
	private double velocity, mass, facing;
	private Shape hitbox;

	private Bullet(double x, double y, double f) {
		// GRAPHICS VARIABLES
		this.lastUpdate = System.currentTimeMillis();

		// MOTION VARIABLES
		this.velocity = Settings.bulletVel;
		this.mass = Settings.bulletMass;
		this.facing = f;

		// GEOMETRY VARIABLES
		this.hitbox = new Rectangle2D.Double(x, y, Settings.bulletLength, Settings.bulletWidth);
		AffineTransform rotate = new AffineTransform();
		rotate.rotate(facing, x, y);
		this.hitbox = rotate.createTransformedShape(this.hitbox);
	}

	/*
	Spawns a bullet with the specifications
	@param x and y are the staring x and y coordinates
	@param f is its facing angle
	*/
	public static void shoot(double x, double y, double f) {
		if (GameState.ammo > 0) {
			bullets.add(new Bullet(x, y, f));
			GameState.ammo--;
		}
	}

	/*
	Reloads based on system time
	*/
	public static void reload() {
		if (System.currentTimeMillis() - lastReload > Settings.bulletReload && GameState.ammo < Settings.maxAmmo) {
			GameState.ammo += Settings.reloadAmount;
			lastReload = System.currentTimeMillis();
		}
	}

	/*
	Moves the bullet based on its motion specifications
	*/
	public void move() {
		// Time
		long nowUpdate = System.currentTimeMillis();
		double time = (double)(nowUpdate - this.lastUpdate) / 1000;
		this.lastUpdate = nowUpdate;

		// Transform
		AffineTransform transform = new AffineTransform();
		transform.translate(Math.cos(this.facing) * this.velocity * time, Math.sin(this.facing) * this.velocity * time);
		this.hitbox = transform.createTransformedShape(this.hitbox);

		// Kill Offscreen
		double boundX = this.hitbox.getBounds2D().getX();
		double boundY = this.hitbox.getBounds2D().getY();
		double boundW = this.hitbox.getBounds2D().getWidth();
		double boundH = this.hitbox.getBounds2D().getHeight();

		if (boundY + boundH < 0) kill(); // Top
		if (boundY > Settings.screenHeight) kill(); // Bottom
		if (boundX + boundW < 0) kill(); // Left
		if (boundX > Settings.screenWidth) kill(); // Right
	}

	/*
	Kills the bullet
	*/
	public void kill() {
		killQueue.add(this);
	}

	/*
	Kills all bullets
	*/
	public static void killAll() {
		for (Bullet b : killQueue) {
			int index = bullets.indexOf(b);
			if (index >= 0) {
				bullets.remove(index);
			}
		}
	}

	/*
	Moves all bullets
	*/
	public static void moveAll() {
		ArrayList<Bullet> temp = new ArrayList<Bullet>();
		for (Bullet b : bullets) {
			temp.add(b);
		}
		for (Bullet b : temp) {
			b.move();
		}
		bullets = temp;
	}

	/*
	Speciications for drawing all bullets. @param g is passed from DisplayComponent
	*/
	public static void draw(Graphics g) {
		Graphics2D g2d = (Graphics2D) g;
		for (Bullet b : bullets) {
			g2d.setColor(Settings.bulletColor);
			g2d.setStroke(new BasicStroke(1));
			g2d.fill(b.hitbox);

			if (Settings.debug) {
			g2d.setColor(Color.MAGENTA);
			g2d.draw(b.hitbox.getBounds2D());
			}
		}
	}

	// BASIC ACCESSORS
	public double getX() {return this.hitbox.getBounds2D().getCenterX();}
	public double getY() {return this.hitbox.getBounds2D().getCenterY();}
	public double getVel() {return this.velocity;}
	public double getMass() {return this.mass;}
	public double getFacing() {return this.facing;}
	public Shape getHitbox() {return this.hitbox;}
}