package com.example.MCTS;

import com.example.Color;
import com.example.GameState;
import com.example.Minimax;
import com.example.models.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

public class MCTSNode extends GameState {
    List<Move> untriedMoves;
    private List<MCTSNode> children;
    int wins;
    int visits;
    final MCTSNode parent;
    final Move moveThatHappenedBefore;

    public List<MCTSNode> getChildren() {
        return children;
    }

    public MCTSNode(Board board, Player currentPlayer, MCTSNode parent, Move move) {
        super(board, currentPlayer);
        this.untriedMoves = Minimax.getPossibleMoves(board, currentPlayer);
        this.children = new ArrayList<>();
        this. wins =0;
        this. visits = 0;
        this.parent = parent;
        this.moveThatHappenedBefore = move;
    }

    public boolean isTerminal () {
        return getBoard().getWinner() != null;
    }

    public boolean isFullyExpanded() {
        return untriedMoves.size() == 0;
    }

    public Player getNextPlayer() {
        return getCurrentPlayer().getType() == PieceType.WHITE ?
                getBoard().getBlack() :
                getBoard().getWhite();
    }

    public MCTSNode selectChild(Boolean opponent) {
        MCTSNode bestChild = null;
        double bestValuePlayer = -Double.MAX_VALUE;
        double bestValueOpponent = Double.MAX_VALUE;

        if(children.size() == 0) {
            //пропуск хода
            Player currentPlayer = getCurrentPlayer().getType() == PieceType.WHITE ?
                    getBoard().getBlack() : getBoard().getWhite();
            Board newBoard = new Board(getBoard());
            int cellRow = newBoard.getNextToMovePiece().getRow();
            int cellCol = newBoard.getNextToMovePiece().getCol();
            Color nextRequiredColor = newBoard.getGrid()[cellRow][cellCol].getCellColor();
            Piece nextCurrPiece = newBoard.getPieceByPlayerAndColor(currentPlayer, nextRequiredColor);
            newBoard.setNextToMovePiece(nextCurrPiece);

            bestChild = new MCTSNode(newBoard, currentPlayer, this, null);
            children.add(bestChild);

            if(Minimax.getPossibleMoves(bestChild.getBoard(), currentPlayer).isEmpty()) {
                   return null;
            }
        }
        else {

            for (MCTSNode child : children) {
                //выбор по правилу UCT
                double uctValue = child.wins / (double) child.visits +
                        Math.sqrt(2) * Math.sqrt(Math.log(this.visits) / child.visits);

                if(opponent) {
                    if (uctValue < bestValueOpponent) {
                        bestValueOpponent = uctValue;
                        bestChild = child;
                    }
                } else {
                    if (uctValue > bestValuePlayer) {
                        bestValuePlayer = uctValue;
                        bestChild = child;
                    }
                }
            }
        }
        return bestChild;
    }

    public void update(int result) {
        visits +=1;
        wins += result;
    }

    public MCTSNode expand() {
        //если есть победный ход, то ходим только в него
        List<Move> winningMoves = untriedMoves.stream().filter(x -> x.getToCell().getRow() == 0 || x.getToCell().getRow() == 7).collect(Collectors.toList());
        Move nextMove;
        if (!winningMoves.isEmpty()) {
            nextMove = winningMoves.get(0);
            untriedMoves.clear();
        } else {
            Random random = new Random();
            int index = random.nextInt(untriedMoves.size());
            nextMove = untriedMoves.remove(index);
        }

        Board newBoard = new Board(getBoard());
        makeFullMove(newBoard, nextMove, getCurrentPlayer());
        Player player = getNextPlayer();

        MCTSNode child = new MCTSNode(newBoard, player, this, nextMove);
        children.add(child);
        return child;
    }

    public static void makeFullMove(Board board, Move move, Player playerToMove) {
        Player nextPlayer = changePlayer(playerToMove, board);
        board.makeMove(move.getFromCell().getRow(), move.getFromCell().getCol(),
                move.getToCell().getRow(), move.getToCell().getCol());

        Cell targetCell = board.getGrid()[move.getToCell().getRow()][move.getToCell().getCol()];
        Color nextRequiredColor = targetCell.getCellColor();

        Piece nextCurrPiece = board.getPieceByPlayerAndColor(nextPlayer, nextRequiredColor);

        board.setNextToMovePiece(nextCurrPiece);
    }

    public static Player changePlayer(Player player, Board board) {
        return player.getType() == PieceType.WHITE ?
                board.getBlack() :
                board.getWhite();
    }
}
