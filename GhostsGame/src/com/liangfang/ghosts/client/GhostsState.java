package com.liangfang.ghosts.client;

import static com.google.common.base.Preconditions.checkNotNull;

import com.google.common.base.Optional;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableList;

public class GhostsState {
	private final Color turn;

	/**
	 * Pieces has 16 piece representing P0 to P15 in order, some of entries will be null,
	 * meaning not visible
	 */
	private final ImmutableList<Optional<Piece>> Pieces;

	/**
	 * If map contains a null value, meaning there is no piece on the
	 * square.
	 * @arg String here is whether null or one of "P0"~"P15"
	 */
	private final ImmutableMap<Position, String> Squares;

	public GhostsState(Color turn, ImmutableList<Optional<Piece>> Pieces,
			ImmutableMap<Position, String> Squares) {
		super();
		this.turn = checkNotNull(turn);
		this.Pieces = checkNotNull(Pieces);
		this.Squares = checkNotNull(Squares);
	}

	public Color getTurn() {
		return turn;
	}

	public ImmutableList<Optional<Piece>> getPieces() {
		return Pieces;
	}

	public ImmutableMap<Position, String> getSquares() {
		return Squares;
	}

}
