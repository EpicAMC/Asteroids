/*
Program: AsteroidsChen
File: Asteroid.java
Author: Andrew Chen 
Purpose: Implements the movement and defines the graphics of the asteroids
*/
import java.util.*;
import java.awt.geom.*;
import java.awt.*;

public class Asteroid {
	public static ArrayList<Asteroid> asteroids = new ArrayList<Asteroid>();
	private static ArrayList<Asteroid> killQueue = new ArrayList<Asteroid>();
	private static ArrayList<Asteroid> addQueue = new ArrayList<Asteroid>();

	private long lastUpdate;
	private long grace;
	private Asteroid blacklist;
	
	private double velocity, omega, mass, facing;
	
	private Shape hitbox;
	private Shape visionbox;
	private Shape spriteBody;
	private Shape spriteInner;
	private int size;
	private double side;

	private Shape bound1;
	private Shape bound2;
	private Shape bound3;
	private Shape bound4;

	private double HP;

	private Asteroid(int s, double x, double y, double f, double v, double o) {
		this.lastUpdate = System.currentTimeMillis();
		this.grace = Settings.initialGrace;
		
		this.size = s;
		this.side = Settings.baseSide * Settings.sizeScale[size];
		this.mass = Settings.baseMass * Settings.sizeScale[size] * Settings.sizeScale[size] * Settings.sizeScale[size];
		this.HP = Settings.sizeHP[size];

		this.velocity = v;
		this.omega = o;
		this.facing = f;

		this.hitbox = new Rectangle2D.Double(x, y, this.side, this.side);
		this.spriteBody = new RoundRectangle2D.Double(
			x - this.side * 1/25, 
			y - this.side * 1/25, 
			this.side + this.side * 2/25, 
			this.side + this.side * 2/25, 
			(this.side + this.side * 2/25) * 5/27, 
			(this.side + this.side * 2/25) * 5/27
		);
		this.spriteInner = new RoundRectangle2D.Double(
			x + this.side * 1/25, 
			y + this.side * 1/25, 
			this.side - this.side * 2/25, 
			this.side - this.side * 2/25, 
			(this.side + this.side * 2/25) * 5/27, 
			(this.side + this.side * 2/25) * 5/27
		);
		this.visionbox = this.spriteBody.getBounds2D();

		this.bound1 = new Line2D.Double(x, y, x, y);
		this.bound2 = new Line2D.Double(x + this.side, y, x + this.side, y);
		this.bound3 = new Line2D.Double(x + this.side, y + this.side, x + this.side, y + this.side);
		this.bound4 = new Line2D.Double(x, y + this.side, x, y + this.side);
	}
	
	/*
	Spawns an asteroid with size @param s
	*/
	public static void spawn(int s) {
		Rectangle2D.Double screenRect = new Rectangle2D.Double(0, 0, Settings.screenWidth, Settings.screenHeight); // Rectangle with dimensions of screen
		
		// Location
		double[] pos = Util.randomRectPos(screenRect);
		if (pos[0] <= 10) {
			pos[0] = pos[0] - Settings.baseSide * Settings.sizeScale[s] * 1.5;
		}
		else if (pos[1] <= 10) {
			pos[1] = pos[1] - Settings.baseSide * Settings.sizeScale[s] * 1.5;
		}
		
		// Facing
		double fBase = Util.toFacing(pos[0], pos[1], screenRect.getCenterX(), screenRect.getCenterY());
		double fVary = Math.random() * Math.PI * 2/5 - Math.PI * 1/5;
		double f = Util.addAngle(fBase, fVary);

		// Velocity
		double v = (Settings.startVel + Math.random() * Settings.rangeVel * 2 - Settings.rangeVel) * GameState.rampUp;

		// Angular Velocity
		double o = Settings.startOmega * Math.random() * Settings.rangeOmega * 2 - Settings.rangeOmega;

		asteroids.add(new Asteroid(s, pos[0], pos[1], f, v, o));
	}

	public void resetGrace(Asteroid collision) {
		this.blacklist = collision;
		this.grace = Settings.collisionGrace;
	}

	public boolean canCollide(Asteroid collision) {
		if (this.blacklist == collision || this.grace > 0) return false;
		else return true;
	}

	/*
	Moves the asteroid based on its motion specifications
	*/
	public void move() {
		// Time
		long nowUpdate = System.currentTimeMillis();
		this.grace = this.grace - (nowUpdate - this.lastUpdate);
		double time = (double)(nowUpdate - this.lastUpdate) / 1000;
		this.lastUpdate = nowUpdate;
		
		// WRAP-AROUND
		double testY = Math.sin(facing) * velocity;
		double testX = Math.cos(facing) * velocity;
		double boundY = this.visionbox.getBounds2D().getY();
		double boundX = this.visionbox.getBounds2D().getX();
		double boundH = this.visionbox.getBounds2D().getHeight();
		double boundL = this.visionbox.getBounds2D().getWidth();
		if (testY < 0 && boundY + boundH < 0) { // Top Edge --> Bottom Edge
			AffineTransform wrap = new AffineTransform();
			wrap.translate(0, (Settings.screenHeight + boundH));
			transform(wrap);
		}
		else if (testY > 0 && boundY > Settings.screenHeight) { // Bottom 	Edge --> Top Edge
			AffineTransform wrap = new AffineTransform();
			wrap.translate(0, (Settings.screenHeight + boundH) * (-1));
			transform(wrap);
		}
		if (testX > 0 && boundX > Settings.screenWidth) { // Right Edge --> Left Edge
			AffineTransform wrap = new AffineTransform();
			wrap.translate((Settings.screenWidth + boundL) * (-1), 0);
			transform(wrap);
		}
		else if (testX < 0 && boundX + boundL < 0) { // Left Edge --> Right Edge
			AffineTransform wrap = new AffineTransform();
			wrap.translate((Settings.screenWidth + boundL), 0);
			transform(wrap);
		}
		

		// Transform
		AffineTransform transform = new AffineTransform();
		transform.rotate(omega * time, this.hitbox.getBounds2D().getCenterX(), this.hitbox.getBounds2D().getCenterY());
		transform.translate(Math.cos(this.facing) * this.velocity * time, Math.sin(this.facing) * this.velocity * time);
		transform(transform);
	}

