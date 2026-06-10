package com.example;

import com.example.models.Board;
import com.example.models.Cell;
import com.example.models.Piece;

public class KamisadoController {
    public static void executeMove(Cell destinationCell, Piece piece, Board board) {
        //очистить старую клетку
        board.getGrid()[piece.getRow()][piece.getCol()].setPiece(null);
        //поменять координаты фигуры
        piece.setRow(destinationCell.getRow());
        piece.setCol(destinationCell.getCol());
        //заполнить новую клетку
        board.getGrid()[destinationCell.getRow()][destinationCell.getCol()].setPiece(piece);
    }

    public static Boolean isValidMoveForHuman(Cell beginCell, Cell endCell, Board board) {
        int row1 = beginCell.getRow();
        int col1 = beginCell.getCol();

        int row2 = endCell.getRow();
        int col2 = endCell.getCol();


        int deltaRow = row2 - row1;
        int deltaCol = col2 - col1;
        Piece currentPiece = board.getNextToMovePiece();
        //Проверка, что ход не идёт назад
        if (deltaRow * currentPiece.getDirection() <0) {
            return false;
        }

        // Проверка, что ход не стоит на месте
        if (deltaRow == 0 && deltaCol == 0) {
            return false;
        }

        // Ход должен быть либо вертикальным, либо диагональным
        boolean isVertical = (deltaCol == 0);
        boolean isDiagonal = (Math.abs(deltaRow) == Math.abs(deltaCol));

        if (!isVertical && !isDiagonal) {
            return false;
        }

        // Направление шага
        int stepRow = Integer.compare(deltaRow, 0);
        int stepCol = Integer.compare(deltaCol, 0);

        int r = row1 + stepRow;
        int c = col1 + stepCol;

        while (r != row2 || c != col2) {
            if (board.getGrid()[r][c].getPiece() != null) {
                return false; // на пути есть фигура
            }
            r += stepRow;
            c += stepCol;
        }

        // Если дошли до конечной клетки — путь свободен
        return true;
    }

    public void tmp(){}
}
