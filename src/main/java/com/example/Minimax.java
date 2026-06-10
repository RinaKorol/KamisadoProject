package com.example;

import com.example.models.*;

import java.util.*;
import java.util.stream.Collectors;

public class Minimax {
    private static int MAX_DEPTH = 6;
    private static double[] w;
    public static Move findBestMove(int depth, GameState state, Player currentPlayer, double[] weights, boolean useAlphaBeta,
                                    boolean useRating, boolean useRatingAtStart) {
        w = weights;
        MAX_DEPTH = depth;
        List<Move> possibleMoves = getPossibleMoves(state.getBoard(), state.getCurrentPlayer());

        if (possibleMoves.isEmpty()) {
            return null;
        }

        List<Integer> f1 = new ArrayList<>();
        List<Integer> f2 = new ArrayList<>();
        List<Integer> f3 = new ArrayList<>();
        List<Integer> r1 = new ArrayList<>();
        List<Integer> r2 = new ArrayList<>();
        List<Integer> r3 = new ArrayList<>();
        List<Double> rating = new ArrayList<>();

        possibleMoves = possibleMoves.stream().sorted(Comparator.comparingInt((Move x) ->
                                Math.abs(x.getToCell().getRow() - x.getFromCell().getRow()))
                        .reversed()
                )
                .collect(Collectors.toList());
        for (Move move : possibleMoves) {
            GameState possibleState = makeMove(state, move);
            int winningResult= getIsWin(possibleState.getBoard(), currentPlayer);
            if (winningResult!= 0) {
                f1.add(winningResult);
                f2.add(winningResult);
                f3.add(winningResult);
                continue;
            }

            int[] moveValue = minimax(possibleState, 1, false, currentPlayer,
                    useAlphaBeta, -100000, 100000, useRating);
            f1.add(moveValue[0]);
            f2.add(moveValue[1]);
            f3.add(moveValue[2]);
        }
        if(useRatingAtStart) {
            r1 = replaceWithRanks(f1);
            r2 = replaceWithRanks(f2);
            r3 = replaceWithRanks(f3);
            for (int i =0; i< possibleMoves.size(); i++) {
                //rating.add(r1.get(i)*3+r2.get(i)*2+r3.get(i)*1.0);
                rating.add(r1.get(i)*w[0]+r2.get(i)*w[1]+r3.get(i)*w[2]);
            }
        } else {
            for (int i = 0; i < possibleMoves.size(); i++) {
                rating.add(f1.get(i) * w[0] + f2.get(i) * w[1] + f3.get(i) * w[2]);
            }
        }

        int index = rating.indexOf(Collections.max(rating));

        return possibleMoves.get(index);

    }

