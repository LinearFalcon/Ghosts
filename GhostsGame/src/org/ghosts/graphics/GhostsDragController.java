package org.ghosts.graphics;

import java.util.List;
import java.util.Map;

import org.ghosts.client.GhostsPresenter;
import org.ghosts.client.GhostsState;
import org.ghosts.client.Piece;
import org.ghosts.client.Position;

import com.allen_sauer.gwt.dnd.client.PickupDragController;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.Image;

public class GhostsDragController extends PickupDragController {
	private final GhostsPresenter presenter;

	public GhostsDragController(AbsolutePanel boundaryPanel,
			boolean allowDroppingOnBoundaryPanel, GhostsPresenter presenter) {
		super(boundaryPanel, allowDroppingOnBoundaryPanel);
		this.presenter = presenter;
	}

	@Override
	public void dragStart() {
		super.dragStart();
		saveSelectedWidgetsLocationAndStyle();

		GhostsState state = presenter.getState();
		// Get the dragger's position
		Position startPos = getPosition((Image) context.draggable);
		// Get squares info
		Map<Position, String> squares = state.getSquares();
		// Get pieceName(e.g: P2, P13) by squares
		String pieceName = squares.get(startPos);
		// Get pieces list from presenter
		List<Piece> pieces = presenter.getPiecesList();
		// Get piece index from pieceName
		int index = getIndexFromPieceName(pieceName);
		// Get Piece object
		Piece piece = pieces.get(index);
		
		presenter.pieceSelectedToMove(piece);
	}

	public Position getPosition(Image image) {
		int row = (image.getAbsoluteTop() / 96);
		int col = (image.getAbsoluteLeft() / 96);
		return new Position(row, col);
	}

	/* 
	 * return int value of the number after "P" in Px/xx
	 */
	int getIndexFromPieceName(String piecename) {
		if (piecename.length() == 2)
			return (int) (piecename.charAt(1) - '0');
		else { // P10~P15
			int tenth = (int) (piecename.charAt(1) - '0') * 10;
			return tenth + (int) (piecename.charAt(2) - '0');
		}
	}
}
