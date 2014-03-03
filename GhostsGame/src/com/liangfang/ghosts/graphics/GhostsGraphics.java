package com.liangfang.ghosts.graphics;

import java.util.Collections;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import com.liangfang.ghosts.client.Color;
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
import com.google.gwt.user.client.ui.HTMLTable;
import com.google.gwt.user.client.ui.HTMLTable.Cell;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Widget;

/**
 * Graphics for the game of ghosts.
 */
public class GhostsGraphics extends Composite implements GhostsPresenter.View {
	public interface GhostsGraphicsUiBinder extends
			UiBinder<Widget, GhostsGraphics> {
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
		GhostsGraphicsUiBinder uiBinder = GWT
				.create(GhostsGraphicsUiBinder.class);
		initWidget(uiBinder.createAndBindUi(this));

		gameGrid.resize(6, 6);
		gameGrid.setBorderWidth(1);
		for (int i = 0; i < 6; i++) {
			for (int j = 0; j < 6; j++) {
				gameGrid.getCellFormatter().setWidth(i, j, "96px");
				gameGrid.getCellFormatter().setHeight(i, j, "96px");
			}
		}
	}

	/*
	 * Create a 6*6 Image matrix with all pieces only display back
	 */
	private Image[][] createAllBackPieces(Map<Position, String> squares) {
		PieceImage[][] images = new PieceImage[6][6];
		for (int i = 0; i < 6; i++) {
			for (int j = 0; j < 6; j++) {
				if (squares.get(new Position(i, j)) != null) {
					int index = getIndexFromPieceName(squares.get(new Position(i, j)));
					if (index < 8)
						images[i][j] = PieceImage.Factory.getWhiteBackOfPieceImage();
					else
						images[i][j] = PieceImage.Factory.getBlackBackOfPieceImage();
				} else {
					images[i][j] = null;
				}
			}
		}
		return createImagesForGrid(images, false, null);	// don't need to use turn in createImagesForGrid since withClick is false already
	}
	
	/*
	 * Create a 6*6 Image matrix with current player's pieces display front and 
	 * opponent's pieces only display back 
	 */
	private Image[][] createNormalPieces(List<Piece> pieces, Map<Position, String> squares, Color color, boolean withClick) {
		PieceImage[][] images = new PieceImage[6][6];
		for (int i = 0; i < 6; i++) {
			for (int j = 0; j < 6; j++) {
				if (squares.get(new Position(i, j)) != null) {
					int index = getIndexFromPieceName(squares.get(new Position(i, j)));
					if (color.isWhite()) {
						if (index < 8)
							images[i][j] = PieceImage.Factory.getFrontOfPieceImage(pieces.get(index));			
						else
							images[i][j] = PieceImage.Factory.getBlackBackOfPieceImage();	
					} else {	
						if (index >= 8)
							images[i][j] = PieceImage.Factory.getFrontOfPieceImage(pieces.get(index));
						else
							images[i][j] = PieceImage.Factory.getWhiteBackOfPieceImage();
					}
				} else {
					images[i][j] = null;
				}
			}
		}
		
		return createImagesForGrid(images, withClick, color);
	}
	
	/*
	 * Create a 6*6 Image matrix with current player's pieces display front and 
	 * opponent's pieces only display back 
	 */
	private Image[][] createDeployedPieces(Map<Position, Piece> deployTable, Color turn, boolean withClick) {
		PieceImage[][] images = new PieceImage[6][6];
		for (int i = 0; i < 6; i++) {
			for (int j = 0; j < 6; j++) {
				Position pos = new Position(i, j);
				if (deployTable.containsKey(pos)) {
					Piece piece = deployTable.get(pos);
					int index = getIndexFromPieceName(piece.getPieceName());
					if (turn.isWhite()) {
						if (index < 8)
							images[i][j] = PieceImage.Factory.getFrontOfPieceImage(piece);
						else
							images[i][j] = PieceImage.Factory.getBlackBackOfPieceImage();
					} else {	
						if (index >= 8)
							images[i][j] = PieceImage.Factory.getFrontOfPieceImage(piece);
						else
							images[i][j] = PieceImage.Factory.getWhiteBackOfPieceImage();
					}
				} else {
					images[i][j] = null;
				}
			}
		}
		
		return createImagesForGrid(images, withClick, turn);
	}

	/*
	 * Helper function for createAllBackPieces and createNormalPieces
	 */
	private Image[][] createImagesForGrid(PieceImage[][] images, boolean withClick, Color turn) {
		Image[][] res = new Image[6][6];
		for (int i = 0; i < 6; i++) {
			for (int j = 0; j < 6; j++) {
				if (images[i][j] != null) {
					PieceImage img = images[i][j];
					final PieceImage imgFinal = img;
					
//					System.out.println(i + "," + j);
					
					Image image = new Image(pieceImageSupplier.getResource(img));
					
					if (withClick && imgFinal.piece != null) {
						if ((turn.isWhite() && imgFinal.piece.isWhitePiece()) || 			// only add click listener to current player's piece
								(turn.isBlack() && imgFinal.piece.isBlackPiece())) {
							image.addClickHandler(new ClickHandler() {
						          @Override
						          public void onClick(ClickEvent event) {
						        	  if (enableClicks) {
						        		  presenter.pieceSelectedToMove(imgFinal.piece);
						        	  }
						          }
						    });
						} 
					}
					
					res[i][j] = image;
				} else {
					res[i][j] = null;
				}
			}
		}
		return res;
	}

	/*
	 * Place piece images on grid
	 */
	private void placeImagesOnGrid(Grid grid, Image[][] images) {
		grid.clear();
		for (int i = 0; i < 6; i++) {
			for (int j = 0; j < 6; j++) {
				if (images[i][j] != null) {			// means there is a piece on this position
					FlowPanel imageContainer = new FlowPanel();
					imageContainer.setStyleName("imgContainer");
					imageContainer.add(images[i][j]);
					grid.setWidget(i, j, imageContainer);
					
				}
			}
		}
	}
	
