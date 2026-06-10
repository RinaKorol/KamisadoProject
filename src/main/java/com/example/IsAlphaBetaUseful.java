package com.example;

import com.example.MCTS.MCTS;
import com.example.models.*;

import java.util.*;

public class IsAlphaBetaUseful {
    public static void main(String[] args) {
        long startTime = System.currentTimeMillis();
        runTournament(5);
        System.out.println(System.currentTimeMillis() - startTime);
    }

    private static void runTournament(int number) {
        int iterations = 6000;
        int n = number;
        int i =0;
        int weight1Counter = 0;
        int weight2Counter = 0;

        while (i<n) {
            Board board = KamisadoView.initializeBoard();
            Random random = new Random();
            int randomNum = random.nextInt(8);

            Player whitePlayer = board.getWhite();
            Player blackPlayer = board.getBlack();
            Player currentPlayer = whitePlayer;

            boolean whiteUseAlpha = true;
            boolean blackUseAlpha = true;

            double[] whiteWeight = new double[] {0.8, 3, 0.6};
            double[] blackWeight = new double[] {0.8, 3, 0.6};

            Piece startingPiece = board.getGrid()[currentPlayer.getStartRow()][randomNum].getPiece();
            if(startingPiece!=null){
                board.setNextToMovePiece(startingPiece);
            }

            List<Move> log = new ArrayList<>();
            int steps = 0;

            Player winner = board.getWinner();
            while (winner == null) {
                //идет игра
                GameState state = new GameState(board, currentPlayer);
                double[] w = currentPlayer.getType() == PieceType.WHITE ? whiteWeight : blackWeight;
                boolean use = currentPlayer.getType() == PieceType.WHITE ? whiteUseAlpha : blackUseAlpha;
                //Move bestMove = currentPlayer.findBestMove(state, w, true, false, new double[]{1, 2, 1});
                Move bestMove = MCTS.findBestMove(board, currentPlayer, iterations);

                if(bestMove!= null) {
                    board.makeMove(bestMove);
                    log.add(bestMove);
                    winner = board.getWinner();
                    currentPlayer = currentPlayer.getType() == PieceType.WHITE ? blackPlayer : whitePlayer;
                    Cell targetCell = bestMove.getToCell();
                    Color nextRequiredColor = targetCell.getCellColor();
                    Piece nextCurrPiece = state.getBoard().getPieceByPlayerAndColor(currentPlayer, nextRequiredColor);
                    board.setNextToMovePiece(nextCurrPiece);
                } else {
                    //пропуск хода
                    winner = board.getWinner();
                    currentPlayer = currentPlayer.getType() == PieceType.WHITE ? blackPlayer : whitePlayer;
                    log.add(null);

                    int cellRow = board.getNextToMovePiece().getRow();
                    int cellCol = board.getNextToMovePiece().getCol();
                    Color nextRequiredColor = board.getGrid()[cellRow][cellCol].getCellColor();
                    Piece nextCurrPiece = board.getPieceByPlayerAndColor(currentPlayer, nextRequiredColor);
                    board.setNextToMovePiece(nextCurrPiece);
                }
                if(steps > 1000) {
                    System.out.println("Stopped because of long match");
                    break;
                }
                steps++;
            }
            if(winner != null) {
                if (winner.getType() == PieceType.WHITE) {
                    weight1Counter++;
                } else {
                    weight2Counter++;
                }
            }
            i++;
            //игра начинается заново
        }

        //перевернуть веса для черного и белого и сделать цикл снова
        i=0;
        boolean whiteUseAlpha = true;
        boolean blackUseAlpha = true;
        List<Move>log2 = new ArrayList<>();
        while (i<n) {
            Board board = KamisadoView.initializeBoard();
            Random random = new Random();
            int randomNum = random.nextInt(8);

            Player whitePlayer = board.getWhite();
            Player blackPlayer = board.getBlack();
            Player currentPlayer = whitePlayer;
            double[] whiteWeight = new double[] {0.8, 3, 0.6};
            double[] blackWeight = new double[] {0.8, 3, 0.6};

            Piece startingPiece = board.getGrid()[currentPlayer.getStartRow()][randomNum].getPiece();
            if(startingPiece!=null){
                board.setNextToMovePiece(startingPiece);
            }

            int steps = 0;
            Player winner = board.getWinner();
            while (winner == null) {
                //идет игра
                GameState state = new GameState(board, currentPlayer);
                double[] w = currentPlayer.getType() == PieceType.WHITE ? whiteWeight : blackWeight;
                boolean use = currentPlayer.getType() == PieceType.WHITE ? whiteUseAlpha : blackUseAlpha;
                //Move bestMove = currentPlayer.findBestMove(state, w, true, false, new double[]{1, 2, 1});
                Move bestMove = MCTS.findBestMove(board, currentPlayer, iterations);
                if(bestMove!= null) {
                    board.makeMove(bestMove);
                    log2.add(bestMove);
                    winner = board.getWinner();
                    currentPlayer = currentPlayer.getType() == PieceType.WHITE ? blackPlayer : whitePlayer;
                    Cell targetCell = bestMove.getToCell();
                    Color nextRequiredColor = targetCell.getCellColor();
                    Piece nextCurrPiece = state.getBoard().getPieceByPlayerAndColor(currentPlayer, nextRequiredColor);
                    board.setNextToMovePiece(nextCurrPiece);
                } else {
                    //пропуск хода
                    log2.add(null);
                    winner = board.getWinner();
                    currentPlayer = currentPlayer.getType() == PieceType.WHITE ? blackPlayer : whitePlayer;

                    int cellRow = board.getNextToMovePiece().getRow();
                    int cellCol = board.getNextToMovePiece().getCol();
                    Color nextRequiredColor = board.getGrid()[cellRow][cellCol].getCellColor();
                    Piece nextCurrPiece = board.getPieceByPlayerAndColor(currentPlayer, nextRequiredColor);
                    board.setNextToMovePiece(nextCurrPiece);
                }
                if(steps > 1000) {
                    System.out.println("Stopped because of long match");
                    break;
                }
                steps++;
            }
            if(winner!= null) {
                if (winner.getType() == PieceType.WHITE) {
                    weight2Counter++;
                } else {
                    weight1Counter++;
                }
            }
            i++;
            //игра начинается заново
        }
        //results.get(whiteWeight)
//        System.out.println("whiteWon " + whiteWon);
//        System.out.println("blackWon " + blackWon);

        System.out.println("use  vs  notUse");
        System.out.println("Use wins " + weight1Counter);
        System.out.println("NotUse wins " + weight2Counter);
        System.out.println();

    }

}