    private static int[] minimax(GameState state, int depth, boolean isMaximizing, Player originalPlayer,
                                 boolean useAlphaBeta, double alpha, double beta, boolean useRating) {
        int winningResult= getIsWin(state.getBoard(), originalPlayer);
        if (winningResult!= 0) {
            return new int[]{winningResult, winningResult, winningResult};
        }
        List<Move> possibleMoves = getPossibleMoves(state.getBoard(), state.getCurrentPlayer());
        if (depth == MAX_DEPTH) {
            return evaluate(state.getBoard(), originalPlayer);
            //выдаем значения функций, но чтобы выбрать лучшую используем голосование
        }
        if (possibleMoves.isEmpty()) {
            //поменять фигурур которая ходит в доске
            GameState newState = skipMove(state);
            int[] eval = minimax(newState, depth+1, !isMaximizing, originalPlayer, useAlphaBeta, alpha, beta, useRating);
            return eval;
            //return evaluate(state.getBoard(), originalPlayer);
        }
        List<GameState> states = new ArrayList<>();
        List<Integer> f1 = new ArrayList<>();
        List<Integer> f2 = new ArrayList<>();
        List<Integer> f3 = new ArrayList<>();
        List<Integer> r1 = new ArrayList<>();
        List<Integer> r2 = new ArrayList<>();
        List<Integer> r3 = new ArrayList<>();
        List<Double> rating = new ArrayList<>();

        possibleMoves = possibleMoves.stream().sorted(Comparator.comparingInt((Move x) ->
                                Math.abs(x.getToCell().getRow() - x.getFromCell().getRow()))
                        .reversed()
                )
                .collect(Collectors.toList());
        if(isMaximizing) {
            //играем мы - максимизируем
            for(Move move: possibleMoves) {
                GameState possibleState = makeMove(state, move);
                int[] eval = minimax(possibleState, depth+1, false, originalPlayer, useAlphaBeta, alpha, beta, useRating);
                states.add(possibleState);
                f1.add(eval[0]);
                f2.add(eval[1]);
                f3.add(eval[2]);
                double best = eval[0] * w[0] +eval[1] * w[1] +eval[2] * w[2];
                alpha = Math.max(alpha, best);
                if(useAlphaBeta) {
                    if (beta <= alpha)
                        break;
                }
            }
            if(useRating) {
                r1 = replaceWithRanks(f1);
                r2 = replaceWithRanks(f2);
                r3 = replaceWithRanks(f3);

                for (int i = 0; i < r1.size(); i++) {
                    rating.add(r1.get(i) * w[0] + r2.get(i) * w[1] + r3.get(i) * w[2]);
                }
            } else {
                for (int i = 0; i < f1.size(); i++) {
                    rating.add(f1.get(i) * w[0] + f2.get(i) * w[1] + f3.get(i) * w[2]);
                }
            }
            int index = rating.indexOf(Collections.max(rating));
            return new int[]{f1.get(index), f2.get(index), f3.get(index)};
        }
        else {
            //играет противник - минимизируем
            for(Move move: possibleMoves) {
                GameState possibleState = makeMove(state, move);
                int[] eval = minimax(possibleState, depth+1, true, originalPlayer, useAlphaBeta, alpha, beta, useRating);
                states.add(possibleState);
                f1.add(eval[0]);
                f2.add(eval[1]);
                f3.add(eval[2]);
                double best = eval[0] * w[0] +eval[1] * w[1] +eval[2] * w[2];
                beta = Math.min(beta, best);
                if(useAlphaBeta) {
                    if (beta <= alpha)
                        break;
                }
            }
            if(useRating) {
                r1 = replaceWithRanks(f1);
                r2 = replaceWithRanks(f2);
                r3 = replaceWithRanks(f3);
                for (int i = 0; i < r1.size(); i++) {
                    rating.add(r1.get(i) * w[0] + r2.get(i) * w[1] + r3.get(i) * w[2]);
                }
            } else {
                for (int i = 0; i < f1.size(); i++) {
                    rating.add(f1.get(i) * w[0] + f2.get(i) * w[1] + f3.get(i) * w[2]);
                }
            }
            int index = rating.indexOf(Collections.min(rating));
            return new int[]{f1.get(index), f2.get(index), f3.get(index)};
        }
    }

    public static List<Integer> replaceWithRanks(List<Integer> list) {
        if (list == null || list.isEmpty()) return Collections.emptyList();
        List<Integer> newList = new ArrayList<>(list);

        Map<Integer, Integer> rankMap = new HashMap<>();
        int[] rank = {1};

        newList.stream()
                .distinct()
                .sorted()
                .forEach(v -> rankMap.put(v, rank[0]++));

        newList.replaceAll(rankMap::get);
        return newList;
    }

    private static GameState skipMove(GameState state) {
        Board newBoard = new Board(state.getBoard());

        Piece currentPiece = state.getBoard().getNextToMovePiece();
        Cell targetCell = state.getBoard().getGrid()[currentPiece.getRow()][currentPiece.getCol()];

        Piece pieceToSet = newBoard.findNextToMovePiece(state.getBoard().getNextToMovePiece(),targetCell);
        newBoard.setNextToMovePiece(pieceToSet);
        Player currPlayer = state.getCurrentPlayer().getType() == PieceType.WHITE ? state.getBoard().getBlack() : state.getBoard().getWhite();
        return new GameState(
                newBoard,
                currPlayer
        );
    }

    private static int[] evaluate(Board board, final Player originalPlayer) {
        int f1 =0;
        int f2 = 0;
        int f3 = 0;
        //если одна из фигур достигла другого конца поля - то вохвращаем максимум по всем функциям
        int winningResult= getIsWin(board, originalPlayer);
        if (winningResult!= 0) {
            f1 = winningResult;
            f2 = winningResult;
            f3 = winningResult;
            return new int[]{f1, f2, f3};
        }
        f1 = getF1(board, originalPlayer);
        f2 = getF2(board,originalPlayer);
        f3 = getF3(board, originalPlayer);

        return new int[]{f1, f2, f3};

    }

