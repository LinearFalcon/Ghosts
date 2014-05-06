package org.ghosts.client;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.game_api.GameApi.*;
import org.ghosts.ai.AlphaBetaPruning;
import org.ghosts.ai.DateTimer;
import org.ghosts.ai.Heuristic;

import com.google.common.base.Optional;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.gwt.user.client.Window;

public class GhostsPresenter {

	public interface View {
		/**
		 * Sets the presenter. The viewer will call certain methods on the
		 * presenter.
		 * 
		 * The process of making a move looks as follows to the viewer: 
		 * 1) The viewer calls {@link #pieceSelectedToMove} to select one piece to move 
		 * 2) The viewer calls {@link #squareSelectedToMove} to pass the selected square to move, which sends the move.
		 * 
		 * The process of making a move looks as follows to the presenter: 
		 * 1) The presenter calls {@link #chooseNextPieceToMove} and passes the piece.
		 * 2) The presenter  calls {@link #chooseNextSquareToMove} and passes the position.
		 * 
		 * The process of deploying a piece looks as follows to the viewer: 
		 * 1) The viewer calls {@link #pieceSelectedToDeploy} to select one piece to move 
		 * 2) The viewer calls {@link #squareSelectedToDeploy} to pass the selected square to move, which sends the move.
		 * 3) The viewer calls {@link #deployFinished} to send make move
		 * 
		 * The process of deploying a piece looks as follows to the presenter: 
		 * 1) The presenter calls {@link #chooseNextPieceToDeploy} and passes the piece.
		 * 2) The presenter  calls {@link #chooseNextSquareToDeploy} and passes the position.
		 */
		void setPresenter(GhostsPresenter ghostsPresenter);

		/** Sets the state for a viewer, i.e., not one of the players. */
		void setViewerState(Map<Position, String> squares);																		

		/**
		 * Sets the state for a player (whether the player has the turn or not).
		 */
		void setPlayerState(List<Piece> pieces, Map<Position, String> squares, Color myColor, List<Boolean> pieceDeployed);

		/**
		 * Asks the player to choose the next piece to move. We pass what piece
		 * are selected, and what pieces will remain in the player hands. The
		 * user can select a piece (by calling {@link #pieceSelectedToMove) If the
		 * user selects a piece which is already in selectedPieces, then it
		 * moves that piece to remainingPieces. If the user selects a piece from
		 * remainingPieces, then selectedPiece clear and add this new piece.
		 * selectedPieces can only have no more than one piece
		 */
		void chooseNextPieceToMove(List<Piece> pieces, Map<Position, String> squares, Color turn);
//				List<Piece> remainingPiece);

		/**
		 * After the player finished selecting a piece, the player needs to
		 * choose the square to move.
		 */
		void chooseSquareToMove(List<Position> possiblePositions);
		
		/**
		 * Asks the player to choose a piece to deploy
		 */
		void chooseNextPieceToDeploy(List<Piece> pieces, Map<Position, Piece> deployTable, Color turn, List<Boolean> pieceDeployed);
		
		/**
		 * After the player finished deploy(by calling {@link #pieceSelectedToDeploy})
		 */
		void chooseSquareToDeploy(List<Position> possiblePositions);
		
		
		void animateMove(List<Piece> pieces, Map<Position, String> squares, Position startPos, Position endPos);

		/*
		 * update animate arguments in graphics so that we can 
		 */
		void setAnimateArgs(List<Piece> piecesList, Map<Position, String> squares, Position startPos, Position endPosition, boolean isDnd);
	}

	private final GhostsLogic ghostsLogic = new GhostsLogic();
	private final View view;
	private final Container container;
	/** A viewer doesn't have a color. */
	private Optional<Color> myColor;
	private boolean hasAiMakeMove = true;
	private GhostsState ghostsState;
	private List<Piece> selectedPieceToMove;
	private List<Piece> selectedPieceToDeploy;
	private final List<Boolean> pieceDeployed = Lists.newArrayList();
	private List<Operation> deployOperations = Lists.newArrayList();			// Store deploy operations "Set(Sxx, Pxx)"
	private final List<Position> wPossiblePositions = Lists.newArrayList();
	private final List<Position> bPossiblePositions = Lists.newArrayList();
	private final Map<Position, Piece> deployTable = new HashMap<Position, Piece>();		// final means when white finish deploying, deploytable will still keep his deploy info

	
	public GhostsPresenter(View view, Container container) {
		this.view = view;
		this.container = container;
		view.setPresenter(this);
		
		for (int i = 0; i < 16; i++) {								// Initialize pieceDeployed
			pieceDeployed.add(false);
		}
	    for (int i = 0; i < 2; i++) {								// Initialize possiblePositions for deploy phase
			for (int j = 1; j < 5; j++) {
				bPossiblePositions.add(new Position(i,j));
			}
	    }
	    for (int i = 4; i < 6; i++) {
			for (int j = 1; j < 5; j++) {
				wPossiblePositions.add(new Position(i,j));
			}
	    }
	}

