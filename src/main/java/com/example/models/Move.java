package com.example.models;

public class Move {
    private final Piece piece;
    private final Cell fromCell;
    private final Cell toCell;
    private final Player player;

    public Move(Piece piece, Cell fromCell, Cell toCell, Player player) {
        this.piece = piece;
        this.fromCell = fromCell;
        this.toCell = toCell;
        this.player = player;
    }

    @Override
    public String toString() {
        return this.getPiece().getId() +" "+ this.toCell.getRow() +" "+ toCell.getCol() +" "+toCell.getCellColor();
    }

    public Player getPlayer() {
        return player;
    }

    public Cell getToCell() {
        return toCell;
    }

    public Cell getFromCell() {
        return fromCell;
    }

    public Piece getPiece() {
        return piece;
    }
}

