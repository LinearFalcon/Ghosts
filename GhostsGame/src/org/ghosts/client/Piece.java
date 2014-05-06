package org.ghosts.client;

//import static com.google.common.base.Preconditions.checkNotNull;

//import com.google.common.base.Objects;

public class Piece extends Equality {
	private String pieceKind;	// "WGood" or "WEvil" or "BGood" or "BEvil"
	private String pieceName;	// "P0" ~ "P15"

	public Piece(String kind, String name) {
		this.pieceKind = kind;
		this.pieceName = name;
	}

	public String getPieceKind() {
		return pieceKind;
	}
	
	public String getPieceName() {
		return pieceName;
	}
	
	public boolean isWhitePiece() {
		return pieceKind.charAt(0) == 'W';
	}
	
	public boolean isBlackPiece() {
		return pieceKind.charAt(0) == 'B';
	}

	@Override
	public Object getId() {
		return pieceKind;
	}
}
