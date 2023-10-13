/*
Program: AsteroidsChen
File: Game.java
Author: Andrew Chen 
Purpose: Implements the progression and interactions of the game and menus
*/
import java.util.*;
import java.io.*;
import java.awt.*;

public class Game {
	private static Scanner levelReader = new Scanner(System.in); // Initial Placeholder
	private static ArrayList<Integer> spawnQueue = new ArrayList<Integer>();
	private static long lastUpdate;
	private static long nextSpawn;
	private static long nextLevel;
	private static boolean started = false;

	private static int levelReward = 0;

	private static String line;
	private static String[] level = {"0", "0", "0", "0", "0", "0", "0"};
	
	// Handle Collisions
	public static void checkCollisions() {
		bulletToAst();
		astToAst();
		shipToAst();
	}

	/*
	Handles a bullet to asteroid collision
	*/
	private static void bulletToAst() {
		for (Asteroid a : Asteroid.asteroids) {
			for (Bullet b : Bullet.bullets) {
				if (a.getHitbox().contains(b.getX(), b.getY())) {
					double[] intersect = Util.intersect(a.getCenterX(), a.getCenterY(), -1 / Math.tan(b.getFacing()), b.getX(), b.getY(), Math.tan(b.getFacing()));
					double r = Util.distance(a.getCenterX(), a.getCenterY(), intersect[0], intersect[1]);
					double vChange = Settings.bulletMomentum / (a.getMass() + a.getMOI() / r);
					double wChange = Settings.bulletMomentum - vChange;

					// Implement Velocity Changes
					double[] changeVel = Util.resultant(a.getVel() * a.getMass(), a.getFacing(), b.getVel() * b.getMass(), b.getFacing());
					a.setVel(changeVel[0] / a.getMass());
					a.setFacing(changeVel[1]);

					// Implement Angular Velocity Changes
					if (Util.clockwise(a.getCenterX(), a.getCenterY(), intersect[0], intersect[1], b.getFacing())) {
						double prev = a.getOmega();
						a.setOmega(prev + wChange / a.getMOI());
					}
					else {
						double prev = a.getOmega();
						a.setOmega(prev - wChange / a.getMOI());
					}

					a.damage(Settings.bulletDMG);
					b.kill();
					if (GameState.active) GameState.score += 10 * GameState.rampUp;
				}
			}
		}
	}

