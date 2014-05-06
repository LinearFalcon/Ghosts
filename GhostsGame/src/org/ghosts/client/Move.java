package org.ghosts.client;

import static com.google.common.base.Preconditions.checkNotNull;

import com.google.common.base.Objects;

public class Move {
	public enum MoveType {EXIT, CAPTURE, MOVE};
	
	private Position start;
	private Position destination;
	private MoveType type;
	
	public Move(Position start, Position destination) {
		this.start = checkNotNull(start);
		this.destination = checkNotNull(destination);
	}
	
	public Move(Position start, Position destination, MoveType type) {
		this.start = checkNotNull(start);
		this.destination = checkNotNull(destination);
		this.type = type;
	}
	
	public Position getStart() {
		return start;
	}
	
	public Position getDestination() {
		return destination;
	}
	
	public MoveType getType() {
		return type;
	}

	@Override
	public String toString() {
		return start + "->" + destination;
	}

	@Override
	public int hashCode() {
		return Objects.hashCode(start, destination);
	}

	@Override
	public boolean equals(Object obj) {
	    if (this == obj) return true;
	    if (obj == null) return false;
	    if (!(obj instanceof Move)) return false;
	    Move other = (Move) obj;
	    return Objects.equal(start, other.start)
	      && Objects.equal(destination, other.destination);
	}
}
