package com.example.models;

import com.example.Color;

public class Piece {
    private final String id;

    private final PieceType type;

    private final int direction;
    private final Color pieceColor;
    private Player owner;
    private int row;
    private int col;

    public PieceType getType() {
        return type;
    }

    public Piece(String id, Color pieceColor, int row, int col, int destination, PieceType type) {
        this.direction = destination;
        this.id = id;
        this.pieceColor = pieceColor;
        this.row = row;
        this.col = col;
        this.type = type;
    }

    public Player getOwner() {
        return owner;
    }

    public Piece(Piece original) {
        this.id = original.id;
        this.pieceColor = original.pieceColor;
        this.owner = original.owner;
        this.row = original.getRow();
        this.col = original.getCol();
        this.direction = original.getDirection();
        this.type = original.type;
    }

    public Cell getCell(Cell[][] grid) {
        return grid[row][col];
    }

    public void setOwner(Player owner) {
        this.owner = owner;
    }

    public int getDirection() {
        return direction;
    }

    public int getRow() {
        return row;
    }

    public void setRow(int row) {
        this.row = row;
    }

    public int getCol() {
        return col;
    }

    public void setCol(int col) {
        this.col = col;
    }

    public String getId() {
        return id;
    }

    public Color getPieceColor() {
        return pieceColor;
    }
}

