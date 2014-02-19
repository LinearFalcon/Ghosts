package com.liangfang.ghosts.client;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import com.liangfang.ghosts.client.GameApi.Delete;
import com.liangfang.ghosts.client.GameApi.EndGame;
import com.liangfang.ghosts.client.GameApi.Operation;
import com.liangfang.ghosts.client.GameApi.Set;
import com.liangfang.ghosts.client.GameApi.SetVisibility;
import com.liangfang.ghosts.client.GameApi.Shuffle;
import com.liangfang.ghosts.client.GameApi.VerifyMove;
import com.liangfang.ghosts.client.GameApi.VerifyMoveDone;

import com.google.common.base.Optional;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

public class GhostsLogic {
	private static final String TURN = "turn";
	private static final String W = "W"; // White hand
	private static final String B = "B"; // Black hand
	private static final String P = "P"; // Piece key (P0...P15)
	private static final String S = "S"; // Square key (S00...S55)
	private static final String WDeployed = "WDeployed"; // white deploy key
	private static final String BDeployed = "BDeployed"; // black deploy key
	private final int wId = 23;
	private final int bId = 24;

	public VerifyMoveDone verify(VerifyMove verifyMove) {
		try {
			checkMoveIsLegal(verifyMove);
			return new VerifyMoveDone();
		} catch (Exception e) {
			return new VerifyMoveDone(verifyMove.getLastMovePlayerId(),
					e.getMessage());
		}
	}

	/** compare whether lastmove's operations equals to expected operations */
	void checkMoveIsLegal(VerifyMove verifyMove) {
		List<Operation> lastMove = verifyMove.getLastMove();
		Map<String, Object> lastState = verifyMove.getLastState();

		if (lastState.isEmpty()) {								                  // White player needs to deploy
			check(doesWhiteDeployValid(lastMove, verifyMove.getPlayerIds().get(0), verifyMove.getPlayerIds().get(1)), lastMove); 
		} else {
		
			GhostsState lastGameState = gameApiStateToGhostsState(lastState);
		
			if (lastGameState.isWhiteDeployed() && !lastGameState.isBlackDeployed()) {// Black player needs to deploy
				check(doesBlackDeployValid(lastMove), lastMove);
			} else {
		
				// Checking the operations are as expected.
				List<Operation> expectedOperations = getExpectedOperations(lastGameState,
						lastMove, verifyMove.getPlayerIds());
				check(expectedOperations.equals(lastMove), expectedOperations, lastMove);

				// We don't need to check that the correct player did the move.
				// However, we do need to check the first move is done by the white player
				if (verifyMove.getLastState().isEmpty()) {
					check(verifyMove.getLastMovePlayerId() == verifyMove.getPlayerIds().get(0));
				}
			}
		}
	}

	@SuppressWarnings("unchecked")
	List<Operation> getExpectedOperations(GhostsState lastState,
			List<Operation> lastMove, List<Integer> playerIds) {
		
		String endSquare = ((Set) lastMove.get(1)).getKey(); 		// "Sxx", end square string S00~S55
		String startSquare = ((Delete) lastMove.get(2)).getKey(); 	// "Sxx", start square string S00~S55
		String movingPiece = (String) ((Set) lastMove.get(1)).getValue(); // "Px/xx", moving piece string, x or xx since could be 10~15

		// check if in lastState, game already end because of all capturing of
		// good or evil ghosts
		check(notGoodOrEvilAllCaptured(lastState), lastState);

		// check if in lastState, game already end because of good ghost is
		// already in exit
		check(!alreadyExit(lastState), lastState);

		// check whether lastMove is valid
		check(!(lastMove.isEmpty() || lastMove.size() < 3), lastMove);

		// check whether lastMove's start and end square is valid
		check(validStartAndEndSquareNumber(startSquare, endSquare),
				startSquare, endSquare);

		// check whether moving piece exists and it is in right color
		check(movingPieceExistAndValid(movingPiece, lastState), movingPiece,
				lastState);

		// check whether moving piece was originally in start Square
		check(movingPieceWasInStartSquare(movingPiece, startSquare, lastState),
				movingPiece, startSquare, lastState);

		// check wheter one ghost captures another ghost in same color
		check(!sameSideCapture(endSquare, lastState), endSquare, lastState);

		// Move to empty square can be either a normal move or successful exit
		if (isEndSquareEmpty(endSquare, lastState)) {
			if (isMovingGoodToExit(movingPiece, endSquare, lastState)) { // exit
				return exitMove(movingPiece, startSquare, endSquare, lastState);
			} else { // normal move to empty square
				return normalMoveToEmptySquare(movingPiece, startSquare, endSquare, lastState);
			}
		} else { // capture happens
			if (isMovingGoodToExit(movingPiece, endSquare, lastState)) { // exit
				return captureAndExit(movingPiece, startSquare, endSquare, lastState);
			} else { // normal move to empty square
				return normalCapture(movingPiece, startSquare, endSquare, lastState);
			}
		}
	}
	
