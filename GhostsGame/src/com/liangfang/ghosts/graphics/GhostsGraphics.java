package com.liangfang.ghosts.graphics;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import com.liangfang.ghosts.client.GhostsPresenter;
import com.liangfang.ghosts.client.Piece;
import com.liangfang.ghosts.client.Position;

import com.google.common.base.Optional;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.gwt.core.shared.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Widget;

/**
 * Graphics for the game of ghosts.
 */
public class GhostsGraphics extends Composite implements GhostsPresenter.View {
	public interface GhostsGraphicsUiBinder extends UiBinder<Widget, GhostsGraphics> {
	}
	
	@UiField
	Grid gameGrid;
	@UiField
	HorizontalPanel deploySelectArea;
	@UiField
	Button deployBtn;
	
	private boolean enableClicks = false;
	private final PieceImageSupplier pieceImageSupplier;
	private GhostsPresenter presenter;
	
	public GhostsGraphics() {
		PieceImages pieceImages = GWT.create(PieceImages.class);
	    this.pieceImageSupplier = new PieceImageSupplier(pieceImages);
	    GhostsGraphicsUiBinder uiBinder = GWT.create(GhostsGraphicsUiBinder.class);
	    initWidget(uiBinder.createAndBindUi(this));
	    
	    gameGrid.resize(6, 6);
        gameGrid.setCellPadding(0);
        gameGrid.setCellSpacing(0);
        gameGrid.setBorderWidth(1);
	}

	private void placeImagesOnGrid(Grid grid, List<Image> images) {
	    grid.clear();
	    Image last = images.isEmpty() ? null : images.get(images.size() - 1);
	    for (Image image : images) {
	      FlowPanel imageContainer = new FlowPanel();
	      imageContainer.setStyleName(image != last ? "imgShortContainer" : "imgContainer");
	      imageContainer.add(image);
	      
	    }
	}
	
	private void placeImagesOnHorizontalPanel(Grid grid, List<Image> images) {
		
	}
	
	private void disableClicks() {
	    deployBtn.setEnabled(false);
	    enableClicks = false;
	}

	@UiHandler("deployBtn")
	void onClickClaimBtn(ClickEvent e) {
	    disableClicks();
	    presenter.deployFinished();
	}
	
	@Override
	public void setPresenter(GhostsPresenter ghostsPresenter) {
		this.presenter = ghostsPresenter;
	}

	@Override
	public void setViewerState(Map<Position, String> squares) {
		
	}

	@Override
	public void setPlayerState(List<Piece> pieces, Map<Position, String> squares) {
		
	}

	@Override
	public void chooseNextPieceToMove(List<Piece> selectedPiece,
			List<Piece> remainingPiece) {
		
	}

	@Override
	public void chooseSquareToMove(List<Position> possiblePositions) {
		
	}

	@Override
	public void chooseNextPieceToDeploy(List<Piece> seletedPieceToDeploy,
			List<Boolean> pieceDeployed) {
		
	}

	@Override
	public void chooseSquareToDeploy(List<Position> possiblePositions) {
		
	}

}
