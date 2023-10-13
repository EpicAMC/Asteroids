/*
Program: AsteroidsChen
File: Ship.java
Author: Andrew Chen 
Purpose: Implements the movement, actions, and graphics of the player's ship
*/
import java.awt.geom.*;
import java.awt.*;


public class Ship {
	public static Ship ship = new Ship();

	private long lastUpdate, cooldown, immunity;
	private Asteroid blacklist;

	private double x, y, velocity, acceleration, omega, alpha, mass, MOI, facing;
	
	private double axisX;
	private double axisY;
	private Shape axis; // Line2D
	private Shape hitbox; // Polygon
	private Shape boundbox; // Rectangle2D
	private Shape visionbox; // Rectangle2D
	
	private Shape bound1;
	private Shape bound2;
	private Shape bound3;

	private Ship() {
		Line2D a = (Line2D) bound1;
		// Graphics Variables
		this.lastUpdate = System.currentTimeMillis();
		this.cooldown = System.currentTimeMillis();
		this.immunity = 0;

		// Motion Variables
		this.x = Screen.gameScreen.getWidth() / 2 - Settings.shipLength / 2;
		this.y = Screen.gameScreen.getHeight() / 2 - 30;
		this.velocity = 0;
		this.acceleration = 0;
		this.omega = 0;
		this.alpha = 0;
		this.facing = 0;
		this.mass = Settings.shipMass;
		this.MOI = Settings.shipMOI;

		// Geometry Variables
		this.axisX = this.x + Settings.shipLength * (3.0 / 6.4);
		this.axisY = this.y + Settings.shipWidth / 2.0;
		this.axis = new Line2D.Double(
			this.axisX - 150,
			this.axisY,
			this.axisX + 150,
			this.axisY
		);
		int[] tempX = {
			(int)(this.x), 
			(int)(this.x + Settings.shipLength), 
			(int)(this.x), 
			(int)(this.x + Settings.cutLength)
		};
		int[] tempY = {
			(int)(this.y), 
			(int)(this.y + Settings.shipWidth / 2), 
			(int)(this.y + Settings.shipWidth), 
			(int)(this.y + Settings.shipWidth / 2)
		};
		hitbox = new Polygon(tempX, tempY, 4);
		boundbox = new Rectangle2D.Double(this.x, this.y, Settings.shipLength, Settings.shipWidth);
		this.visionbox = boundbox.getBounds2D();

		// Bounding Points
		this.bound1 = new Line2D.Double(tempX[0], tempY[0], tempX[0], tempY[0]);
		this.bound2 = new Line2D.Double(tempX[1], tempY[1], tempX[1], tempY[1]);
		this.bound3 = new Line2D.Double(tempX[2], tempY[2], tempX[2], tempY[2]);
	}

	public void resetImmunity(Asteroid collision) {
		this.blacklist = collision;
		this.immunity = Settings.collisionImmunity;
	}

	public boolean canCollide(Asteroid collision) {
		if (this.blacklist == collision && this.immunity > 0) return false;
		else return true;
	}

	public void accelerate() {
		this.acceleration = 0;
		this.alpha = 0;
		if (GameState.a) this.alpha -= Settings.angAccel;
		if (GameState.d) this.alpha += Settings.angAccel;
		if (GameState.w) this.acceleration += Settings.fwdAccel;
		if (GameState.s) this.acceleration -= Settings.backAccel;

		this.acceleration -= Settings.transDrag * this.velocity * Math.abs(this.velocity);
		this.alpha -= Settings.angDrag * this.omega * Math.abs(this.omega);

		if (GameState.lShift) {
			this.velocity = Settings.boostVel;
			this.acceleration = 0;
			
			this.omega = 0;
			this.alpha = 0;
		}
	}

	public void rotateTo(double newFacing) {
		AffineTransform t = new AffineTransform();
		t.rotate(newFacing - this.facing, this.axisX, this.axisY);
		transform(t);
		this.facing = newFacing;
	}

	public void shoot() {
		if (System.currentTimeMillis() - this.cooldown >= Settings.bulletCooldown) {
			Rectangle2D bound = hitbox.getBounds2D();
			Bullet.shoot(bound.getCenterX(), bound.getCenterY(), this.facing);
			this.cooldown = System.currentTimeMillis();
		}
	}
	