    private static int getIsWin(Board board, Player originalPlayer) {
        int ourProgress = 0;
        int opponentProgress = 0;

        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                Cell cell = board.getGrid()[row][col];
                if (cell.getPiece() != null) {
                    Piece piece = cell.getPiece();
                    if(piece.getOwner() == originalPlayer && piece.getRow() == (7 - piece.getOwner().getStartRow())) {//originalPlayer.getStartRow()
                        ourProgress = Integer.MAX_VALUE -1 ;
                    } else if(piece.getRow() == (7 - piece.getOwner().getStartRow())){
                        opponentProgress = Integer.MAX_VALUE-1;
                    }
                }
            }
        }
        return ourProgress - opponentProgress;
    }

    private static int getF3(Board board, Player originalPlayer) {
        int ourProgress = 0;
        int opponentProgress = 0;

        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                Cell cell = board.getGrid()[row][col];
                if (cell.getPiece() != null) {
                    Piece piece = cell.getPiece();
                    List<Move> moves = getPossibleMovesByPiece(board, originalPlayer, piece);
                    if(piece.getOwner() == originalPlayer) {
                        ourProgress+= moves.size();
                    } else {
                        opponentProgress += moves.size();
                    }
                }
            }
        }
        return ourProgress - opponentProgress;
    }

    private static int getF2(Board board, Player originalPlayer) {
        int ourProgress = 0;
        int opponentProgress = 0;

        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                Cell cell = board.getGrid()[row][col];
                if (cell.getPiece() != null) {
                    Piece piece = cell.getPiece();
                    List<Move> moves = getPossibleMovesByPiece(board, originalPlayer, piece);
                    boolean ableToWin = !moves.stream().filter(x-> x.getToCell().getRow() == (7 - piece.getOwner().getStartRow())).collect(Collectors.toList()).isEmpty();
                    if(ableToWin && piece.getOwner() == originalPlayer) {
                        ourProgress++;
                    } else if(ableToWin){
                    opponentProgress ++;
                    }
                }
            }
        }
        return ourProgress - opponentProgress;
    }

    private static int getF1(Board board, Player originalPlayer) {
        int ourProgress = 0;
        int opponentProgress = 0;

        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                Cell cell = board.getGrid()[row][col];
                if (cell.getPiece() != null) {
                    Piece piece = cell.getPiece();
                    int progress = calculateProgress(piece);

                    if (piece.getOwner() == originalPlayer) {
                        ourProgress += progress;
                    } else {
                        opponentProgress += progress;
                    }
                }
            }
        }
        return ourProgress - opponentProgress;
    }

    // Расчет прогресса фигуры (количество пройденных клеток)
    private static int calculateProgress(Piece piece) {
        int startRow = piece.getOwner().getStartRow();
        int currentRow = piece.getRow();
        int direction = piece.getDirection();

        return Math.abs(currentRow - startRow);
    }

    private static GameState makeMove(final GameState state, Move move) {
       // Создаем глубокую копию текущего состояния
        Board newBoard = new Board(state.getBoard());
        Cell targetCell = state.getBoard().getGrid()[move.getToCell().getRow()][move.getToCell().getCol()];

        newBoard.makeMove(move.getFromCell().getRow(), move.getFromCell().getCol(),
                move.getToCell().getRow(), move.getToCell().getCol());

        Player currPlayer = state.getCurrentPlayer().getType() == PieceType.WHITE ? state.getBoard().getBlack() : state.getBoard().getWhite();

        Color nextRequiredColor = targetCell.getCellColor();
        Piece nextCurrPiece = state.getBoard().getPieceByPlayerAndColor(currPlayer, nextRequiredColor);

        newBoard.setNextToMovePiece(nextCurrPiece);

        return new GameState(
                newBoard,
                currPlayer
        );
    }

    public static List<Move> getPossibleMovesByPiece(Board board, Player currentPlayer, Piece piece) {
        List<Move> moves = new ArrayList<>();

        List<Cell> possibleTargets = getPossibleTargets(piece, board);
        for (Cell target : possibleTargets) {
            moves.add(new Move(piece, piece.getCell(board.getGrid()), target, currentPlayer));
        }
        return moves;
    }

    public static List<Move> getPossibleMoves(Board board, Player currentPlayer) {
        List<Move> moves = new ArrayList<>();
        Piece pieceToMove = board.getNextToMovePiece();

        List<Cell> possibleTargets = getPossibleTargets(pieceToMove, board);
        for (Cell target : possibleTargets) {
            moves.add(new Move(pieceToMove, pieceToMove.getCell(board.getGrid()), target, currentPlayer));
        }
        return moves;
    }


    private static List<Cell> getPossibleTargets(Piece piece, Board board) {
        List<Cell> targets = new ArrayList<>();
        Cell currentCell = piece.getCell(board.getGrid());
        int row = currentCell.getRow();
        int col = currentCell.getCol();
        int direction = piece.getDirection();

        int[][] directions = {
                {direction, 0},     // прямо вперед
                {direction, 1},     // диагональ вперед-вправо
                {direction, -1}     // диагональ вперед-влево
        };

        for (int[] dir : directions) {
            int newRow = row + dir[0];
            int newCol = col + dir[1];

            while (board.isValidPosition(newRow, newCol)) {
                Cell targetCell = board.getCell(newRow, newCol);

                // Проверяем, пуста ли клетка
                if (targetCell.getPiece() == null) {
                    targets.add(targetCell);
                } else {
                    break; // фигура на пути - дальше нельзя
                }

                newRow += dir[0];
                newCol += dir[1];
            }
        }

        return targets;
    }

}
