package com.example.models;

import com.example.GameState;
import com.example.Minimax;

public class Player {
    private final PieceType type;
    private final PlayerType playerType;
    private final int startRow;

    public Player(PieceType type, int startRow, PlayerType playerType) {
        this.type = type;
        this.startRow = startRow;
        this.playerType = playerType;
    }

    public PlayerType getPlayerType() {
        return playerType;
    }

    public int getStartRow() {
        return  startRow;
    }

    public PieceType getType() {
        return type;
    }

    public Move findBestMove (GameState state, double[] w, boolean useAlphaBeta, boolean use, double[] w2) {
        return Minimax.findBestMove(4, state, this, w, useAlphaBeta, false, use);
    }
}


