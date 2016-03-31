

// Pathfinder class, Aaron Steed 2007

// Some code and structure still intact from Tom Carden's version:
// <http://www.tom-carden.co.uk/p5/a_star_web/applet/index.html>
// Some links that helped me:
// <http://theory.stanford.edu/~amitp/GameProgramming/AStarComparison.html>
// <http://www.policyalmanac.org/games/aStarTutorial.htm>
// <http://www.geocities.com/jheyesjones/astar.html>
// <http://www-b2.is.tokushima-u.ac.jp/~ikeda/suuri/dijkstra/Dijkstra.shtml>
// <http://www.cs.usask.ca/resources/tutorials/csconcepts/1999_8/tutorial/advanced/dijkstra/dijkstra.html>

import java.util.ArrayList;
import java.util.List;

public class AIPathfinder {

	public List<AINode>	nodes;									// Storage
	// ArrayList for
	// the Nodes
	public List<AINode>	open		= new ArrayList<>();	// Possible
	// Nodes for
	// consideration
	public List<AINode>	closed		= new ArrayList<>();	// Best of the
	// Nodes
	public boolean		wrap;					// Setting for
	// makeCuboidWeb()
	// for grid wrap
	// around
	public boolean		corners		= true;					// Setting for
	// makeCuboidWeb()
	// for
	// connecting
	// nodes at
	// corners
	public boolean		manhattan;					// Setting for
	// using
	// Manhattan
	// distance
	// measuring
	// method (false
	// uses
	// Euclidean
	// method)
	public float		offsetX, offsetY, offsetZ; // Offset

	// to
	// added
	// to
	// Nodes
	// made
	// with
	// makeCuboidWeb

	// Constructors

	public AIPathfinder() {
		this.nodes = new ArrayList<>();
	}

	public AIPathfinder(final List<AINode> graph) {
		this.nodes = graph;
	}

	public AIPathfinder(final int w, final int h, final float scale) {
		this.setCuboidNodes(w, h, scale);
	}

	public AIPathfinder(final int w, final int h, final int d, final float scale) {
		this.setCuboidNodes(w, h, d, scale);
	}

	//
	// Search algortihms
	//

	// ASTAR

	public ArrayList<AINode> aStar(final AINode start, final AINode finish) {
		for (final AINode n : this.nodes) {
			n.reset();
		}
		this.open.clear();
		this.closed.clear();
		this.open.add(start);
		while (this.open.size() > 0) {
			float lowest = Float.MAX_VALUE;
			int c = -1;
			for (int i = 0; i < this.open.size(); i++) {
				final AINode temp = this.open.get(i);
				if (temp.sumGoalHeuristic < lowest) {
					lowest = temp.sumGoalHeuristic;
					c = i;
				}
			}
			final AINode current = this.open.remove(c);
			this.closed.add(current);
			if (current == finish) {
				break;
			}
			for (int i = 0; i < current.links.size(); i++) {
				final AIConnector a = current.links.get(i);
				final AINode adjacent = a.n;
				if (adjacent.walkable && !this.listContains(this.closed, adjacent)) {
					if (!this.listContains(this.open, adjacent)) {
						this.open.add(adjacent);
						adjacent.parent = current;
						adjacent.setG(a);
						if (this.manhattan) {
							adjacent.MsetF(finish);
						} else {
							adjacent.setF(finish);
						}
					} else {
						if (adjacent.costGoal > current.costGoal + a.d) {
							adjacent.parent = current;
							adjacent.setG(a);
							if (this.manhattan) {
								adjacent.MsetF(finish);
							} else {
								adjacent.setF(finish);
							}
						}
					}
				}
			}
		}
		// Path generation
		ArrayList<AINode> path = new ArrayList<>();
		AINode pathNode = finish;
		while (pathNode != null) {
			path.add(pathNode);
			pathNode = pathNode.parent;
		}
		// Hack to provide a compromise path when a route to the finish node is
		// unavailable
		final AINode test = path.get(path.size() - 1);
		if (test == finish) {
			float leastDist = Float.MAX_VALUE;
			AINode bestNode = null;
			for (final AINode n : this.closed) {
				final float nDist = n.dist(finish);
				if (nDist < leastDist) {
					leastDist = nDist;
					bestNode = n;
				}
			}
			if (bestNode == null) {
				throw new RuntimeException("Could not determine best node!");
			}
			if (bestNode.parent != null) {
				pathNode = bestNode;
				path = new ArrayList<>();
				while (pathNode != null) {
					path.add(pathNode);
					pathNode = pathNode.parent;
				}
			}
		}
		return path;
	}

	// BEST FIRST SEARCH

