package com.liangfang.ghosts.client;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Map;

import com.google.common.base.Optional;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableList;

public class GhostsState {
	private final Color turn;
	private final ImmutableList<Integer> playerIds;
	/**
	 * Pieces has 16 piece representing P0 to P15 in order, some of entries will be null,
	 * meaning not visible
	 */
	private final ImmutableList<Optional<Piece>> Pieces;

	/**
	 * If map contains a null value, meaning there is no piece on the
	 * square.
	 * @arg String here is whether null or one of "P0"~"P15"
	 * @arg Position here will be object of Position class
	 */
	private final Map<Position, String> Squares;
	
	// if one player hasn't deploy his ghosts, then game cannot start
	boolean wDeployFinished;
	boolean bDeployFinished;
	

	public GhostsState(Color turn, ImmutableList<Integer> playerIds, ImmutableList<Optional<Piece>> Pieces,
			Map<Position, String> Squares, boolean wDeployFinished, boolean bDeployFinished) {
		super();
		this.turn = checkNotNull(turn);
		this.playerIds = checkNotNull(playerIds);
		this.Pieces = checkNotNull(Pieces);
		this.Squares = checkNotNull(Squares);
		this.wDeployFinished = wDeployFinished;
		this.bDeployFinished = bDeployFinished;
	}

	public Color getTurn() {
		return turn;
	}

	public ImmutableList<Optional<Piece>> getPieces() {
		return Pieces;
	}

	public Map<Position, String> getSquares() {
		return Squares;
	}
	
	public boolean isWhiteDeployed() {
		return wDeployFinished;
	}
	
	public boolean isBlackDeployed() {
		return bDeployFinished;
	}

	public ImmutableList<Integer> getPlayerIds() {
	    return playerIds;
	}
	
	public int getPlayerId(Color color) {
	    return playerIds.get(color.ordinal());
	}
}
