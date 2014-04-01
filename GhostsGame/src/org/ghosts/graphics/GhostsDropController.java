package org.ghosts.graphics;

import java.util.List;
import org.ghosts.client.GhostsPresenter;
import org.ghosts.client.Position;

import com.allen_sauer.gwt.dnd.client.DragContext;
import com.allen_sauer.gwt.dnd.client.VetoDragException;
import com.allen_sauer.gwt.dnd.client.drop.SimpleDropController;
import com.google.gwt.media.client.Audio;
import com.google.gwt.user.client.ui.AbsolutePanel;

public class GhostsDropController extends SimpleDropController {
	private final Position pos;
	private final GhostsPresenter presenter;
	private final AbsolutePanel panel;
	private Audio sound; 
	private final List<Position> possiblePositions;
	
	public GhostsDropController(AbsolutePanel panel, Position pos, 
			GhostsPresenter presenter, List<Position> possiblePositions, Audio sound) {		
		super(panel);
		this.panel = panel;
		this.pos = pos;
		this.presenter = presenter;
		this.sound = sound;
		this.possiblePositions = possiblePositions;
	}

	@Override
	public void onDrop(DragContext context) {
		// If dropping position is contained in possiblePositions, then call squareSelectedToMove
		if (possiblePositions.contains(pos)) {
			presenter.squareSelectedToMove(pos, true);
			if (sound != null)
				sound.play();
		} 
		// If not, just reconstruct game UI and make everything like before
		else {
			presenter.reupdateUI();
		}
	}


	@Override
	public void onPreviewDrop(DragContext context) throws VetoDragException {
		if (panel == null) {
			throw new VetoDragException();
		}
		super.onPreviewDrop(context);
	}
}
