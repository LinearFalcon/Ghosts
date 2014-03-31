package org.ghosts.client;

import com.google.common.base.Objects;

public class Position {
	private int row;
	private int col;

	public Position() {
		row = -1;
		col = -1;
	}

	public Position(int row, int col) {
		this.row = row;
		this.col = col;
	}

	public int getRow() {
		return row;
	}

	public int getCol() {
		return col;
	}

	/*
	 * Determine whether position pos is exit for specific side
	 */
/*	public boolean isExitForWhite(Position pos) {
		return isExitForWhite(pos.getRow(), pos.getCol());
	}

	public boolean isExitForWhite(int row, int col) {
		return (row == 5 && col == 0) || (row == 5 && col == 5);
	}

	public boolean isExitForBlack(Position pos) {
		return isExitForWhite(pos.getRow(), pos.getCol());
	}

	public boolean isExitForBlack(int row, int col) {
		return (row == 0 && col == 0) || (row == 0 && col == 5);
	}
*/
	public String toSquareString() {
		return ("S" + row) + col;
	}

	@Override
	public int hashCode() {
		return Objects.hashCode(row, col);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof Position))
			return false;
		Position other = (Position) obj;
		return row == other.row && col == other.col;
	}

}
