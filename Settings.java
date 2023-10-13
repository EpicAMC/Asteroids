/*
Program: AsteroidsChen
File: Settings.java
Author: Andrew Chen 
Purpose: Holds the settings that do not change ingame
*/
import java.awt.Color;

public class Settings {
	public static final boolean debug = false; // Debug Graphics

/// GENERAL GRAPHICS SETTINGS
	public static final int screenHeight = 400;
	public static final int screenWidth = 700;
	public static final Color bgColor = new Color(0, 0, 0);
	public static final Color shipColor = new Color(255, 255, 255);
	public static final Color bulletColor = new Color(255, 20, 100);
	public static final Color bstColor = new Color(255, 255, 255);
	public static final Color hpColor = new Color(230, 0, 0);
	public static final Color astColor = new Color(85, 85, 95);
	public static final Color astInColor = new Color(60, 60, 75);
	public static final Color txtColor = new Color(255, 255, 255);
	public static final Color titleColor = new Color(255, 255, 255);
	public static final Color menuColor = new Color(60, 60, 60);
	public static final Color pbColor = new Color(60, 60, 60);

/// OBJECT GRAPHICS SETTINGS
	// Ship Graphics and Physics Settings
	public static final double fwdAccel = 45;
	public static final double backAccel = 30;
	public static final double angAccel = 0.85;
	public static final double shipMass = 100;
	public static final double shipMOI = 1750;
	public static final double maxVel = 50;
	public static final double maxAngVel = 1;
	public static final double boostVel = 70;
	public static final double transDrag = fwdAccel / (maxVel * maxVel);
	public static final double angDrag = angAccel / (maxAngVel * maxAngVel);

	public static final double shipLength = 32; 
	public static final double shipWidth = 10; 
	public static final double cutLength = 10.5; 

	// Bullet Graphics and Physics Settings Settings
	public static final double bulletVel = 120;
	public static final double bulletMass = 200;
	public static final double bulletMomentum = bulletVel * bulletMass;
	
	public static final double bulletLength = 5;
	public static final double bulletWidth = 1.5;
	
	// Asteroid Graphics and Physics Settings
	public static final double baseMass = 10000; // MOI should be 1/3 * Mass * Side ^ 2
	public static final double startVel = 10;
	public static final double rangeVel = 1;
	
	public static final double baseSide = 50;
	public static final double[] sizeScale = {1.00, 1.33, 1.66, 2.00, 2.33, 2.66, 3.00};

/// GAME INTERACTION SETTINGS
	// Ship
	public static final double shipMaxHP = 100;
	public static final long collisionImmunity = 2000;

	// Bullet
	public static final int bulletCooldown = 75;
	public static final int bulletReload = 800;
	public static final int reloadAmount = 3;
	public static final int maxAmmo = 30;
	public static final double bulletDMG = 1;

	// Asteroid
	public static final double rampUpCoefficient = 1.02;
	public static final double startOmega = 0.5;
	public static final double rangeOmega = 0.2;
	public static final double[] sizeHP = {10, 25, 55, 75, 115, 150, 200};
	public static final double[] sizeCollideDMG = {1, 1, 2, 4, 6, 8, 10};
	public static final double[] sizeBaseShipDMG = {1, 2, 3, 5, 8, 13, 21};
	public static final long initialGrace = 2000;
	public static final long collisionGrace = 1000;
}