	/** Updates the presenter and the view with the state in updateUI. */
	public void updateUI(UpdateUI updateUI) {
		List<String> playerIds = updateUI.getPlayerIds();
		String yourPlayerId = updateUI.getYourPlayerId();
	    int yourPlayerIndex = updateUI.getPlayerIndex(yourPlayerId);
	    myColor = yourPlayerIndex == 0 ? Optional.of(Color.W)
	        : yourPlayerIndex == 1 ? Optional.of(Color.B) : Optional.<Color>absent();
	    selectedPieceToMove = Lists.newArrayList();
	    selectedPieceToDeploy = Lists.newArrayList();  
	    
	    if (updateUI.getState().isEmpty()) {						// Game board intialization
	        
	        if (myColor.isPresent() && myColor.get().isWhite()) {
	        	sendBoardInitialMove(playerIds);
	        }
	        return;
	    }
	    Color turnOfColor = null;
	    for (Operation operation : updateUI.getLastMove()) {
	    	if (operation instanceof SetTurn) {
	    		turnOfColor = Color.values()[playerIds.indexOf(((SetTurn) operation).getPlayerId())];
	      }
	    }	
	    
	    ghostsState = ghostsLogic.gameApiStateToGhostsState(updateUI.getState(), turnOfColor, playerIds);
    
	    if (!ghostsState.isWhiteDeployed()) {						// The W player initialize board and deploy
	    	if (myColor.isPresent() && myColor.get().isWhite()) {
	        	chooseNextPieceToDeploy();
	        }
	        return;
	        
	    } else if (!ghostsState.isBlackDeployed()) {				// The B player start to deploy         If updateUI.isAiPlayer(), random deploy!!!!****************(unfinished)
	    	if (myColor.isPresent() && myColor.get().isBlack()) {
	        	chooseNextPieceToDeploy();
	        }
	        return;
	    }
	    
	    
	    if (updateUI.isViewer()) {
	    	view.setViewerState(ghostsState.getSquares());
	        return;
	    }
	    if (updateUI.isAiPlayer()) {							// temporarilly don't check if turn is AI(black), since view is changed automatically by emulator, so turn will be black
	        if (!hasAiMakeMove) {
	        	hasAiMakeMove = true;
	        	Heuristic heuristic = new Heuristic();	    
	        	
	        	GhostsState passState = new GhostsState(ghostsState.getTurn(), 
	       			 ImmutableList.copyOf(ghostsState.getPlayerIds()), 
	       			 ImmutableList.copyOf(ghostsState.getPieces()), 
	       			 ghostsState.getSquares(), 
	       			 ghostsState.isWhiteDeployed(), 
	       			 ghostsState.isBlackDeployed());
				
		        AlphaBetaPruning ai = new AlphaBetaPruning(heuristic, passState);
		        
		        // The move of the AI takes at most 0.5 second
		        DateTimer timer = new DateTimer(500);
		        
		        for (int i = 0; i < 6; i++)
		        	for(int j = 0; j < 6; j++)
		        		System.out.println(("S"+ i) + j + ": " + ghostsState.getSquares().get(new Position(i, j)));
		        System.out.println("above before findBestMove");
		       
		        // The depth is 4 though due to the time limit, it may not reach that deep
		        Move move = ai.findBestMove(4, timer);
		        
		        for (int i = 0; i < 6; i++)
		        	for(int j = 0; j < 6; j++)
		        		System.out.println(("S"+ i) + j + ": " + ghostsState.getSquares().get(new Position(i, j)));
		        
		        String startSquare = move.getStart().toSquareString();
		        String endSquare = move.getDestination().toSquareString();
		        String movingPiece = ghostsState.getSquares().get(move.getStart());							// movingPiece is null!!!!!!!!!!!!**************
		        
		        
		        System.out.println("startSquare: " + startSquare + " endSquare: " + endSquare + " movingPiece: " + movingPiece);
		        
		        container.sendMakeMove(ghostsLogic.getMove(movingPiece, startSquare, 
		        		endSquare, ghostsState));
		        return;
	        }
	    }
	 
	    // Now must be a player not viewer	    
	    view.setPlayerState(getPiecesList(), ghostsState.getSquares(), myColor.get(), pieceDeployed);
	    
	    // Check if game is already end by looking at lastMove                  						may put before AI player !!!!!!!!!!!!!!!!!!
	    List<Operation> lastmove = updateUI.getLastMove();
	    if (lastmove.get(lastmove.size() - 1) instanceof EndGame) {
//	    	Window.alert("Game already end!");
	    	return;
	    }
	    
	    if (isMyTurn()) {
	    	hasAiMakeMove = false;
	    	chooseNextPieceToMove();
	    }
	    
	    
	}

