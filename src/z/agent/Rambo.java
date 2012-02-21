package z.agent;

import java.awt.Point;
import java.util.List;
import java.util.Random;

import z.wumpus.StateSpace;

/**
 * @author ebertb, Schmidbauerk
 * @date 2/17/12
 * This class is the Rambo AI
 */
public class Rambo extends Agent {

	public static Random r = new Random();
	
	/**
	 * @see z.agent.Agent#resolveFringe(aima.util.Pair, java.util.List, z.wumpus.StateSpace)
	 */
	@Override
	public Point search(Point current, StateSpace stateSpace) {
		
		Point resolved = null;
		
		List<Point> safeMoves = Agent.getSafeMoves(stateSpace);
		
		if (safeMoves.size() < 1) {
			System.out.println("I'm trapped. Crap.");
			return null;
		} else if (safeMoves.size() == 1) {
			resolved = safeMoves.get(0);
		} else {
			resolved = safeMoves.get(r.nextInt(safeMoves.size()));
		}
		
		return resolved;
	}
	
	
	@Override
	public String identify() {
		return "Rambo";
	}

}
