// Copyright 2012 Google Inc.
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
// http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.
// //////////////////////////////////////////////////////////////////////////////

package org.ghosts.ai;

import java.util.Collections;
import java.util.List;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Random;

import com.google.common.base.Optional;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import org.ghosts.client.Color;
import org.ghosts.client.GhostsState;
import org.ghosts.client.Move;
import org.ghosts.client.Piece;
import org.ghosts.client.Position;

/**
 * http://en.wikipedia.org/wiki/Alpha-beta_pruning<br>
 * This algorithm performs both A* and alpha-beta pruning.<br>
 * The set of possible moves is maintained ordered by the current heuristic value of each move. We
 * first use depth=1, and update the heuristic value of each move, then use depth=2, and so on until
 * we get a timeout or reach maximum depth. <br>
 * If a state has {@link TurnBasedState#whoseTurn} null (which happens in backgammon when we should
 * roll the dice), then I treat all the possible moves with equal probabilities. <br>
 * 
 * @author yzibin@google.com (Yoav Zibin)
 * 
 * we assume AI as black player
 */
public class AlphaBetaPruning {
//	private GhostsState state;
	private Heuristic heuristic;
	private GhostsState AIGuessState;	// AI random guess a complete state of two players by existing pieces for following inference

  static class TimeoutException extends RuntimeException {

    private static final long serialVersionUID = 1L;
  }

  static class MoveScore<Move> implements Comparable<MoveScore<Move>> {
    Move move;
    int score; 

    @Override
    public int compareTo(MoveScore<Move> o) {
      return o.score - score; 								// sort DESC (best score first)
    }
  }

  public AlphaBetaPruning(Heuristic heuristic, GhostsState ghostsState) {
    this.heuristic = heuristic;

    // We have to avoid shallow copy, if just use new GhostsState(...), the original ghostsState will be moddified!
    Map<Position, String> newSquares = Maps.newHashMap();
	for (Position p : ghostsState.getSquares().keySet())
		newSquares.put(p, ghostsState.getSquares().get(p));
	
	
    this.AIGuessState = getAIGuessState(new GhostsState(ghostsState.getTurn(), 
			 								ImmutableList.copyOf(ghostsState.getPlayerIds()), 
			 								ImmutableList.copyOf(ghostsState.getPieces()), 
			 								newSquares, 
			 								ghostsState.isWhiteDeployed(), 
			 								ghostsState.isBlackDeployed()));

//    this.AIGuessState = getAIGuessState(ghostsState);
  }


  public Move findBestMove(int depth, Timer timer) {
	  
	boolean isBlack = AIGuessState.getTurn().isBlack();
    // For each move, there's a state for it
    List<GhostsState> states = new ArrayList<GhostsState>();
    
    // Do iterative deepening (A*), and slow get better heuristic values for the states.
    List<MoveScore<Move>> scores = Lists.newArrayList();
    Iterable<Move> possibleMoves = heuristic.getOrderedMoves(AIGuessState);
    for (Move move : possibleMoves) {
      MoveScore<Move> score = new MoveScore<Move>();
      score.move = move;
      score.score = Integer.MIN_VALUE;
      scores.add(score);
      states.add(new GhostsState(AIGuessState.getTurn(), 
					 ImmutableList.copyOf(AIGuessState.getPlayerIds()), 
					 ImmutableList.copyOf(AIGuessState.getPieces()), 
					 AIGuessState.getSquares(), 
					 AIGuessState.isWhiteDeployed(), 
					 AIGuessState.isBlackDeployed()));
					 
    }

    try {
      for (int i = 0; i < depth; i++) {
        for (int j = 0; j < scores.size(); j++) {
          GhostsState state = states.get(j);
          Move move = null;
          MoveScore<Move> moveScore = scores.get(j);
          move = moveScore.move;
          int score = findMoveScore(makeMove(state, move), i, Integer.MIN_VALUE, Integer.MAX_VALUE, timer);
          if (!isBlack) {
            // the scores are from the point of view of the black(AI), so for white
            // we need to switch. We consider Black as AI player.
            score = -score;
          }
          moveScore.score = score;
        }
        // This will give better pruning on the next iteration.
        Collections.sort(scores); 
      }
    } catch (TimeoutException e) {
      // OK, it should happen
    }

    Collections.sort(scores);
    return scores.get(0).move;		// choose the move with highest score(best move)
 
 
//    return new Move(new Position(1,1), new Position(2,1), Move.MoveType.MOVE);
  }


