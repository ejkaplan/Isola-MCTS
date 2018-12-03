import processing.core.PApplet;

public class IsolaGame {

	private GameState gameState;
	private PApplet parent;
	public boolean ready;

	public IsolaGame(PApplet parent, Player p0, Player p1) {
		this.parent = parent;
		gameState = new GameState(parent, p0, p1);
		p0.setGame(this);
		p1.setGame(this);
		ready = true;
	}

	public void update() {
		if (ready && gameState.getWinner() == null) {
			makeMove();
		}
	}

	public void display() {
		gameState.display();
	}

	public void makeMove() {
		ready = false;
		Runnable r = new Runnable() {
			public void run() {
				try {
					Coordinate[] stuff = gameState.getCurrentPlayer().getMove(gameState);
					GameState newState = gameState.makeMove(stuff[0], stuff[1]);
					gameState = newState;
					ready = true;
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		};
		Thread t = new Thread(r);
		t.start();
	}

}
