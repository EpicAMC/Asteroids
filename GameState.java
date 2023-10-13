/*
Program: AsteroidsChen
File: GameState.java
Author: Andrew Chen 
Purpose: Keep track of the changing game states
*/

public class GameState {
	// Menu States
	public static boolean active = false; // Game Active Flag
	public static boolean tutorial = false; // Tutorial Displays
	public static boolean input = false; // Leaderboard Input Flag

	// Player Controls
	public static boolean w = false;
	public static boolean a = false;
	public static boolean s = false;
	public static boolean d = false;
	public static boolean lShift = false;
	public static boolean space = false;
	public static boolean enter = false;

	// Dynamic Values
	public static int ammo = Settings.maxAmmo;
	public static double playerHP = Settings.shipMaxHP;
	public static double score = 0;
	public static double rampUp = 1.0;

	public static String username = "Anonymous";
}