	public ArrayList<AINode> bfs(final AINode start, final AINode finish) {
		for (final AINode n : this.nodes) {
			n.reset();
		}
		this.open.clear();
		this.closed.clear();
		this.open.add(start);
		while (this.open.size() > 0) {
			float lowest = Float.MAX_VALUE;
			int c = -1;
			for (int i = 0; i < this.open.size(); i++) {
				final AINode temp = this.open.get(i);
				if (temp.costHeuristic < lowest) {
					lowest = temp.costHeuristic;
					c = i;
				}
			}
			final AINode current = this.open.remove(c);
			this.closed.add(current);
			if (current == finish) {
				break;
			}
			for (int i = 0; i < current.links.size(); i++) {
				final AIConnector a = current.links.get(i);
				final AINode adjacent = a.n;
				if (adjacent.walkable && !this.listContains(this.closed, adjacent)) {
					if (!this.listContains(this.open, adjacent)) {
						this.open.add(adjacent);
						adjacent.parent = current;
						if (this.manhattan) {
							adjacent.MsetH(finish);
						} else {
							adjacent.setH(finish);
						}
					}
				}
			}
		}
		// Path generation
		ArrayList<AINode> path = new ArrayList<>();
		AINode pathNode = finish;
		while (pathNode != null) {
			path.add(pathNode);
			pathNode = pathNode.parent;
		}
		// Hack to provide a compromise path when a route to the finish node is
		// unavailable
		final AINode test = path.get(path.size() - 1);
		if (test == finish) {
			float leastDist = Float.MAX_VALUE;
			AINode bestNode = null;
			for (final AINode n : this.closed) {
				final float nDist = n.dist(finish);
				if (nDist < leastDist) {
					leastDist = nDist;
					bestNode = n;
				}
			}
			if (bestNode == null) {
				throw new RuntimeException("Could not determine best node!");
			}
			if (bestNode.parent != null) {
				pathNode = bestNode;
				path = new ArrayList<>();
				while (pathNode != null) {
					path.add(pathNode);
					pathNode = pathNode.parent;
				}
			}
		}
		return path;
	}

	// DIJKSTRA

	public void dijkstra(final AINode start) {
		this.dijkstra(start, null);
	}

	public ArrayList<AINode> dijkstra(final AINode start, final AINode finish) {
		for (final AINode n : this.nodes) {
			n.reset();
		}
		this.open.clear();
		this.closed.clear();
		this.open.add(start);
		start.costGoal = 0;
		while (this.open.size() > 0) {
			final AINode current = this.open.remove(0);
			this.closed.add(current);
			// FIXME KAI remove to build path for all?
			if (current == finish) {
				break;
			}
			for (int i = 0; i < current.links.size(); i++) {
				final AIConnector a = current.links.get(i);
				final AINode adjacent = a.n;
				if (adjacent.walkable && !this.listContains(this.closed, adjacent)) {
					if (!this.listContains(this.open, adjacent)) {
						this.open.add(adjacent);
						adjacent.parent = current;
						adjacent.setG(a);
					} else {
						if (adjacent.costGoal > current.costGoal + a.d) {
							adjacent.parent = current;
							adjacent.setG(a);
						}
					}
				}
			}
		}
		// Path generation
		final ArrayList<AINode> path = new ArrayList<>();
		AINode pathNode = finish;
		while (pathNode != null) {
			path.add(pathNode);
			pathNode = pathNode.parent;
		}
		return path;
	}

	public ArrayList<AINode> getPath(AINode pathNode) {
		final ArrayList<AINode> path = new ArrayList<>();
		while (pathNode != null) {
			path.add(pathNode);
			pathNode = pathNode.parent;
		}
		return path;
	}

	// Shortcut to adding a makeCuboidWeb construct to Pathfinder

	public void setCuboidNodes(final int w, final int h, final float scale) {
		this.nodes = new ArrayList<>();
		this.nodes = this.createCuboidNodes(new int[] { w, h }, scale);
	}

	public void setCuboidNodes(final int w, final int h, final int d, final float scale) {
		this.nodes = new ArrayList<>();
		this.nodes = this.createCuboidNodes(new int[] { w, h, d }, scale);
	}

	public void addNodes(final ArrayList<AINode> nodes) {
		this.nodes.addAll(nodes);
	}

	// Creates a construct of Nodes and connects them across adjacent
	// dimensions.
	// Adapts for corners and wrap-around but at a cost of speed - only for init

	public ArrayList<AINode> createCuboidNodes(final int w, final int h, final float scale) {
		return this.createCuboidNodes(new int[] { w, h }, scale);
	}

	public ArrayList<AINode> createCuboidNodes(final int w, final int h, final int d, final float scale) {
		return this.createCuboidNodes(new int[] { w, h, d }, scale);
	}

	/*
	 * // Just some notes incase I have to remove the array method of building a map public ArrayList createCuboidNodes(int w, int h, int d, float scale){ ArrayList world = new ArrayList(); int totalLength = w * h * d; for(int i = 0; i < totalLength;
	 * i++){ float x = offsetX + ((i % (w * h)) % w) * scale; float y = offsetY + ((i % (w * h)) / w) * scale; float z = offsetZ + i / (w * h); world.add(new Node(x, y, z)); } }
	 */

