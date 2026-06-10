package com.example.models;

import com.example.Color;

import java.util.*;

public class Board {
    private final Cell[][] grid = new Cell[8][8];             // 8x8 игровое поле
    private List<Piece> whitePieces;
    private List<Piece> blackPieces;

    private PieceType humanType = PieceType.WHITE;

    private Player white;
    private Player black;

    private Piece nextToMovePiece;


    public PieceType getHumanType() {
        return humanType;
    }

    public Piece getPieceByPlayerAndColor(Player player, Color color) {
        if(player.getType() == PieceType.WHITE) {
            return whitePieces.stream().filter(p -> p.getPieceColor() == color).toList().get(0);
        } else {
            return blackPieces.stream().filter(p -> p.getPieceColor() == color).toList().get(0);
        }
    }

    public void setBlack(Player black) {
        this.black = black;
    }

    public void setWhite(Player white) {
        this.white = white;
    }

    public Player getBlack() {
        return black;
    }

    public Player getWhite() {
        return white;
    }

    public Board() {
    }

    public Piece getNextToMovePiece() {
        return nextToMovePiece;
    }

    public void setNextToMovePiece(Piece nextToMovePiece) {
        this.nextToMovePiece = nextToMovePiece;
    }

    public List<Piece> getWhitePieces() {
        return whitePieces;
    }

    public void setWhitePieces(List<Piece> whitePieces) {
        this.whitePieces = whitePieces;
    }

    public List<Piece> getBlackPieces() {
        return blackPieces;
    }

    public void setBlackPieces(List<Piece> blackPieces) {
        this.blackPieces = blackPieces;
    }

    public Cell[][] getGrid() {
        return grid;
    }


    public Board(Board original) {
        List<Piece> newWhitePieces = new ArrayList<>();
        List<Piece> newBlackPieces = new ArrayList<>();

        Piece originalNextMove = original.getNextToMovePiece();
        int nextRow = originalNextMove.getRow();
        int nextCol = originalNextMove.getCol();
        Piece nextToMove = null;

        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                Cell newCell = new Cell(original.grid[i][j]);
                this.grid[i][j] = newCell;
                if(newCell.getPiece()!= null) {
                    if(newCell.getPiece().getType() == PieceType.WHITE) {
                        newWhitePieces.add(newCell.getPiece());
                    }
                    else{
                        newBlackPieces.add(newCell.getPiece());
                    }
                    if(i == nextRow && j == nextCol) {
                        nextToMove = newCell.getPiece();
                    }
                }
            }
        }
        this.whitePieces = newWhitePieces;
        this.blackPieces = newBlackPieces;
        this.white = original.getWhite();
        this.black = original.getBlack();

        this.setNextToMovePiece(nextToMove);
    }


    public Piece findNextToMovePiece(Piece currentPiece, Cell toCell) {// передавать цвет
        Player currentPlayer = currentPiece.getOwner().getType() == PieceType.WHITE ? getBlack() : getWhite();
        Piece nextToMove = getPieceByPlayerAndColor(currentPlayer, toCell.getCellColor());
        return nextToMove;
    }
    public Player getWinner (Player originalPlayer) {
        int ourProgress = 0;
        int opponentProgress = 0;

        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                Cell cell = grid[row][col];
                if (cell.getPiece() != null) {
                    Piece piece = cell.getPiece();
                    if(piece.getOwner() == originalPlayer && piece.getRow() == (7 - piece.getOwner().getStartRow())) {//originalPlayer.getStartRow()
                        return originalPlayer;
                    } else if(piece.getRow() == (7 - piece.getOwner().getStartRow())){
                        return originalPlayer.getType() == PieceType.WHITE ? black : white;
                    }
                }
            }
        }
        return null;
    }

    public Player getWinner () {

        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                Cell cell = grid[row][col];
                if (cell.getPiece() != null) {
                    Piece piece = cell.getPiece();
                    if(piece.getRow() == (7 - piece.getOwner().getStartRow())) {//originalPlayer.getStartRow()
                        return piece.getOwner();
                    }
                }
            }
        }
        return null;
    }

    public boolean isValidPosition(int row, int col) {
        if (row >= 0 && row <= 7 && col >=0 && col <= 7) {
            return true;
        }
        return false;
    }

    public Cell getCell(int row, int col) {
        return getGrid()[row][col];
    }

    public void makeMove(Move move) {
        Piece movedPiece = move.getPiece();
        Cell targetCell = move.getToCell();
        Cell fromCell = move.getFromCell();

        movedPiece.setRow(targetCell.getRow());
        movedPiece.setCol(targetCell.getCol());

        targetCell.setPiece(movedPiece);
        fromCell.setPiece(null);
    }

    public void makeMove(int startRow, int startCol, int endRow, int endCol) {
        Cell fromCell = grid[startRow][startCol];
        Cell targetCell = grid[endRow][endCol];
        Piece movedPiece = fromCell.getPiece();

        movedPiece.setRow(targetCell.getRow());
        movedPiece.setCol(targetCell.getCol());

        targetCell.setPiece(movedPiece);
        fromCell.setPiece(null);
    }
}

