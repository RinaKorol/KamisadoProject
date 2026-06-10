package com.example.models;

import com.example.Color;

public class Cell {
    private final int row;
    private final int col;
    private final Color cellColor;
    private Piece piece;

    public Cell(int row, int col, Color cellColor) {
        this.row = row;
        this.col = col;
        this.cellColor = cellColor;
    }

    // Конструктор для копирования
    public Cell(Cell original) {
        this.row = original.row;
        this.col = original.col;
        this.cellColor = original.cellColor;
        if(original.getPiece() != null) {
            this.piece = new Piece(original.getPiece());
        } else this.piece = null;

    }

    public int getRow() {
        return row;
    }

    public int getCol() {
        return col;
    }

    public Piece getPiece() {
        return piece;
    }

    public void setPiece(Piece piece) {
        this.piece = piece;
    }

    public Color getCellColor() {
        return cellColor;
    }
}

