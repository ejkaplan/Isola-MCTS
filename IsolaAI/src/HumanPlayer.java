import processing.core.PApplet;

public class HumanPlayer extends Player {

	public HumanPlayer(PApplet parent, int color) {
		super(parent, color);
	}

	@Override
	public Coordinate[] getMove(GameState gameState) throws InterruptedException {
		while (parent.mousePressed) {
			Thread.sleep(1);
		}
		while (!parent.mousePressed) {
			Thread.sleep(1);
		}
		Coordinate move = new Coordinate(
				(int) PApplet.map(parent.mouseX, 0, parent.width, 0, gameState.getBoardWidth()),
				(int) PApplet.map(parent.mouseY, 0, parent.height, 0, gameState.getBoardHeight()));
//		System.out.println(move);
		while (parent.mousePressed) {
			Thread.sleep(1);
		}
		while (!parent.mousePressed) {
			Thread.sleep(1);
		}
		Coordinate erase = new Coordinate(
				(int) PApplet.map(parent.mouseX, 0, parent.width, 0, gameState.getBoardWidth()),
				(int) PApplet.map(parent.mouseY, 0, parent.height, 0, gameState.getBoardHeight()));
//		System.out.println(erase);
		return new Coordinate[] { move, erase };
	}

}
