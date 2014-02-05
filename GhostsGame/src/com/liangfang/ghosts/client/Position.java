package com.liangfang.ghosts.client;

import com.google.common.base.Objects;

public class Position {
	public int row;
	public int col;
	
	public Position() {
		row = -1;
		col = -1;
	}

	public Position(int row, int col) {
		this.row = row;
		this.col = col;
	}

	@Override
	public String toString() {
	    return "(" + row + "," + col + ")";
	}

	@Override
	public int hashCode() {
	    return Objects.hashCode(row, col);
	}

	@Override
	public boolean equals(Object obj) {
	    if (this == obj) return true;
	    if (obj == null) return false;
	    if (!(obj instanceof Position)) return false;
	    Position other = (Position) obj;
	    return row == other.row 
	      && col == other.col;
	}

}
