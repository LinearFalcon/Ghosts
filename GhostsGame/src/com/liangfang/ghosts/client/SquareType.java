package com.liangfang.ghosts.client;

public enum SquareType {
	WHITE, 
	BLACK, 
	EMPTY, 
	EXIT,
	;
	
	public boolean isWhite() {
		return this == WHITE;
	}
	
	public boolean isBlack() {
		return this == BLACK;
	}
	
	public SquareType changeColor() {
		return this == WHITE ? BLACK : WHITE;
	}
	
	public String toString() {
	    return isWhite() ? "W" : "B";
	}
}	//Each square has four types 
