/*
Program: AsteroidsChen
File: DisplayComponent.java
Author: Andrew Chen 
Purpose: Combines all the elements into one component so JSwing doesn't throw a tantrum with layers
*/
import javax.swing.JComponent;
import java.awt.Graphics;
import java.awt.Graphics2D;

public class DisplayComponent extends JComponent {
	public static DisplayComponent mainComponent = new DisplayComponent();

	private DisplayComponent() {
		super();
	}

	/*
	Draw all non-interacting components
	*/
	public void paintComponent(Graphics g) {
		// Draw Background
		g.setColor(Settings.bgColor);
		g.fillRect(0, 0, Settings.screenWidth, Settings.screenHeight);
		
		// Draw Bullets
		Bullet.draw(g);

		// Draw Ship
		Ship.ship.draw(g);

		// Draw Asteroids
		Asteroid.draw(g);
		
		// Draw Labels
		if (!GameState.active && !GameState.tutorial) Label.drawTitle(g);
		if (GameState.tutorial) Label.drawTutorial(g);
		if (GameState.active) {
			Label.drawScore(g);
			Label.drawHP(g);
			Label.drawAmmo(g);
		}

		// Draw Leaderboard Menu
		if (!GameState.active && !GameState.tutorial) Leaderboard.draw(g);
	}
}