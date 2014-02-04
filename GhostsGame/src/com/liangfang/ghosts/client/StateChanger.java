package com.liangfang.ghosts.client;

public interface StateChanger {
	  /**
	   * Make a ghost move and change state to reflect the new game state.
	   * If the move is illegal, the method throws IllegalMove.
	   */
	  public void makeMove(State state, Move move) throws IllegalMove;
}