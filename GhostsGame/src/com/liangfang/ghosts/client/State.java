package com.liangfang.ghosts.client;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Arrays;

import com.google.common.base.Objects;

public class State {
	public static final int ROW = 6;
	public static final int COLUMN = 6;
	private Result finalResult;							//null when no result
	private Piece[][] board = new Piece[ROW][COLUMN];
	private Color turn = Color.WHITE;					//used to determine whose turn
	
	/*
	 * lastCapturedPiece is the piece which is captured in last move.
	 * set to null when no piece captured
	 */
	private Piece lastCapturedPiece;		//Not sure it is good design???????????????????
	
	public State() {
	}
	
	public State(Color turn, Piece[][] board, Result finalResult, Piece lastCapturedPiece) {
		this.turn = turn;
		this.finalResult = finalResult;
		this.lastCapturedPiece = lastCapturedPiece;
		for (int i = 0; i < ROW; i++) {
			for (int j = 0; j < COLUMN; j++) {
				this.board[i][j] = board[i][j];
			}
		}
	}
	
	public void setPiece(Position pos, Piece piece) {
		setPiece(pos.getRow(), pos.getCol(), piece);
	}
	
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
	
	public Color getTurn() {
		return turn;
	}
	
	public void setTurn(Color turn) {
		this.turn = checkNotNull(turn);
	}
	
	public void setLastCapturedPiece(Piece piece) {
		this.lastCapturedPiece = piece;
	}
	
	public Piece getLastCapturedPiece() {
		return lastCapturedPiece;
	}
	
	public State copy() {
	    return new State(turn, board, finalResult, lastCapturedPiece);
	}
	
	public int hashCode() {
		return Objects.hashCode(turn, Arrays.deepHashCode(board), finalResult, lastCapturedPiece);
	}
	
	public boolean equals(Object obj) {
	    if (this == obj) return true;
	    if (obj == null) return false;
	    if (!(obj instanceof State)) return false;
	    State other = (State) obj;
	    return Arrays.deepEquals(board, other.board)
	      && Objects.equal(turn, other.turn)
	      && Objects.equal(finalResult, other.finalResult)
	      && Objects.equal(lastCapturedPiece, other.lastCapturedPiece);
	  }

	  public String toString() {
	    return "State [" 
	        + "turn=" + turn + ", " 
	        + "board=" + Arrays.deepToString(board)
	        + (finalResult != null ? "finalResult=" + finalResult + ", " : "")
	        + (lastCapturedPiece != null ? "lastCapturedPiece=" + lastCapturedPiece + ", " : "")
	        + "]";
	  }
}
