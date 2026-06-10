package com.example;

import com.example.MCTS.MCTS;
import com.example.models.*;

import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

public class AlgsComparing {
    public static void main(String[] args) {
        runTournament(25);
    }

    private static void runTournament(int number) {

        int iterations = 6000;

        int n = number;
        int i =0;
        int minimaxCounter = 0;
        int monteCarloCounter = 0;
        int whiteminimax = 0;
        int whitemontecarlo = 0;

        int counter = 0;

        while (i<n) {
            try (FileWriter writer = new FileWriter("moves" +i+".txt")) {
                Board board = KamisadoView.initializeBoard();
                Random random = new Random();
                int randomNum = random.nextInt(8);

                Player whitePlayer = board.getWhite();
                Player blackPlayer = board.getBlack();
                Player currentPlayer = whitePlayer;

                Piece startingPiece = board.getGrid()[currentPlayer.getStartRow()][randomNum].getPiece();
                if (startingPiece != null) {
                    board.setNextToMovePiece(startingPiece);
                }

                List<Move> log = new ArrayList<>();
                int steps = 0;

                Player winner = board.getWinner();
                int flag = 0;
                Player blameOn = null;
                while (winner == null) {
                    //идет игра
                    GameState state = new GameState(board, currentPlayer);
                    Move bestMove = null;
                    if (currentPlayer.getType() == PieceType.WHITE) {
                        bestMove = MCTS.findBestMove(board, currentPlayer, iterations);
//                        bestMove = currentPlayer.findBestMove(state, new double[]{0.8, 3, 0.6},
//                                true, true, new double[]{2, 2, 1});
                    } else {
                        bestMove = Minimax.findBestMove(6,state, currentPlayer, new double[]{1, 2, 1},
                                true, true,true);
//                        bestMove = currentPlayer.findBestMove(state, new double[]{0.8, 3, 0.6},
//                                true, false, new double[]{2, 2, 1});
                        //bestMove = MCTS.findBestMove(board, currentPlayer);
                    }

                    if (bestMove != null) {
                        flag = 0;
                        blameOn = null;
                        board.makeMove(bestMove);
                        log.add(bestMove);
                        writer.write("\n");
                        writer.write(bestMove.toString());
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
                        if (flag == 0) {
                            blameOn = currentPlayer;
                        }
                        flag++;
                        log.add(null);
                        writer.write("skip move");
                        writer.write("\n");

                        int cellRow = board.getNextToMovePiece().getRow();
                        int cellCol = board.getNextToMovePiece().getCol();
                        Color nextRequiredColor = board.getGrid()[cellRow][cellCol].getCellColor();
                        Piece nextCurrPiece = board.getPieceByPlayerAndColor(currentPlayer, nextRequiredColor);
                        board.setNextToMovePiece(nextCurrPiece);
                    }
                    counter ++;
                    if (flag == 3) {
                        winner = blameOn.getType() == PieceType.WHITE ? blackPlayer : whitePlayer;
                    }
                    if (steps > 10000) {
                        System.out.println("Stopped because of long match");
                        break;
                    }
                    steps++;
                }
                System.out.println(counter);
                counter = 0;
                if (winner != null) {
                    if (winner.getType() == PieceType.WHITE) {
                        monteCarloCounter++;
                    } else {
                        minimaxCounter++;
                    }
                }
                i++;
                //игра начинается заново
            } catch (IOException e) {
            e.printStackTrace();
        }
        }
System.out.println("whitemontecarlo");
        System.out.println(monteCarloCounter);
        System.out.println(minimaxCounter);
        //перевернуть веса для черного и белого и сделать цикл снова
        i=0;
        List<Move>log2 = new ArrayList<>();
        counter = 0;
        while (i<n) {
            try (FileWriter writer = new FileWriter("moves2_" +i+".txt")) {
            Board board = KamisadoView.initializeBoard();
            Random random = new Random();
            int randomNum = random.nextInt(8);

            Player whitePlayer = board.getWhite();
            Player blackPlayer = board.getBlack();
            Player currentPlayer = whitePlayer;

            Piece startingPiece = board.getGrid()[currentPlayer.getStartRow()][randomNum].getPiece();
            if(startingPiece!=null){
                board.setNextToMovePiece(startingPiece);
            }

            int flag = 0;
            Player blameOn = null;

            int steps = 0;
            Player winner = board.getWinner();
            while (winner == null) {
                //идет игра
                GameState state = new GameState(board, currentPlayer);
                Move bestMove = null;
                if(currentPlayer.getType() == PieceType.WHITE) {
                            bestMove = Minimax.findBestMove(6, state, currentPlayer, new double[]{1, 2, 1},
                            true, true, true);
                } else {
                    bestMove = MCTS.findBestMove(board, currentPlayer, iterations);
            }

                if(bestMove!= null) {
                    flag = 0;
                    blameOn = null;
                    board.makeMove(bestMove);
                    log2.add(bestMove);
                    writer.write("\n");
                    writer.write(bestMove.toString());
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

                    if(flag == 0) {
                        blameOn = currentPlayer;
                    }
                    flag++;
                    log2.add(null);
                    writer.write("skip move");
                    writer.write("\n");

                    int cellRow = board.getNextToMovePiece().getRow();
                    int cellCol = board.getNextToMovePiece().getCol();
                    Color nextRequiredColor = board.getGrid()[cellRow][cellCol].getCellColor();
                    Piece nextCurrPiece = board.getPieceByPlayerAndColor(currentPlayer, nextRequiredColor);
                    board.setNextToMovePiece(nextCurrPiece);
                }
                counter++;
                if(flag == 3) {
                    winner = blameOn.getType() == PieceType.WHITE ? blackPlayer : whitePlayer;
                }
                if(steps > 10000) {
                    System.out.println("Stopped because of long match");
                    break;
                }
                steps++;
            }
                System.out.println(counter);
                counter = 0;
            if(winner!= null) {
                if (winner.getType() == PieceType.WHITE) {
                    minimaxCounter++;
                } else {
                    monteCarloCounter++;
                }
            }
            i++;
            } catch (IOException e) {
                e.printStackTrace();
            }
            //игра начинается заново
        }


        System.out.println("minimax vs monteCarlo");
        System.out.println("falsetrue wins " + minimaxCounter);
        System.out.println("monteCarlo wins " + monteCarloCounter);
        System.out.println();

    }

}