	// Determine if white player initial board and deploy ghosts in valid way
	boolean doesWhiteDeployValid(List<Operation> lastMove, int whitePlayerId, int blackPlayerId) { 
		
		check(lastMove.get(0).equals(new Set(TURN, B)));		// next turn should be black deploy
		// check if white sets all 16 pieces: set(P0,"WGood"), ..., set(P15,"BEvil")
		for (int i = 0; i < 16; i++) {
			check(lastMove.get(i + 1).equals(new Set(P + i, pieceIdToString(i))));
		}

		// check shuffle(P0,...,P7) and shuffle(P8,...,P15)
		check(lastMove.get(17).equals(new Shuffle(getPiecesInRange(0, 7))));
		check(lastMove.get(18).equals(new Shuffle(getPiecesInRange(8, 15))));

		// sets visibility
		for (int i = 0; i < 8; i++) {
			check(lastMove.get(i + 19).equals(new SetVisibility(P + i, ImmutableList
					.of(whitePlayerId))));
		}
		for (int i = 8; i < 16; i++) {
			check(lastMove.get(i + 19).equals(new SetVisibility(P + i, ImmutableList
					.of(blackPlayerId))));
		} 
		
		//check if white deploy his ghosts in valid way
		ArrayList<String> squares = new ArrayList<String>();
		ArrayList<String> pieces = new ArrayList<String>();
		for (int i = 0; i < 8; i++) {			
			String square = ((Set) lastMove.get(i + 35)).getKey();
			String piece = (String)((Set) lastMove.get(i + 35)).getValue();
			squares.add(square);
			pieces.add(piece);
		}
		for (int i = 0; i <= 1; i++) {
			for (int j = 1; j <=4; j++) {
				check(squares.contains((S + i) + j), squares);
			}
		}
		for (int i = 0; i < 8; i++) {
			check(pieces.contains(P + i), pieces);
		}
		
		check(lastMove.get(lastMove.size() - 1).equals(new Set(WDeployed, "true")), lastMove);
		return true;
	}
	
	// Determine if black player deploy ghosts in valid way
	boolean doesBlackDeployValid(List<Operation> lastMove) {
		check(lastMove.get(0).equals(new Set(TURN, W)));		// next turn game begins by white moving first

		//check if black deploy his ghosts in valid way
		ArrayList<String> squares = new ArrayList<String>();
		ArrayList<String> pieces = new ArrayList<String>();
		for (int i = 0; i < 8; i++) {			
			String square = ((Set) lastMove.get(i + 1)).getKey();
			String piece = (String)((Set) lastMove.get(i + 1)).getValue();
			squares.add(square);
			pieces.add(piece);
		}
		for (int i = 4; i <= 5; i++) {
			for (int j = 1; j <=4; j++) {
				check(squares.contains((S + i) + j), squares);
			}
		}
		for (int i = 8; i < 16; i++) {
			check(pieces.contains(P + i), pieces);
		}
		
		check(lastMove.get(lastMove.size() - 1).equals(new Set(BDeployed, "true")), lastMove);
		return true;
	}
	
