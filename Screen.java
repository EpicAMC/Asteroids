/*
Program: AsteroidsChen
File: Screen.java
Author: Andrew Chen 
Purpose: Implements the game screen and its inputs
Reference for KeyListener: https://stackoverflow.com/questions/21997130/how-to-use-keylistener-with-jframe
Reference for JButton Customization: https://stackoverflow.com/questions/14159536/creating-jbutton-with-customized-look
*/
import javax.swing.*;
import javax.swing.border.*;
import java.awt.event.*;
import java.awt.*;

public class Screen extends JFrame implements KeyListener {
	//// STATIC VARIABLES
	public static JButton playButton = new JButton("Play!");
	public static Screen gameScreen = new Screen();

	//// INSTANCE VARIABLES
	public JLayeredPane pane = this.getLayeredPane();
	

	public Screen() {
		this.setSize(Settings.screenWidth, Settings.screenHeight);
		this.setTitle("Asteroids");
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setVisible(true);
		this.setResizable(false);

		DisplayComponent.mainComponent.setLocation(0, 0); // Set the main component's location to 0,0
		DisplayComponent.mainComponent.setBounds(0, 0, 10000, 10000); // Set the main component's bounds
		this.pane.add(DisplayComponent.mainComponent, JLayeredPane.DEFAULT_LAYER); // Put on default layer

		// Initialize playButton
		playButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				GameState.tutorial = true;
				GameState.active = true;
				playButton.setVisible(false);
			}
		});
		playButton.setBounds(
			Settings.screenWidth * 150 / 700, 
			Settings.screenHeight * 270 / 400, 
			Settings.screenWidth * 100 / 700, 
			Settings.screenHeight * 50 / 400
		);
		playButton.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.RAISED));
		playButton.setBackground(Settings.pbColor);
		playButton.setForeground(Settings.txtColor);
		playButton.setFocusPainted(false);

		playButton.setFont(new Font("Courier", Font.PLAIN, 18));
		this.pane.add(playButton, JLayeredPane.POPUP_LAYER);
		playButton.setVisible(true);

		// Initialize KeyListener
		addKeyListener(this);
    setFocusable(true);
    setFocusTraversalKeysEnabled(false);

		System.out.println("Initialized Screen");
	}

	public void keyPressed(KeyEvent e) {
		int key = e.getKeyCode();

		if (key == 87 && GameState.active) GameState.w = true;
		if (key == 65 && GameState.active) GameState.a = true;
		if (key == 83 && GameState.active) GameState.s = true;
		if (key == 68 && GameState.active) GameState.d = true;
		if (key == 16 && GameState.active) GameState.lShift = true;
		if (key == 32 && GameState.active) GameState.space = true;
		if (key == 10 && GameState.active) GameState.enter = true;

		if ((key == 13 || key == 10) && GameState.tutorial) {
			GameState.tutorial = false;
		}
	}

	public void keyReleased(KeyEvent e) {
		int key = e.getKeyCode();

		if (key == 87 && GameState.active) GameState.w = false;
		if (key == 65 && GameState.active) GameState.a = false;
		if (key == 83 && GameState.active) GameState.s = false;
		if (key == 68 && GameState.active) GameState.d = false;
		if (key == 16 && GameState.active) GameState.lShift = false;
		if (key == 32 && GameState.active) GameState.space = false;
		if (key == 10 && GameState.active) GameState.enter = false;
	}

	public void keyTyped(KeyEvent e) {}

	public static String getUsername(String prompt) {
		String name = (String)JOptionPane.showInputDialog(gameScreen, prompt, "Leaderboards", JOptionPane.QUESTION_MESSAGE);
		if (name.length() > 20) return getUsername("Username is too long! Must be under 20 characters:");
		if (name.indexOf(":") >= 0) return getUsername("Sorry! Colons are not allowed:"); // Colons interfere with leaderboard formatting
		return name;
	}
}