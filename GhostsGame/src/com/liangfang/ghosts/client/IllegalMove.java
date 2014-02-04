package com.liangfang.ghosts.client;

/**
 * Possible reasons for a illegal move are:
 * The player with the wrong color did a move,
 * The move starts from an empty square,
 * Game already over,
 * illegal capturing
 */
public class IllegalMove extends RuntimeException {
  private static final long serialVersionUID = 1L;

  public IllegalMove() {
  }
}