	// Return exit move operations
	List<Operation> exitMove(String movingPiece, String startSquare, 
			String endSquare, GhostsState lastState) {
		Color turn = lastState.getTurn();
		List<Optional<Piece>> pieces = lastState.getPieces();
		Map<Position, String> squares = lastState.getSquares();
		List<Operation> operations = Lists.newArrayList();
		operations.add(new Set(TURN, turn.getOpposite().name()));
		operations.add(new Set(endSquare, movingPiece));
		operations.add(new Delete(startSquare));
		
		for (int i = 0; i < 16; i++) {   		//examine P0 to P15 and set winner's piece 
												//visible to all
			if (pieces.get(i).isPresent()) {
				operations.add(new SetVisibility(P + i));
			}
		}
		if (turn.isBlack())
			operations.add(new EndGame(bId));
		else
			operations.add(new EndGame(wId));
		
		return operations;
	}
	
	// Return normal move operations
	List<Operation> normalMoveToEmptySquare(String movingPiece, String startSquare, 
			String endSquare, GhostsState lastState) {
		Color turn = lastState.getTurn();
		List<Operation> operations = Lists.newArrayList();
		operations.add(new Set(TURN, turn.getOpposite().name()));
		operations.add(new Set(endSquare, movingPiece));
		operations.add(new Delete(startSquare));
		return operations;
	}

	// Return normal capture move operations
	List<Operation> normalCapture(String movingPiece, String startSquare, 
			String endSquare, GhostsState lastState) {
		Color turn = lastState.getTurn();
		Map<Position, String> squares = lastState.getSquares();
		List<Operation> operations = Lists.newArrayList();
		operations.add(new Set(TURN, turn.getOpposite().name()));
		operations.add(new Set(endSquare, movingPiece));
		operations.add(new Delete(startSquare));
		int row = (int) (endSquare.charAt(1) - '0');
		int col = (int) (endSquare.charAt(2) - '0');
		operations.add(new Delete(squares.get(new Position(row, col))));
		return operations;
	}
	
	// Return capture and exit operations
	List<Operation> captureAndExit(String movingPiece, String startSquare, 
			String endSquare, GhostsState lastState) {
		Color turn = lastState.getTurn();
		List<Optional<Piece>> pieces = lastState.getPieces();
		Map<Position, String> squares = lastState.getSquares();
		List<Operation> operations = Lists.newArrayList();
		operations.add(new Set(TURN, turn.getOpposite().name()));
		operations.add(new Set(endSquare, movingPiece));
		operations.add(new Delete(startSquare));
		int row = (int) (endSquare.charAt(1) - '0');
		int col = (int) (endSquare.charAt(2) - '0');
		operations.add(new Delete(squares.get(new Position(row, col))));
		for (int i = 0; i < 16; i++) {   		//examine P0 to P15 and set winner's piece 
			 									//visible to all
			if (pieces.get(i).isPresent()) {
				operations.add(new SetVisibility(P + i));
			}
		}
		
		if (turn.isBlack())
			operations.add(new EndGame(bId));
		else
			operations.add(new EndGame(wId));
		return operations;
	}

	// Determine if player is moving to empty square
	boolean isEndSquareEmpty(String endSquare, GhostsState lastState) {
		Map<Position, String> squares = lastState.getSquares();
		int row = (int) (endSquare.charAt(1) - '0');
		int col = (int) (endSquare.charAt(2) - '0');
		String pieceStr = squares.get(new Position(row, col));
		return pieceStr == null;
	}

	//Determine if player is moving a good ghost to his exit square
	boolean isMovingGoodToExit(String movingPiece, String endSquare, GhostsState lastState) {
		Color turn = lastState.getTurn();
		List<Optional<Piece>> pieces = lastState.getPieces();
		int index = getIndexFromPieceName(movingPiece);
		char row = endSquare.charAt(1);
		char col = endSquare.charAt(2);
		if (turn.isBlack()) {
			String pieceKind = pieces.get(index).get().getPieceKind();
			return pieceKind.charAt(1) == 'G' && ((row == '0' && col == '0') 
													|| (row == '0' && col == '5'));
		} else {
			String pieceKind = pieces.get(index).get().getPieceKind();
			return pieceKind.charAt(1) == 'G' && ((row == '5' && col == '0') 
													|| (row == '5' && col == '5'));
		}
	}

