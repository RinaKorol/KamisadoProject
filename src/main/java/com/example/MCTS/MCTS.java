package com.example.MCTS;

import com.example.Color;
import com.example.Minimax;
import com.example.models.*;

import java.util.Comparator;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

public class MCTS {
    private static int ITERATIONS = 10000;
    static Player player;


    public static Move findBestMove(Board board, Player currPlayer, int iters) {
        ITERATIONS = iters;

        player = currPlayer;
        Board rootBoard = new Board(board);
        MCTSNode root = new MCTSNode(rootBoard, currPlayer,null, null);
        search(root, true);
        // Выбор лучшего хода из имеющихся
        MCTSNode bestChild = null;
        int maxVisits = -1;
        for (MCTSNode child : root.getChildren()) {
            if (child.visits > maxVisits) {
                maxVisits = child.visits;
                bestChild = child;
            }
        }
        Move happenedMove = bestChild.moveThatHappenedBefore;
        Move bestMove = null;
        if(happenedMove!=null) {
            int startRow = happenedMove.getFromCell().getRow();
            int startCol = happenedMove.getFromCell().getCol();
            int endRow = happenedMove.getToCell().getRow();
            int endCol = happenedMove.getToCell().getCol();

            Cell startCell = board.getCell(startRow,startCol);
            Cell endCell = board.getCell(endRow, endCol);

            Piece movedPiece = startCell.getPiece();
            bestMove = new Move(movedPiece,startCell, endCell,currPlayer);
        }
        return bestMove;
    }

    public static void search(MCTSNode root, boolean fullRandom) {
        for (int i = 0; i < ITERATIONS; i++) {
            MCTSNode node = root;
            // Выбор - Selection
            while (node != null && !node.isTerminal() && node.isFullyExpanded()) {//
                Boolean isOpponent;
                    isOpponent =node.getCurrentPlayer().getType() == player.getType();
                node = node.selectChild(!isOpponent);
            }

            // Расширение - Expansion
            if (node != null && !node.isTerminal() && ! node.isFullyExpanded()) {
                node = node.expand();
                if(node!=null) {
                }
            }

            int result;
            // Симуляция - Simulation
            if (node == null) {
                result = -1;
            } else {
                result = simulate(node, fullRandom);
            }
            // Обратное распространение - Backpropagation
            while (node != null) {
                node.update(result);
                node = node.parent;
            }
        }
    }

    private static int simulate(MCTSNode node, boolean fullRandom) {
        Board newBoard = new Board(node.getBoard());
        Player currentPlayer = node.getCurrentPlayer();
        Player winner = newBoard.getWinner();

        int flag = 0;
        Player blameOn = null;
        while (winner == null) {
            List<Move> moves = Minimax.getPossibleMoves(newBoard, currentPlayer);

            if (moves.isEmpty()) {
                //пропуск хода
                winner = newBoard.getWinner();
                currentPlayer = currentPlayer.getType() == PieceType.WHITE ? node.getBoard().getBlack()
                        : node.getBoard().getWhite();
                if(flag == 0) {
                    blameOn = currentPlayer;
                }
                flag++;

                int cellRow = newBoard.getNextToMovePiece().getRow();
                int cellCol = newBoard.getNextToMovePiece().getCol();
                Color nextRequiredColor = newBoard.getGrid()[cellRow][cellCol].getCellColor();
                Piece nextCurrPiece = newBoard.getPieceByPlayerAndColor(currentPlayer, nextRequiredColor);
                newBoard.setNextToMovePiece(nextCurrPiece);
            } else {
                flag = 0;
                blameOn = null;
                Random random = new Random();
                int index = 0;
                if(fullRandom) {
                    index = random.nextInt(moves.size());
                } else {
                    //выбор из наиболее дальних ходов
                    moves = moves.stream().sorted(Comparator.comparingInt((Move x) ->
                            Math.abs(x.getToCell().getRow() - x.getFromCell().getRow()))
                                            .reversed()
                            )
                            .collect(Collectors.toList());
                    int bound = moves.size()/2;
                    if(bound != 0) {
                        index = random.nextInt(bound);
                    }

                }
                Move move = moves.get(index);
                //если есть выигрышные ход - то ходим именно туда
                List<Move> winningMoves = moves.stream().filter(x -> x.getToCell().getRow() == 0 || x.getToCell().getRow() == 7).collect(Collectors.toList());
                if (!winningMoves.isEmpty()) {
                    move = winningMoves.get(0);
                }
                //делаем ход
                MCTSNode.makeFullMove(newBoard, move, currentPlayer);
                currentPlayer = currentPlayer.getType() == PieceType.WHITE ? node.getBoard().getBlack()
                        : node.getBoard().getWhite();
                winner = newBoard.getWinner();
            }
            if(flag == 3) {
                winner = blameOn.getType() == PieceType.WHITE ? node.getBoard().getBlack()
                        : node.getBoard().getWhite();
            }
        }
        //возвращаем значение в зависимости от победителя
        int playerResult = player.getType() == PieceType.WHITE ? -1 : 1;
        if(winner.getType() == PieceType.BLACK){
            return 1 * playerResult;
        } else {
            return -1 * playerResult;
        }

    }
}
