package com.liangfang.ghosts.client;

import org.junit.Before;

public abstract class AbstractStateChangerTest {		//do some pre-stuff before JUnit test
	protected State start;
	protected StateChanger stateChanger;

	public abstract StateChanger getStateChanger();
	  
	@Before
	public void setup() {
		start = new State();
	    final StateChanger impl = getStateChanger();	
	    stateChanger = new StateChanger() {
	    	@Override
	    	public void makeMove(State state, Move move) throws IllegalMove {
	    		assertStatePossible(state);
	    		impl.makeMove(state, move);
	    	}
	    };
	}

	public static void assertStatePossible(State state) {
		int blackGood = 0, blackEvil = 0,
			whiteGood = 0, whiteEvil = 0;
		
	    for (int r = 0; r < 6; r++) {
	      for (int c = 0; c < 6; c++) {
	        Piece piece = state.getPiece(r, c);
	        if (piece == null) {
	          continue;
	        }
	        else if (piece.getColor() == Color.BLACK) {
	        	if (piece.getPieceKind() == PieceKind.good) {
	        		blackGood++;
	        	}
	        	else {
	        		blackEvil++;
	        	}
	        }
	        else {
	        	if (piece.getPieceKind() == PieceKind.good) {
	        		whiteGood++;
	        	}
	        	else {
	        		whiteEvil++;
	        	}
	        }
	      }
	    }
	    check(blackGood <= 4, "Black player can only have at most 4 good ghosts");
	    check(blackEvil <= 4, "Black player can only have at most 4 evil ghosts");
	    check(whiteGood <= 4, "White player can only have at most 4 good ghosts");
	    check(whiteEvil <= 4, "White player can only have at most 4 evil ghosts");
	  }
	  
	private static void check(boolean condition, String message) {
		if (!condition) {
			throw new RuntimeException(message);
	    }
	}
}