	public void move() {
		// GETTING MOTION VALUES
		// Time
		long nowUpdate = System.currentTimeMillis();
		this.immunity -= nowUpdate - lastUpdate;
		double time = (double)(nowUpdate - this.lastUpdate) / 1000;
		this.lastUpdate = nowUpdate;

		// Velocity
		double nowVel = this.velocity + this.acceleration * time;
		double avgVel = (this.velocity + nowVel) / 2;
		this.velocity = nowVel;

		// Average Angular Velocity
		double nowOmega = this.omega + this.alpha * time;
		double avgOmega = (this.omega + nowOmega) / 2;
		this.omega = nowOmega;

		// Direction
		double avgFacing = Util.addAngle(facing, time * avgOmega / 2);
		double dirChange = time * avgOmega;
		this.facing += dirChange;

		// WRAP-AROUND
		double testY = Math.sin(facing) * velocity;
		double boundY = this.visionbox.getBounds2D().getY();
		double boundX = this.visionbox.getBounds2D().getX();
		double boundH = this.visionbox.getBounds2D().getHeight();
		double boundL = this.visionbox.getBounds2D().getWidth();
		if (testY < 0 && boundY + boundH < 0) { // Top Edge --> Bottom Edge
			AffineTransform wrap = new AffineTransform();
			wrap.translate(0, (Settings.screenHeight + boundH));
			transform(wrap);
		}
		else if (testY > 0 && boundY > Settings.screenHeight) { // Bottom Edge --> Top Edge
			AffineTransform wrap = new AffineTransform();
			wrap.translate(0, (Settings.screenHeight + boundH) * (-1));
			transform(wrap);
		}
		double testX = Math.cos(facing) * velocity;
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

		// MOTION MOVE
		AffineTransform transform = new AffineTransform();
		transform.rotate(dirChange, this.axisX, this.axisY);
		transform.translate(Math.cos(avgFacing) * avgVel * time, Math.sin(avgFacing) * avgVel * time);
		
		transform(transform);
	}
	
	private void transform(AffineTransform transform) {
		this.hitbox = transform.createTransformedShape(this.hitbox);
		this.visionbox = this.hitbox.getBounds2D();
		this.boundbox = transform.createTransformedShape(this.boundbox);
		this.axis = transform.createTransformedShape(this.axis);
		Rectangle2D temp = this.axis.getBounds2D();
		this.axisX = temp.getCenterX();
		this.axisY = temp.getCenterY();

		this.bound1 = transform.createTransformedShape(this.bound1);
		this.bound2 = transform.createTransformedShape(this.bound2);
		this.bound3 = transform.createTransformedShape(this.bound3);
	}

	public void draw(Graphics g) {
		Graphics2D g2d = (Graphics2D) g;
		g2d.setColor(Settings.shipColor);
		g2d.setStroke(new BasicStroke(1));
		g2d.fill(this.hitbox);

		if (Settings.debug) {
			g2d.setColor(Color.BLUE);
			g2d.draw(this.boundbox);
			g2d.setColor(Color.MAGENTA);
			g2d.draw(this.visionbox);
			g2d.setColor(Color.RED);
			g2d.draw(this.axis);
			g2d.setStroke(new BasicStroke(3));
			g2d.setColor(Color.GREEN);
			g2d.drawLine((int)this.axisX, (int)this.axisY, (int)this.axisX, (int)this.axisY);

			g2d.setColor(Color.YELLOW);
			g2d.draw(this.bound1);
			g2d.draw(this.bound2);
			g2d.draw(this.bound3);
		}
	}

	public double getVel() {return this.velocity;}
	public double getMass() {return this.mass;}
	public double getOmega() {return this.omega;}
	public double getMOI() {return this.MOI;}
	public double getFacing() {return this.facing;}
	public Shape getHitbox() {return this.hitbox;}
	public long getImmunity() {return this.immunity;}

	public Shape[] getBounds() {
		Shape[] bounds = {bound1, bound2, bound3};
		return bounds;
	}

	public void setVel(double n) {this.velocity = n;}
	public void setOmega(double n) {this.omega = n;}
}