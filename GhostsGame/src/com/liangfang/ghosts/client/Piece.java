package com.liangfang.ghosts.client;

//import static com.google.common.base.Preconditions.checkNotNull;

//import com.google.common.base.Objects;

public class Piece {
	private String pieceString;

	public Piece(String str) {
//		this.pieceString = checkNotNull(str);
		this.pieceString = str;
	}

	public String getPieceString() {
		return pieceString;
	}
	
}
