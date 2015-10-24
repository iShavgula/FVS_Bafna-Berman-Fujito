package algorithms;

import java.awt.Point;
import java.util.ArrayList;

/**
 * @author iShavgula
 *
 */
public class FVSPoint {
	private Point root;
	private double w;
	private boolean isUsed;

	public FVSPoint(Point root) {
		this.root = root;
		this.w = 1;
		this.isUsed = false;
	}

	public FVSPoint(Point root, double w) {
		this.root = root;
		this.w = w;
		this.isUsed = false;
	}

	public Point getRoot() {
		return root;
	}

	public double w() {
		return this.w;
	}

	public void setW(double w) {
		this.w = w;
	}
	
	public boolean getIsUsed() {
		return this.isUsed;
	}
	
	public void setIsUsed(boolean val) {
		this.isUsed = val;
	}
}
