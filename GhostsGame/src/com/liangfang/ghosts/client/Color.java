package com.liangfang.ghosts.client;

public enum Color {
	W, B;

	public boolean isWhite() {
		return this == W;
	}

	public boolean isBlack() {
		return this == B;
	}

	public Color getOpposite() {
		return this == W ? B : W;
	}

	public String toString() {
		return isWhite() ? "W" : "B";
	}
}
