package algorithms;

import java.awt.Point;
import java.util.ArrayList;

import javax.naming.spi.DirStateFactory.Result;

/**
 * @author iShavgula
 *
 */
/**
 * @author iShavgula
 *
 */
public class DefaultTeam {

	private ArrayList<Point> originalPoints;
	private ArrayList<FVSPoint> G;
	private ArrayList<FVSPoint> F;
	private ArrayList<FVSPoint> STACK;

	/**
	 * @param points - List of 'Point' typed objects
	 * @return the list of points which should be removed from the original tree
	 */
	public ArrayList<Point> calculFVS(ArrayList<Point> points) {
		G = new ArrayList<FVSPoint>();
		F = new ArrayList<FVSPoint>();
		STACK = new ArrayList<FVSPoint>();
		
		originalPoints = points;
		
		for (Point point : points) {
			G.add(new FVSPoint((Point)point.clone()));
		}

		start();

		return fvsListToPointList(F);
	}

	/**
	 * @param fvsPoints
	 * @return list of 'Point' typed objects corresponding to the root points of fvsPoints typed list objects.
	 */
	private ArrayList<Point> fvsListToPointList(ArrayList<FVSPoint> fvsPoints) {
		ArrayList<Point> points = new ArrayList<>();
		for (FVSPoint fvsPoint : fvsPoints) {
			points.add(fvsPoint.getRoot());
		}

		return points;
	}

	/**
	 * @param p
	 * @return the number of neighbors for the point 'p'
	 */
	private int getDegree(FVSPoint p) {
		int res = 0;

		for (FVSPoint fvsPoint : G) {
			Point point = fvsPoint.getRoot();
			if (point.distance(p.getRoot()) < 100 && !point.equals(p.getRoot())) {
				res++;
			}
		}

		return res;
	}

	/**
	 * @param p
	 * @return List of points which are considered as neighbors of the Point 'p'
	 */
	private ArrayList<FVSPoint> neighbour(FVSPoint p) {
		ArrayList<FVSPoint> result = new ArrayList<FVSPoint>();

		for (FVSPoint fvsPoint : G) {
			Point point = fvsPoint.getRoot();
			if (point.distance(p.getRoot()) < 100 && !point.equals(p.getRoot())) {
				result.add(fvsPoint);
			}
		}

		return result;
	}

	/**
	 * Starts evaluating the APPROXIMATION ALGORITHM FOR THE UNDIRECTED FEEDBACK VERTEX SET PROBLEM
	 */
	private void start() {
		Evaluation evaluation = new Evaluation();

		while (G.size() > 0) {
			for (FVSPoint point : G) {
				if (point.getIsUsed() == false) {
					ArrayList<FVSPoint> semiDisjointCycle = getSemidisjointCycle(null, point,
							new ArrayList<FVSPoint>());

					if (semiDisjointCycle != null) {
						processSemidisjointFound(semiDisjointCycle);
					} else {
						processSemidisjointNotFound(G);
					}

					updateGraph();
					cleanUp();

					break;
				}
			}

			for (FVSPoint point : G) {
				point.setIsUsed(false);
			}
		}
		
		while (STACK.size() != 0) {
			FVSPoint u = STACK.get(STACK.size() - 1);
			STACK.remove(u);
			F.remove(u);
			
			if (!evaluation.isValide(originalPoints, fvsListToPointList(F))) {
				F.add(u);
			}
		}
	}
	
	/**
	 * @param points
	 * Processes the case where semi-disjoint cycle is found
	 */
	private void processSemidisjointFound(ArrayList<FVSPoint> points) {
		double gama = Double.MAX_VALUE;

		for (FVSPoint p : points) {
			gama = Math.min(gama, p.w());
		}

		for (FVSPoint p : points) {
			p.setW(p.w() - gama);
		}
	}

	/**
	 * @param points
	 * Processes the case when semi-disjoint cycle cann't be found
	 */
	private void processSemidisjointNotFound(ArrayList<FVSPoint> points) {
		double gama = Double.MAX_VALUE;

		for (FVSPoint p : points) {
			gama = Math.min(gama, p.w() / (getDegree(p) - 1));
		}

		for (FVSPoint p : points) {
			p.setW(p.w() - gama * (getDegree(p) - 1));
		}
	}

	/**
	 * Removes points with 0 weight from the G graph and adds them into F and STACK
	 */
	private void updateGraph() {
		for (int i = G.size() - 1; i >= 0; i--) {
			if (Math.abs(G.get(i).w()) < 0.000000001) {
				FVSPoint p = G.get(i);
				G.remove(i);
				F.add(p);
				STACK.add(p);
			}
		}
	}

	/**
	 * Removes points with 0 neighbor from the graph G
	 */
	private void cleanUp() {
		for (int i = G.size() - 1; i >= 0; i--) {
			if (getDegree(G.get(i)) <= 1) {
				G.remove(i);
			}
		}
	}

	
	/**
	 * @param points; points are meant to be a sequence of points which represent a cycle in the graph G 
	 * @return true if 'points' represent semi-disjoint cycle, else - otherwise
	 */
	private boolean isSemidisjointCycle(ArrayList<FVSPoint> points) {
		int cntNotTwo = 0;

		for (int i = 0; i < points.size(); i++) {
			if (getDegree(points.get(i)) != 2) {
				cntNotTwo++;

				if (cntNotTwo > 1) {
					return false;
				}
			}
		}

		return true;
	}

	/**
	 * @param parent
	 * @param current
	 * @param path - represents sequence of points from the starting point to the parent 
	 * @return list of 'FVSPoints' representing semi-disjoint cycle if found, null - otherwise
	 * Searching algorithm is based on a simple DFS
	 */
	private ArrayList<FVSPoint> getSemidisjointCycle(FVSPoint parent, FVSPoint current, ArrayList<FVSPoint> path) {
		ArrayList<FVSPoint> neighbours = neighbour(current);

		if (current.getIsUsed() == true) {
			// cycle found
			ArrayList<FVSPoint> result = new ArrayList<FVSPoint>();
			result.add(current);
			for (int i = path.size() - 1; i >= 0; i--) {
				if (path.get(i) == current) {
					break;
				}
				result.add(path.get(i));
			}

			if (result.size() >= 3 && isSemidisjointCycle(result)) {
				// the cycle found is semi-disjoint
				current.setIsUsed(false);
				
				return result;
			}
			
			// the cycle found isn't semi-disjoint: return null
			
			return null;
		}

		path.add(current);
		current.setIsUsed(true);

		for (FVSPoint point : neighbours) {
			if (parent != point) {
				ArrayList<FVSPoint> result = getSemidisjointCycle(current, point, path);
				if (result != null) {
					path.remove(path.size() - 1);
					current.setIsUsed(false);
					return result;
				}
			}
		}

		path.remove(path.size() - 1);

		return null;
	}

}