	// This beast I'd rather leave as is. Sorry.
	// I'm hiding it though. I may rely on array building ArrayList3Ds in the
	// future.
	private ArrayList<AINode> createCuboidNodes(final int[] dim, final float scale) {
		final ArrayList<AINode> world = new ArrayList<>();
		int totalLength = 1;
		for (int aDim : dim) {
			if (aDim > 0) {
				totalLength *= aDim;
			}
		}
		for (int i = 0; i < totalLength; i++) {
			final int[] intP = this.getFolded(i, dim);
			final float[] p = new float[intP.length];
			for (int j = 0; j < p.length; j++) {
				p[j] = intP[j] * scale;
			}
			final AINode temp = new AINode(p);
			temp.x += this.offsetX;
			temp.y += this.offsetY;
			temp.z += this.offsetZ;
			world.add(temp);
		}
		final int directions = (int) Math.pow(3, dim.length);
		for (int i = 0; i < totalLength; i++) {
			final int[] p = this.getFolded(i, dim);
			final AINode myNode = world.get(i);
			final int[] b = new int[p.length];
			final int[] w = new int[p.length];
			for (int j = 0; j < b.length; j++) {
				b[j] = p[j] - 1;
				w[j] = 0;
			}
			for (int j = 0; j < directions; j++) {
				boolean valid = true;
				for (int k = 0; k < dim.length; k++) {
					if (b[k] > dim[k] - 1 || b[k] < 0) {
						if (this.wrap) {
							if (b[k] > dim[k] - 1) {
								b[k] -= dim[k];
								w[k]--;
							}
							if (b[k] < 0) {
								b[k] += dim[k];
								w[k]++;
							}
						} else {
							valid = false;
						}
					}
					if (!this.corners) {
						int combinations = 0;
						for (int l = 0; l < dim.length; l++) {
							if (b[l] != p[l]) {
								combinations++;
							}
						}
						if (combinations > 1) {
							valid = false;
						}
					}
				}
				if (valid) {
					final AINode connectee = world.get(this.getUnfolded(b, dim));
					if (myNode != connectee) {
						myNode.connect(connectee);
					}
				}
				if (this.wrap) {
					for (int k = 0; k < dim.length; k++) {
						switch (w[k]) {
						case 1:
							b[k] -= dim[k];
							w[k] = 0;
							break;
						case -1:
							b[k] += dim[k];
							w[k] = 0;
							break;
						}
					}
				}
				b[0]++;
				for (int k = 0; k < b.length - 1; k++) {
					if (b[k] > p[k] + 1) {
						b[k + 1]++;
						b[k] -= 3;
					}
				}
			}
		}
		return world;
	}

	// The next two functions are shortcut methods for disconnecting unwalkables

	public void disconnectUnwalkables() {
		for (final AINode temp : this.nodes) {
			if (!temp.walkable) {
				temp.disconnect();
			}
		}
	}

	public void radialDisconnectUnwalkables() {
		for (final AINode temp : this.nodes) {
			if (!temp.walkable) {
				temp.radialDisconnect();
			}
		}
	}

	//
	// Utilities
	//

	// Faster than running ArrayList.contains - we only need the reference, not
	// an
	// object match

	public boolean listContains(final List<AINode> c, final AINode n) {
		for (final AINode o : c) {
			if (o == n) {
				return true;
			}
		}
		return false;
	}

	// Faster than running ArrayList.indexOf - we only need the reference, not
	// an
	// object match

	public int indexOf(final AINode n) {
		for (int i = 0; i < this.nodes.size(); i++) {
			final AINode o = this.nodes.get(i);
			if (o == n) {
				return i;
			}
		}
		return -1;
	}

	// Returns an n-dimensional arrayList from a point on a line of units given
	// an
	// n-dimensional space

	public int[] getFolded(final int n, final int[] d) {
		final int[] coord = new int[d.length];
		for (int i = 0; i < d.length; i++) {
			coord[i] = n;
			for (int j = d.length - 1; j > i; j--) {
				int level = 1;
				for (int k = 0; k < j; k++) {
					level *= d[k];
				}
				coord[i] %= level;
			}
			int level = 1;
			for (int j = 0; j < i; j++) {
				level *= d[j];
			}
			coord[i] /= level;
		}
		return coord;
	}

	// Returns a point on a line of units from an n-dimensional arrayList in an
	// n-dimensional space

	public int getUnfolded(final int[] p, final int[] d) {
		int coord = 0;
		for (int i = 0; i < p.length; i++) {
			int level = 1;
			for (int j = 0; j < i; j++) {
				level *= d[j];
			}
			coord += p[i] * level;
		}
		return coord;
	}

}
