package com.liangfang.ghosts.client;

import static org.junit.Assert.*;

import org.junit.Test;

public abstract class AbstractStateChangerAllTest extends AbstractStateChangerTest{
	
	@Test
	public void testGhostCanMoveDown() {
		Move move = new Move(new Position(3, 2), new Position(2, 2));
		start.setPiece(3, 2, new Piece(Color.BLACK, PieceKind.evil));
		start.setPiece(1, 2, new Piece(Color.WHITE, PieceKind.good));
		start.setPiece(2, 3, new Piece(Color.BLACK, PieceKind.good));
		start.setPiece(5, 2, new Piece(Color.WHITE, PieceKind.evil));
		start.setTurn(Color.BLACK);
		State expected = start.copy();
		expected.setTurn(Color.WHITE);
		expected.setPiece(3, 2, null);
		expected.setPiece(2, 2, new Piece(Color.BLACK, PieceKind.evil));
		stateChanger.makeMove(start, move);
		assertEquals(expected, start);
	}
	
	@Test
	public void testGhostCanMoveUp() {
		Move move = new Move(new Position(3, 2), new Position(4, 2));
		start.setPiece(3, 2, new Piece(Color.BLACK, PieceKind.evil));
		start.setPiece(1, 2, new Piece(Color.WHITE, PieceKind.good));
		start.setPiece(2, 3, new Piece(Color.BLACK, PieceKind.good));
		start.setPiece(5, 2, new Piece(Color.WHITE, PieceKind.evil));
		start.setTurn(Color.BLACK);
		State expected = start.copy();
		expected.setTurn(Color.WHITE);
		expected.setPiece(3, 2, null);
		expected.setPiece(4, 2, new Piece(Color.BLACK, PieceKind.evil));
		stateChanger.makeMove(start, move);
		assertEquals(expected, start);
	}
	
	@Test
	public void testGhostCanMoveLeft() {
		Move move = new Move(new Position(3, 2), new Position(3, 1));
		start.setPiece(3, 2, new Piece(Color.BLACK, PieceKind.evil));
		start.setPiece(1, 2, new Piece(Color.WHITE, PieceKind.good));
		start.setPiece(2, 3, new Piece(Color.BLACK, PieceKind.good));
		start.setPiece(5, 2, new Piece(Color.WHITE, PieceKind.evil));
		start.setTurn(Color.BLACK);
		State expected = start.copy();
		expected.setTurn(Color.WHITE);
		expected.setPiece(3, 2, null);
		expected.setPiece(3, 1, new Piece(Color.BLACK, PieceKind.evil));
		stateChanger.makeMove(start, move);
		assertEquals(expected, start);
	}
	
	@Test
	public void testGhostCanMoveRight() {
		Move move = new Move(new Position(3, 2), new Position(3, 3));
		start.setPiece(3, 2, new Piece(Color.BLACK, PieceKind.evil));
		start.setPiece(1, 2, new Piece(Color.WHITE, PieceKind.good));
		start.setPiece(2, 3, new Piece(Color.BLACK, PieceKind.good));
		start.setPiece(5, 2, new Piece(Color.WHITE, PieceKind.evil));
		start.setTurn(Color.BLACK);
		State expected = start.copy();
		expected.setTurn(Color.WHITE);
		expected.setPiece(3, 2, null);
		expected.setPiece(3, 3, new Piece(Color.BLACK, PieceKind.evil));
		stateChanger.makeMove(start, move);
		assertEquals(expected, start);
	}
	
	@Test
	public void testBlackGoodCaptureWhiteGood() {
		Move move = new Move(new Position(2, 2), new Position(2, 3));
		start.setPiece(2, 2, new Piece(Color.BLACK, PieceKind.good));
		start.setPiece(2, 3, new Piece(Color.WHITE, PieceKind.good));
		start.setPiece(4, 2, new Piece(Color.BLACK, PieceKind.evil));
		start.setPiece(4, 4, new Piece(Color.WHITE, PieceKind.evil));
		start.setPiece(1, 3, new Piece(Color.WHITE, PieceKind.good));
		start.setTurn(Color.BLACK);
		State expected = start.copy();
		expected.setTurn(Color.WHITE);
		expected.setLastCapturedPiece(expected.getPiece(new Position(2, 3)));
		expected.setPiece(2, 2, null);
		expected.setPiece(2, 3, new Piece(Color.BLACK, PieceKind.good));
		stateChanger.makeMove(start, move);
		assertEquals(expected, start);
	}
	
