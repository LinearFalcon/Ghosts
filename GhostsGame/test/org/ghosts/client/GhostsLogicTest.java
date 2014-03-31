package org.ghosts.client;

import static org.junit.Assert.*;

import java.util.List;
import java.util.Map;

import org.game_api.GameApi.*;
import org.ghosts.client.GhostsLogic;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

public class GhostsLogicTest {
	GhostsLogic ghostsLogic = new GhostsLogic();
	private static final String PLAYER_ID = "playerId";

	private static final String TURN = "turn"; // turn of which player (either W or B)
	private static final String W = "W"; // White hand
	private static final String B = "B"; // Black hand
	private static final String WDeployed = "WDeployed"; // white deploy key
	private static final String BDeployed = "BDeployed"; // black deploy key
	private final String wId = "42";
	private final String bId = "43";
	private final ImmutableList<String> visibleToW = ImmutableList.of(wId);
	private final ImmutableList<String> visibleToB = ImmutableList.of(bId);
	private final ImmutableMap<String, Object> wInfo = ImmutableMap
			.<String, Object> of(PLAYER_ID, wId);
	private final ImmutableMap<String, Object> bInfo = ImmutableMap
			.<String, Object> of(PLAYER_ID, bId);
	private final ImmutableList<Map<String, Object>> playersInfo = ImmutableList
			.<Map<String, Object>> of(wInfo, bInfo);
	private final ImmutableMap<String, Object> emptyState = ImmutableMap
			.<String, Object> of();
	private final ImmutableMap<String, Object> nonEmptyState = ImmutableMap
			.<String, Object> of("k", "v"); 

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

	private Map<String, Object> whiteDeployState = ImmutableMap
			.<String, Object> builder()
			.put(P[0], "WGood")
			.put(P[1], "WGood")
			.put(P[2], "WGood")
			.put(P[3], "WGood")
			.put(P[4], "WEvil")
			.put(P[5], "WEvil")
			.put(P[6], "WEvil")
			.put(P[7], "WEvil")
			.build();
	
	private Map<String, Object> blackDeployState = ImmutableMap
			.<String, Object> builder()
			.put(P[8], "BGood")
			.put(P[9], "BGood")
			.put(P[10], "BGood")
			.put(P[11], "BGood")
			.put(P[12], "BEvil")
			.put(P[13], "BEvil")
			.put(P[14], "BEvil")
			.put(P[15], "BEvil")
			.put(S[4][1], P[0]) 
			.put(S[4][2], P[1]) 
			.put(S[4][3], P[2]) 
			.put(S[4][4], P[3]) 
			.put(S[5][1], P[4]) 
			.put(S[5][2], P[5]) 
			.put(S[5][3], P[6]) 
			.put(S[5][4], P[7])
			.put(WDeployed, "true")
			.build();
			
	/** Here we don't need to consider visibility */
	private Map<String, Object> randomWhiteState = ImmutableMap
			.<String, Object> builder()
			.put(P[0], "WGood")
			.put(P[1], "WEvil")
			.put(P[8], "BGood")
			.put(P[9], "BEvil")
			.put(S[4][1], P[0]) // S41, WGood
			.put(S[4][3], P[1]) // S43, WEvil
			.put(S[1][1], P[8]) // S11, BGood
			.put(S[1][3], P[9]) // S13, BEvil
			.put(WDeployed, "true")
			.put(BDeployed, "true")
			.build();

	private Map<String, Object> randomBlackState = ImmutableMap
			.<String, Object> builder()
			.put(P[0], "WGood")
			.put(P[1], "WEvil")
			.put(P[8], "BGood")
			.put(P[9], "BEvil")
			.put(S[4][1], P[0]) // S41, WGood
			.put(S[4][3], P[1]) // S43, WEvil
			.put(S[1][1], P[8]) // S11, BGood
			.put(S[1][3], P[9]) // S13, BEvil
			.put(WDeployed, "true")
			.put(BDeployed, "true")
			.build();

	/** Assume black player has pieces which not stop white's move
	 *  Black pieces are not visible
	 */
	private Map<String, Object> whiteToExitState = ImmutableMap
			.<String, Object> builder()
			.put(P[0], "WGood")
			.put(P[1], "WGood")
			.put(P[2], "WEvil")
			.put(S[1][0], P[0]) // S10, WGood
			.put(S[0][4], P[1]) // S04, WGood
			.put(S[1][4], P[2])
			.put(S[1][1], P[8])
			.put(S[1][3], P[9])
			.put(WDeployed, "true")
			.put(BDeployed, "true")
			.build();

