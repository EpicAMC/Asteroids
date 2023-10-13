/*
Program: AsteroidsChen
File: Leaderboard.java
Author: Andrew Chen 
Purpose: Defines the graphics for the leaderboard
Reference for File Writing:
https://www.w3schools.com/java/java_files_create.asp
*/
import java.util.*;
import java.io.*;
import java.awt.*;

public class Leaderboard {
	private static ArrayList<Entry> leaderboards = new ArrayList<Entry>();

	// Scan Text File
	public static void scan() throws IOException{
		Scanner reader = new Scanner(new File("Highscores.txt"));
		while (reader.hasNextLine()) {
			String line = reader.nextLine();
			String[] splits = line.split(":");
			if (splits.length >= 2) {
				try {
					String username = splits[0];
					int score = Integer.parseInt(splits[1]);
					leaderboards.add(new Entry(username, score));
				}
				catch (NumberFormatException e) {
					System.out.println("Invalid Leaderboard Entry Detected");
				}
			}
		}
		System.out.println("Finished scanning leaderboards");
		reader.close();
	}

	// Get Lowest Leaderboard Score
	public static int getLow() {return leaderboards.get(leaderboards.size() - 1).getScore();}

	// Insert Score into Leaderboards. @param name is player name. @param score is player score
	public static void insert(String name, int score) {
		int i = 0;
		while (i < leaderboards.size() && leaderboards.get(i).getScore() > score) i++;
		leaderboards.add(i, new Entry(name, score));

		try {write();}
		catch (IOException e) {System.out.println("Failed to write to leaderboards!");}
	}

	// Helper method to write to file
	private static void write() throws IOException{
		FileWriter writer = new FileWriter(new File("Highscores.txt"));
		for (int i = 0; i <leaderboards.size(); i++) {
			writer.write(leaderboards.get(i).getName() + ":" + leaderboards.get(i).getScore());
			if (i != leaderboards.size() - 1) writer.write("\n");
		}
		writer.close();
	}

	// Specifications for drawing the leaderboards. @param g is passed by DisplayComponent
	public static void draw(Graphics g) {
		Graphics2D g2d = (Graphics2D) g;
		g2d.setStroke(new BasicStroke(1));
		g2d.setColor(Settings.menuColor);
		
		g2d.fillRect(Settings.screenWidth * 400 / 700, 0, Settings.screenWidth * 300 / 700, Settings.screenHeight);

		g2d.setColor(Settings.txtColor);
		g2d.setFont(new Font("Monospaced", Font.BOLD, 24));
		g2d.drawString("Leaderboards:", Settings.screenWidth * 450 / 700, 30);

		g2d.setFont(new Font("Monospaced", Font.PLAIN, 16));
		int line = 60;
		for (int i = 0; i < 20 && i < leaderboards.size(); i++) {
			g2d.drawString(
				"[#" + (i + 1) + "] " + leaderboards.get(i).getName() + ": " + leaderboards.get(i).getScore(),
				Settings.screenWidth * 410 / 700,
				line
			);
			line += 20;
		}
	}
}

class Entry {
	private String name;
	private int score;

	public Entry(String n, int s) {
		name = n;
		score = s;
	}

	public String getName() {return name;}
	public int getScore() {return score;}
}