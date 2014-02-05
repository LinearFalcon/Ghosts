package com.liangfang.ghosts.client;

public class StateChangerImpl implements StateChanger {

	@Override
	public void makeMove(State state, Move move) throws IllegalMove {
		if (state.getResult() != null)
            throw new IllegalMove();
    
		if (move.getStart().getRow() < 0 || move.getStart().getRow() >= State.ROW
					|| move.getStart().getCol() < 0 
					|| move.getStart().getCol() >= State.COLUMN)
			throw new IllegalMove();
		if (move.getDestination().getRow() < 0 || move.getDestination().getRow() >= State.ROW
					|| move.getDestination().getCol() < 0 
					|| move.getDestination().getCol() >= State.COLUMN)
            throw new IllegalMove();
    
		Piece movingPiece = state.getPiece(move.getStart());
		if (movingPiece == null)
            throw new IllegalMove();
		if (movingPiece.gettype() != state.getTrun())
            throw new IllegalMove();

	}

}
