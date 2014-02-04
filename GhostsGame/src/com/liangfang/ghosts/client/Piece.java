package com.liangfang.ghosts.client;

import static com.google.common.base.Preconditions.checkNotNull;

import com.google.common.base.Objects;


public class Piece {
	private SquareType type;
	private PieceKind kind;
	
	public Piece(SquareType type, PieceKind kind) {
		this.type = checkNotNull(type);
		this.kind = kind;		//piece kind can be null since this piece could be exit
	}
	
	public SquareType gettype() {
		return type;
	}
	
	public PieceKind getPieceKind() {
		return kind;
	}
	
	@Override
	public String toString() {
	    return "(" + type + " " + kind + ")";
	}

	@Override
	public int hashCode() {
	    return Objects.hashCode(type, kind);
	}

	@Override
	public boolean equals(Object obj) {
	    if (this == obj) return true;
	    if (obj == null) return false;
	    if (!(obj instanceof Piece)) return false;
	    Piece other = (Piece) obj;
	    return Objects.equal(type, other.type)
	      && Objects.equal(kind, other.kind);
	}
}