	// Determine if game is ended by one player's all good or evil ghosts are captured
	boolean notGoodOrEvilAllCaptured(GhostsState lastState) {
		Color turn = lastState.getTurn();
		boolean hasGood = false, hasEvil = false;
		List<Optional<Piece>> pieces = lastState.getPieces();
		if (turn.isBlack()) {
			for (int i = 8; i < 16; i++) {		
				if (pieces.get(i).isPresent()) {				//Returns true if this holder contains a (non-null) instance.
					if (pieces.get(i).get().getPieceKind() == "BGood") {
						hasGood = true;
					} else if (pieces.get(i).get().getPieceKind() == "BEvil") {
						hasEvil = true;
					}
				}
				else {
					continue;
				}
			}
		} else {
			for (int i = 0; i < 8; i++) {
				if (pieces.get(i).isPresent()) {	
					if (pieces.get(i).get().getPieceKind() == "WGood") {
						hasGood = true;
					} else if (pieces.get(i).get().getPieceKind() == "WEvil") {
						hasEvil = true;
					}
				}
				else {
					continue;
				}
			}
		}
		return (hasGood && hasEvil);
	}

	// Determine if last player already exit successfully
	boolean alreadyExit(GhostsState lastState) {
		Color turn = lastState.getTurn();
		Map<Position, String> squares = lastState.getSquares();
		List<Optional<Piece>> pieces = lastState.getPieces();
		String downLeft = squares.get(new Position(0, 0));
		String downRight = squares.get(new Position(0, 5));
		String upLeft = squares.get(new Position(5, 0));
		String upRight = squares.get(new Position(5, 5));

		if (turn.isBlack()) {
			if (downLeft != null) {
				int index = getIndexFromPieceName(downLeft);
				if (pieces.get(index).isPresent())
					return pieces.get(index).get().getPieceKind() == "BGood";
				else
					return false;
			} else if (downRight != null) {
				int index = getIndexFromPieceName(downRight);
				if (pieces.get(index).isPresent())
					return pieces.get(index).get().getPieceKind() == "BGood";
				else
					return false;
			} else {
				return false;
			}
		} else {
			if (upLeft != null) {
				int index = getIndexFromPieceName(upLeft);
				if (pieces.get(index).isPresent())
					return pieces.get(index).get().getPieceKind() == "WGood";
				else
					return false;
			} else if (upRight != null) {
				int index = getIndexFromPieceName(upRight);
				if (pieces.get(index).isPresent())
					return pieces.get(index).get().getPieceKind() == "WGood";
				else
					return false;
			} else {
				return false;
			}
		}
	}

	// Determine if player is moving from valid start square to valid end square
	boolean validStartAndEndSquareNumber(String startSquare, String endSquare) {
		check(startSquare.length() == 3, startSquare);
		check(endSquare.length() == 3, endSquare);
		int i1 = (int) (startSquare.charAt(1) - '0');
		int i2 = (int) (startSquare.charAt(2) - '0');
		int i3 = (int) (endSquare.charAt(1) - '0');
		int i4 = (int) (endSquare.charAt(2) - '0');

		if (Math.abs(i1 - i3) >= 1 && Math.abs(i2 - i4) >= 1)
			return false;

		return (startSquare.charAt(0) == 'S' && endSquare.charAt(0) == 'S')
				&& (Math.abs(i1 - i3) == 1 || Math.abs(i2 - i4) == 1)
				&& (i1 >= 0 && i1 < 6) && (i2 >= 0 && i2 < 6)
				&& (i3 >= 0 && i3 < 6) && (i4 >= 0 && i4 < 6);
	}

	// Determine if movinf piece exists and it is right color
	boolean movingPieceExistAndValid(String movingPiece, GhostsState lastState) {
		Color turn = lastState.getTurn();
		List<Optional<Piece>> pieces = lastState.getPieces();
		if (movingPiece == null) {
			return false;
		} else {
			int index = getIndexFromPieceName(movingPiece);
			if (turn.isBlack()) {
				if (index >= 8 && index < 16) {
					return pieces.get(index).isPresent();
				} else {
					return false;
				}
			} else {
				if (index >= 0 && index < 8) {
					return pieces.get(index).isPresent();
				} else {
					return false;
				}
			}
		}
	}

