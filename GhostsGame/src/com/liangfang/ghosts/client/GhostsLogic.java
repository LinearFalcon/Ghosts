package com.liangfang.ghosts.client;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

//import com.liangfang.ghosts.client.Card.Rank;
//import com.liangfang.ghosts.client.Card.Suit;
import com.liangfang.ghosts.client.GameApi.Delete;
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

		// Checking the operations are as expected.
		List<Operation> expectedOperations = getExpectedOperations(lastState,
				lastMove, verifyMove.getPlayerIds());
		check(expectedOperations.equals(lastMove), expectedOperations, lastMove);

		// Checking the right player did the move.
		Color gotMoveFromColor = Color.values()[verifyMove
				.getPlayerIndex(verifyMove.getLastMovePlayerId())];
		check(gotMoveFromColor == getExpectedMoveFromColor(lastState),
				gotMoveFromColor);

	}

	@SuppressWarnings("unchecked")
	List<Operation> getExpectedOperations(Map<String, Object> lastApiState,
			List<Operation> lastMove, List<Integer> playerIds) {

		if (lastApiState.isEmpty()) {
			return getInitialMove(playerIds.get(0), playerIds.get(1)); // **********Initial
																		// includes
																		// deploy
																		// which
																		// not
																		// inplement
																		// yet!!!!!!
		}

		GhostsState lastState = gameApiStateToGhostsState(lastApiState);
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
				return exitMove();
			} else { // normal move to empty square
				return normalMoveToEmptySquare();
			}
		} else { // capture happens
			return captureMove();
		}
	}
	
	List<Operation> exitMove() {
		
	}
	
	List<Operation> normalMoveToEmptySquare() {
		
	}

	List<Operation> captureMove() {
	
	}

	boolean isEndSquareEmpty(String endSquare, GhostsState lastState) {
		Map<Position, String> squares = lastState.getSquares();
		int row = (int) (endSquare.charAt(1) - '0');
		int col = (int) (endSquare.charAt(2) - '0');
		String pieceStr = squares.get(new Position(row, col));
		return pieceStr == null;
	}

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

	boolean notGoodOrEvilAllCaptured(GhostsState lastState) {
		Color turn = lastState.getTurn();
		boolean hasGood = false, hasEvil = false;

		if (turn.isBlack()) {
			for (int i = 8; i < 16; i++) {
				Piece piece = lastState.getPieces().get(i).get();
				if (piece.getPieceKind() == "BGood") {
					hasGood = true;
				} else if (piece.getPieceKind() == "BEvil") {
					hasEvil = true;
				}
			}
		} else {
			for (int i = 0; i < 8; i++) {
				Piece piece = lastState.getPieces().get(i).get();
				if (piece.getPieceKind() == "WGood") {
					hasGood = true;
				} else if (piece.getPieceKind() == "WEvil") {
					hasEvil = true;
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
				if (pieces.get(index) != null)
					return pieces.get(index).get().getPieceKind() == "BGood";
				else
					return false;
			} else if (downRight != null) {
				int index = getIndexFromPieceName(downRight);
				if (pieces.get(index) != null)
					return pieces.get(index).get().getPieceKind() == "BGood";
				else
					return false;
			} else {
				return false;
			}
		} else {
			if (upLeft != null) {
				int index = getIndexFromPieceName(upLeft);
				if (pieces.get(index) != null)
					return pieces.get(index).get().getPieceKind() == "WGood";
				else
					return false;
			} else if (upRight != null) {
				int index = getIndexFromPieceName(upRight);
				if (pieces.get(index) != null)
					return pieces.get(index).get().getPieceKind() == "WGood";
				else
					return false;
			} else {
				return false;
			}
		}
	}

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

	boolean movingPieceExistAndValid(String movingPiece, GhostsState lastState) {
		Color turn = lastState.getTurn();
		List<Optional<Piece>> pieces = lastState.getPieces();
		if (movingPiece == null) {
			return false;
		} else {
			int index = getIndexFromPieceName(movingPiece);
			if (turn.isBlack()) {
				if (index >= 8 && index < 16) {
					return pieces.get(index).get() != null;
				} else {
					return false;
				}
			} else {
				if (index >= 0 && index < 8) {
					return pieces.get(index).get() != null;
				} else {
					return false;
				}
			}
		}
	}

	boolean movingPieceWasInStartSquare(String movingPiece, String startSquare, 
			GhostsState lastState) {
		Map<Position, String> squares = lastState.getSquares();
		int row = (int) (startSquare.charAt(1) - '0');
		int col = (int) (startSquare.charAt(2) - '0');
		String pieceStr = squares.get(new Position(row, col));
		return movingPiece == pieceStr;
	}

	boolean sameSideCapture(String endSquare, GhostsState lastState) {
		Color turn = lastState.getTurn();
		List<Optional<Piece>> pieces = lastState.getPieces();
		Map<Position, String> squares = lastState.getSquares();
		int row = (int) (endSquare.charAt(1) - '0');
		int col = (int) (endSquare.charAt(2) - '0');
		String pieceStr = squares.get(new Position(row, col));
		if (pieceStr != null) {
			int index = getIndexFromPieceName(pieceStr);
			Piece tempP = pieces.get(index).get();
			if (tempP != null) { // endSquare's piece is not null means it is on
									// the same
				return true; // side of last player, so sameSideCapture happens.
			} else {
				return false;
			}
		}
		return false;
	}

	List<Operation> getInitialMove(int whitePlayerId, int blackPlayerId) { // initial
																			// state
																			// may
																			// have
																			// problem!!!!!!*********************
		List<Operation> operations = Lists.newArrayList();
		// The order of operations: turn, isCheater, W, B, M, claim, C0...C51
		operations.add(new Set(TURN, W));

		// sets all 16 pieces: set(P0,"WGood"), set(P15,"BEvil")
		for (int i = 0; i < 16; i++) {
			operations.add(new Set(P + i, pieceIdToString(i)));
		}

		// shuffle(P0,...,P7) and shuffle(P8,...,P15)
		operations.add(new Shuffle(getPiecesInRange(0, 7)));
		operations.add(new Shuffle(getPiecesInRange(8, 15)));

		// sets visibility
		for (int i = 0; i < 8; i++) {
			operations.add(new SetVisibility(P + i, ImmutableList
					.of(whitePlayerId)));
		}
		for (int i = 8; i < 16; i++) {
			operations.add(new SetVisibility(P + i, ImmutableList
					.of(blackPlayerId)));
		}
		return operations;
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
	
	/** return int value of the number after "P" in Px/xx */
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

	/** Returns the color that should make the move. */
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

		return new GhostsState(Color.valueOf((String) gameApiState.get(TURN)),
				ImmutableList.copyOf(Pieces), ImmutableMap.copyOf(Squares));
	}
}