	/*
	Handles an asteroid to asteroid collision
	*/
	private static void astToAst() {
		for (Asteroid a : Asteroid.asteroids) {
			Shape[] boundsA = a.getBounds();
			for (Asteroid b : Asteroid.asteroids) {
				Shape[] boundsB = b.getBounds();
				if (!(Asteroid.asteroids.indexOf(a) == Asteroid.asteroids.indexOf(b)) && 
						a.canCollide(b) && b.canCollide(a) && (
					a.getHitbox().contains(boundsB[0].getBounds2D().getCenterX(), boundsB[0].getBounds2D().getCenterY()) ||
					a.getHitbox().contains(boundsB[1].getBounds2D().getCenterX(), boundsB[1].getBounds2D().getCenterY()) ||
					a.getHitbox().contains(boundsB[2].getBounds2D().getCenterX(), boundsB[2].getBounds2D().getCenterY()) ||
					a.getHitbox().contains(boundsB[3].getBounds2D().getCenterX(), boundsB[3].getBounds2D().getCenterY()) ||
					b.getHitbox().contains(boundsA[0].getBounds2D().getCenterX(), boundsA[0].getBounds2D().getCenterY()) ||
					b.getHitbox().contains(boundsA[1].getBounds2D().getCenterX(), boundsA[1].getBounds2D().getCenterY()) ||
					b.getHitbox().contains(boundsA[2].getBounds2D().getCenterX(), boundsA[2].getBounds2D().getCenterY()) ||
					b.getHitbox().contains(boundsA[3].getBounds2D().getCenterX(), boundsA[3].getBounds2D().getCenterY())
				)) {
					// Translational
					double vxai = a.getVel() * Math.cos(a.getFacing()); // Initial X Velocity of a
					double vxbi = b.getVel() * Math.cos(b.getFacing()); // Initial X Velocity of b
					double vyai = a.getVel() * Math.sin(a.getFacing()); // Initial Y Velocity of a
					double vybi = b.getVel() * Math.sin(b.getFacing()); // Initial Y Velocity of b

					double TXTKE = (0.5 * a.getMass() * vxai * vxai + 0.5 * b.getMass() * vxbi * vxbi); // Total X Translational Kinetic Energy
					double TYTKE = (0.5 * a.getMass() * vyai * vyai + 0.5 * b.getMass() * vybi * vybi); // Total Y Translational Kinetic Energy
					double TXTM = a.getMass() * vxai + b.getMass() * vxbi; // Total X Translational Momentum
					double TYTM = a.getMass() * vyai + b.getMass() * vybi; // Total Y Translational Momentum

					double stepConstX = TXTM / b.getMass();
					double stepCoeffX = -a.getMass() / b.getMass();
					double stepConstY = TYTM / b.getMass();
					double stepCoeffY = -a.getMass() / b.getMass();

					double ax = a.getMass() + b.getMass() * stepCoeffX * stepCoeffX;
					double bx = b.getMass() * 2 * stepCoeffX * stepConstX;
					double cx = b.getMass() * stepConstX * stepConstX - TXTKE * 2;
					double ay = a.getMass() + b.getMass() * stepCoeffY * stepCoeffY;
					double by = b.getMass() * 2 * stepCoeffY * stepConstY;
					double cy = b.getMass() * stepConstY * stepConstY - TYTKE * 2;
					
					double lowvx = (-bx - Math.sqrt(bx * bx - 4 * ax * cx)) / (2 * ax);
					double highvx = (-bx + Math.sqrt(bx * bx - 4 * ax * cx)) / (2 * ax);
					double lowvy = (-by - Math.sqrt(by * by - 4 * ay * cy)) / (2 * ay);
					double highvy = (-by + Math.sqrt(by * by - 4 * ay * cy)) / (2 * ay);

					double finvxa; // Final X Velocity of a
					double finvya; // Final Y Velocity of a

					if (Math.abs(vxai - lowvx) > Math.abs(vxai - highvx)) finvxa = lowvx;
					else finvxa = highvx;
					if (Math.abs(vyai - lowvy) > Math.abs(vyai - highvy)) finvya = lowvy;
					else finvya = highvy;

					double finvxb = (TXTM - a.getMass() * finvxa) / b.getMass(); // Final X Velocity of b
					double finvyb = (TYTM - a.getMass() * finvya) / b.getMass(); // Final Y Velocity of b


					// Rotational
					double wai = a.getOmega();
					double wbi = b.getOmega();

					double TRKE = 0.5 * a.getMOI() * wai * wai + 0.5 * b.getMOI() * wbi * wbi;
					double TRM = a.getMOI() * wai + b.getMOI() * wbi;

					double stepConstR = TRM / b.getMOI();
					double stepCoeffR = -a.getMOI() / b.getMOI();

					double ar = a.getMOI() + b.getMOI() * stepCoeffR * stepCoeffR;
					double br = b.getMOI() * 2 * stepCoeffR * stepConstR;
					double cr = b.getMOI() * stepConstR * stepConstR - TRKE * 2;

					double loww = (-br - Math.sqrt(br * br - 4 * ar * cr)) / (2 * ar);
					double highw = (-br + Math.sqrt(br * br - 4 * ar * cr)) / (2 * ar);

					double finwa;
					if (Math.abs(wai - loww) > Math.abs(wai - highw)) finwa = loww;
					else finwa = highw;

					double finwb = (TRM - a.getMOI() * finwa) / b.getMOI();


					a.setVel(Math.sqrt(finvxa * finvxa + finvya * finvya));
					a.setFacing(Util.toFacing(0, 0, finvxa, finvya));
					b.setVel(Math.sqrt(finvxb * finvxb + finvyb * finvyb));
					b.setFacing(Util.toFacing(0, 0, finvxb, finvyb));
					a.setOmega(finwa);
					b.setOmega(finwb);
					
					a.resetGrace(b);
					b.resetGrace(a);
					a.damage(1);
					b.damage(1);

					if (Util.toFacing(0, 0, finvxa, finvya) == -1.0 || Util.toFacing(0, 0, finvxb, finvyb) == -1.0) {
						a.kill();
						b.kill();
					}
				}
			}
		}
	}

