import processing.core.PApplet;

public abstract class Player {

	private int color;
	private IsolaGame game;
	protected PApplet parent;

	public Player(PApplet parent, int color) {
		this.parent = parent;
		this.color = color;
	}

	public int getColor() {
		return color;
	}

	public void setGame(IsolaGame game) {
		this.game = game;
	}

	public abstract Coordinate[] getMove(GameState gameState) throws InterruptedException;

}
