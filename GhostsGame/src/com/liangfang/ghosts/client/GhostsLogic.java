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
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;

public class GhostsLogic {
	public VerifyMoveDone verify(VerifyMove verifyMove) {
	    // TODO: I will implement this method in HW2
	    return new VerifyMoveDone();
	}
/*	
	void checkMoveIsLegal(VerifyMove verifyMove) {
	    List<Operation> lastMove = verifyMove.getLastMove();
	    Map<String, Object> lastState = verifyMove.getLastState();
	    // Checking the operations are as expected.
	    List<Operation> expectedOperations = getExpectedOperations(
	        lastState, lastMove, verifyMove.getPlayerIds());
	    check(expectedOperations.equals(lastMove), expectedOperations, lastMove);

	    // Checking the right player did the move.
	    Color gotMoveFromColor =
	        Color.values()[verifyMove.getPlayerIndex(verifyMove.getLastMovePlayerId())];
	    check(gotMoveFromColor == getExpectedMoveFromColor(lastState), gotMoveFromColor);
	}
	*/
}
