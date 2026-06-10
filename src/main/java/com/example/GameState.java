package com.example;

import com.example.models.*;

public class GameState {
    private Board board;
    private Player currentPlayer;

    public GameState(Board board,  Player currentPlayer
                      ) {
        this.board = board;
        this.currentPlayer = currentPlayer;

    }

    public Board getBoard() { return board; }
    public Player getCurrentPlayer() { return currentPlayer; }
}
