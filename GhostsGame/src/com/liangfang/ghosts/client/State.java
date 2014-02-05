package com.liangfang.ghosts.client;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Arrays;

import com.google.common.base.Objects;

public class State {
	public static final int ROW = 6;
	public static final int COLUMN = 6;
	private Result finalResult;							//null when no result
	private Piece[][] board = new Piece[ROW][COLUMN];
	private SquareType turn = SquareType.WHITE;					//used to determine whose turn
	
	public State() {
		for (int i = 0; i < ROW; i++) {
			for (int j = 0; j < COLUMN; j++) {
				if ((i == 0 || i == 5) && (j == 0 || j == 5)) {
					setPiece(i, j, new Piece(SquareType.EXIT, null));
				}
				else {					//Temporarily we set all other square to SquareType.EMPTY !!!
					setPiece(i, j, new Piece(SquareType.EMPTY, null));
				}
			}
		}
	}
	
	public State(SquareType turn, Piece[][] board, Result finalResult) {
		this.turn = turn;
		this.finalResult = finalResult;
		for (int i = 0; i < ROW; i++) {
			for (int j = 0; j < COLUMN; j++) {
				this.board[i][j] = board[i][j];
			}
		}
	}
/*	
	public void makeMove(Move move) {		//should be in stateChanger class ??????
		// Not yet implemented 
	}
*/	
	public void setPiece(int row, int col, Piece piece) {
		board[row][col] = piece;
	}
	
	public Piece getPiece(Position p) {
		return getPiece(p.getRow(), p.getCol());
	}
	
	public Piece getPiece(int row, int col) {
		return board[row][col];
	}
	
	public void setResult(Result result) {
		this.finalResult = result;
	}
	
	public Result getResult() {
		return finalResult;
	}
	
	public SquareType getTrun() {
		return turn;
	}
	
	public void setTurn(SquareType turn) {
			this.turn = checkNotNull(turn);
	}
	
	public State copy() {
	    return new State(turn, board, finalResult);
	}
	
	public int hashCode() {
		return Objects.hashCode(turn, Arrays.deepHashCode(board), finalResult);
	}
	
	public boolean equals(Object obj) {
	    if (this == obj) return true;
	    if (obj == null) return false;
	    if (!(obj instanceof State)) return false;
	    State other = (State) obj;
	    return Arrays.deepEquals(board, other.board)
	      && Objects.equal(turn, other.turn)
	      && Objects.equal(finalResult, other.finalResult);
	  }

	  public String toString() {
	    return "State [" 
	        + "turn=" + turn + ", " 
	        + "board=" + Arrays.deepToString(board)
	        + (finalResult != null ? "finalResult=" + finalResult + ", " : "")
	        + "]";
	  }
}
