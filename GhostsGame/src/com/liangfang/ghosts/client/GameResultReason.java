package com.liangfang.ghosts.client;

/**
 * Reasons why the chess game ended in draw or victory.
 * See http://en.wikipedia.org/wiki/Ghosts_(board_game)
 */
public enum GameResultReason {
	/** 
	 * A win is declared if a player capture all good ghosts of opposing player
	 */
	CAPTURE_ALL_GOOD_GHOST,
	
	/** 
	 * A draw is declared if a player capture all evil ghosts of opposing player
	 */
	CAPTURE_ALL_EVIL_GHOST,
	
	/** 
	 * A win is declared if a player enter opposing exit with a good ghost
	 */
	EXIT_SUCCEED,
	

}
