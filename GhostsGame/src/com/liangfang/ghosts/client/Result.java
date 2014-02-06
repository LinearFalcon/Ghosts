package com.liangfang.ghosts.client;

import static com.google.common.base.Preconditions.checkNotNull;

import com.google.common.base.Objects;

public class Result {
	private Color winner;
	private GameResultReason reason;
	
	public Result(Color winner, GameResultReason reason) {
		this.winner = winner;
		this.reason = checkNotNull(reason);
	}
	
/*	public void setResult(SquareType winner) {
		this.winner = winner;
	}
*/	
	public boolean isDraw() {
	    return winner == null;
	}
	
	public Color getWinner() {
		return winner;
	}
	
	@Override
	public String toString() {
	    return "GameResult [winner=" + winner + ", ResultReason=" + reason + "]";
	}

	@Override
	public int hashCode() {
		return Objects.hashCode(winner, reason);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) return true;
	    if (obj == null) return false;
	    if (!(obj instanceof Result)) return false;
	    Result other = (Result) obj;
	    return Objects.equal(reason, other.reason)
	      && Objects.equal(winner, other.winner);
	}
}
