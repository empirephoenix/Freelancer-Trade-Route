

// AStar Node class, Aaron Steed 2006

import java.util.ArrayList;

public class AINode {

	public float					x, y, z;											// Location,
	// variable
	// dimensions
	public AINode					parent;						// Parent
	// Node
	// setting
	public float					sumGoalHeuristic;						// Sum
	// of
	// goal
	// and
	// heuristic
	// calculations
	public float					costGoal;						// Cost
	// of
	// reaching
	// goal
	public float					costHeuristic;						// Heuristic
	// distance
	// calculation
	public ArrayList<AIConnector>	links				= new ArrayList<>(); // Connectors
	// to
	// other
	// Nodes
	public boolean					walkable			= true;						// Is

	// this
	// Node
	// to
	// be
	// ignored?

	@Override
	public String toString() {
		return "AINode [x=" + this.x + ", y=" + this.y + ", z=" + this.z + ", sumGoalHeuristic=" + this.sumGoalHeuristic + ", costGoal=" + this.costGoal + ", costHeuristic=" + this.costHeuristic + "]";
	}

	// Constructors

	public AINode() {
		this(0.0f, 0.0f, 0.0f);
	}

	public AINode(final float x, final float y) {
		this.x = x;
		this.y = y;
		this.z = 0.0f;
	}

	public AINode(final float x, final float y, final float z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}

	// Undocumented constructor - used by makeCuboidNodes(int [] dim, float
	// scale)

	public AINode(final float[] p) {
		this.x = p[0];
		this.y = p[1];
		this.z = 0.0f;
		if (p.length > 2) {
			this.z = p[2];
		}
	}

	public AINode(final float x, final float y, final ArrayList<AIConnector> links) {
		this.x = x;
		this.y = y;
		this.z = 0.0f;
		this.links = links;
	}

	public AINode(final float x, final float y, final float z, final ArrayList<AIConnector> links) {
		this.x = x;
		this.y = y;
		this.z = z;
		this.links = links;
	}

	//
	// Field utilities
	//

	public void reset() {
		this.parent = null;
		this.sumGoalHeuristic = this.costGoal = this.costHeuristic = 0;
	}

	// Calculate G

	public void setG(final AIConnector o) {
		this.costGoal = this.parent.costGoal + o.d;
	}

	// Euclidean field methods calculate F & H

	public void setF(final AINode finish) {
		this.setH(finish);
		this.sumGoalHeuristic = this.costGoal + this.costHeuristic;
	}

	public void setH(final AINode finish) {
		this.costHeuristic = this.dist(finish);
	}

	// Manhattan field methods calculate F & H

	public void MsetF(final AINode finish) {
		this.MsetH(finish);
		this.sumGoalHeuristic = this.costGoal + this.costHeuristic;
	}

	public void MsetH(final AINode finish) {
		this.costHeuristic = this.manhattan(finish);
	}

	//
	// Linking tools
	//

	public AINode copy() {
		final ArrayList<AIConnector> temp = new ArrayList<>();
		temp.addAll(this.links);
		return new AINode(this.x, this.y, this.z, temp);
	}

	public void connect(final AINode n) {
		this.links.add(new AIConnector(n, this.dist(n)));
	}

	public void connect(final AINode n, final float d) {
		this.links.add(new AIConnector(n, d));
	}

	public void connect(final ArrayList<AIConnector> links) {
		this.links.addAll(links);
	}

	public void connectBoth(final AINode n) {
		this.links.add(new AIConnector(n, this.dist(n)));
		n.links.add(new AIConnector(this, this.dist(n)));
	}

	public void connectBoth(final AINode n, final float d) {
		this.links.add(new AIConnector(n, d));
		n.links.add(new AIConnector(this, d));
	}

	public int indexOf(final AINode n) {
		for (int i = 0; i < this.links.size(); i++) {
			final AIConnector c = this.links.get(i);
			if (c.n == n) {
				return i;
			}
		}
		return -1;
	}

	public boolean connectedTo(final AINode n) {
		for (final AIConnector c : this.links) {
			if (c.n == n) {
				return true;
			}
		}
		return false;
	}

