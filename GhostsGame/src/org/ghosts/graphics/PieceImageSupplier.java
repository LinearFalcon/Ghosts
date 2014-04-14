package org.ghosts.graphics;

import org.ghosts.client.Piece;

import com.google.gwt.resources.client.ImageResource;

/**
 * A mapping from Piece to its ImageResource. The images are all of size ____
 * (width x height).
 */
public class PieceImageSupplier {
	private final PieceImages pieceImages;

	public PieceImageSupplier(PieceImages pieceImages) {
		this.pieceImages = pieceImages;
	}

	public ImageResource getResource(PieceImage pieceImage) {
		switch (pieceImage.kind) {
		case WHITEBACK:
			return getWhiteBackOfPieceImage();
		case BLACKBACK:
			return getBlackBackOfPieceImage();
		case BACKGROUND:
			return getBackground();
		case FRONT:
			return getFrontOfPieceImage(pieceImage.piece);
		case LEFTEXIT:
			return getLeftExit();
		case RIGHTEXIT:
			return getRightExit();
		case DEPLOYPLACE:
			return getDeployPlace();
		default:
			throw new RuntimeException("Forgot kind=" + pieceImage.kind);
		}
	}

	private ImageResource getFrontOfPieceImage(Piece piece) {		
		if (piece.getPieceKind() == "WGood" || piece.getPieceKind() == "BGood") {
			return pieceImages.good();
		} else if (piece.getPieceKind() == "WEvil" || piece.getPieceKind() == "BEvil") {
			return pieceImages.evil();
		} else {
			throw new RuntimeException("Forgot pieceKind=" + piece.getPieceKind());
		}
	}

	private ImageResource getBackground() {											// Don't know if we need empty ???*******************
		return pieceImages.backround();
	}

	private ImageResource getWhiteBackOfPieceImage() {
		return pieceImages.whiteback();
	}

	private ImageResource getBlackBackOfPieceImage() {
		return pieceImages.blackback();
	}
	
	private ImageResource getRightExit() {
		return pieceImages.rightexit();
	}

	private ImageResource getLeftExit() {
		return pieceImages.leftexit();
	}
	
	private ImageResource getDeployPlace() {
		return pieceImages.deployplace();
	}
}
