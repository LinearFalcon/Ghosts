package org.ghosts.client;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.ghosts.client.Move.MoveType;

import com.google.common.base.Optional;
import com.google.common.collect.Lists;

public class StateExplorer {
	GhostsLogic ghostsLogic = new GhostsLogic();
	
	/*
	 * return all possible moves of this state according to turn
	 */
	public Set<Move> getPossibleMoves(GhostsState state) {
	    Set<Move> possibleMoves = new HashSet<Move>();
	    Set<Position> possiblePiecePositions = new HashSet<Position>();

	    possiblePiecePositions = getPossiblePiecePositions(state);
	    for (Position piecePostion : possiblePiecePositions) {
	      Set<Move> possibleMovesOfPiece = getPossibleMovesFromPosition(state, piecePostion);
	      for (Move move : possibleMovesOfPiece) {
	        possibleMoves.add(move);
	      }
	    }
	    return possibleMoves;
	}

	public Set<Move> getPossibleMovesFromPosition(GhostsState state, Position piecePosition) {
		Set<Move> moves = new HashSet<Move>();								
		Position origin = piecePosition;
		int row = origin.getRow();
		int col = origin.getCol();
		List<Position> list = Lists.newArrayList();
		list.add(new Position(row - 1, col));
		list.add(new Position(row + 1, col));
		list.add(new Position(row, col - 1));
		list.add(new Position(row, col + 1));
		for (Position pos : list) {
			if (isInsideBoard(pos) && !sameSideCapture(state, pos)) {
				if (isExitMove(state, origin, pos)) {
					moves.add(new Move(origin, pos, MoveType.EXIT));
				} else if (isCaptureMove(state, pos)) {
					moves.add(new Move(origin, pos, MoveType.CAPTURE));
				} else {
					moves.add(new Move(origin, pos, MoveType.MOVE));
				}
			}
		}
		
		return moves;
	}

	private boolean sameSideCapture(GhostsState state, Position end) {
		Map<Position, String> squares = state.getSquares();
		int row = end.getRow();
		int col = end.getCol();
		String pieceStr = squares.get(new Position(row, col));
		if (pieceStr != null) {
			int index = getIndexFromPieceName(pieceStr);
			if ( (state.getTurn().isWhite() && index < 8) || (state.getTurn().isBlack() && index >= 8) ) {
				return true;
			}
		}
		return false;
	}

	private boolean isCaptureMove(GhostsState state, Position pos) {
		Map<Position, String> squares = state.getSquares();
		int row = pos.getRow();
		int col = pos.getCol();
		String pieceStr = squares.get(new Position(row, col));
		if (pieceStr != null) {									 
/*			int index = getIndexFromPieceName(pieceStr);
			if (state.getTurn().isWhite() && index >= 8) {
				return true;
			} else if (state.getTurn().isBlack() && index < 8){
				return true;
			}
			*/
			return true;		// since we already check if sameSideCapture, if there is piece on end position, it must be capture move
		}
		return false;
	}

	// Used in Heuristic class, so in that situation we will set a complete guess state that all pieces are visible (Attention! Not to used in normal case!!!!!!!!!!!!!)
	private boolean isExitMove(GhostsState state, Position origin, Position pos) {		
		Color turn = state.getTurn();
		if (turn.isWhite()) {
			if (pos.getRow() == 0 && (pos.getCol() == 0 || pos.getCol() == 5)) {
				Piece p = getPieceFromOrigin(state, origin);
				if(p.isWhitePiece())
					return true;
			}
		} else {
			if (pos.getRow() == 5 && (pos.getCol() == 0 || pos.getCol() == 5)) {
				Piece p = getPieceFromOrigin(state, origin);
				if(p.isWhitePiece())
					return true;
			}
		}
		return false;
	}

	// return player's piece of current turn
	public Set<Position> getPossiblePiecePositions(GhostsState state) {
		Set<Position> piecePositions = new HashSet<Position>();
		Color turn = state.getTurn();
		Map<Position, String> squares = state.getSquares();
		for (int i = 0; i < 6; i++) {
			for (int j = 0; j < 6; j++) {
				Position pos = new Position(i, j);
				String pieceName = squares.get(pos);
				if (pieceName != null) {			// means there is a piece on this position
					int index = getIndexFromPieceName(pieceName);
					if (turn.isWhite() && index < 8) {
						piecePositions.add(pos);
					} else if (turn.isBlack() && index >= 8) {
						piecePositions.add(pos);
					}
				}
			}
		}
		
		return piecePositions;
	}
	
	// return int value of the number after "P" in Px/xx
	public int getIndexFromPieceName(String piecename) {
		if (piecename.length() == 2)
			return (int)(piecename.charAt(1) - '0');
		else {			// P10~P15
			int tenth = (int)(piecename.charAt(1) - '0') * 10;
			return tenth + (int)(piecename.charAt(2) - '0');
		}
	}
	
	public boolean isInsideBoard(Position p) {
		int row = p.getRow();
		int col = p.getCol();
		return row >= 0 && row <= 5 && col >= 0 && col <= 5;
	}
	
	public Piece getPieceFromOrigin(GhostsState state, Position origin) {
		int index = getIndexFromPieceName(state.getSquares().get(origin));
		if (state.getPieces().get(index).isPresent())
			return state.getPieces().get(index).get();
		return null;
	}
}
