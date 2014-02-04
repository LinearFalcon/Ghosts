package com.liangfang.ghosts.client;

import com.google.common.base.Objects;

public class Position {
	public int x;
	public int y;
	
	public Position() {
		x = -1;
		y = -1;
	}

	public Position(int x, int y) {
		this.x = x;
		this.y = y;
	}

	@Override
	public String toString() {
	    return "(" + x + "," + y + ")";
	}

	@Override
	public int hashCode() {
	    return Objects.hashCode(x, y);
	}

	@Override
	public boolean equals(Object obj) {
	    if (this == obj) return true;
	    if (obj == null) return false;
	    if (!(obj instanceof Position)) return false;
	    Position other = (Position) obj;
	    return x == other.x 
	      && y == other.y;
	}

}
