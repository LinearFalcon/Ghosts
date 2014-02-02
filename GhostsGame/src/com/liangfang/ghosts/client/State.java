package com.liangfang.ghosts.client;

enum Color {WHITE, BLACK};

public class State {
	private int ROW = 6;
	private int COLUMN = 6;
	private Result finalResult;
	private Piece[][] board = new Piece[ROW][COLUMN];
	private Color turn = Color.WHITE;
	
}
