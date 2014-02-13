package com.liangfang.ghosts.client;

import static org.junit.Assert.*;
import com.liangfang.ghosts.client.GameApi.*;

import java.util.List;
import java.util.Map;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

public class GhostsLogicTest {
	GhostsLogic ghostsLogic = new GhostsLogic();
	private static final String PLAYER_ID = "playerId";

	private static final String TURN = "turn"; // turn of which player (either W
												// or B)
	private static final String W = "W"; // White hand
	private static final String B = "B"; // Black hand
	private final int wId = 23;
	private final int bId = 24;
	private final ImmutableList<Integer> visibleToW = ImmutableList.of(wId);
	private final ImmutableList<Integer> visibleToB = ImmutableList.of(bId);
	private final ImmutableMap<String, Object> wInfo = ImmutableMap
			.<String, Object> of(PLAYER_ID, wId);
	private final ImmutableMap<String, Object> bInfo = ImmutableMap
			.<String, Object> of(PLAYER_ID, bId);
	private final ImmutableList<Map<String, Object>> playersInfo = ImmutableList
			.<Map<String, Object>> of(wInfo, bInfo);
	private final ImmutableMap<String, Object> emptyState = ImmutableMap
			.<String, Object> of();
	private final ImmutableMap<String, Object> nonEmptyState = ImmutableMap
			.<String, Object> of("k", "v"); // ????????????

	private String[] P = createP();
	private String[][] S = createS();

	private String[] createP() {
		String[] arr = new String[16];
		for (int i = 0; i < 16; i++) {
			arr[i] = "P" + i;
		}
		return arr;
	}

	private String[][] createS() {
		String[][] arr = new String[6][6];
		for (int i = 0; i < 6; i++)
			for (int j = 0; j < 6; j++)
				arr[i][j] = ("S" + i) + j;
		return arr;
	}

	// here we don't need to consider visibility************
	private Map<String, Object> randomWhiteState = ImmutableMap
			.<String, Object> builder().put(TURN, W).put(P[0], "WGood")
			.put(P[1], "WEvil").put(P[8], "BGood").put(P[9], "BEvil")
			.put(S[1][1], P[0]) // S11, WGood
			.put(S[1][3], P[1]) // S13, WEvil
			.put(S[4][1], P[8]) // S41, BGood
			.put(S[4][3], P[9]) // S43, BEvil
			.build();

	private Map<String, Object> randomBlackState = ImmutableMap
			.<String, Object> builder().put(TURN, B).put(P[0], "WGood")
			.put(P[1], "WEvil").put(P[8], "BGood").put(P[9], "BEvil")
			.put(S[1][1], P[0]) // S11, WGood
			.put(S[1][3], P[1]) // S13, WEvil
			.put(S[4][1], P[8]) // S41, BGood
			.put(S[4][3], P[9]) // S43, BEvil
			.build();

	/** Assume black player has pieces which not stop white's move
	 *  Black pieces are not visible
	 */
	private Map<String, Object> whiteToExitState = ImmutableMap
			.<String, Object> builder().put(TURN, W).put(P[0], "WGood")
			.put(P[1], "WGood").put(P[2], "WEvil")
			.put(S[4][0], P[0]) // S40, WGood
			.put(S[5][4], P[1]) // S54, WGood
			.put(S[4][4], P[2])
			.put(S[4][1], P[8])
			.put(S[4][3], P[9])
			.build();

	/** Assume white player has pieces which not stop black's move
	 *  White pieces are not visible
	 */
	private Map<String, Object> blackToExitState = ImmutableMap
			.<String, Object> builder().put(TURN, B).put(P[8], "BGood").put(P[9], "BGood")
			.put(P[10], "BEvil")
			.put(S[1][1], P[0])
			.put(S[1][3], P[1])
			.put(S[0][1], P[8]) // S01, BGood
			.put(S[1][5], P[9]) // S15, BGood
			.put(S[5][2], P[10]).build();

	// we don't care about current state, just verify last operation/move on
	// last state
	private VerifyMove move(int lastMovePlayerId,
			Map<String, Object> lastState, List<Operation> lastMove) {
		return new VerifyMove(wId, playersInfo, emptyState, lastState, lastMove, lastMovePlayerId);
	}

	private void assertHacker(VerifyMove verifyMove) {
		VerifyMoveDone verifyDone = ghostsLogic.verify(verifyMove);
		assertEquals(verifyMove.getLastMovePlayerId(),
				verifyDone.getHackerPlayerId());
	}

