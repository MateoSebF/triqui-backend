package com.triqui.backend.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Game {

    private String[][] board;
    private String currentPlayer;
    private boolean gameEnded;

    public Game() {
        this.board = new String[3][3];
        this.currentPlayer = "X";
        this.gameEnded = false;
        resetBoard();
    }

    public void resetBoard() {
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                board[i][j] = "";
            }
        }
    }

    public boolean makeMove(String player, int row, int col) {
        if (gameEnded || board[row][col] != "" || !player.equals(currentPlayer)) {
            return false;
        }

        board[row][col] = currentPlayer;
        checkWinner();
        switchPlayer();
        return true;
    }

    public String convertToMessage() {
        StringBuilder message = new StringBuilder();
        message.append("{");
        message.append("\"board\": [");
        for (int i = 0; i < 3; i++) {
            message.append("[");
            for (int j = 0; j < 3; j++) {
                message.append("\"" + board[i][j] + "\"");
                if (j < 2) {
                    message.append(",");
                }
            }
            message.append("]");
            if (i < 2) {
                message.append(",");
            }
        }
        message.append("],");
        message.append("\"gameEnded\": " + gameEnded);
        message.append("}");
        return message.toString();
    }

    private void switchPlayer() {
        currentPlayer = currentPlayer.equals("X") ? "O" : "X";
    }

    private void checkWinner() {
        // Check rows, columns, and diagonals for a winner
        if (checkRows() || checkCols() || checkDiagonals()) {
            gameEnded = true;
        }
    }

    private boolean checkRows() {
        for (int i = 0; i < 3; i++) {
            if (board[i][0].equals(board[i][1]) && board[i][1].equals(board[i][2]) && !board[i][0].isEmpty()) {
                return true;
            }
        }
        return false;
    }

    private boolean checkCols() {
        for (int i = 0; i < 3; i++) {
            if (board[0][i].equals(board[1][i]) && board[1][i].equals(board[2][i]) && !board[0][i].isEmpty()) {
                return true;
            }
        }
        return false;
    }

    private boolean checkDiagonals() {
        if (board[0][0].equals(board[1][1]) && board[1][1].equals(board[2][2]) && !board[0][0].isEmpty()) {
            return true;
        }
        if (board[0][2].equals(board[1][1]) && board[1][1].equals(board[2][0]) && !board[0][2].isEmpty()) {
            return true;
        }
        return false;
    }

}