	/** Assume white player has pieces which not stop black's move
	 *  White pieces are not visible
	 */
	private Map<String, Object> blackToExitState = ImmutableMap
			.<String, Object> builder()
			.put(P[8], "BGood")
			.put(P[9], "BGood")
			.put(P[10], "BEvil")
			.put(S[4][1], P[0])
			.put(S[4][3], P[1])
			.put(S[5][1], P[8]) // S51, BGood
			.put(S[4][5], P[9]) // S45, BGood
			.put(S[0][2], P[10])
			.put(WDeployed, "true")
			.put(BDeployed, "true")
			.build();
	
	/** Assume black player has pieces which not stop white's move
	 *  Black pieces are not visible
	 */
	private Map<String, Object> captureState = ImmutableMap
			.<String, Object> builder()
			.put(P[0], "WGood")
			.put(P[1], "WEvil")
			.put(S[1][0], P[0]) // S10, WGood
			.put(S[2][0], P[1]) // S20, WEvil
			.put(S[1][1], P[8])	// S11, some black ghost
			.put(S[0][0], P[9]) // S00, some black ghost in white's exit
			.put(S[3][0], P[10]) // some black ghost to maintain at least one good and one evil
			.put(WDeployed, "true")
			.put(BDeployed, "true")
			.build();

	/** we don't care about current state, just verify last operation/move on
	 * last state
	 * @param lastMovePlayerId
	 * @param lastState
	 * @param lastMove
	 * @return VerifyMove object
	 */
	private VerifyMove move(String lastMovePlayerId,
			Map<String, Object> lastState, List<Operation> lastMove) {
		return new VerifyMove(playersInfo, emptyState, lastState, lastMove, 
				lastMovePlayerId, ImmutableMap.<String, Integer>of());   //My game doesn't need pot
	}

	private void assertHacker(VerifyMove verifyMove) {
		VerifyMoveDone verifyDone = ghostsLogic.verify(verifyMove);
		assertEquals(verifyMove.getLastMovePlayerId(),
				verifyDone.getHackerPlayerId());
	}
	
	List<String> getPiecesInRange(int fromInclusive, int toInclusive) {
		return ghostsLogic.getPiecesInRange(fromInclusive, toInclusive);
	}

	@Test
	public void testBoardInitial() {

		List<Operation> operations = ImmutableList.<Operation> of(
				new SetTurn(wId), 
				new Set(P[0], "WGood"), new Set(P[1], "WGood"), new Set(P[2], "WGood"),
				new Set(P[3], "WGood"), new Set(P[4], "WEvil"), new Set(P[5], "WEvil"),
				new Set(P[6], "WEvil"), new Set(P[7], "WEvil"),	new Set(P[8], "BGood"),
				new Set(P[9], "BGood"), new Set(P[10], "BGood"), new Set(P[11], "BGood"),
				new Set(P[12], "BEvil"), new Set(P[13], "BEvil"), new Set(P[14], "BEvil"),
				new Set(P[15], "BEvil"),
				new Shuffle(getPiecesInRange(0, 7)),
				new Shuffle(getPiecesInRange(8, 15)),
				new SetVisibility(P[0], visibleToW), new SetVisibility(P[1], visibleToW), new SetVisibility(P[2], visibleToW),
				new SetVisibility(P[3], visibleToW), new SetVisibility(P[4], visibleToW), new SetVisibility(P[5], visibleToW),
				new SetVisibility(P[6], visibleToW), new SetVisibility(P[7], visibleToW),	new SetVisibility(P[8], visibleToB),
				new SetVisibility(P[9], visibleToB), new SetVisibility(P[10], visibleToB), new SetVisibility(P[11], visibleToB),
				new SetVisibility(P[12], visibleToB), new SetVisibility(P[13], visibleToB), new SetVisibility(P[14], visibleToB),
				new SetVisibility(P[15], visibleToB));

		VerifyMove verifyMove = move(wId, emptyState, operations);
		VerifyMoveDone verifyDone = new GhostsLogic().verify(verifyMove);
		assertEquals(null, verifyDone.getHackerPlayerId());
	}
	
