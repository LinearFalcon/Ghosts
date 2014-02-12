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
		if (movingPiece.getColor() != state.getTurn())
            throw new IllegalMove();
		
		Piece attackedPiece = state.getPiece(move.getDestination());
		//not mover's turn, throw error
        if (attackedPiece != null && attackedPiece.getColor() == state.getTurn())
                throw new IllegalMove();

        if (isMoveLegal(state, move.getStart(), move.getDestination())) 
                executeMove(state, move);               
        else
                throw new IllegalMove();

	}

	public void executeMove(State state, Move move) {
		Position startPos = move.getStart();
		Position endPos = move.getDestination();
		Piece movingPiece = state.getPiece(startPos);
        Piece destroyedPiece = state.getPiece(endPos);
        PieceKind movingKind = movingPiece.getPieceKind();
        
        state.setLastCapturedPiece(destroyedPiece);
        state.setPiece(startPos, null);
        state.setPiece(endPos, movingPiece);
        state.setTurn(movingPiece.getColor().getOpposite());
        
        //According to example, I should use StateExploer to determine ******************* 
        if (doesPlayerExit(state, movingPiece.getColor())) {
        	state.setResult(new Result(movingPiece.getColor(), GameResultReason.EXIT_SUCCEED));
        }
        else if (doesOpposingGoodAllCaptured(state, movingPiece.getColor())){
        	state.setResult(new Result(movingPiece.getColor(), GameResultReason.CAPTURE_ALL_GOOD_GHOST));
        }
        else if (doesOpposingEvilAllCaptured(state, movingPiece.getColor())){
        	state.setResult(new Result(movingPiece.getColor().getOpposite(), GameResultReason.CAPTURE_ALL_EVIL_GHOST));
        }
	}
	
	public boolean isMoveLegal(State state, Position from, Position to) {
		if (from.equals(to))
            return false;
		
		//capture ghost of own side, illegal move
		if (state.getPiece(to) != null && state.getPiece(from).getColor() 
				== state.getPiece(to).getColor())
            return false;
		
		int startRow = from.getRow(), startCol = from.getCol();
		int endRow = to.getRow(), endCol = to.getCol();    
		//if ghost moves more than one square, illegal move
		if ((Math.abs(startRow - endRow) > 1) || (Math.abs(startCol - endCol) > 1)) {
			return false;
		}
		return true;
	}
	
	public boolean doesPlayerExit(State state, Color moverColor) {
		Piece leftup = state.getPiece(new Position(5,0));
		Piece rightup = state.getPiece(new Position(5,5));
		Piece leftdown = state.getPiece(new Position(0,0));
		Piece rightdown = state.getPiece(new Position(0,5));
		
		if (moverColor == Color.W) {
			if (leftup != null && leftup.getColor() == moverColor 
					&& leftup.getPieceKind() == PieceKind.good) {
				return true;
			}
			else if (rightup != null && rightup.getColor() == moverColor
					&& rightup.getPieceKind() == PieceKind.good) {
				return true;
			}
			else {
				return false;
			}
		}
		else {
			if (leftdown != null && leftdown.getColor() == moverColor
					&& leftdown.getPieceKind() == PieceKind.good) {
				return true;
			}
			else if (rightdown != null && rightdown.getColor() == moverColor
					&& rightdown.getPieceKind() == PieceKind.good) {
				return true;
			}
			else {
				return false;
			}
		}
	}
	
	public boolean doesOpposingGoodAllCaptured(State state, Color moverColor) {
		int count = 0;
		for (int i = 0; i < State.ROW; i++) {
			for (int j = 0; j < State.COLUMN; j++) {
				Piece piece = state.getPiece(new Position(i,j));
				if (piece != null && 
						piece.getPieceKind() == PieceKind.good && 
						piece.getColor() == moverColor.getOpposite()) {
					count++;
				}
			}
		}
		if (count > 0)
			return false;
		return true;
	}
	
	public boolean doesOpposingEvilAllCaptured(State state, Color moverColor) {
		int count = 0;
		for (int i = 0; i < State.ROW; i++) {
			for (int j = 0; j < State.COLUMN; j++) {
				Piece piece = state.getPiece(new Position(i,j));
				if (piece != null && 
						piece.getPieceKind() == PieceKind.evil && 
						piece.getColor() == moverColor.getOpposite()) {
					count++;
				}
			}
		}
		if (count > 0)
			return false;
		return true;
	}
}
