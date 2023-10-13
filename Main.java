/*
Program: AsteroidsChen
Author: Andrew Chen 
Purpose: Runs the main loop of the game
*/

import javax.swing.SwingUtilities;
import java.io.IOException;

class Main {
  public static void main(String[] args) throws IOException{
		Screen.gameScreen.setVisible(true);

		Leaderboard.scan();

		Game.reset();
		long prev = System.currentTimeMillis();
		while (true) { // Main Loop
			if (System.currentTimeMillis() - prev > 30) {
				// Update Frame Timer
				prev = System.currentTimeMillis();

				// Game
				Game.checkCollisions();
				Game.progressLevel();
				if (!GameState.active) Game.decay();

				// Ship
				Ship.ship.accelerate();
				Ship.ship.move();
				if (GameState.space) {
					Ship.ship.shoot();
				}

				// Bullets
				Bullet.reload();
				Bullet.moveAll();
				Bullet.killAll();

				// Asteroids
				Asteroid.moveAll();
				Asteroid.killAll();
				
				// Flip Screen
				SwingUtilities.updateComponentTreeUI(Screen.gameScreen);
			}
		}
	}
}