	@Test
	public void testWhiteGoodCaptureBlackGood() {
		Move move = new Move(new Position(2, 2), new Position(2, 3));
		start.setPiece(2, 2, new Piece(Color.WHITE, PieceKind.good));
		start.setPiece(2, 3, new Piece(Color.BLACK, PieceKind.good));
		start.setPiece(4, 2, new Piece(Color.WHITE, PieceKind.evil));
		start.setPiece(4, 4, new Piece(Color.BLACK, PieceKind.evil));
		start.setPiece(1, 3, new Piece(Color.BLACK, PieceKind.good));
		State expected = start.copy();
		expected.setTurn(Color.BLACK);
		expected.setLastCapturedPiece(expected.getPiece(new Position(2, 3)));
		expected.setPiece(2, 2, null);
		expected.setPiece(2, 3, new Piece(Color.WHITE, PieceKind.good));
		stateChanger.makeMove(start, move);
		assertEquals(expected, start);
	}
	
	@Test
	public void testWhiteGoodCaptureBlackEvil() {
		Move move = new Move(new Position(4, 5), new Position(4, 4));
		start.setPiece(4, 5, new Piece(Color.WHITE, PieceKind.good));
		start.setPiece(4, 4, new Piece(Color.BLACK, PieceKind.evil));
		start.setPiece(2, 3, new Piece(Color.BLACK, PieceKind.good));
		start.setPiece(1, 5, new Piece(Color.BLACK, PieceKind.evil));
		start.setPiece(2, 5, new Piece(Color.WHITE, PieceKind.evil));
		State expected = start.copy();
		expected.setTurn(Color.BLACK);
		expected.setLastCapturedPiece(expected.getPiece(new Position(4, 4)));
		expected.setPiece(4, 5, null);
		expected.setPiece(4, 4, new Piece(Color.WHITE, PieceKind.good));
		stateChanger.makeMove(start, move);
		assertEquals(expected, start);
	}
	
	@Test
	public void testBlackGoodCaptureWhiteEvil() {
		Move move = new Move(new Position(4, 5), new Position(4, 4));
		start.setPiece(4, 5, new Piece(Color.BLACK, PieceKind.good));
		start.setPiece(4, 4, new Piece(Color.WHITE, PieceKind.evil));
		start.setPiece(2, 3, new Piece(Color.WHITE, PieceKind.good));
		start.setPiece(1, 5, new Piece(Color.WHITE, PieceKind.evil));
		start.setPiece(2, 5, new Piece(Color.BLACK, PieceKind.evil));
		start.setTurn(Color.BLACK);
		State expected = start.copy();
		expected.setTurn(Color.WHITE);
		expected.setLastCapturedPiece(expected.getPiece(new Position(4, 4)));
		expected.setPiece(4, 5, null);
		expected.setPiece(4, 4, new Piece(Color.BLACK, PieceKind.good));
		stateChanger.makeMove(start, move);
		assertEquals(expected, start);
	}

	@Test
	public void testBlackEvilCaptureWhiteGood() {
		Move move = new Move(new Position(3, 2), new Position(3, 3));
		start.setPiece(3, 2, new Piece(Color.BLACK, PieceKind.evil));
		start.setPiece(3, 3, new Piece(Color.WHITE, PieceKind.good));
		start.setPiece(4, 2, new Piece(Color.BLACK, PieceKind.good));
		start.setPiece(4, 4, new Piece(Color.WHITE, PieceKind.evil));
		start.setPiece(1, 3, new Piece(Color.WHITE, PieceKind.good));
		start.setTurn(Color.BLACK);
		State expected = start.copy();
		expected.setTurn(Color.WHITE);
		expected.setLastCapturedPiece(expected.getPiece(new Position(3, 3)));
		expected.setPiece(3, 2, null);
		expected.setPiece(3, 3, new Piece(Color.BLACK, PieceKind.evil));
		stateChanger.makeMove(start, move);
		assertEquals(expected, start);
	}
	
