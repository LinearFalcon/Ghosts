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
	private static final String S = "S"; // Square key (S11...S66)

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
			return getInitialMove(playerIds.get(0), playerIds.get(1));
		}
		GhostsState lastState = gameApiStateToGhostsState(lastApiState);

		// There are 3 types of moves:
		// 1) doing a claim.
		// 2) claiming a cheater (then we have Set(isCheater, yes)).
		// 3) checking if we had a cheater (then we have Delete(isCheater)).
		if (lastMove.contains(new Set(IS_CHEATER, YES))) {
			return declareCheaterMove(lastState);

		} else if (lastMove.contains(new Delete(IS_CHEATER))) {
			return checkIfCheatedMove(lastState, playerIds);

		} else {
			List<Integer> lastM = lastState.getMiddle();
			Set setM = (Set) lastMove.get(2);
			List<Integer> newM = (List<Integer>) setM.getValue();
			List<Integer> diffM = subtract(newM, lastM);
			Set setClaim = (Set) lastMove.get(3);
			Claim claim = checkNotNull(Claim
					.fromClaimEntryInGameState((List<String>) setClaim
							.getValue()));
			return doClaimMove(lastState, claim.getCardRank(), diffM);
		}
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
	private GhostsState gameApiStateToGhostsState(Map<String, Object> gameApiState) {
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
		for (int i = 1; i <= 6; i++) {
			for (int j = 1; j <= 6; j++) {
				// if squareString is null, meaning no piece here
				String squareString = (String) gameApiState.get((S + i) + j);
				Position pos = new Position(i - 1, j - 1);
				Squares.put(pos, squareString);
			}
		}

		return new GhostsState(Color.valueOf((String) gameApiState.get(TURN)),
				ImmutableList.copyOf(Pieces), ImmutableMap.copyOf(Squares));
	}
}
