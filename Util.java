/*
Program: AsteroidsChen
File: Util.java
Author: Andrew Chen 
Purpose: Utility methods for various calculations and special functionalities
*/
import java.awt.geom.*;

public class Util {
	/*
	Adds two radian measures together and keeps the sum between 0 and 2*PI
	*/
	public static double addAngle(double a1, double a2) {
		double a = a1 + a2;
		if (a > 2 * Math.PI) a -= 2 * Math.PI;
		return a;
	}

	/*
	Returns a set of coordinates that corresponds to a random point on the edge of a rectangle
	*/
	public static double[] randomRectPos(Rectangle2D.Double rect) {
		double x = rect.getX();
		double y = rect.getY();
		double height = rect.getHeight();
		double width = rect.getWidth();
		double rand = Math.random() * (height * 2 + width * 2);
		double[] pos = new double[2];

		if (rand < width) {
			pos[0] = x + rand;
			pos[1] = y;
		}
		else if (rand <= width + height) {
			pos[0] = x + width;
			pos[1] = y + (rand - width);
		}
		else if (rand <= width * 2 + height) {
			pos[0] = x + width - (rand - width - height);
			pos[1] = y + height;
		}
		else if (rand <= width * 2 + height * 2) {
			pos[0] = x;
			pos[1] = y + height - (rand - width * 2 - height);
		}
		else {
			System.out.println("randomRectPos() Error");
			pos[0] = x;
			pos[1] = y;
		}

		return pos;
	}


	/*
	Returns the facing value for a vector starting at point 1 to point 2 (represented by x1, y1 and x2, y2 respectively)
	*/
	public static double toFacing(double x1, double y1, double x2, double y2) {
		// Check axes to make sure no divide by 0 errors occur
		if (x1 == x2) { 
			if (y2 > y1) return Math.PI * 1/2;
			if (y2 < y1) return Math.PI * 3/2;
			else return 0.0;
		}
		if (y1 == y2) {
			if (x2 > x1) return 0.0;
			if (x2 < x1) return Math.PI;
		}
		
		if (x1 < x2 && y1 < y2) { // Quadrant 1
			return Math.atan((y2 - y1) / (x2 - x1));
		}
		else if (x1 > x2 && y1 < y2) { // Quadrant 2
			return Math.PI * 1/2 + Math.atan((x1 - x2) / (y2 - y1));
		}
		else if (x1 > x2 && y1 > y2) { //Quadrant 3
			return Math.PI + Math.atan((y1 - y2) / (x1 - x2));
		}
		else if (x1 < x2 && y1 > y2) { // Quadrant 4
			return Math.PI * 3/2 + Math.atan((x2 - x1) / (y1 - y2));
		}

		System.out.println("toFacing() Error");
		return -1.0;
	}

	/*
	Returns the distance between the two points
	*/
	public static double distance(double x1, double y1, double x2, double y2) {
		return Math.sqrt((x2 - x1) * (x2 - x1) + (y2 - y1) * (y2 - y1));
	}

	/*
	Returns the x, y, intersection point of two lines defined by point-slope values
	*/
	public static double[] intersect(double x1, double y1, double s1, double x2, double y2, double s2) {
		double[] output = new double[2];
		double xTemp = y1 - s1 * x1;
		double yTemp = y2 - s2 * x2;
		double[][] matrixStep = {{1, -1}, {s2, s1 * (-1)}};
		double determinantRec = 1 / (s1 * (-1) + s2);
		double[][] invMatrix = {
			{matrixStep[0][0] * determinantRec, matrixStep[0][1] * determinantRec}, 
			{matrixStep[1][0] * determinantRec, matrixStep[1][1] * determinantRec}
		};
		output[0] = xTemp * invMatrix[0][0] + yTemp * invMatrix[0][1];
		output[1] = xTemp * invMatrix[1][0] + yTemp * invMatrix[1][1];
		return output;
	}

	/*
	Returns the magnitude, direction of the resultant vector from two vectors defined by magnitude, direction
	*/
	public static double[] resultant(double m1, double f1, double m2, double f2) {
		double x1 = m1 * Math.cos(f1);
		double y1 = m1 * Math.sin(f1);
		double x2 = m2 * Math.cos(f2);
		double y2 = m2 * Math.sin(f2);

		double[] output = {Math.sqrt((x1 + x2)*(x1 + x2) + (y1 + y2)*(y1 + y2)), toFacing(0, 0, (x1 + x2), (y1 + y2))};
		return output;
	}

	/*
	Returns true if the vector causes counterclockwise (positive) rotation and false if it is clockwise
	*/
	public static boolean clockwise(double xc, double yc, double xp, double yp, double fp) {
		double ps = -1 / ((yp - yc) / (xp - xc)); // Perpendicular slope
		double testB = (-1 * ps * xp + yp) - (-1 * ps * xc + yc);
		double testCos = Math.cos(fp);

		if (testB * testCos <= 0) return true;
		else return false;
	}
}