	@Test
	public void testWhiteDeploy() {
		List<Operation> operations = ImmutableList.<Operation> of(
				new SetTurn(bId),
				new Set(S[5][1], P[0]), 
				new Set(S[5][2], P[7]),
				new Set(S[5][3], P[3]),
				new Set(S[5][4], P[4]),
				new Set(S[4][1], P[2]),
				new Set(S[4][2], P[6]),
				new Set(S[4][3], P[5]),
				new Set(S[4][4], P[1]),
				new Set(WDeployed, "true"));
		
		VerifyMove verifyMove = move(wId, whiteDeployState, operations);
		VerifyMoveDone verifyDone = new GhostsLogic().verify(verifyMove);
		assertEquals(null, verifyDone.getHackerPlayerId());
	}
	
	@Test
	public void testInvalidBlackDeployFirst() {

		List<Operation> operations = ImmutableList.<Operation> of(
				new SetTurn(wId), 
				new Set(S[1][1], P[8]), 
				new Set(S[1][2], P[9]),
				new Set(S[1][3], P[10]),
				new Set(S[1][4], P[11]),
				new Set(S[0][1], P[12]),
				new Set(S[0][2], P[13]),
				new Set(S[0][3], P[14]),
				new Set(S[0][4], P[15]),
				new Set(BDeployed, "true"));

		VerifyMove verifyMove = move(bId, emptyState, operations);
		assertHacker(verifyMove);
	}
	
	@Test
	public void testBlackDeploy() {
		List<Operation> operations = ImmutableList.<Operation> of(
				new SetTurn(wId),
				new Set(S[1][1], P[8]), 
				new Set(S[1][2], P[9]),
				new Set(S[1][3], P[10]),
				new Set(S[1][4], P[11]),
				new Set(S[0][1], P[12]),
				new Set(S[0][2], P[13]),
				new Set(S[0][3], P[14]),
				new Set(S[0][4], P[15]),
				new Set(BDeployed, "true"));
		
		VerifyMove verifyMove = move(bId, blackDeployState, operations);
		VerifyMoveDone verifyDone = new GhostsLogic().verify(verifyMove);
		assertEquals(null, verifyDone.getHackerPlayerId());
	}
	
	@Test
	public void testWhiteMoveUp() {

		List<Operation> operations = ImmutableList.<Operation> of(new SetTurn(bId), 
				new Set(S[3][1], P[0]), new Delete(S[4][1]));

		VerifyMove verifyMove = move(wId, randomWhiteState, operations);
		VerifyMoveDone verifyDone = new GhostsLogic().verify(verifyMove);
		assertEquals(null, verifyDone.getHackerPlayerId());
	}

	@Test
	public void testIllegalWhiteMoveTwoSteps() {

		List<Operation> operations = ImmutableList.<Operation> of(new SetTurn(bId), 
				new Set(S[2][1], P[0]), new Delete(S[4][1]));

		VerifyMove verifyMove = move(wId, randomWhiteState, operations);
		assertHacker(verifyMove);
	}

	@Test
	public void testWhiteMoveLeft() {

		List<Operation> operations = ImmutableList.<Operation> of(new SetTurn(bId), 
				new Set(S[4][0], P[0]), new Delete(S[4][1]));

		VerifyMove verifyMove = move(wId, randomWhiteState, operations);
		VerifyMoveDone verifyDone = new GhostsLogic().verify(verifyMove);
		assertEquals(null, verifyDone.getHackerPlayerId());
	}

	@Test
	public void testIllegalWhiteMoveLeftUp() {

		List<Operation> operations = ImmutableList.<Operation> of(new SetTurn(bId), 
				new Set(S[3][0], P[0]), new Delete(S[4][1]));

		VerifyMove verifyMove = move(wId, randomWhiteState, operations);
		assertHacker(verifyMove);
	}

	@Test
	public void testWhiteMoveRight() {

		List<Operation> operations = ImmutableList.<Operation> of(new SetTurn(bId), 
				new Set(S[4][2], P[0]), new Delete(S[4][1]));

		VerifyMove verifyMove = move(wId, randomWhiteState, operations);
		VerifyMoveDone verifyDone = new GhostsLogic().verify(verifyMove);
		assertEquals(null, verifyDone.getHackerPlayerId());
	}

	@Test
	public void testIllegalWhiteMoveRightUp() {

		List<Operation> operations = ImmutableList.<Operation> of(new SetTurn(bId), 
				new Set(S[3][2], P[0]), new Delete(S[4][1]));

		VerifyMove verifyMove = move(wId, randomWhiteState, operations);
		assertHacker(verifyMove);
	}