	@Test
	public void testWhiteMoveUp() {

		List<Operation> operations = ImmutableList.<Operation> of(new Set(TURN,
				B), new Set(S[2][1], P[0]), new Delete(S[1][1]));

		VerifyMove verifyMove = move(wId, randomWhiteState, operations);
		VerifyMoveDone verifyDone = new GhostsLogic().verify(verifyMove);
		assertEquals(0, verifyDone.getHackerPlayerId());
	}

	@Test
	public void testIllegalWhiteMoveTwoSteps() {

		List<Operation> operations = ImmutableList.<Operation> of(new Set(TURN,
				B), new Set(S[3][1], P[0]), new Delete(S[1][1]));

		VerifyMove verifyMove = move(wId, randomWhiteState, operations);
		assertHacker(verifyMove);
	}

	@Test
	public void testWhiteMoveLeft() {

		List<Operation> operations = ImmutableList.<Operation> of(new Set(TURN,
				B), new Set(S[1][0], P[0]), new Delete(S[1][1]));

		VerifyMove verifyMove = move(wId, randomWhiteState, operations);
		VerifyMoveDone verifyDone = new GhostsLogic().verify(verifyMove);
		assertEquals(0, verifyDone.getHackerPlayerId());
	}

	@Test
	public void testIllegalWhiteMoveLeftUp() {

		List<Operation> operations = ImmutableList.<Operation> of(new Set(TURN,
				B), new Set(S[2][0], P[0]), new Delete(S[1][1]));

		VerifyMove verifyMove = move(wId, randomWhiteState, operations);
		assertHacker(verifyMove);
	}

	@Test
	public void testWhiteMoveRight() {

		List<Operation> operations = ImmutableList.<Operation> of(new Set(TURN,
				B), new Set(S[1][2], P[0]), new Delete(S[1][1]));

		VerifyMove verifyMove = move(wId, randomWhiteState, operations);
		VerifyMoveDone verifyDone = new GhostsLogic().verify(verifyMove);
		assertEquals(0, verifyDone.getHackerPlayerId());
	}

	@Test
	public void testIllegalWhiteMoveRightUp() {

		List<Operation> operations = ImmutableList.<Operation> of(new Set(TURN,
				B), new Set(S[2][2], P[0]), new Delete(S[1][1]));

		VerifyMove verifyMove = move(wId, randomWhiteState, operations);
		assertHacker(verifyMove);
	}

	@Test
	public void testWhiteMoveDown() {

		List<Operation> operations = ImmutableList.<Operation> of(new Set(TURN,
				B), new Set(S[0][1], P[0]), new Delete(S[1][1]));

		VerifyMove verifyMove = move(wId, randomWhiteState, operations);
		VerifyMoveDone verifyDone = new GhostsLogic().verify(verifyMove);
		assertEquals(0, verifyDone.getHackerPlayerId());
	}

	@Test
	public void testIllegalWhiteMoveDownRight() {

		List<Operation> operations = ImmutableList.<Operation> of(new Set(TURN,
				B), new Set(S[0][2], P[0]), new Delete(S[1][1]));

		VerifyMove verifyMove = move(wId, randomWhiteState, operations);
		assertHacker(verifyMove);
	}

	@Test
	public void testBlackMoveUp() {

		List<Operation> operations = ImmutableList.<Operation> of(new Set(TURN,
				W), new Set(S[5][1], P[8]), new Delete(S[4][1]));

		VerifyMove verifyMove = move(bId, randomBlackState, operations);
		VerifyMoveDone verifyDone = new GhostsLogic().verify(verifyMove);
		assertEquals(0, verifyDone.getHackerPlayerId());
	}

	@Test
	public void testIllegalBlackMoveUpLeft() {

		List<Operation> operations = ImmutableList.<Operation> of(new Set(TURN,
				W), new Set(S[5][0], P[8]), new Delete(S[4][1]));

		VerifyMove verifyMove = move(bId, randomBlackState, operations);
		assertHacker(verifyMove);
	}

	@Test
	public void testBlackMoveDown() {

		List<Operation> operations = ImmutableList.<Operation> of(new Set(TURN,
				W), new Set(S[3][1], P[8]), new Delete(S[4][1]));

		VerifyMove verifyMove = move(bId, randomBlackState, operations);
		VerifyMoveDone verifyDone = new GhostsLogic().verify(verifyMove);
		assertEquals(0, verifyDone.getHackerPlayerId());
	}