	@Test
	public void testWhiteEvilCaptureBlackGood() {
		Move move = new Move(new Position(3, 2), new Position(3, 3));
		start.setPiece(3, 2, new Piece(Color.WHITE, PieceKind.evil));
		start.setPiece(3, 3, new Piece(Color.BLACK, PieceKind.good));
		start.setPiece(4, 2, new Piece(Color.WHITE, PieceKind.good));
		start.setPiece(4, 4, new Piece(Color.BLACK, PieceKind.evil));
		start.setPiece(1, 3, new Piece(Color.BLACK, PieceKind.good));
		State expected = start.copy();
		expected.setTurn(Color.BLACK);
		expected.setLastCapturedPiece(expected.getPiece(new Position(3, 3)));
		expected.setPiece(3, 2, null);
		expected.setPiece(3, 3, new Piece(Color.WHITE, PieceKind.evil));
		stateChanger.makeMove(start, move);
		assertEquals(expected, start);
	}
	
	@Test
	public void testBlackEvilCaptureWhiteEvil() {
		Move move = new Move(new Position(3, 2), new Position(3, 3));
		start.setPiece(3, 2, new Piece(Color.BLACK, PieceKind.evil));
		start.setPiece(3, 3, new Piece(Color.WHITE, PieceKind.evil));
		start.setPiece(4, 2, new Piece(Color.BLACK, PieceKind.good));
		start.setPiece(4, 4, new Piece(Color.WHITE, PieceKind.evil));
		start.setPiece(1, 3, new Piece(Color.WHITE, PieceKind.good));
		start.setTurn(Color.BLACK);
		State expected = start.copy();
		expected.setTurn(Color.WHITE);
		expected.setLastCapturedPiece(expected.getPiece(new Position(3, 3)));
		expected.setPiece(3, 2, null);
		expected.setPiece(3, 3, new Piece(Color.BLACK, PieceKind.evil));
		stateChanger.makeMove(start, move);
		assertEquals(expected, start);
	}
	
	@Test
	public void testWhiteEvilCaptureBlackEvil() {
		Move move = new Move(new Position(3, 2), new Position(3, 3));
		start.setPiece(3, 2, new Piece(Color.WHITE, PieceKind.evil));
		start.setPiece(3, 3, new Piece(Color.BLACK, PieceKind.evil));
		start.setPiece(4, 2, new Piece(Color.WHITE, PieceKind.good));
		start.setPiece(4, 4, new Piece(Color.BLACK, PieceKind.evil));
		start.setPiece(1, 3, new Piece(Color.BLACK, PieceKind.good));
		State expected = start.copy();
		expected.setTurn(Color.BLACK);
		expected.setLastCapturedPiece(expected.getPiece(new Position(3, 3)));
		expected.setPiece(3, 2, null);
		expected.setPiece(3, 3, new Piece(Color.WHITE, PieceKind.evil));
		stateChanger.makeMove(start, move);
		assertEquals(expected, start);
	}

	@Test
	public void testBlackWinByLeftExit() {
		Move move = new Move(new Position(0, 1), new Position(0, 0));
		start.setPiece(0, 1, new Piece(Color.BLACK, PieceKind.good));
		start.setPiece(4, 2, new Piece(Color.BLACK, PieceKind.evil));
		start.setPiece(4, 4, new Piece(Color.WHITE, PieceKind.evil));
		start.setPiece(1, 3, new Piece(Color.WHITE, PieceKind.good));
		start.setTurn(Color.BLACK);
		State expected = start.copy();
		expected.setTurn(Color.WHITE);
		expected.setPiece(0, 1, null);
		expected.setPiece(0, 0, new Piece(Color.BLACK, PieceKind.good));
		expected.setResult(new Result(Color.BLACK, GameResultReason.EXIT_SUCCEED));
		stateChanger.makeMove(start, move);
		assertEquals(expected, start);
	}

	@Test
	public void testBlackWinByRightExit() {
		Move move = new Move(new Position(1, 5), new Position(0, 5));
		start.setPiece(1, 5, new Piece(Color.BLACK, PieceKind.good));
		start.setPiece(4, 2, new Piece(Color.BLACK, PieceKind.evil));
		start.setPiece(4, 0, new Piece(Color.WHITE, PieceKind.evil));
		start.setPiece(1, 3, new Piece(Color.WHITE, PieceKind.good));
		start.setTurn(Color.BLACK);
		State expected = start.copy();
		expected.setTurn(Color.WHITE);
		expected.setPiece(1, 5, null);
		expected.setPiece(0, 5, new Piece(Color.BLACK, PieceKind.good));
		expected.setResult(new Result(Color.BLACK, GameResultReason.EXIT_SUCCEED));
		stateChanger.makeMove(start, move);
		assertEquals(expected, start);
	}

