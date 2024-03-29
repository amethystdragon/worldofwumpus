/*****************************************************************************
 * FILE: ChickenLittle.java
 * DATE: 02/08/12
 * AUTHOR: 	Karl Schmidbauer <schmidbauerk@msoe.edu>
 * 			Ben Ebert <ebertb@msoe.edu>
 * 
 * PURPOSE: Cautious board searching agent. 
 * 			Will avoid taking risks when possible.
 * 
 ****************************************************************************/
package zz.agent;


import java.awt.Point;
import java.util.LinkedList;
import java.util.List;

import zz.board.GameBoard;
import zz.exceptions.IllegalMove;


public class ChickenLittle extends Agent {


	public ChickenLittle(GameBoard board) {
		super(board);
	}

	@Override
	public void search() {
		//Creates a list of visited nodes
		List<MemoryNode> visitedNodes = new LinkedList<MemoryNode>();
		//Creates a starting node
		MemoryNode startingNode =  new MemoryNode(null, board, 0, null);
		//Has the gold been found variable
		boolean goldFound = false;
		//Tiles statuses
		String statuses = board.getStatusAtLocation(currentPosition);
		//If the gold is on the tile pick it up and bounce out
		if (statuses.contains("A")) {
			grabGold();
			climb("Gold");
			goldFound = true;
			return;
		}
		//If there is a breeze try to go back or bounce
		if(statuses.contains("B") && !statuses.contains("NB")){
			climb("Brease");
			return;
		}
		
		//Adds what is here to the data base
		kb.updateTile("NB", new Point(0,0));
		kb.updateTile("NS", new Point(0,0));

		currentNode = startingNode;

		while (!goldFound) {
			//Adds the current node to the path
			visitedNodes.add(currentNode);

			//Decides where to move
			direction choice = null;
			if (kb.isSafe(new Point(currentPosition.x, currentPosition.y - 1)) && memory[currentPosition.x][currentPosition.y - 1] != null) {
				choice = direction.goUp; // create the node where the agent moves up
			} else if (kb.isSafe(new Point(currentPosition.x + 1, currentPosition.y)) && memory[currentPosition.x + 1][currentPosition.y]!=null) {
				choice = direction.goRight; // create the node where the agent moves right
			} else if (kb.isSafe(new Point(currentPosition.x, currentPosition.y + 1)) && memory[currentPosition.x][currentPosition.y + 1]!=null) {
				choice = direction.goDown; // create the node where the agent moves down
			} else if (kb.isSafe(new Point(currentPosition.x - 1, currentPosition.y)) && memory[currentPosition.x - 1][currentPosition.y] !=null) {
				choice = direction.goLeft; // create the node where the agent moves left
			} else {
				choice = currentNode.getDirection();
			}
			
			//Tries to move
			try {
				move(choice);
			} catch (IllegalMove e) {
				e.printStackTrace();
			}
			//Selects the next node
			if(choice == null) nextNode = currentNode.getParent();
			else nextNode = new MemoryNode(currentNode, board, currentNode.getPathCost() + 1, choice);
			
			if(nextNode == null) break;
			statuses = nextNode.getBoard().getStatusAtLocation(currentPosition);
			if (statuses.isEmpty()) {
				kb.updateTile("NB", currentPosition);
				kb.updateTile("NS", currentPosition);
			} else {
				boolean breeze = false;
				boolean stench = false;
				if (statuses.contains("A")) {
					try {
						Thread.sleep(250);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					grabGold();
					System.out.println("Gold Found");
					goldFound = true;
				}
				
				if (statuses.contains("B") && !statuses.contains("NB")) {
					breeze = true;
					kb.updateTile("B", currentPosition);
				} 
				if (statuses.contains("S") && !statuses.contains("NS")) {
					stench = true;
					if (kb.wumpusFound()) {
						break;
					}
					kb.updateTile("S", currentPosition);
					kb.findWumpus();
					if (kb.wumpusFound()) {
						System.out.println("WumpusFound");
						try {
							Thread.sleep(250);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
						Point wumpusLocation = kb.getWumpusLocation();
						
						direction whereToShoot = null;
						if (wumpusLocation.x > currentPosition.x) {
							whereToShoot = direction.goRight;
						} else if (wumpusLocation.y > currentPosition.y) {
							whereToShoot = direction.goUp;
						} else if (wumpusLocation.x < currentPosition.x) {
							whereToShoot = direction.goLeft;
						} else if (wumpusLocation.y < currentPosition.y) {
							whereToShoot = direction.goDown;
						}
						try {
							shootArrow(whereToShoot);
						} catch (IllegalMove e) {
							e.printStackTrace();
						}
						kb.setWumpusDead(board.wumpusDead());
					}
				}
				
				if(breeze) kb.updateTile("B", currentPosition);
				else kb.updateTile("NB", currentPosition);
				
				if(stench) kb.updateTile("S", currentPosition);
				else kb.updateTile("S", currentPosition);
				
			}
			currentNode = nextNode;
		}
		
		


		while (currentNode.getParent() != null) {
			try {
				move(currentNode.getDirection());
			} catch (IllegalMove e) {
				e.printStackTrace();
			}
			try {
				Thread.sleep(100);
			} catch (InterruptedException e1) {
				e1.printStackTrace();
			}
			currentNode = currentNode.getParent();
		}
		climb("Give Up");
	}
}