	/*
	Handles ship to asteroid collisions
	*/
	private static void shipToAst() {
		Shape[] boundsS = Ship.ship.getBounds();
		for (Asteroid a : Asteroid.asteroids) {
			Shape[] boundsA = a.getBounds();
			double collideX;
			double collideY;
			if (Ship.ship.getHitbox().contains(boundsA[0].getBounds2D().getCenterX(), boundsA[0].getBounds2D().getCenterY())) {
				collideX = boundsA[0].getBounds2D().getCenterX();
				collideY = boundsA[0].getBounds2D().getCenterY();
			}
			else if (Ship.ship.getHitbox().contains(boundsA[1].getBounds2D().getCenterX(), boundsA[1].getBounds2D().getCenterY())) {
				collideX = boundsA[1].getBounds2D().getCenterX();
				collideY = boundsA[1].getBounds2D().getCenterY();
			}
			else if (Ship.ship.getHitbox().contains(boundsA[2].getBounds2D().getCenterX(), boundsA[2].getBounds2D().getCenterY())) {
				collideX = boundsA[2].getBounds2D().getCenterX();
				collideY = boundsA[2].getBounds2D().getCenterY();
			}
			else if (Ship.ship.getHitbox().contains(boundsA[3].getBounds2D().getCenterX(), boundsA[3].getBounds2D().getCenterY())) {
				collideX = boundsA[3].getBounds2D().getCenterX();
				collideY = boundsA[3].getBounds2D().getCenterY();
			}
			else if (a.getHitbox().contains(boundsS[0].getBounds2D().getCenterX(), boundsS[0].getBounds2D().getCenterY())) {
				collideX = boundsS[0].getBounds2D().getCenterX();
				collideY = boundsS[0].getBounds2D().getCenterY();
		 	}
			else if (a.getHitbox().contains(boundsS[1].getBounds2D().getCenterX(), boundsS[1].getBounds2D().getCenterY())) {
				collideX = boundsS[1].getBounds2D().getCenterX();
				collideY = boundsS[1].getBounds2D().getCenterY();
			}
			else if (a.getHitbox().contains(boundsS[2].getBounds2D().getCenterX(), boundsS[2].getBounds2D().getCenterY())) {
				collideX = boundsS[2].getBounds2D().getCenterX();
				collideY = boundsS[2].getBounds2D().getCenterY();
			}
			else {
				collideX = -1;
				collideY = -1;
			}

			if (collideX >= 0 && collideX <= Settings.screenWidth && 
					collideY >= 0 && collideY <= Settings.screenHeight && 
					Ship.ship.canCollide(a)) {
				// Translational
				double vxai = a.getVel() * Math.cos(a.getFacing()); // Initial X Velocity of a
				double vxsi = Ship.ship.getVel() * Math.cos(Ship.ship.getFacing()); // Initial X Velocity of Ship
				double vyai = a.getVel() * Math.sin(a.getFacing()); // Initial Y Velocity of a
				double vysi = Ship.ship.getVel() * Math.sin(Ship.ship.getFacing()); // Initial Y Velocity of Ship

				double TXTKE = (0.5 * a.getMass() * vxai * vxai + 0.5 * Ship.ship.getMass() * vxsi * vxsi); // Total X Translational Kinetic Energy
				double TYTKE = (0.5 * a.getMass() * vyai * vyai + 0.5 * Ship.ship.getMass() * vysi * vysi); // Total Y Translational Kinetic Energy
				double TXTM = a.getMass() * vxai + Ship.ship.getMass() * vxsi; // Total X Translational Momentum
				double TYTM = a.getMass() * vyai + Ship.ship.getMass() * vysi; // Total Y Translational Momentum

				double stepConstX = TXTM / Ship.ship.getMass();
				double stepCoeffX = -a.getMass() / Ship.ship.getMass();
				double stepConstY = TYTM / Ship.ship.getMass();
				double stepCoeffY = -a.getMass() / Ship.ship.getMass();
		
				double ax = a.getMass() + Ship.ship.getMass() * stepCoeffX * stepCoeffX;
				double bx = Ship.ship.getMass() * 2 * stepCoeffX * stepConstX;
				double cx = Ship.ship.getMass() * stepConstX * stepConstX - TXTKE * 2;
				double ay = a.getMass() + Ship.ship.getMass() * stepCoeffY * stepCoeffY;
				double by = Ship.ship.getMass() * 2 * stepCoeffY * stepConstY;
				double cy = Ship.ship.getMass() * stepConstY * stepConstY - TYTKE * 2;
					
				double lowvx = (-bx - Math.sqrt(bx * bx - 4 * ax * cx)) / (2 * ax);
				double highvx = (-bx + Math.sqrt(bx * bx - 4 * ax * cx)) / (2 * ax);
				double lowvy = (-by - Math.sqrt(by * by - 4 * ay * cy)) / (2 * ay);
				double highvy = (-by + Math.sqrt(by * by - 4 * ay * cy)) / (2 * ay);

				double finvxa; // Final X Velocity of a
				double finvya; // Final Y Velocity of a

				if (Math.abs(vxai - lowvx) > Math.abs(vxai - highvx)) finvxa = lowvx;
				else finvxa = highvx;
				if (Math.abs(vyai - lowvy) > Math.abs(vyai - highvy)) finvya = lowvy;
				else finvya = highvy;

				double finvxs = (TXTM - a.getMass() * finvxa) / Ship.ship.getMass(); // Final X Velocity of b
				double finvys = (TYTM - a.getMass() * finvya) / Ship.ship.getMass(); // Final Y Velocity of b

				a.setVel(Math.sqrt(finvxa * finvxa + finvya * finvya));
				a.setFacing(Util.toFacing(0, 0, finvxa, finvya));
				Ship.ship.setVel(Math.sqrt(finvxs * finvxs + finvys * finvys));
				Ship.ship.rotateTo(Util.toFacing(0, 0, finvxs, finvys));
				Ship.ship.resetImmunity(a);

				a.damage(2.0);
				if (GameState.active) GameState.playerHP -= Settings.sizeBaseShipDMG[a.getSize()] *  Math.sqrt(a.getVel()/Settings.startVel);
				System.out.println("Ship hit.");
				if (Util.toFacing(0, 0, finvxa, finvya) == -1.0 || Util.toFacing(0, 0, finvxs, finvys) == -1.0) {
					a.kill();
				}
				if (GameState.playerHP < -5) {
					try {
						gameOver();
					}
					catch (IOException e) {
						System.out.println("Unable to end game!");
					}
				}
			}
		}
	}