	@Test
	public void testWhiteMoveDown() {

		List<Operation> operations = ImmutableList.<Operation> of(new SetTurn(bId), 
				new Set(S[5][1], P[0]), new Delete(S[4][1]));

		VerifyMove verifyMove = move(wId, randomWhiteState, operations);
		VerifyMoveDone verifyDone = new GhostsLogic().verify(verifyMove);
		assertEquals(null, verifyDone.getHackerPlayerId());
	}

	@Test
	public void testIllegalWhiteMoveDownRight() {

		List<Operation> operations = ImmutableList.<Operation> of(new SetTurn(bId), 
				new Set(S[5][2], P[0]), new Delete(S[4][1]));

		VerifyMove verifyMove = move(wId, randomWhiteState, operations);
		assertHacker(verifyMove);
	}

	@Test
	public void testBlackMoveUp() {

		List<Operation> operations = ImmutableList.<Operation> of(new SetTurn(wId), 
				new Set(S[0][1], P[8]), new Delete(S[1][1]));

		VerifyMove verifyMove = move(bId, randomBlackState, operations);
		VerifyMoveDone verifyDone = new GhostsLogic().verify(verifyMove);
		assertEquals(null, verifyDone.getHackerPlayerId());
	}

	@Test
	public void testIllegalBlackMoveUpLeft() {

		List<Operation> operations = ImmutableList.<Operation> of(new SetTurn(wId), 
				new Set(S[0][0], P[8]), new Delete(S[1][1]));

		VerifyMove verifyMove = move(bId, randomBlackState, operations);
		assertHacker(verifyMove);
	}

	@Test
	public void testBlackMoveDown() {

		List<Operation> operations = ImmutableList.<Operation> of(new SetTurn(wId), 
				new Set(S[2][1], P[8]), new Delete(S[1][1]));

		VerifyMove verifyMove = move(bId, randomBlackState, operations);
		VerifyMoveDone verifyDone = new GhostsLogic().verify(verifyMove);
		assertEquals(null, verifyDone.getHackerPlayerId());
	}

	@Test
	public void testIllegalBlackMoveTwoSteps() {

		List<Operation> operations = ImmutableList.<Operation> of(new SetTurn(wId), 
				new Set(S[3][1], P[8]), new Delete(S[1][1]));

		VerifyMove verifyMove = move(bId, randomBlackState, operations);
		assertHacker(verifyMove);
	}

	@Test
	public void testBlackMoveLeft() {

		List<Operation> operations = ImmutableList.<Operation> of(new SetTurn(wId), 
				new Set(S[1][0], P[8]), new Delete(S[1][1]));

		VerifyMove verifyMove = move(bId, randomBlackState, operations);
		VerifyMoveDone verifyDone = new GhostsLogic().verify(verifyMove);
		assertEquals(null, verifyDone.getHackerPlayerId());
	}

	@Test
	public void testIllegalBlackMoveDownLeft() {

		List<Operation> operations = ImmutableList.<Operation> of(new SetTurn(wId), 
				new Set(S[2][0], P[8]), new Delete(S[1][1]));

		VerifyMove verifyMove = move(bId, randomBlackState, operations);
		assertHacker(verifyMove);
	}

	@Test
	public void testBlackMoveRight() {

		List<Operation> operations = ImmutableList.<Operation> of(new SetTurn(wId), 
				new Set(S[1][2], P[8]), new Delete(S[1][1]));

		VerifyMove verifyMove = move(bId, randomBlackState, operations);
		VerifyMoveDone verifyDone = new GhostsLogic().verify(verifyMove);
		assertEquals(null, verifyDone.getHackerPlayerId());
	}

	@Test
	public void testIllegalBlackMoveDownRight() {

		List<Operation> operations = ImmutableList.<Operation> of(new SetTurn(wId), 
				new Set(S[2][2], P[8]), new Delete(S[1][1]));

		VerifyMove verifyMove = move(bId, randomBlackState, operations);
		assertHacker(verifyMove);
	}
	
	@Test
	public void testNotMoving() {
		
		List<Operation> operations = ImmutableList.<Operation> of(
				new SetTurn(bId), 
				new Set(S[4][1], P[0]), 
				new Delete(S[4][1]));

		VerifyMove verifyMove = move(wId, randomWhiteState, operations);
		assertHacker(verifyMove);
	}
	
