/*
Program: AsteroidsChen
File: Label.java
Author: Andrew Chen 
Purpose: Defines the graphics for the various labels ingame
*/
import java.awt.*;

public class Label {
	// Draw Title Text
	public static void drawTitle(Graphics g) {
		Graphics2D g2d = (Graphics2D) g;
		g2d.setStroke(new BasicStroke(3));
		g2d.setColor(Settings.txtColor);
		g2d.setFont(new Font("Monospaced", Font.BOLD, 54));

		g2d.drawString("Asteroids", Settings.screenWidth * 55 / 700, Settings.screenHeight * 150 / 400);
	}

	// Draw Tutorial Text
	public static void drawTutorial(Graphics g) {
		Graphics2D g2d = (Graphics2D) g;
		g2d.setStroke(new BasicStroke(3));
		g2d.setColor(Settings.txtColor);
		g2d.setFont(new Font("Monospaced", Font.BOLD, 20));

		g2d.drawString("w - Move Forward", Settings.screenWidth * 30 / 700, Settings.screenHeight * 70 / 400);
		g2d.drawString("s - Move Backward", Settings.screenWidth * 30 / 700, Settings.screenHeight * 100 / 400);
		g2d.drawString("a - Turn Counterclockwise", Settings.screenWidth * 30 / 700, Settings.screenHeight * 130 / 400);
		g2d.drawString("d - Turn Clockwise", Settings.screenWidth * 30 / 700, Settings.screenHeight * 160 / 400);
		g2d.drawString("SPACE - Shoot", Settings.screenWidth * 30 / 700, Settings.screenHeight * 190 / 400);
		g2d.drawString("SHIFT - Dash", Settings.screenWidth * 30 / 700, Settings.screenHeight * 220 / 400);

		g2d.drawString("Survive and shoot", Settings.screenWidth * 350 / 700, Settings.screenHeight * 70 / 400);
		g2d.drawString("asteroids for points.", Settings.screenWidth * 350 / 700, Settings.screenHeight * 100 / 400);
		g2d.drawString("Beware of fast asteroids!", Settings.screenWidth * 350 / 700, Settings.screenHeight * 160 / 400);
		g2d.drawString("Faster asteroids deal", Settings.screenWidth * 350 / 700, Settings.screenHeight * 190 / 400);
		g2d.drawString("more damage to your ship.", Settings.screenWidth * 350 / 700, Settings.screenHeight * 220 / 400);
		
		
		g2d.drawString("Press ENTER to continue", Settings.screenWidth * 300 / 700, Settings.screenHeight * 280 / 400);
	}

	// Draw Score Text
	public static void drawScore(Graphics g) {
		Graphics2D g2d = (Graphics2D) g;
		g2d.setStroke(new BasicStroke(3));
		g2d.setColor(Settings.txtColor);
		g2d.setFont(new Font("Monospaced", Font.BOLD, 15));

		g2d.drawString(
			"Score: " + (int)GameState.score, 
			Settings.screenWidth - 15 - 12 * (7 + (int)(Math.log10(GameState.score))),
			15
		);
	}

	// Draw HP Bar
	public static void drawHP(Graphics g) {
		Graphics2D g2d = (Graphics2D) g;
		g2d.setStroke(new BasicStroke(3));
		g2d.setColor(Settings.txtColor);
		g2d.setFont(new Font("Monospaced", Font.BOLD, 15));

		String msg = "HP:";

		int fill = (int)(((GameState.playerHP / Settings.shipMaxHP) * 20) + 0.5);
		if (fill <= 0) fill = 0;
		int empty = 20 - fill;
		

		for (int i = 0; i < fill; i++) {
			if (i % 2 == 0) msg += "[";
			else msg += "]";
		}
		for (int i = 0; i < empty; i++) {
			msg += "-";
		}

		g2d.drawString(msg, 15, 15);
	}

	// Draw Ammo Text
	public static void drawAmmo(Graphics g) {
		Graphics2D g2d = (Graphics2D) g;
		g2d.setStroke(new BasicStroke(3));
		g2d.setColor(Settings.txtColor);
		g2d.setFont(new Font("Monospaced", Font.BOLD, 15));

		g2d.drawString("Ammo: " + GameState.ammo, 325, 15);
	}
}