// Base codes copied from http://bit.ly/1hnmalY

//Copyright 2012 Google Inc.
//
//Licensed under the Apache License, Version 2.0 (the "License");
//you may not use this file except in compliance with the License.
//You may obtain a copy of the License at
//
//http://www.apache.org/licenses/LICENSE-2.0
//
//Unless required by applicable law or agreed to in writing, software
//distributed under the License is distributed on an "AS IS" BASIS,
//WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
//See the License for the specific language governing permissions and
//limitations under the License.
////////////////////////////////////////////////////////////////////////////////

package org.ghosts.ai;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.ghosts.client.GhostsState;
import org.ghosts.client.Move;
import org.ghosts.client.Piece;
import org.ghosts.client.Position;
import org.ghosts.client.StateExplorer;

import com.google.common.base.Optional;

public class Heuristic {
	
	// We just assume if player has more white ghost, he has more chance to win
	private static final int ValueOfWhiteGhost = 3;
	private static final int ValueOfBlackGhost = 1;
	public final StateExplorer stateExplorer = new StateExplorer();
	
	public Heuristic() {
	}
	
	/**
	 * Get the value of the current state of AI player(Black).
	 * Since use guess state, so all pieces are visible
	 * 
	 * Higher value means the black has a better position. When the black wins you can return
     * {@link Integer#MAX_VALUE}, and when the black loses you can return {@link Integer#MIN_VALUE}.
	 * 
	 * @param ghostsState The state for evaluation.
	 * @return stateValue The value of the state.
	 */
	public int getStateValue(final GhostsState ghostsState) { 
		int stateValue = 0;
		List<Optional<Piece>> pieces = ghostsState.getPieces();
		
		// The game is over
	    if (hasGameEnded(ghostsState)) {
	    	if (ifBlackAIExit(ghostsState)) {
	    		return Integer.MAX_VALUE;
	    	} else {
	    		return Integer.MIN_VALUE;
	    	}
	    }
	    
	    // count number of existing white and black pieces
	    int numOfGoodForWhite = 0;
	    int numOfEvilForWhite = 0;
	    int numOfGoodForBlack = 0;
	    int numOfEvilForBlack = 0;
	   
	    for (int i = 0; i < 16; i++) {
	    	if (pieces.get(i).isPresent()) {
	    		if (pieces.get(i).get().isWhitePiece()) {
	    			if (pieces.get(i).get().getPieceKind().charAt(1) == 'G') {		// WGood
	    				numOfGoodForWhite++;
	    			} else {														// WEvil
	    				numOfEvilForWhite++;
	    			}
	    		} else {
	    			if (pieces.get(i).get().getPieceKind().charAt(1) == 'G') {		// BGood
	    				numOfGoodForBlack++;
	    			} else {														// BEvil
	    				numOfEvilForBlack++;
	    			}
	    		}
	    	}
	    }
	    stateValue = (numOfGoodForWhite - numOfGoodForBlack) * ValueOfWhiteGhost 
	    		   + (numOfEvilForWhite - numOfEvilForBlack) * ValueOfBlackGhost;		
		return stateValue;
	}
	

	/**
	 * Get all possible moves and reorder them.
	 * Priority 1: Exit. Exit is the win move.
	 * Priority 2: Capture. Because capture could effectively reduce opponent's winning odd
	 * Priority 3: Move. Regular move to empty position
	 * 
	 * @param ghostsState The current state.
	 * @return orderedMoves The ordered all possible moves.
	 */
	public Iterable<Move> getOrderedMoves(final GhostsState ghostsState) {
		
		List<Move> orderedMoves = new ArrayList<Move>();
	    List<Move> captureMoves = new ArrayList<Move>();
	    List<Move> exitMoves = new ArrayList<Move>();
	    List<Move> moveMoves = new ArrayList<Move>();
	    
	    // Get all possible moves
	    Set<Move> allPossibleMoves = stateExplorer.getPossibleMoves(ghostsState);
	    
	    for (Move move : allPossibleMoves) {
	        if (move.getType() == Move.MoveType.EXIT) {
	        	exitMoves.add(move);
	        } else if (move.getType() == Move.MoveType.CAPTURE) {
	        	captureMoves.add(move);
	        } else {
	          moveMoves.add(move);
	        }
	    }
	    
	    // According to the type of the move, reorder all the moves
	    orderedMoves.addAll(exitMoves);
	    orderedMoves.addAll(captureMoves);
	    orderedMoves.addAll(moveMoves);
	    return orderedMoves;
	}
	
	/*
	 * For heuristic and alpha-beta pruning use, so every piece is visible
	 * Check four exit square and see if some player has exit 
	 */
	public boolean hasGameEnded(final GhostsState state) {
		Position leftExitForW = new Position(0, 0);
		Position rightExitForW = new Position(0, 5);
		Position leftExitForB = new Position(5, 0);
		Position rightExitForB = new Position(5, 5);
		Map<Position, String> squares = state.getSquares();
		List<Optional<Piece>> pieces = state.getPieces();
		String pieceStr;
		
		if (squares.get(leftExitForW) != null) {
			pieceStr = squares.get(leftExitForW);
			int index = stateExplorer.getIndexFromPieceName(pieceStr);
			if (index < 8 && pieces.get(index).get().isWhitePiece()) {
				return true;
			}
		}
		if (squares.get(rightExitForW) != null) {
			pieceStr = squares.get(rightExitForW);
			int index = stateExplorer.getIndexFromPieceName(pieceStr);
			if (index < 8 && pieces.get(index).get().isWhitePiece()) {
				return true;
			}
		}
		if (squares.get(leftExitForB) != null) {
			pieceStr = squares.get(leftExitForB);
			int index = stateExplorer.getIndexFromPieceName(pieceStr);
			if (index >= 8 && pieces.get(index).get().isWhitePiece()) {
				return true;
			}
		}
		if (squares.get(rightExitForB) != null) {
			pieceStr = squares.get(rightExitForB);
			int index = stateExplorer.getIndexFromPieceName(pieceStr);
			if (index >= 8 && pieces.get(index).get().isWhitePiece()) {
				return true;
			}
		}
		
		return false;
	}
	
	// check if Black player (AI) has exit
	public boolean ifBlackAIExit(final GhostsState state) {
		Position leftExitForB = new Position(5, 0);
		Position rightExitForB = new Position(5, 5);
		Map<Position, String> squares = state.getSquares();
		List<Optional<Piece>> pieces = state.getPieces();
		String pieceStr;
		if (squares.get(leftExitForB) != null) {
			pieceStr = squares.get(leftExitForB);
			int index = stateExplorer.getIndexFromPieceName(pieceStr);
			if (index >= 8 && pieces.get(index).get().isWhitePiece()) {
				return true;
			}
		}
		if (squares.get(rightExitForB) != null) {
			pieceStr = squares.get(rightExitForB);
			int index = stateExplorer.getIndexFromPieceName(pieceStr);
			if (index >= 8 && pieces.get(index).get().isWhitePiece()) {
				return true;
			}
		}
		return false;
	}
}