	/*
	 * Create a list of all current player's pieces image which are not deployed yet 
	 */
	private List<Image> createRemainingPiecesToDeploy(List<Piece> pieces, List<Boolean> pieceDeployed, Color turn) {
		List<PieceImage> images = Lists.newArrayList();
		if (turn.isWhite()) {
			for (int i = 0; i < 8; i++) {
				if (!pieceDeployed.get(i)) {		// if this piece is not deployed
					images.add(PieceImage.Factory.getFrontOfPieceImage(pieces.get(i)));
				}
			}
		} else {
			for (int i = 8; i < 16; i++) {
				if (!pieceDeployed.get(i)) {		// if this piece is not deployed
					images.add(PieceImage.Factory.getFrontOfPieceImage(pieces.get(i)));
				}
			}
		}
		return createImagesForDeployPanel(images);
	}
	
	/*
	 * Helper function for createRemainingPiecesToDeploy
	 */
	private List<Image> createImagesForDeployPanel(List<PieceImage> images) {
		List<Image> res = Lists.newArrayList();
	    for (PieceImage img : images) {
	      final PieceImage imgFinal = img;
	      Image image = new Image(pieceImageSupplier.getResource(img));

	      image.addClickHandler(new ClickHandler() {
	    	  @Override
	          public void onClick(ClickEvent event) {
	    		  if (enableClicks) {													// Not sure this enableClicks
	    			  presenter.pieceSelectedToDeploy(imgFinal.piece);
	    		  }
	          }
	      });

	      res.add(image);
	    }
	    return res;
	}

	/*
	 * Place piece images of current player which are not deployed on deploy panel
	 */
	private void placeImagesOnDeployPanel(HorizontalPanel panel, List<Image> images) {
		panel.clear();
		Image last = images.isEmpty() ? null : images.get(images.size() - 1);
		for (Image image : images) {
			FlowPanel imageContainer = new FlowPanel();
			imageContainer.setStyleName("imgContainer");
			imageContainer.add(image);
			panel.add(imageContainer);
		}
	}

	private void disableClicks() {
		deployBtn.setEnabled(false);
		enableClicks = false;
	}
	
	/*
	 * Deploy Finish Button click handler
	 */
	@UiHandler("deployBtn")
	void onClickDeployBtn(ClickEvent e) {
		disableClicks();
		presenter.deployFinished();
	}

	@Override
	public void setPresenter(GhostsPresenter ghostsPresenter) {
		this.presenter = ghostsPresenter;
	}

	@Override
	public void setViewerState(Map<Position, String> squares) {
		placeImagesOnGrid(gameGrid, createAllBackPieces(squares));
		placeImagesOnDeployPanel(deploySelectArea, ImmutableList.<Image> of()); // For viewer, we don't care about deploy panel
		disableClicks();
	}

	@Override
	public void setPlayerState(List<Piece> pieces, Map<Position, String> squares, Color myColor, List<Boolean> pieceDeployed) {
//		System.out.println("setPlayerState called!");
		placeImagesOnGrid(gameGrid, createNormalPieces(pieces, squares, myColor, false));
		placeImagesOnDeployPanel(deploySelectArea, createRemainingPiecesToDeploy(pieces, pieceDeployed, myColor));
		disableClicks();
	}

	@Override
	public void chooseNextPieceToMove(List<Piece> pieces, Map<Position, String> squares, Color turn) {		
		enableClicks = true;																// here we need to add clickhandler to every current player's piece image
		placeImagesOnGrid(gameGrid, createNormalPieces(pieces, squares, turn, true));		// this turn is myColor
		
		
	}

	@Override
	public void chooseSquareToMove(List<Position> possiblePositions) {		
		final List<Position> pos = possiblePositions;
		gameGrid.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
		      	  Cell cell = ((HTMLTable)event.getSource()).getCellForEvent(event);			
		      	  Position p = new Position(cell.getRowIndex(), cell.getCellIndex());
		      	  if (pos.contains(p))
		      		  presenter.squareSelectedToMove(p);
			}
		});

	}

	@Override
	public void chooseNextPieceToDeploy(List<Piece> pieces, Map<Position, Piece> deployTable, Color turn, List<Boolean> pieceDeployed) {
		enableClicks = true;
		placeImagesOnGrid(gameGrid, createDeployedPieces(deployTable, turn, false));   
		placeImagesOnDeployPanel(deploySelectArea, createRemainingPiecesToDeploy(pieces, pieceDeployed, turn));
		deployBtn.setEnabled(playerAllDeployed(turn, pieceDeployed));
		
	}

	@Override
	public void chooseSquareToDeploy(List<Position> possiblePositions) {
		final List<Position> pos = possiblePositions;
    	  
		gameGrid.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
		      	  Cell cell = ((HTMLTable)event.getSource()).getCellForEvent(event);		      	  
		      	  Position p = new Position(cell.getRowIndex(), cell.getCellIndex());
		      	  if (pos.contains(p))
		      		  presenter.squareSelectedToDeploy(p);
			}
		});
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
	
	/*
	 * check if all player deploy finished
	 */
	boolean playerAllDeployed(Color turn, List<Boolean> pieceDeployed) {
		if (turn.isWhite()) {
			for (int i = 0; i < 8; i++) {
				if (!pieceDeployed.get(i)) {
					return false;
				}
			}
		} else {
			for (int i = 8; i < 16; i++) {
				if (!pieceDeployed.get(i)) {
					return false;
				}
			}
		}
		return true;
	}

}