	// Determine if there is a piece on start square
	boolean movingPieceWasInStartSquare(String movingPiece, String startSquare, 
			GhostsState lastState) {
		Map<Position, String> squares = lastState.getSquares();
		int row = (int) (startSquare.charAt(1) - '0');
		int col = (int) (startSquare.charAt(2) - '0');
		String pieceStr = squares.get(new Position(row, col));
		return movingPiece == pieceStr;
	}

	// Determine if player is moving a piece to capture his another piece
	boolean sameSideCapture(String endSquare, GhostsState lastState) {
		Color turn = lastState.getTurn();
		List<Optional<Piece>> pieces = lastState.getPieces();
		Map<Position, String> squares = lastState.getSquares();
		int row = (int) (endSquare.charAt(1) - '0');
		int col = (int) (endSquare.charAt(2) - '0');
		String pieceStr = squares.get(new Position(row, col));
		if (pieceStr != null) {
			int index = getIndexFromPieceName(pieceStr);
			
			if (pieces.get(index).isPresent()) {// endSquare's piece is not null means it is on
												// the same
				return true; 					// side of last player, so sameSideCapture happens.
			} else {
				return false;
			}
		}
		return false;
	}

	String pieceIdToString(int pieceId) {
		if (pieceId >= 0 && pieceId <= 3)
			return "WGood";
		else if (pieceId >= 4 && pieceId <= 7)
			return "WEvil";
		else if (pieceId >= 8 && pieceId <= 11)
			return "BGood";
		else
			return "BEvil";
	}
	
	// return int value of the number after "P" in Px/xx
	int getIndexFromPieceName(String piecename) {
		if (piecename.length() == 2)
			return (int)(piecename.charAt(1) - '0');
		else {			// P10~P15
			int tenth = (int)(piecename.charAt(1) - '0') * 10;
			return tenth + (int)(piecename.charAt(2) - '0');
		}
	}

	List<String> getPiecesInRange(int fromInclusive, int toInclusive) {
		List<String> keys = Lists.newArrayList();
		for (int i = fromInclusive; i <= toInclusive; i++) {
			keys.add(P + i);
		}
		return keys;
	}

	// Returns the color that should make the move. 
	Color getExpectedMoveFromColor(Map<String, Object> lastState) {
		if (lastState.isEmpty()) {
			return Color.W;
		}
		return Color.valueOf((String) lastState.get(TURN));
	}

	// check accepts a boolean parameter, and zero or more Object parameters
	private void check(boolean val, Object... debugArguments) {
		if (!val) {
			throw new RuntimeException("We have a hacker! debugArguments="
					+ Arrays.toString(debugArguments));
		}
	}

	@SuppressWarnings("unchecked")
	private GhostsState gameApiStateToGhostsState(
			Map<String, Object> gameApiState) {
		List<Optional<Piece>> Pieces = Lists.newArrayList();
		Map<Position, String> Squares = Maps.newHashMap();
		boolean wFinished, bFinished;

		/** Convert Piece info to GhostsState form */
		for (int i = 0; i < 16; i++) {
			String pieceString = (String) gameApiState.get(P + i);
			Piece piece;
			if (pieceString == null) { // means not visible
				piece = null;
			} else {
				piece = new Piece(pieceString);
			}
			Pieces.add(Optional.fromNullable(piece));
		}

		/** Convert Square info to GhostsState form */
		for (int i = 0; i < 6; i++) {
			for (int j = 0; j < 6; j++) {
				// if squareString is null, meaning no piece here
				String squareString = (String) gameApiState.get((S + i) + j);
				Position pos = new Position(i, j);
				Squares.put(pos, squareString);
			}
		}
		
		/** Convert Deploy info to GhostsState form */
		String wDeployInfo = (String) gameApiState.get(WDeployed);
		String bDeployInfo = (String) gameApiState.get(BDeployed);
		wFinished = (wDeployInfo == "true") ? true : false;
		bFinished = (bDeployInfo == "true") ? true : false;

		return new GhostsState(Color.valueOf((String) gameApiState.get(TURN)),
				ImmutableList.copyOf(Pieces), Squares, wFinished, bFinished);
	}
}
