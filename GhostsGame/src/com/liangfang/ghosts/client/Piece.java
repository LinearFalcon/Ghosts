package com.liangfang.ghosts.client;

//import static com.google.common.base.Preconditions.checkNotNull;

//import com.google.common.base.Objects;

public class Piece {
	private String pieceKind;

	public Piece(String str) {
//		this.pieceString = checkNotNull(str);
		this.pieceKind = str;
	}

	public String getPieceKind() {
		return pieceKind;
	}
	
	public boolean isWhitePiece() {
		return pieceKind.charAt(0) == 'W';
	}
	
	public boolean isBlackPiece() {
		return pieceKind.charAt(0) == 'B';
	}
}
