package com.example;

import com.example.models.*;

import java.util.*;

public class WeightFinding {

    public static void main(String[] args) {
        List<double[]> allWeightSets = generateWeightCombinations();
        // Храним результаты для каждого набора весов
        Map<String, Integer> results = new HashMap<>();
        // Перебираем все пары наборов весов
        for (int i = 0; i < allWeightSets.size(); i++) {
            double[] weights1 = allWeightSets.get(i);

            for (int j = i + 1; j < allWeightSets.size(); j++) {
                double[] weights2 = allWeightSets.get(j);

                runTournament(15 ,weights1, weights2, results);
            }
        }

        printResults(results);


    }

    private static void runTournament(int number, double[] weights1, double[] weights2, Map<String, Integer> results) {
        String key1 = Arrays.toString(weights1);
        String key2 = Arrays.toString(weights2);
        results.putIfAbsent(key1, 0);
        results.putIfAbsent(key2, 0);

        int whiteWon = 0;
        int blackWon = 0;
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

            double[] whiteWeight = weights1;
            double[] blackWeight = weights2;

            Piece startingPiece = board.getGrid()[currentPlayer.getStartRow()][randomNum].getPiece();
            if(startingPiece!=null){
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
                double[] w = currentPlayer.getType() == PieceType.WHITE ? whiteWeight : blackWeight;
                Move bestMove = currentPlayer.findBestMove(state, w, false, false, w);


                if(bestMove!= null) {
                    flag = 0;
                    blameOn = null;
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
                    if(flag == 0) {
                        blameOn = currentPlayer;
                    }
                    flag++;
                    log.add(null);

                    int cellRow = board.getNextToMovePiece().getRow();
                    int cellCol = board.getNextToMovePiece().getCol();
                    Color nextRequiredColor = board.getGrid()[cellRow][cellCol].getCellColor();
                    Piece nextCurrPiece = board.getPieceByPlayerAndColor(currentPlayer, nextRequiredColor);
                    board.setNextToMovePiece(nextCurrPiece);
                }
                if(flag == 3) {
                    winner = blameOn.getType() == PieceType.WHITE ? blackPlayer : whitePlayer;
                }
                if(steps > 10000) {
                    System.out.println("Stopped because of long match");
                    break;
                }
                steps++;
            }
            if(winner != null) {
                if (winner.getType() == PieceType.WHITE) {
                    whiteWon++;
                    weight1Counter++;
                } else {
                    blackWon++;
                    weight2Counter++;
                }
            }
            i++;
            //игра начинается заново
        }

        //перевернуть веса для черного и белого и сделать цикл снова
        i=0;
        List<Move>log2 = new ArrayList<>();
        while (i<n) {
            Board board = KamisadoView.initializeBoard();
            Random random = new Random();
            int randomNum = random.nextInt(8);

            Player whitePlayer = board.getWhite();
            Player blackPlayer = board.getBlack();
            Player currentPlayer = whitePlayer;
            double[] whiteWeight = weights2;
            double[] blackWeight = weights1;

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
                double[] w = currentPlayer.getType() == PieceType.WHITE ? whiteWeight : blackWeight;
                Move bestMove = currentPlayer.findBestMove(state, w, false, false, w);

                if(bestMove!= null) {
                    flag = 0;
                    blameOn = null;
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
                    winner = board.getWinner();
                    currentPlayer = currentPlayer.getType() == PieceType.WHITE ? blackPlayer : whitePlayer;

                    if(flag == 0) {
                        blameOn = currentPlayer;
                    }
                    flag++;
                    log2.add(null);

                    int cellRow = board.getNextToMovePiece().getRow();
                    int cellCol = board.getNextToMovePiece().getCol();
                    Color nextRequiredColor = board.getGrid()[cellRow][cellCol].getCellColor();
                    Piece nextCurrPiece = board.getPieceByPlayerAndColor(currentPlayer, nextRequiredColor);
                    board.setNextToMovePiece(nextCurrPiece);
                }
                if(flag == 3) {
                    winner = blameOn.getType() == PieceType.WHITE ? blackPlayer : whitePlayer;
                }
                if(steps > 10000) {
                    System.out.println("Stopped because of long match");
                    break;
                }
                steps++;
            }
            if(winner!= null) {
                if (winner.getType() == PieceType.WHITE) {
                    whiteWon++;
                    weight2Counter++;
                } else {
                    blackWon++;
                    weight1Counter++;
                }
            }
            i++;
            //игра начинается заново
        }
//        System.out.println("whiteWon " + whiteWon);
//        System.out.println("blackWon " + blackWon);
        int tmp1 = results.get(key1);
        int tmp2 = results.get(key2);

        results.put(key1, tmp1 + weight1Counter);
        results.put(key2, tmp2 + weight2Counter);

        System.out.println(key1 + " vs " + key2);
        System.out.println(key1 +" wins " + weight1Counter);
        System.out.println(key2 +" wins " + weight2Counter);
        System.out.println();

    }


    // Генерирует все комбинации весов от 1 до 3
    static List<double[]> generateWeightCombinations() {
        List<double[]> combinations = new ArrayList<>();

//        for (double w1 = 1; w1 <= 3; w1+=1) {
//            for (double w2 = 1; w2 <= 3; w2+=1) {
//                for (double w3 = 1; w3 <= 3; w3+=1) {
//                    combinations.add(new double[]{w1, w2, w3});
//                }
//            }
//        }

        for (double w1 = 0.6; w1 <= 1; w1+=0.2) {
            for (double w2 = 1; w2 <= 3; w2+=1) {
                for (double w3 = 0.6; w3 <= 1; w3+=0.2) {
                    combinations.add(new double[]{w1, w2, w3});
                }
            }
        }

        return combinations;
    }

    // Выводит результаты всех наборов весов
    static void printResults(Map<String, Integer> results) {
        System.out.println("Результаты для всех наборов весов:");
        System.out.println("==================================");

        List<Map.Entry<String, Integer>> sorted = new ArrayList<>(results.entrySet());
        sorted.sort((a, b) -> Integer.compare(b.getValue(), a.getValue()));

        for (Map.Entry<String, Integer> entry : sorted) {
            Integer r = entry.getValue();
            System.out.printf("%s -> Побед: %d\n",
                    entry.getKey(), r);
        }
    }
}
