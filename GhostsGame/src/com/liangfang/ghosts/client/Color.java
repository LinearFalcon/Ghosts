package com.liangfang.ghosts.client;

public enum Color {
	WHITE, 
	BLACK, 
	;
	
	public boolean isWhite() {
		return this == WHITE;
	}
	
	public boolean isBlack() {
		return this == BLACK;
	}
	
	public Color getOpposite() {
		return this == WHITE ? BLACK : WHITE;
	}
	
	public String toString() {
	    return isWhite() ? "W" : "B";
	}
}	 
