package com.liangfang.ghosts.graphics;

import java.util.Arrays;

import com.liangfang.ghosts.client.Equality;
import com.liangfang.ghosts.client.Piece;

/**
 * A representation of a piece image.
 */
public final class PieceImage extends Equality {

	enum PieceImageKind {
		WHITEBACK, 			// cannot determine which kind of ghost it is
		BLACKBACK, 			// cannot determine which kind of ghost it is
//		EMPTY, 
		FRONT, 			// can see ghost kind
	}

	public static class Factory {
//		public static PieceImage getEmpty() {
//			return new PieceImage(PieceImageKind.EMPTY, null);
//		}

		public static PieceImage getWhiteBackOfPieceImage() {
			return new PieceImage(PieceImageKind.WHITEBACK, null);
		}
		
		public static PieceImage getBlackBackOfPieceImage() {
			return new PieceImage(PieceImageKind.BLACKBACK, null);
		}

		public static PieceImage getFrontOfPieceImage(Piece piece) {
			return new PieceImage(PieceImageKind.FRONT, piece);
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
//		case EMPTY:
//			return "pieces/empty.png";
		case FRONT:
			return "pieces/" + piece2str().toUpperCase() + ".png";

		default:
			return "Forgot kind=" + kind;
		}
	}
}