	@Test
	public void testIllegalBlackMoveTwoSteps() {

		List<Operation> operations = ImmutableList.<Operation> of(new Set(TURN,
				W), new Set(S[2][1], P[8]), new Delete(S[4][1]));

		VerifyMove verifyMove = move(bId, randomBlackState, operations);
		assertHacker(verifyMove);
	}

	@Test
	public void testBlackMoveLeft() {

		List<Operation> operations = ImmutableList.<Operation> of(new Set(TURN,
				W), new Set(S[4][0], P[8]), new Delete(S[4][1]));

		VerifyMove verifyMove = move(bId, randomBlackState, operations);
		VerifyMoveDone verifyDone = new GhostsLogic().verify(verifyMove);
		assertEquals(0, verifyDone.getHackerPlayerId());
	}

	@Test
	public void testIllegalBlackMoveDownLeft() {

		List<Operation> operations = ImmutableList.<Operation> of(new Set(TURN,
				W), new Set(S[3][0], P[8]), new Delete(S[4][1]));

		VerifyMove verifyMove = move(bId, randomBlackState, operations);
		assertHacker(verifyMove);
	}

	@Test
	public void testBlackMoveRight() {

		List<Operation> operations = ImmutableList.<Operation> of(new Set(TURN,
				W), new Set(S[4][2], P[8]), new Delete(S[4][1]));

		VerifyMove verifyMove = move(bId, randomBlackState, operations);
		VerifyMoveDone verifyDone = new GhostsLogic().verify(verifyMove);
		assertEquals(0, verifyDone.getHackerPlayerId());
	}

	@Test
	public void testIllegalBlackMoveDownRight() {

		List<Operation> operations = ImmutableList.<Operation> of(new Set(TURN,
				W), new Set(S[3][2], P[8]), new Delete(S[4][1]));

		VerifyMove verifyMove = move(bId, randomBlackState, operations);
		assertHacker(verifyMove);
	}

	@Test
	public void testBlackExitRight() {

		List<Operation> operations = ImmutableList.<Operation> of(
				new Set(TURN, W), 
				new Set(S[0][5], P[9]), 
				new Delete(S[1][5]),
				new SetVisibility(P[8]), 
				new SetVisibility(P[9]), 
				new SetVisibility(P[10]),
				new EndGame(bId));

		VerifyMove verifyMove = move(bId, blackToExitState, operations);
		VerifyMoveDone verifyDone = new GhostsLogic().verify(verifyMove);
		assertEquals(0, verifyDone.getHackerPlayerId());
	}

	@Test
	public void testBlackExitLeft() {

		List<Operation> operations = ImmutableList.<Operation> of(
				new Set(TURN, W), 
				new Set(S[0][0], P[8]), 
				new Delete(S[0][1]),
				new SetVisibility(P[8]), 
				new SetVisibility(P[9]), 
				new SetVisibility(P[10]),
				new EndGame(bId));

		VerifyMove verifyMove = move(bId, blackToExitState, operations);
		VerifyMoveDone verifyDone = new GhostsLogic().verify(verifyMove);
		assertEquals(0, verifyDone.getHackerPlayerId());
	}

	@Test
	public void testWhiteExitRight() {

		List<Operation> operations = ImmutableList.<Operation> of(
				new Set(TURN, B), 
				new Set(S[5][5], P[1]), 
				new Delete(S[5][4]),
				new SetVisibility(P[0]), 
				new SetVisibility(P[1]),
				new SetVisibility(P[2]),
				new EndGame(wId));

		VerifyMove verifyMove = move(wId, whiteToExitState, operations);
		VerifyMoveDone verifyDone = new GhostsLogic().verify(verifyMove);
		assertEquals(0, verifyDone.getHackerPlayerId());
	}

	@Test
	public void testWhiteExitLeft() {

		List<Operation> operations = ImmutableList.<Operation> of(
				new Set(TURN, B), 
				new Set(S[5][0], P[0]), 
				new Delete(S[4][0]),
				new SetVisibility(P[0]), 
				new SetVisibility(P[1]), 
				new SetVisibility(P[2]),
				new EndGame(wId));

		VerifyMove verifyMove = move(wId, whiteToExitState, operations);
		VerifyMoveDone verifyDone = new GhostsLogic().verify(verifyMove);
		assertEquals(0, verifyDone.getHackerPlayerId());
	}
}