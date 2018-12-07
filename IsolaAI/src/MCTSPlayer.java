import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

import processing.core.PApplet;

public class MCTSPlayer extends Player {

	private static final int MAX_TIME = 3000;
	private static final double C = 1;
	private Random r;
	private static Set<GameState> usedStates;

	public MCTSPlayer(PApplet parent, int color) {
		super(parent, color);
		r = new Random();
	}

	@Override
	public Coordinate[] getMove(GameState gameState) throws InterruptedException {
		usedStates = new HashSet<GameState>();
		int startTime = parent.millis();
		MCTSNode root = new MCTSNode(gameState);
		usedStates.add(root.gameState);
		while (parent.millis() - startTime < MAX_TIME) {
			MCTSNode node = selection(root);
			if (node.visits > 0)
				node = expansion(node);
			Player winner = simulation(node);
			backpropogation(node, winner);
		}
		MCTSNode best = root.children.get(0);
		double bestScore = 0;
		for (MCTSNode node : root.children) {
			// double score = node.visits;
			double score = (double) node.wins / node.visits;
			if (score > bestScore) {
				bestScore = score;
				best = node;
			}
		}
		System.out.println(best + ", " + root.visits);
		return new Coordinate[] { best.move, best.erase };
	}

	public MCTSNode selection(MCTSNode node) {
		while (node.children.size() > 0) {
			MCTSNode best = null;
			double bestScore = Double.NEGATIVE_INFINITY;
			for (MCTSNode child : node.children) {
				double score = child.score();
				if (score == Double.POSITIVE_INFINITY)
					return child;
				else if (score > bestScore) {
					best = child;
					bestScore = score;
				}
			}
			node = best;
		}
		return node;
	}

	public MCTSNode expansion(MCTSNode node) {
		if (node.gameState.getWinner() != null)
			return node;
		for (Coordinate[] opt : node.gameState.allLegal()) {
			MCTSNode child = new MCTSNode(node, opt[0], opt[1]);
			node.children.add(child);
		}
//		Collections.shuffle(node.children);
		Collections.sort(node.children);
		return node.children.get(r.nextInt(node.children.size()));
	}

	public Player simulation(MCTSNode node) { // Totally random simulation
		GameState state = node.gameState;
		while (state.getWinner() == null) {
			List<Coordinate[]> opts = state.allLegal();
			Coordinate[] opt = opts.get(r.nextInt(opts.size()));
			state = state.makeMove(opt[0], opt[1]);
		}
		return state.getWinner();
	}

	public void backpropogation(MCTSNode node, Player winner) {
		while (node != null) {
			if (node.gameState.getCurrentPlayer() != winner)
				node.wins++;
			node.visits++;
			node = node.parent;
		}
	}

	private class MCTSNode implements Comparable<MCTSNode> {

		private GameState gameState;
		private Coordinate move;
		private Coordinate erase;
		private MCTSNode parent;
		private List<MCTSNode> children;
		private int wins, visits;
		private Player active, inactive;

		public MCTSNode(GameState gameState) {
			this.gameState = gameState;
			this.move = null;
			this.erase = null;
			parent = null;
			children = new ArrayList<MCTSNode>();
			wins = 0;
			visits = 0;
			active = gameState.getCurrentPlayer();
			inactive = gameState.getInactivePlayer();
		}

		public MCTSNode(MCTSNode parent, Coordinate move, Coordinate erase) {
			this(parent.gameState.makeMove(move, erase));
			this.parent = parent;
			this.move = move;
			this.erase = erase;
		}

		public double score() {
			if (visits == 0)
				return Double.POSITIVE_INFINITY;
			return (double) wins / visits + C * Math.sqrt(Math.log(parent.visits) / visits);
		}

		@Override
		public String toString() {
			return "MCTSNode [children=" + children.size() + ", wins=" + wins + ", visits=" + visits + ", pwin="
					+ 100 * wins / visits + "%]";
		}

		@Override
		public int compareTo(MCTSNode other) {
			return (gameState.legalMoves(inactive).size() - gameState.legalMoves(active).size())
					- (other.gameState.legalMoves(inactive).size() - other.gameState.legalMoves(active).size());
		}

	}

}