	private void chooseNextPieceToDeploy() {
		view.chooseNextPieceToDeploy(getPiecesList(), deployTable, ghostsState.getTurn(), pieceDeployed);
	}
	
	private void chooseNextPieceToMove() {

		view.chooseNextPieceToMove(getPiecesList(), ghostsState.getSquares(), myColor.get());	
	}

	/**
	 * Adds/remove the piece from the {@link #selectedPieceToMove}, only one piece can be selected. The view can only
	 * call this method if the presenter called {@link View#chooseNextPiece}.
	 */
	public void pieceSelectedToMove(Piece piece) {		
		check(isMyTurn());
		if (myColor.get().isWhite()) {								
			check(piece.isWhitePiece());
		} else {
			check(piece.isBlackPiece());
		}
		if (selectedPieceToMove.contains(piece)) {														// doesn't function: click one piece and click another piece
			selectedPieceToMove.remove(piece);
		} else if (!selectedPieceToMove.contains(piece) && selectedPieceToMove.size() < 1) {
			selectedPieceToMove.add(piece);
		}
		check(!selectedPieceToMove.isEmpty());		// If already choose a piece, then can choose where to move
		view.chooseSquareToMove(getPossiblePositionsToMove());						
	}

	/**
	 * Selects a destination square and sends a move. The view can only call this method if
	 * the presenter called {@link View#chooseSquareToMove}.
	 */
	public void squareSelectedToMove(Position endPosition, boolean isDnd) {
		check(isMyTurn() && !selectedPieceToMove.isEmpty()												// Sometimes will throw unhandled exception!!!
				&& getPossiblePositionsToMove().contains(endPosition));
		Piece p = selectedPieceToMove.get(0);
		String movingPiece, startSquare;
		movingPiece = p.getPieceName();
		startSquare = getSquarePositionFromPieceName(movingPiece).toSquareString();
		Position startPos = getSquarePositionFromPieceName(movingPiece);

		view.setAnimateArgs(getPiecesList(), ghostsState.getSquares(), startPos, endPosition, isDnd);
		container.sendMakeMove(ghostsLogic.getMove(movingPiece, startSquare, 
				endPosition.toSquareString(), ghostsState));
	}
	
	
	public void pieceSelectedToDeploy(Piece piece) {
		check(isMyTurn());
		if (myColor.get().isWhite()) {								// Maybe we need to check if choose a piece of right color
			check(piece.isWhitePiece());
		} else {
			check(piece.isBlackPiece());
		}
		
//		if (selectedPieceToDeploy.contains(piece)) {
//			selectedPieceToDeploy.remove(piece);
//		} else if (!selectedPieceToDeploy.contains(piece) && selectedPieceToDeploy.size() < 1) {
			selectedPieceToDeploy.add(piece);
//		}
		check(!selectedPieceToDeploy.isEmpty());		// If already choose a piece, then can choose where to deploy

		
		if (myColor.get().isWhite()) {
			view.chooseSquareToDeploy(wPossiblePositions);
		}
		else {
			view.chooseSquareToDeploy(bPossiblePositions);
		}
	}
	
	public void squareSelectedToDeploy(Position deployPosition) {
		check(isMyTurn() && !selectedPieceToDeploy.isEmpty());
		if (myColor.get().isWhite())
			check(wPossiblePositions.contains(deployPosition));
		else
			check(bPossiblePositions.contains(deployPosition));
		
		Piece p = selectedPieceToDeploy.get(0);
		deployOperations.add(new Set(deployPosition.toSquareString(), p.getPieceName()));
		pieceDeployed.set(ghostsLogic.getIndexFromPieceName(p.getPieceName()), true);
		selectedPieceToDeploy.clear();												//	during deploy, no move sent to server, so must clear
		if (myColor.get().isWhite()) {
			wPossiblePositions.remove(deployPosition);
		}
		else {
			bPossiblePositions.remove(deployPosition);
		}
		
		deployTable.put(deployPosition, p);
		
//		view.animateMove(getPiecesList(), ghostsState.getSquares(), startPos, deployPosition);	
		
		chooseNextPieceToDeploy();
	}
	