	@Test
	public void testWrongTurnMove() {
		
		List<Operation> operations = ImmutableList.<Operation> of(
				new SetTurn(wId), 
				new Set(S[2][1], P[8]), 
				new Delete(S[1][1]));

		VerifyMove verifyMove = move(wId, randomWhiteState, operations);
		assertHacker(verifyMove);
	}
	
	@Test
	public void testMovingPieceNotExist() {
		
		List<Operation> operations = ImmutableList.<Operation> of(
				new SetTurn(bId), 
				new Set(S[2][2], P[8]), 
				new Delete(S[2][1]));

		VerifyMove verifyMove = move(wId, randomWhiteState, operations);
		assertHacker(verifyMove);
	}

	@Test
	public void testNormalWhiteCaptureBlack() {

		List<Operation> operations = ImmutableList.<Operation> of(
				new SetTurn(bId), 
				new Set(S[1][1], P[0]), 
				new Delete(S[1][0]),
				new Delete(P[8]));

		VerifyMove verifyMove = move(wId, captureState, operations);
		VerifyMoveDone verifyDone = new GhostsLogic().verify(verifyMove);
		assertEquals(null, verifyDone.getHackerPlayerId());
	}
	
	@Test
	public void testCaptureSameSide() {

		List<Operation> operations = ImmutableList.<Operation> of(
				new SetTurn(bId), 
				new Set(S[2][0], P[0]), 
				new Delete(S[1][0]),
				new Delete(P[1]));

		VerifyMove verifyMove = move(wId, captureState, operations);
		assertHacker(verifyMove);
	}
	
	@Test
	public void testWhiteCaptureAndExit() {

		List<Operation> operations = ImmutableList.<Operation> of(
				new SetTurn(bId), 
				new Set(S[0][0], P[0]), 
				new Delete(S[1][0]),
				new Delete(P[9]),
				new SetVisibility(P[0]),
				new SetVisibility(P[1]),
				new EndGame(wId));

		VerifyMove verifyMove = move(wId, captureState, operations);
		VerifyMoveDone verifyDone = new GhostsLogic().verify(verifyMove);
		assertEquals(null, verifyDone.getHackerPlayerId());
	}
	
	@Test
	public void testBlackExitRight() {

		List<Operation> operations = ImmutableList.<Operation> of(
				new SetTurn(wId), 
				new Set(S[5][5], P[9]), 
				new Delete(S[4][5]),
				new SetVisibility(P[8]), 
				new SetVisibility(P[9]), 
				new SetVisibility(P[10]),
				new EndGame(bId));

		VerifyMove verifyMove = move(bId, blackToExitState, operations);
		VerifyMoveDone verifyDone = new GhostsLogic().verify(verifyMove);
		assertEquals(null, verifyDone.getHackerPlayerId());
	}

	@Test
	public void testBlackExitLeft() {

		List<Operation> operations = ImmutableList.<Operation> of(
				new SetTurn(wId), 
				new Set(S[5][0], P[8]), 
				new Delete(S[5][1]),
				new SetVisibility(P[8]), 
				new SetVisibility(P[9]), 
				new SetVisibility(P[10]),
				new EndGame(bId));

		VerifyMove verifyMove = move(bId, blackToExitState, operations);
		VerifyMoveDone verifyDone = new GhostsLogic().verify(verifyMove);
		assertEquals(null, verifyDone.getHackerPlayerId());
	}

	@Test
	public void testWhiteExitRight() {

		List<Operation> operations = ImmutableList.<Operation> of(
				new SetTurn(bId), 
				new Set(S[0][5], P[1]), 
				new Delete(S[0][4]),
				new SetVisibility(P[0]), 
				new SetVisibility(P[1]),
				new SetVisibility(P[2]),
				new EndGame(wId));

		VerifyMove verifyMove = move(wId, whiteToExitState, operations);
		VerifyMoveDone verifyDone = new GhostsLogic().verify(verifyMove);
		assertEquals(null, verifyDone.getHackerPlayerId());
	}

	@Test
	public void testWhiteExitLeft() {

		List<Operation> operations = ImmutableList.<Operation> of(
				new SetTurn(bId), 
				new Set(S[0][0], P[0]), 
				new Delete(S[1][0]),
				new SetVisibility(P[0]), 
				new SetVisibility(P[1]), 
				new SetVisibility(P[2]),
				new EndGame(wId));

		VerifyMove verifyMove = move(wId, whiteToExitState, operations);
		VerifyMoveDone verifyDone = new GhostsLogic().verify(verifyMove);
		assertEquals(null, verifyDone.getHackerPlayerId());
	}
}
