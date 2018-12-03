import java.util.List;
import java.util.Random;

import processing.core.PApplet;

public class RandomPlayer extends Player {

	private Random r;

	public RandomPlayer(PApplet parent, int color) {
		super(parent, color);
		r = new Random();
	}

	@Override
	public Coordinate[] getMove(GameState gameState) throws InterruptedException {
		List<Coordinate[]> opts = gameState.allLegal();
		return opts.get(r.nextInt(opts.size()));
	}

}
