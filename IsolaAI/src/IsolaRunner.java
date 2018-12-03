import processing.core.PApplet;

public class IsolaRunner extends PApplet {

	public static void main(String[] args) {
		PApplet.main("IsolaRunner");
	}

	private IsolaGame game;

	public void settings() {
		size(500, 500);
	}

	public void setup() {
		game = new IsolaGame(this, new HumanPlayer(this, color(255,0,0)), new MCTSPlayer(this, color(0,0,255)));
	}

	public void draw() {
		game.display();
		game.update();
	}

}