	public boolean connectedTogether(final AINode n) {
		for (int i = 0; i < this.links.size(); i++) {
			final AIConnector c = this.links.get(i);
			if (c.n == n) {
				for (int j = 0; j < n.links.size(); j++) {
					final AIConnector o = n.links.get(j);
					if (o.n == this) {
						return true;
					}
				}
			}
		}
		return false;
	}

	public void mulDist(final float m) {
		for (final AIConnector c : this.links) {
			c.d *= m;
		}
	}

	public void setDist(final AINode n, final float d) {
		final int i = this.indexOf(n);
		if (i > -1) {
			final AIConnector temp = this.links.get(i);
			temp.d = d;
		}
	}

	public void setDistBoth(final AINode n, final float d) {
		final int i = this.indexOf(n);
		if (i > -1) {
			AIConnector temp = this.links.get(i);
			temp.d = d;
			final int j = n.indexOf(this);
			if (j > -1) {
				temp = n.links.get(j);
				temp.d = d;
			}
		}
	}

	// Iterates thru neighbours and unlinks Connectors incomming to this - Node
	// is
	// still linked to neighbours though

	public void disconnect() {
		for (int i = 0; i < this.links.size(); i++) {
			final AIConnector c = this.links.get(i);
			final int index = c.n.indexOf(this);
			if (index > -1) {
				c.n.links.remove(index);
			}
		}
	}

	// Calculates shortest link and kills all links around the Node in that
	// radius
	// Used for making routes around objects account for the object's size
	// Uses actual distances rather than Connector settings

	public void radialDisconnect() {
		float radius = 0.0f;
		for (final AIConnector myLink : this.links) {
			if (this.straightLink(myLink.n)) {
				radius = this.dist(myLink.n);
				break;
			}
		}
		for (int j = 0; j < this.links.size(); j++) {
			final AIConnector myLink = this.links.get(j);
			final ArrayList<AINode> removeMe = new ArrayList<>();
			for (int k = 0; k < myLink.n.links.size(); k++) {
				final AIConnector myLinkLink = myLink.n.links.get(k);
				final float midX = (myLink.n.x + myLinkLink.n.x) * 0.5f;
				final float midY = (myLink.n.y + myLinkLink.n.y) * 0.5f;
				final float midZ = (myLink.n.z + myLinkLink.n.z) * 0.5f;
				final AINode temp = new AINode(midX, midY, midZ);
				if (this.dist(temp) <= radius) {
					removeMe.add(myLinkLink.n);
				}
			}
			for (final AINode temp : removeMe) {
				final int index = myLink.n.indexOf(temp);
				if (index > -1) {
					myLink.n.links.remove(index);
				}
			}
		}
	}

	// Checks if a Node's position differs along one dimension only

	public boolean straightLink(final AINode myLink) {
		if (this.indexOf(myLink) < 0) {
			return false;
		}
		int dimDelta = 0;
		if (this.x != myLink.x) {
			dimDelta++;
		}
		if (this.y != myLink.y) {
			dimDelta++;
		}
		if (this.z != myLink.z) {
			dimDelta++;
		}
		return dimDelta == 1;
	}

	//
	// Location tools
	//

	// Euclidean distance measuring for accuracy

	public float dist(final AINode n) {
		if (this.z == 0.0 && n.z == 0.0) {
			return (float) Math.sqrt((this.x - n.x) * (this.x - n.x) + (this.y - n.y) * (this.y - n.y));
		}
		return (float) Math.sqrt((this.x - n.x) * (this.x - n.x) + (this.y - n.y) * (this.y - n.y) + (this.z - n.z) * (this.z - n.z));
	}

	// Manhattan distance measuring for avoiding jagged paths

	public float manhattan(final AINode n) {
		if (this.z == 0.0 && n.z == 0.0) {
			return (this.x - n.x) * (this.x - n.x) + (this.y - n.y) * (this.y - n.y) + (this.z - n.z) * (this.z - n.z);
		}
		return (this.x - n.x) * (this.x - n.x) + (this.y - n.y) * (this.y - n.y);
	}
}