	/*
	Helper method that transforms the asteroid based on @param transform
	*/
	private void transform(AffineTransform transform) {
		this.hitbox = transform.createTransformedShape(this.hitbox);
		this.visionbox = hitbox.getBounds2D();
		this.spriteBody = transform.createTransformedShape(this.spriteBody);
		this.spriteInner = transform.createTransformedShape(this.spriteInner);

		this.bound1 = transform.createTransformedShape(this.bound1);
		this.bound2 = transform.createTransformedShape(this.bound2);
		this.bound3 = transform.createTransformedShape(this.bound3);
		this.bound4 = transform.createTransformedShape(this.bound4);
	}

	/*
	Kills the asteroid
	*/
	public void kill() {
		killQueue.add(this);
	}

	/*
	Kills all asteroids
	*/
	public static void killAll() {
		for (Asteroid a : killQueue) {
			int index = asteroids.indexOf(a);
			if (index >= 0) {
				asteroids.remove(index);
			}
		}
		for (Asteroid a : addQueue) {
			asteroids.add(a);
		}
		addQueue = new ArrayList<Asteroid>();
	}

	/*
	Damages this asteroid for @param dmg
	*/
	public void damage(double dmg) {
		this.HP -= dmg;
		if (HP <= 0) kill();
		if (this.size > 0 && this.HP <= Settings.sizeHP[this.size - 1]) lowerSize();
	}

	public void lowerSize() {
		if (size <= 0) kill();
		else {
			Asteroid temp = new Asteroid(
				this.size - 1,
				this.hitbox.getBounds2D().getX(),
				this.hitbox.getBounds2D().getY(),
				this.facing,
				this.velocity,
				this.omega
			);
			AffineTransform rotate = new AffineTransform();
			rotate.rotate(this.facing, temp.hitbox.getBounds2D().getCenterX(), temp.hitbox.getBounds2D().getCenterY());
			temp.transform(rotate);
		
			double xOffset = (this.hitbox.getBounds2D().getWidth() - temp.hitbox.getBounds2D().getWidth()) / 2;
			double yOffset = (this.hitbox.getBounds2D().getHeight() - temp.hitbox.getBounds2D().getHeight()) / 2;
			
			AffineTransform translate = new AffineTransform();
			translate.translate(xOffset, yOffset);
			temp.transform(translate);

			addQueue.add(temp);
			kill();
		}
	}

	/*
	Moves all asteroids
	*/
	public static void moveAll() {
		ArrayList<Asteroid> temp = new ArrayList<Asteroid>();
		for (Asteroid a : asteroids) {
			temp.add(a);
		}
		for (Asteroid a : temp) {
			a.move();
		}
		asteroids = temp;
	}

	/*
	Specifications for drawing all asteroids. @param g is passed rom DisplayComponent
	*/
	public static void draw(Graphics g) {
		Graphics2D g2d = (Graphics2D) g;
		for (Asteroid a : asteroids) {
			g2d.setColor(Settings.astColor);
			g2d.setStroke(new BasicStroke(1));
			g2d.fill(a.spriteBody);
			g2d.setColor(Settings.astInColor);
			g2d.fill(a.spriteInner);

			if (Settings.debug) {
				g2d.setColor(Color.RED);
				g2d.draw(a.hitbox);
				g2d.setColor(Color.MAGENTA);
				g2d.draw(a.visionbox);
				g2d.setColor(Color.GREEN);
				g2d.drawLine(
					(int)(a.hitbox.getBounds2D().getCenterX()),
					(int)(a.hitbox.getBounds2D().getCenterY()),
					(int)(a.hitbox.getBounds2D().getCenterX() + Math.cos(a.facing) * a.velocity * 5),
					(int)(a.hitbox.getBounds2D().getCenterY() + Math.sin(a.facing) * a.velocity * 5)
				);

				g2d.setColor(Color.YELLOW);
				g2d.setStroke(new BasicStroke(3));
				g2d.draw(a.bound1);
				g2d.draw(a.bound2);
				g2d.draw(a.bound3);
				g2d.draw(a.bound4);
			}
		}
	}

	// BASIC ACCESSORS
	public int getSize() {return this.size;}
	public double getVel() {return this.velocity;}
	public double getMass() {return this.mass;}
	public double getOmega() {return this.omega;}
	// Lowered moment of inertia by a factor of 10 from normal cube
	public double getMOI() {return this.mass * this.side * this.side / 3 / 10;}
 	public double getFacing() {return this.facing;}
	public Shape getHitbox() {return this.hitbox;}
	public double getSide() {return this.side;}
	public double getCenterX() {return this.hitbox.getBounds2D().getCenterX();}
	public double getCenterY() {return this.hitbox.getBounds2D().getCenterY();}
	public long getGrace() {return this.grace;}
	
	public Shape[] getBounds() {
		Shape[] bounds = {bound1, bound2, bound3, bound4};
		return bounds;
	}

	// BASIC MUTATORS
	public void setVel(double n) {this.velocity = n;}
	public void setOmega(double n) {this.omega = n;}
	public void setFacing(double n) {this.facing = n;}
}