	// Handle Game Levels
	public static void progressLevel() {
		if (GameState.active && !GameState.tutorial && !started) {
			lastUpdate = System.currentTimeMillis();
			nextSpawn = System.currentTimeMillis();
			nextLevel = System.currentTimeMillis();
			started = true;
		}
		if (started) {
			if (nextLevel <= System.currentTimeMillis()) {
				GameState.score += levelReward * GameState.rampUp;
				System.out.println("Starting next level:");
				GameState.rampUp *= Settings.rampUpCoefficient;
				queueLevel();
			}
			if (nextSpawn <= System.currentTimeMillis()) {
				System.out.print("Attempting Spawns...");
				for (int i = 0; spawnQueue.size() > 0 && i < 3; i++) {
					System.out.print(". Spawning Size " + spawnQueue.get(0));
					Asteroid.spawn(spawnQueue.get(0));
					spawnQueue.remove(0);
				}
				System.out.println();
				nextSpawn = System.currentTimeMillis() + 4000;
			}
		}
	}

	// Queues the next level
	private static void queueLevel() {
		ArrayList<Integer> newSpawns = new ArrayList<Integer>();
		if (levelReader.hasNextLine()) {
			line = levelReader.nextLine();
			level = line.split(" ");
		}

		System.out.print("Sizes: ");
		for (int i = 0; i < 7; i++) {
			System.out.print("" + level[i] + ", ");
		}
		System.out.println("Cooldown: " + level[7] + "seconds");
		
		for (int i = 0; i < 7; i++) {
			int count = 0;
			count = Integer.parseInt(level[i]);
			while (count > 0) {
				newSpawns.add(i);
				count--;
			}
		}

		Collections.shuffle(newSpawns);
		for (int a : newSpawns) {
			spawnQueue.add(a);
		}
		nextLevel = System.currentTimeMillis() + Integer.parseInt(level[7]) * 1000;
		levelReward = Integer.parseInt(level[7]) * 100;
	}

	// Handle Game Over
	public static void gameOver() throws IOException{
		started = false;
		GameState.active = false;
		GameState.tutorial = false;
		Screen.playButton.setVisible(true);

		leaderboardPrompt((int)GameState.score);

		reset();
	}

	public static void decay() {
		for (Asteroid a : Asteroid.asteroids) {
			a.damage(1);
		}
	}
	
	public static void reset() throws IOException {
		// Level Handler Variables
		levelReader.close();
		levelReader = new Scanner(new File("Levels.txt"));
		String[] resetLevel = {"0", "0", "0", "0", "0", "0", "0"};
		level = resetLevel;

		// Dynamic Variables
		GameState.ammo = Settings.maxAmmo;
		GameState.playerHP = Settings.shipMaxHP;
		GameState.score = 0;
		GameState.rampUp = 1.0;

		GameState.username = "Anonymous";
	}

	// Propmpt for leaderboard input
	public static void leaderboardPrompt(int s) {
		String user = Screen.getUsername("Please enter your name for the leaderboards!");
		Leaderboard.insert(user, s);
	}
}