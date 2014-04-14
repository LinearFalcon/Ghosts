package org.ghosts.graphics;

import java.util.Arrays;

import org.ghosts.client.Equality;
import org.ghosts.client.Piece;


/**
 * A representation of a piece image.
 */
public final class PieceImage extends Equality {

	enum PieceImageKind {
		WHITEBACK, 			// cannot determine which kind of ghost it is
		BLACKBACK, 			// cannot determine which kind of ghost it is
		BACKGROUND, 	
		FRONT, 			// can see ghost kind
		LEFTEXIT,
		RIGHTEXIT,
		DEPLOYPLACE,
	}

	public static class Factory {
		public static PieceImage getBackground() {
			return new PieceImage(PieceImageKind.BACKGROUND, null);
		}

		public static PieceImage getWhiteBackOfPieceImage() {
			return new PieceImage(PieceImageKind.WHITEBACK, null);
		}
		
		public static PieceImage getBlackBackOfPieceImage() {
			return new PieceImage(PieceImageKind.BLACKBACK, null);
		}

		public static PieceImage getFrontOfPieceImage(Piece piece) {
			return new PieceImage(PieceImageKind.FRONT, piece);
		}
		
		public static PieceImage getLeftExit() {
			return new PieceImage(PieceImageKind.LEFTEXIT, null);
		}
		
		public static PieceImage getRightExit() {
			return new PieceImage(PieceImageKind.RIGHTEXIT, null);
		}
		
		public static PieceImage getDeployPlace() {
			return new PieceImage(PieceImageKind.DEPLOYPLACE, null);
		}
	}

	public final PieceImageKind kind;
	public final Piece piece;

	private PieceImage(PieceImageKind kind, Piece piece) {
		this.kind = kind;
		this.piece = piece;
	}

	@Override
	public Object getId() {
		return Arrays.asList(kind, piece);
	}

	private String piece2str() {
		return piece.getPieceKind().substring(1, 5);		//only return "Good" or "Evil"
	}

	@Override
	public String toString() {
		switch (kind) {
		case WHITEBACK:
			return "pieces/WHITEBACK.png";
		case BLACKBACK:
			return "pieces/BLACKBACK.png";
		case BACKGROUND:
			return "pieces/BACKGROUND.png";
		case LEFTEXIT:
			return "pieces/LEFTEXIT.png";
		case RIGHTEXIT:
			return "pieces/RIGHTEXIT.png";
		case FRONT:
			return "pieces/" + piece2str().toUpperCase() + ".png";

		default:
			return "Forgot kind=" + kind;
		}
	}
}
