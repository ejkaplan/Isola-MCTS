import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import processing.core.PApplet;

public class GameState {

	private PApplet parent;
	private boolean[][] board;
	private Map<Player, Coordinate> players;
	private Player[] turnOrder;
	private int turn;
	private Player winner;

	public GameState(PApplet parent, Player p0, Player p1) {
		this.parent = parent;
		board = new boolean[7][7];
		players = new HashMap<Player, Coordinate>();
		players.put(p0, new Coordinate(3, 0));
		players.put(p1, new Coordinate(3, 6));
		turnOrder = new Player[] { p0, p1 };
		turn = 0;
		winner = null;
	}

	private GameState(GameState gs) {
		this.parent = gs.parent;
		board = new boolean[gs.board.length][gs.board[0].length];
		for (int x = 0; x < board.length; x++) {
			for (int y = 0; y < board[0].length; y++) {
				board[x][y] = gs.board[x][y];
			}
		}
		players = new HashMap<Player, Coordinate>();
		for (Player p : gs.players.keySet()) {
			players.put(p, gs.players.get(p));
		}
		turnOrder = Arrays.copyOf(gs.turnOrder, 2);
		turn = gs.turn;
	}

	public Player getWinner() {
		if (winner != null)
			return winner;
		else if (legalMoves(getCurrentPlayer()).size() == 0) {
			winner = turnOrder[1 - turn];
			return winner;
		} else {
			return null;
		}
	}

	public Player getCurrentPlayer() {
		return turnOrder[turn];
	}

	public Coordinate getPosition(Player p) {
		return players.get(p);
	}

	public Coordinate getEnemyPosition(Player p) {
		Player other = turnOrder[0] == p ? turnOrder[1] : turnOrder[0];
		return players.get(other);
	}

	public int getBoardWidth() {
		return board.length;
	}

	public int getBoardHeight() {
		return board[0].length;
	}

	public boolean isSquareRemoved(int x, int y) {
		return board[x][y];
	}

	public GameState makeMove(Coordinate move, Coordinate erase) {
		Player currPlayer = turnOrder[turn];
		if (board[erase.getX()][erase.getY()] || !legalMoves(currPlayer).contains(move) || move.equals(erase)) {
			winner = turnOrder[1 - turn];
			System.out.println(
					"Illegal move: Can't move from " + players.get(currPlayer) + " to " + move + " and erase " + erase);
			return this;
		}
		GameState newState = new GameState(this);
		newState.players.put(currPlayer, move);
		newState.board[erase.getX()][erase.getY()] = true;
		newState.turn = (turn + 1) % 2;
		return newState;
	}

	public void display() {
		float squareW = (float) parent.width / board.length;
		float squareH = (float) parent.height / board[0].length;
		for (int x = 0; x < board.length; x++) {
			for (int y = 0; y < board[0].length; y++) {
				float squareX = PApplet.map(x, 0, board.length, 0, parent.width);
				float squareY = PApplet.map(y, 0, board[0].length, 0, parent.height);
				parent.fill(board[x][y] ? 50 : 200);
				parent.rect(squareX, squareY, squareW, squareH);
			}
		}
		for (Player p : players.keySet()) {
			Coordinate c = players.get(p);
			float playerX = PApplet.map(c.getX(), 0, board.length, 0, parent.width) + squareW / 2;
			float playerY = PApplet.map(c.getY(), 0, board[0].length, 0, parent.height) + squareH / 2;
			parent.fill(p.getColor());
			parent.ellipse(playerX, playerY, 0.8f * squareW, 0.8f * squareH);
			if (getCurrentPlayer() == p) {
				parent.fill(127.5f + 127.5f * PApplet.sin(5 * PApplet.radians(parent.frameCount)), 100);
				parent.ellipse(playerX, playerY, 0.8f * squareW, 0.8f * squareH);
			}
		}
		if (getWinner() != null) {
			parent.fill(0, 200);
			parent.noStroke();
			parent.rect(0, 0, parent.width, parent.height);
			parent.textAlign(PApplet.CENTER, PApplet.CENTER);
			parent.textSize(50);
			parent.fill(winner.getColor());
			parent.text("Winner", parent.width / 2, parent.height / 2);
		}
	}

	public List<Coordinate[]> allLegal() {
		List<Coordinate[]> legal = new ArrayList<Coordinate[]>();
		if (getWinner() != null) return legal;
		for (Coordinate m : legalMoves(getCurrentPlayer())) {
			for (int x = 0; x < getBoardWidth(); x++) {
				for (int y = 0; y < getBoardHeight(); y++) {
					Coordinate e = new Coordinate(x, y);
					if (!e.equals(m) && !e.equals(getEnemyPosition(getCurrentPlayer())) && !board[x][y])
						legal.add(new Coordinate[] { m, e });
				}
			}
		}
		return legal;
	}

	/**
	 * Checks if a space would be legal to move to.
	 * 
	 * @param destination
	 * @return true if the space is legal to move to
	 */
	public boolean legalMove(Coordinate destination) {
		return destination.getX() >= 0 && destination.getX() < board.length && destination.getY() >= 0
				&& destination.getY() < board[0].length && !board[destination.getX()][destination.getY()]
				&& !players.values().contains(destination);
	}

	/**
	 * Finds all the legal places where the given player could move
	 * 
	 * @param p
	 * @return A Set of spaces where that player could move
	 */
	public Set<Coordinate> legalMoves(Player p) {
		Coordinate loc = players.get(p);
		Set<Coordinate> out = new HashSet<Coordinate>();
		for (int x = loc.getX() - 1; x <= loc.getX() + 1; x++) {
			for (int y = loc.getY() - 1; y <= loc.getY() + 1; y++) {
				if (x == loc.getX() && y == loc.getY())
					continue;
				Coordinate neighbor = new Coordinate(x, y);
				if (legalMove(neighbor))
					out.add(neighbor);
			}
		}
		return out;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + Arrays.deepHashCode(board);
		result = prime * result + ((players == null) ? 0 : players.hashCode());
		result = prime * result + turn;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		GameState other = (GameState) obj;
		if (!Arrays.deepEquals(board, other.board))
			return false;
		if (players == null) {
			if (other.players != null)
				return false;
		} else if (!players.equals(other.players))
			return false;
		if (turn != other.turn)
			return false;
		return true;
	}

}