	public void deployFinished() {
		check(isMyTurn());
		List<Operation> operations = Lists.newArrayList();
		if (myColor.get().isWhite()) {
			for (int i = 0; i < 8; i++) {
				check(pieceDeployed.get(i));		//check if all pieces are deployed
			}
			operations.add(new SetTurn(ghostsState.getPlayerId(myColor.get().getOpposite())));			
		} else {
			for (int i = 8; i < 16; i++) {
				check(pieceDeployed.get(i));		//check if all pieces are deployed
			}
			operations.add(new SetTurn(ghostsState.getPlayerId(myColor.get().getOpposite())));					
		}
		
		check(!deployOperations.isEmpty());
		for (int i = 0; i < deployOperations.size(); i++) {
			operations.add(deployOperations.get(i));															
		}
		
		if (myColor.get().isWhite())
			operations.add(new Set("WDeployed", "true"));
		else
			operations.add(new Set("BDeployed", "true"));
		container.sendMakeMove(operations);
		deployOperations.clear();
	}
	
	
	/**
	 * Return possible position to move, each position is
	 * 1) inside game board 
	 * 2) not occupied by piece in same color
	 * Position scan order: down, up, left, right
	 */
	private List<Position> getPossiblePositionsToMove() {		
		Piece p = selectedPieceToMove.get(0);
		String movingPiece;
		movingPiece = p.getPieceName();										
		Position origin = getSquarePositionFromPieceName(movingPiece);
		int row = origin.getRow();
		int col = origin.getCol();
		List<Position> possiblePositions = Lists.newArrayList();
		List<Position> list = Lists.newArrayList();
		list.add(new Position(row - 1, col));
		list.add(new Position(row + 1, col));
		list.add(new Position(row, col - 1));
		list.add(new Position(row, col + 1));
		for (Position pos : list) {
			if (isInsideBoard(pos) && !ghostsLogic.sameSideCapture(pos.toSquareString(), ghostsState)) {
				possiblePositions.add(pos);
			}
		}
		return possiblePositions;
	}
	
	/**
	 * Return possible position to deploy, each position is
	 * 1) on the right side
	 * 2) not already deployed by a piece
	 */
/*	private List<Position> getPossiblePositionsToDeploy() {
		List<Position> possiblePositions = Lists.newArrayList();
		Map<Position, String> squares = ghostsState.getSquares();
		if (myColor.get().isBlack()) {					// check black side
			for (int i = 0; i < 2; i++) {
				for (int j = 1; j < 5; j++) {
					Position pos = new Position(i, j);
					if (squares.get(pos) == null) {		// null means not deployed by others
						possiblePositions.add(pos);
					}
				}
			}
		} else {										// check white side
			for (int i = 4; i < 6; i++) {
				for (int j = 1; j < 5; j++) {
					Position pos = new Position(i, j);
					if (squares.get(pos) == null) {		// null means not deployed by others
						possiblePositions.add(pos);
					}
				}
			}
		}
		return possiblePositions;
	}
*/	
	private boolean isInsideBoard(Position p) {
		int row = p.getRow();
		int col = p.getCol();
		return row >= 0 && row <= 5 && col >= 0 && col <= 5;
	}
	
	private boolean isMyTurn() {
	    return myColor.isPresent() && myColor.get() == ghostsState.getTurn();
	}

	private Position getSquarePositionFromPieceName(String piece) {			// Must can return the square string from name of selected piece
		Map<Position, String> squares = ghostsState.getSquares();
		for (Position p : squares.keySet()) {
			if (squares.get(p) != null) {
				if (squares.get(p).compareTo(piece) == 0)
					return p;
			}
		}
		return null;
	}
	
	/*
	 * Return a List<Piece> form of piecelist, if not visible then it's null
	 */
	public List<Piece> getPiecesList() {
		List<Piece> myPieces = Lists.newArrayList();
		ImmutableList<Optional<Piece>> pieces = ghostsState.getPieces();
		for (int i = 0; i < 16; i++) {
			if (pieces.get(i).isPresent()) {
				myPieces.add(pieces.get(i).get());
			} else {
				myPieces.add(null);
			}
		}
		return myPieces;
	}
	
	private void check(boolean val) {
		if (!val) {
			throw new IllegalArgumentException();
		}
	}
	
	public GhostsState getState() {
	    return this.ghostsState;
	}
	
	/*
	 * check if both player finish deployment
	 */
	public boolean isAllDeployed() {
		return deployTable.size() == 16;
	}
	
	public Color getMyColor() {
		if (myColor.isPresent())
			return myColor.get();
		else
			return null;
	}
	
	private void sendBoardInitialMove(List<String> playerIds) {
		container.sendMakeMove(ghostsLogic.getBoardInitialOperations(playerIds));
	}

	public void reupdateUI() {
		view.setPlayerState(getPiecesList(), ghostsState.getSquares(), myColor.get(), pieceDeployed);
	    
	    if (isMyTurn()) {
	    	chooseNextPieceToMove();
	    }
	}
	
}
