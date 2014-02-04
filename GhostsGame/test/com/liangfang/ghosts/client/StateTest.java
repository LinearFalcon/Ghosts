package com.liangfang.ghosts.client;

import static org.junit.Assert.*;

import org.junit.Test;

public class StateTest {
	
	@Test
	public void testGoodCaptureGood() {
		State original = new State();
		Move move = new Move(new Position(2, 2), new Position(2, 3));
		original.setPiece(2, 2, new Piece(SquareType.BLACK, PieceKind.good));
		original.setPiece(2, 3, new Piece(SquareType.WHITE, PieceKind.good));
		original.setPiece(4, 2, new Piece(SquareType.BLACK, PieceKind.evil));
		original.setPiece(4, 4, new Piece(SquareType.WHITE, PieceKind.evil));
		original.setPiece(1, 3, new Piece(SquareType.WHITE, PieceKind.good));
		State expected = original.copy();
		expected.setTurn(SquareType.WHITE);
		expected.setPiece(2, 2, new Piece(SquareType.EMPTY, null));
		expected.setPiece(2, 3, new Piece(SquareType.BLACK, PieceKind.good));
		original.makeMove(move);
		assertEquals(expected, original);
	}
	
	@Test
	public void testGoodCaptureEvil() {
		State original = new State();
		Move move = new Move(new Position(4, 5), new Position(4, 4));
		original.setPiece(4, 5, new Piece(SquareType.WHITE, PieceKind.good));
		original.setPiece(4, 4, new Piece(SquareType.BLACK, PieceKind.evil));
		original.setPiece(2, 3, new Piece(SquareType.BLACK, PieceKind.good));
		original.setPiece(1, 5, new Piece(SquareType.BLACK, PieceKind.evil));
		original.setPiece(2, 5, new Piece(SquareType.WHITE, PieceKind.evil));
		State expected = original.copy();
		expected.setTurn(SquareType.BLACK);
		expected.setPiece(4, 5, new Piece(SquareType.EMPTY, null));
		expected.setPiece(4, 4, new Piece(SquareType.WHITE, PieceKind.good));
		original.makeMove(move);
		assertEquals(expected, original);
	}

	@Test
	public void testEvilCaptureGood() {
		State original = new State();
		Move move = new Move(new Position(3, 2), new Position(3, 3));
		original.setPiece(3, 2, new Piece(SquareType.BLACK, PieceKind.evil));
		original.setPiece(3, 3, new Piece(SquareType.WHITE, PieceKind.good));
		original.setPiece(4, 2, new Piece(SquareType.BLACK, PieceKind.good));
		original.setPiece(4, 4, new Piece(SquareType.WHITE, PieceKind.evil));
		original.setPiece(1, 3, new Piece(SquareType.WHITE, PieceKind.good));
		State expected = original.copy();
		expected.setTurn(SquareType.WHITE);
		expected.setPiece(3, 2, new Piece(SquareType.EMPTY, null));
		expected.setPiece(3, 3, new Piece(SquareType.BLACK, PieceKind.evil));
		original.makeMove(move);
		assertEquals(expected, original);
	}

	@Test
	public void testBlackWinByLeftExit() {
		State original = new State();
		Move move = new Move(new Position(4, 0), new Position(5, 0));
		original.setPiece(4, 0, new Piece(SquareType.BLACK, PieceKind.good));
		original.setPiece(4, 2, new Piece(SquareType.BLACK, PieceKind.evil));
		original.setPiece(4, 4, new Piece(SquareType.WHITE, PieceKind.evil));
		original.setPiece(1, 3, new Piece(SquareType.WHITE, PieceKind.good));
		
		State expected = original.copy();
		expected.setTurn(SquareType.WHITE);
		expected.setPiece(4, 0, new Piece(SquareType.EMPTY, null));
		expected.setPiece(5, 0, new Piece(SquareType.BLACK, PieceKind.good));
		expected.setResult(new Result(SquareType.BLACK, GameResultReason.EXIT_SUCCEED));
		original.makeMove(move);
		assertEquals(expected, original);
	}

	@Test
	public void testBlackWinByRightExit() {
		State original = new State();
		Move move = new Move(new Position(4, 4), new Position(5, 5));
		original.setPiece(4, 4, new Piece(SquareType.BLACK, PieceKind.good));
		original.setPiece(4, 2, new Piece(SquareType.BLACK, PieceKind.evil));
		original.setPiece(4, 0, new Piece(SquareType.WHITE, PieceKind.evil));
		original.setPiece(1, 3, new Piece(SquareType.WHITE, PieceKind.good));
		State expected = original.copy();
		expected.setTurn(SquareType.WHITE);
		expected.setPiece(4, 4, new Piece(SquareType.EMPTY, null));
		expected.setPiece(5, 5, new Piece(SquareType.BLACK, PieceKind.good));
		expected.setResult(new Result(SquareType.BLACK, GameResultReason.EXIT_SUCCEED));
		original.makeMove(move);
		assertEquals(expected, original);
	}

	@Test
	public void testWhiteWinByLeftExit() {
		State original = new State();
		Move move = new Move(new Position(0, 1), new Position(0, 0));
		original.setPiece(0, 1, new Piece(SquareType.WHITE, PieceKind.good));
		original.setPiece(4, 2, new Piece(SquareType.BLACK, PieceKind.evil));
		original.setPiece(4, 4, new Piece(SquareType.WHITE, PieceKind.evil));
		original.setPiece(1, 3, new Piece(SquareType.BLACK, PieceKind.good));
		State expected = original.copy();
		expected.setTurn(SquareType.BLACK);
		expected.setPiece(0, 1, new Piece(SquareType.EMPTY, null));
		expected.setPiece(0, 0, new Piece(SquareType.WHITE, PieceKind.good));
		expected.setResult(new Result(SquareType.WHITE, GameResultReason.EXIT_SUCCEED));
		original.makeMove(move);
		assertEquals(expected, original);
	}

	@Test
	public void testWhiteWinByRightExit() {
		State original = new State();
		Move move = new Move(new Position(5, 1), new Position(5, 0));
		original.setPiece(5, 1, new Piece(SquareType.WHITE, PieceKind.good));
		original.setPiece(4, 2, new Piece(SquareType.BLACK, PieceKind.evil));
		original.setPiece(4, 4, new Piece(SquareType.WHITE, PieceKind.evil));
		original.setPiece(1, 3, new Piece(SquareType.BLACK, PieceKind.good));
		State expected = original.copy();
		expected.setTurn(SquareType.BLACK);
		expected.setPiece(5, 1, new Piece(SquareType.EMPTY, null));
		expected.setPiece(5, 0, new Piece(SquareType.WHITE, PieceKind.good));
		expected.setResult(new Result(SquareType.WHITE, GameResultReason.EXIT_SUCCEED));
		original.makeMove(move);
		assertEquals(expected, original);
	}

}