  /**
   * If we get a timeout, then the score is invalid.
   */
  private int findMoveScore(final GhostsState passState, int depth, int alpha, int beta, Timer timer)
      throws TimeoutException {
    
	if (timer.didTimeout()) {
		throw new TimeoutException();
	}
	
	GhostsState localstate = new GhostsState(passState.getTurn(), 
			 ImmutableList.copyOf(passState.getPlayerIds()), 
			 ImmutableList.copyOf(passState.getPieces()), 
			 passState.getSquares(), 
			 passState.isWhiteDeployed(), 
			 passState.isBlackDeployed());
	
    if (depth == 0 || heuristic.hasGameEnded(localstate)) {
      return heuristic.getStateValue(localstate);
    }
    
    Color color = localstate.getTurn();
    int scoreSum = 0;
    int count = 0;
    Iterable<Move> possibleMoves = heuristic.getOrderedMoves(localstate);
    for (Move move : possibleMoves) {
      count++;
      int childScore = findMoveScore(makeMove(localstate, move), depth - 1, alpha, beta, timer);
      if (color == null) {
        scoreSum += childScore;
      } else if (color.isBlack()) {
        alpha = Math.max(alpha, childScore);
        if (beta <= alpha) {
          break;
        }
      } else {
        beta = Math.min(beta, childScore);
        if (beta <= alpha) {
          break;
        }
      }
    }
    return color == null ? scoreSum / count : color.isBlack() ? alpha : beta;
  }
  
  /*
   * We randomly guess a complete state by existing pieces for initial inference
   * All pieces will be visible
   */
  private GhostsState getAIGuessState(final GhostsState gState) {
	  int count = 0;										// count how many existing pieces for white(player), currently not visible to AI(Black)
	  List<Optional<Piece>> pieces = gState.getPieces();
	  Map<Position, String> squares = gState.getSquares();
	  HashMap<Integer, Boolean> notVisiblePieceIndex = new HashMap<Integer, Boolean>();		// store piece index that is not visible to AI
	  for (int i = 0; i < 6; i++) {						// only need to count white player since black(AI) is visible to itself
		  for (int j = 0; j < 6; j++) {
			  String pieceStr = squares.get(new Position(i, j));
			  if (pieceStr != null) {
				  int index = heuristic.stateExplorer.getIndexFromPieceName(pieceStr);
				  if (index < 8 && !pieces.get(index).isPresent()) {	// if this piece is white piece and not visible(null)
					  count++;
					  notVisiblePieceIndex.put(index, true);
				  }
			  }
		  }
	  }
	  List<Optional<Piece>> newPieces = Lists.newArrayList();
	  for (int i = 0; i < 16; i++) {
		  Piece piece;
		  if (notVisiblePieceIndex.containsKey(i)) {		// white piece index
			  if (count % 2 == 0) {
				  piece = new Piece("WGood", "P" + i);
			  } else {
				  piece = new Piece("WEvil", "P" + i);
			  }
			  count--;
		  } else if (pieces.get(i).isPresent()) {			// black piece index
			  piece = pieces.get(i).get();
		  } else {											// deleted piece index
			  piece = null;
		  }
		  newPieces.add(Optional.fromNullable(piece));
	  }
	  
	  GhostsState newState = new GhostsState(gState.getTurn(), 
				 ImmutableList.copyOf(gState.getPlayerIds()), 
				 ImmutableList.copyOf(newPieces), 
				 gState.getSquares(), 
				 gState.isWhiteDeployed(), 
				 gState.isBlackDeployed());
	  return newState;
  }
  
  /*
   * Return a new state by applying move to last state
   */
  private GhostsState makeMove(final GhostsState state, Move move) {
	  List<Optional<Piece>> pieces = state.getPieces();
	  Map<Position, String> squares = state.getSquares();
	  
	  Position endPos = move.getDestination();
	  Position startPos = move.getStart();
	  String endPieceStr = squares.get(endPos);
	  String startPieceStr = squares.get(startPos);
	  List<Optional<Piece>> newPieces = Lists.newArrayList();
	  for (int i = 0; i < 16; i++) {
		  Piece piece;
		  if (endPieceStr != null) {
			  int index = heuristic.stateExplorer.getIndexFromPieceName(endPieceStr);
			  if (index == i) {
				  piece = null;
			  } else {
				  if (!pieces.get(i).isPresent()) {			// if this piece is already null(deleted)
					  piece = null;
				  } else {
					  piece = pieces.get(i).get();
				  }
			  }
		  } else {										// no capture, normal move	  
			  if (!pieces.get(i).isPresent()) {			// if this piece is already null(deleted)
				  piece = null;
			  } else {
				  piece = pieces.get(i).get();
			  }
		  }
			
		  newPieces.add(Optional.fromNullable(piece));
	  }
	  
	  // modify squares, Map will just replace value with the same input key
	  squares.put(startPos, null);
	  squares.put(endPos, startPieceStr);
	  
	  GhostsState newState = new GhostsState(state.getTurn().getOpposite(), 
				 ImmutableList.copyOf(state.getPlayerIds()), 
				 ImmutableList.copyOf(newPieces), 
				 squares, 
				 state.isWhiteDeployed(), 
				 state.isBlackDeployed());
	  return newState;
  }
}
