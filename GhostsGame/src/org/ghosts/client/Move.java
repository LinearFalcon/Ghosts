package org.ghosts.client;

import static com.google.common.base.Preconditions.checkNotNull;

import com.google.common.base.Objects;

public class Move {
	private Position start;
	private Position destination;
	
	public Move(Position start, Position destination) {
		this.start = checkNotNull(start);
		this.destination = checkNotNull(destination);
	}
	
	public Position getStart() {
		return start;
	}
	
	public Position getDestination() {
		return destination;
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