	@Test
	public void testWhiteWinByLeftExit() {
		Move move = new Move(new Position(4, 0), new Position(5, 0));
		start.setPiece(4, 0, new Piece(Color.WHITE, PieceKind.good));
		start.setPiece(4, 2, new Piece(Color.BLACK, PieceKind.evil));
		start.setPiece(4, 4, new Piece(Color.WHITE, PieceKind.evil));
		start.setPiece(1, 3, new Piece(Color.BLACK, PieceKind.good));
		State expected = start.copy();
		expected.setTurn(Color.BLACK);
		expected.setPiece(4, 0, null);
		expected.setPiece(5, 0, new Piece(Color.WHITE, PieceKind.good));
		expected.setResult(new Result(Color.WHITE, GameResultReason.EXIT_SUCCEED));
		stateChanger.makeMove(start, move);
		assertEquals(expected, start);
	}

	@Test
	public void testWhiteWinByRightExit() {
		Move move = new Move(new Position(5, 4), new Position(5, 5));
		start.setPiece(5, 4, new Piece(Color.WHITE, PieceKind.good));
		start.setPiece(4, 2, new Piece(Color.BLACK, PieceKind.evil));
		start.setPiece(4, 4, new Piece(Color.WHITE, PieceKind.evil));
		start.setPiece(1, 3, new Piece(Color.BLACK, PieceKind.good));
		State expected = start.copy();
		expected.setTurn(Color.BLACK);
		expected.setPiece(5, 4, null);
		expected.setPiece(5, 5, new Piece(Color.WHITE, PieceKind.good));
		expected.setResult(new Result(Color.WHITE, GameResultReason.EXIT_SUCCEED));
		stateChanger.makeMove(start, move);
		assertEquals(expected, start);
	}
	
	@Test(expected = IllegalMove.class)
	public void testEmptyPieceMove() {
		start.setPiece(5, 1, new Piece(Color.WHITE, PieceKind.good));
		start.setPiece(4, 2, new Piece(Color.BLACK, PieceKind.evil));
		start.setPiece(4, 4, new Piece(Color.WHITE, PieceKind.evil));
		start.setPiece(1, 3, new Piece(Color.BLACK, PieceKind.good));
		Move move = new Move(new Position(2, 0), new Position(3, 0));
		stateChanger.makeMove(start, move);
	}
	
	@Test(expected = IllegalMove.class)
	public void testWrongPlayerMoving() {
		start.setPiece(5, 1, new Piece(Color.WHITE, PieceKind.good));
		start.setPiece(4, 2, new Piece(Color.BLACK, PieceKind.evil));
		start.setPiece(4, 4, new Piece(Color.WHITE, PieceKind.evil));
		start.setPiece(1, 3, new Piece(Color.BLACK, PieceKind.good));
		start.setTurn(Color.BLACK);
		Move move = new Move(new Position(5, 1), new Position(5, 2));
		stateChanger.makeMove(start, move);
	}
	
	@Test(expected = IllegalMove.class)
	public void testGhostMoveMoreThanOneSquare() {
		start.setPiece(5, 1, new Piece(Color.WHITE, PieceKind.good));
		start.setPiece(4, 2, new Piece(Color.BLACK, PieceKind.evil));
		start.setPiece(4, 4, new Piece(Color.WHITE, PieceKind.evil));
		start.setPiece(1, 3, new Piece(Color.BLACK, PieceKind.good));
		Move move = new Move(new Position(5, 1), new Position(5, 3));
		stateChanger.makeMove(start, move);
	}
	
	@Test(expected = IllegalMove.class)
	public void testGameAlreadyOver() {
		start.setPiece(5, 1, new Piece(Color.WHITE, PieceKind.good));
		start.setPiece(4, 2, new Piece(Color.WHITE, PieceKind.evil));
		start.setPiece(4, 4, new Piece(Color.WHITE, PieceKind.evil));
		start.setPiece(1, 3, new Piece(Color.BLACK, PieceKind.evil));
		start.setResult(new Result(Color.WHITE, GameResultReason.CAPTURE_ALL_GOOD_GHOST));
		Move move = new Move(new Position(5, 1), new Position(5, 2));
		stateChanger.makeMove(start, move);
	}

}
