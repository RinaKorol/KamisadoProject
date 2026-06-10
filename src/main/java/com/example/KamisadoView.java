package com.example;

import com.example.MCTS.MCTS;
import com.example.models.*;
import com.vaadin.flow.component.ClientCallable;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.Route;

import java.util.*;
import java.util.concurrent.CompletableFuture;

import static com.example.KamisadoController.isValidMoveForHuman;

@Route(value = "kamisado")
public class KamisadoView extends VerticalLayout implements BeforeEnterObserver {
    private static final int BOARD_SIZE = 8;
    private H2 statusLabel;

    private GameStatusBar statusBar = new GameStatusBar();
    private boolean isWon = false;
    static Map<Integer, List<Color>> colorToCellMap = new HashMap<>();

    private Div boardContainer;

    Piece skippedPiece = null;

    private Board board;
    Notification notification;

    Player currBlameOn = null;
    private String algorithm;
    private String difficulty;

    private final Map<Cell, Div> cellDivMap = new HashMap<>();
    private Map<Div, Div> dotsCellMap = new HashMap<>();
    private final Map<Piece, Div> pieceDivMap = new HashMap<>();

    public KamisadoView() {
    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        algorithm = (String) UI.getCurrent().getSession().getAttribute("selectedAlgorithm");
        difficulty = (String) UI.getCurrent().getSession().getAttribute("selectedDifficulty");

        // Значения по умолчанию, если параметры не найдены
        if (algorithm == null) algorithm = "Минимакс";
        if (difficulty == null) difficulty = "Средняя";
        board = initializeBoard();
        setupUI();
    }

    private void setupUI() {
        setSizeFull();
        setAlignItems(Alignment.CENTER);

        notification = new Notification();
        statusLabel = new H2();
        Div div = new Div(statusLabel);
        statusLabel.setText("Ваши фигуры - белые. Кликните на фигуру, чтобы выбрать её");
        statusLabel.getStyle().set("margin", "30px")
                .set("width", "500px")
                .set("padding", "15px 20px")           // Внутренние отступы
                .set("fontSize", "18px")               // Размер шрифта
                .set("fontWeight", "bold")             // Жирный шрифт
                .set("color", "#000000")               // Цвет текста
                .set("backgroundColor", "#f2ef96")
                .set("borderRadius", "8px")
                .set("boxShadow", "0 2px 4px rgba(0,0,0,0.2)")
                //.set("width", "100%")                  // Ширина
                .set("textAlign", "center"); ;

        boardContainer = new Div();
        boardContainer.getStyle().set("display", "grid");
        boardContainer.getStyle().set("grid-template-columns", "repeat(" + BOARD_SIZE + ", 60px)");
        boardContainer.getStyle().set("grid-template-rows", "repeat(" + BOARD_SIZE + ", 60px)");
        boardContainer.getStyle().set("gap", "0px");
        boardContainer.getStyle().set("border", "2px solid #333");

        renderBoard();

        Button restart = new Button("Начать заново", event -> {
            getUI().ifPresent(ui -> ui.getPage().executeJs("window.location.reload();"));
        });
        restart.getStyle()
                .setBackgroundColor("#0066cc")
                .setColor("white")
                .set("font-family", "cursive")
                .setFontSize("18px")
                .setFontWeight("bold")
                .setPadding("12px 30px")
                .setBorderRadius("8px")
                .setTextDecoration("none")
                .set("margin", "50px")
                .setCursor("pointer")
                .setTransition("all 0.3s ease");

        add(statusBar, boardContainer, restart);
    }

    private void renderBoard() {
        boardContainer.removeAll();
        for (int row = 0; row < BOARD_SIZE; row++) {
            for (int col = 0; col < BOARD_SIZE; col++) {
                Cell cell = board.getGrid()[row][col];

                Div cellDiv = createCellDiv(row, col, cell);
                boardContainer.add(cellDiv);
                cellDivMap.put(cell, cellDiv);
            }
        }
    }

    private Div createCellDiv(int row, int col, Cell cell) {
        Div cellDiv = new Div();
        cellDiv.getStyle()
                .set("width", "60px")
                .set("height", "60px")
                .set("background-color", cell.getCellColor().getColorCode())
                .set("display", "flex")
                .set("align-items", "center")
                .set("justify-content", "center")
                .set("cursor", "pointer")
                .set("position", "relative");

        Piece piece = cell.getPiece();
        if (piece != null) {
            Div pieceDiv = createCircle(piece);
            cellDiv.add(pieceDiv);

        }

        // Обработка клика
        cellDiv.getElement().addEventListener("click", e -> onCellClick(row, col));

        return cellDiv;
    }

    private void choosingFirstPiece(Cell clickedCell) {
        Piece clickedPiece = clickedCell.getPiece();
        if (clickedPiece != null) {
            if(clickedPiece.getType() == board.getHumanType()) {
                statusBar.setMessage(
                        "Кликните на свободную клетку, чтобы сделать ход.",
                        false);
                board.setNextToMovePiece(clickedPiece);
                highlightPiece(clickedPiece);
                highlightMoves(clickedPiece, clickedPiece.getOwner());
            } else {
                statusBar.setMessage("Нельзя выбрать фигуру противника. Ваши фигуры - белые. Кликните на фигуру, чтобы выбрать её",
                        true);
                Notification.show("Нельзя выбрать фигуру противника.", 3000,
                        Notification.Position.MIDDLE);
            }

        }
    }

    private void highlightMoves(Piece piece, Player currentPlayer) {
        List<Move> possibleMoves = Minimax.getPossibleMoves(board, currentPlayer);
        for(Move move: possibleMoves) {
            Cell cell = board.getGrid()[move.getToCell().getRow()][move.getToCell().getCol()];
            Div cellDiv = cellDivMap.get(cell);

            Div dot = new Div();

            dot.getStyle().set("width", "4px");
            dot.getStyle().set("height", "4px");
            //circle.getStyle().set("border-radius", "50%");
            dot.getStyle().set("background-color", "#FFFFFF");

            cellDiv.add(dot);
            dotsCellMap.put(dot, cellDiv);
        }
    }

    @ClientCallable
    private void onCellClick(int row, int col) {
        if(isWon) {
            return;
        }
        Cell clickedCell = board.getGrid()[row][col];

        Piece currentPiece = board.getNextToMovePiece();
        if(currentPiece == null) {
            choosingFirstPiece(clickedCell); //вынесла в метод
        } else {
            statusBar.setMessage("Кликните на свободную клетку, чтобы сделать ход.", false);
            int oldRow = currentPiece.getRow();
            int oldCol = currentPiece.getCol();
            Cell oldCell = board.getGrid()[oldRow][oldCol];
            if (isValidMoveForHuman(oldCell, clickedCell, board)) {
                Player humanPlayer = currentPiece.getOwner();
                makeMove(clickedCell, currentPiece);
                currBlameOn = currentPiece.getOwner();
                if(!isWon) {
                    Player nextPlayer = currentPiece.getOwner().getType() == PieceType.WHITE ? board.getBlack() : board.getWhite();
                    executeAIMove(nextPlayer);

                }
            } else {
                Notification.show("Невозможный ход, фигуры могут ходить прямо и по диагонали и только вперед. Попробуйте снова");
            }
        }
    }

    private void skipMove(Piece piece) {
        skippedPiece = piece;
        unHighlightPiece(piece);
        Piece nextPiece = findNextPiece(piece);
        board.setNextToMovePiece(nextPiece);
        statusBar.setMessage("Фигура заблокирована, ход был пропущен.", false);
        Notification.show("Фигура заблокирована, ход был пропущен.");
        highlightPiece(board.getNextToMovePiece());
    }

    private Piece findNextPiece(Piece currPiece) {
        int cellRow = currPiece.getRow();
        int cellCol = currPiece.getCol();
        Player currentPlayer = currPiece.getOwner().getType() == PieceType.WHITE ? board.getBlack() : board.getWhite();
        Color nextRequiredColor = board.getGrid()[cellRow][cellCol].getCellColor();
        Piece nextCurrPiece = board.getPieceByPlayerAndColor(currentPlayer, nextRequiredColor);
        return nextCurrPiece;
    }

    private CompletableFuture<Void> executeAIMove(Player currentPlayer) {
        UI ui = UI.getCurrent();

        return CompletableFuture.supplyAsync(() -> {
            try {
                GameState state = new GameState(board, currentPlayer);
                if (Objects.equals(algorithm, "Минимакс")) {
                    int depth;
                    if(Objects.equals(difficulty, "Легкая")) {
                        depth = 1;
                    } else if (Objects.equals(difficulty, "Средняя")) {
                        depth = 3;
                    } else {
                        depth = 6;
                    }
                    return Minimax.findBestMove(depth, state, currentPlayer, new double[]{0.8, 3, 0.6},
                            true, false, true);//new double[]{1, 2, 1}
                } else {
                    int iterations;
                    if(Objects.equals(difficulty, "Легкая")) {
                        iterations = 10;
                    } else if (Objects.equals(difficulty, "Средняя")) {
                        iterations = 500;
                    } else {
                        iterations = 6000;
                    }
                    return MCTS.findBestMove(board, currentPlayer, iterations);
                }
            } catch (Exception e) {
                e.printStackTrace();
                return null;
        }
        }).thenAccept(bestMove -> {
            ui.access(() -> {
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
                if (bestMove != null) {
                    makeMove(bestMove.getToCell(), bestMove.getPiece());
                    currBlameOn = currentPlayer;
                    if (!isWon) {
                        checkAndHandleSkips();
                    }
                } else {
                    handleSkipAndContinue();
                }
                if (!isWon && board.getNextToMovePiece().getOwner().getPlayerType()!= PlayerType.AI) {
                    highlightMoves(board.getNextToMovePiece(), board.getNextToMovePiece().getOwner());
                }
            });
        });
    }

    private void checkAndHandleSkips() {
        Player currentPlayer = board.getNextToMovePiece().getOwner();
        List<Move> possibleMoves = Minimax.getPossibleMoves(board, currentPlayer);

        if (possibleMoves.isEmpty()) {
            handleSkipAndContinue();
        }
    }

    private void handleSkipAndContinue() {
        Piece currentPiece = board.getNextToMovePiece();
        Piece nextPiece = findNextPiece(board.getNextToMovePiece());
        if (skippedPiece != null && skippedPiece.getId().equals(nextPiece.getId())) {
            // Зациклились - определяем победителя
            checkPatSituation();
            return;
        }

        skippedPiece = currentPiece;
        skipMove(currentPiece);

        if (!isWon) {
            Player nextPlayer = board.getNextToMovePiece().getOwner();

            // Рекурсивно вызываем AI ход
            if (nextPlayer.getPlayerType() == PlayerType.AI) {
                executeAIMove(nextPlayer);
            } else {
                // Ход человека
                statusBar.setMessage("Ваш ход.", false);
                highlightPiece(board.getNextToMovePiece());
            }
        }
    }

    private void checkPatSituation() {
        isWon = true;
        if(currBlameOn.getPlayerType() ==PlayerType.HUMAN) {
            statusLabel.setText("You lose(");
            statusBar.setMessage("Вы проиграли", false);
            Notification.show("Вы проиграли", 3000, Notification.Position.MIDDLE);
        } else {
            statusLabel.setText("You won!");
            statusBar.setMessage("Вы победили",false);
            Notification.show("Вы победили!", 3000, Notification.Position.MIDDLE);
        }
    }


    private void checkWin (Piece piece) {
        Player winner = board.getWinner(piece.getOwner());
        if (winner != null) {
            isWon = true;
            highlightPiece(piece);
            if (winner.getPlayerType() == PlayerType.HUMAN) {
                //выиграл пользователь вывести you won
                statusLabel.setText("You won!");
                statusBar.setMessage("Вы победили",false);
                Notification.show("Вы победили!", 3000, Notification.Position.MIDDLE);
            } else {
                //выиграл алгоритм вывести you lose
                statusLabel.setText("You lose(");
                statusBar.setMessage("Вы проиграли", false);
                Notification.show("Вы проиграли", 3000, Notification.Position.MIDDLE);
            }
        }
    }

    private void makeMove(Cell cell, Piece currentPiece) {
        if (currentPiece != null) {
            // Сохраняем старые координаты
            int oldRow = board.getNextToMovePiece().getRow();
            int oldCol = board.getNextToMovePiece().getCol();
            Cell oldCell = board.getGrid()[oldRow][oldCol];
            if (isValidMoveForHuman(oldCell, cell, board)) {

                KamisadoController.executeMove(cell, currentPiece, board);

                updateCell(oldRow, oldCol); // Бывшая клетка фигуры
                updateCell(cell.getRow(), cell.getCol());       // Новая клетка фигуры

                Piece next = board.findNextToMovePiece(currentPiece, cell);
                checkWin(board.getNextToMovePiece());
                board.setNextToMovePiece(next);
                clearDots();
                if(!isWon) {
                    highlightPiece(next);
                }
            } else {
                Notification.show("Invalid move. Select a piece again.");
           }
        }
    }

    private void clearDots() {
        for(Div dot: dotsCellMap.keySet()) {
            Div cellDiv = dotsCellMap.get(dot);
            cellDiv.remove(dot);
        }
        dotsCellMap.clear();
    }

    public void highlightPiece(Piece piece) {
        int row = piece.getRow();
        int col = piece.getCol();

        Cell cell = board.getGrid()[row][col];
        Div pieceDiv = pieceDivMap.get(piece);
        if(pieceDiv != null) {
            pieceDiv.getStyle().set("border-width", "8px");
        }
    }

    public void unHighlightPiece(Piece piece) {
        int row = piece.getRow();
        int col = piece.getCol();

        Div pieceDiv = pieceDivMap.get(piece);
        if(pieceDiv != null) {
            pieceDiv.getStyle().set("border-width", "3px");
        }
    }

    private void updateCell(int row, int col) {
        Cell cell = board.getGrid()[row][col];
        Div cellDiv = cellDivMap.get(cell);

        // Очищаем содержимое клетки
        cellDiv.removeAll();

        // Добавляем фигуру, если есть
        Piece piece = cell.getPiece();
        if (piece != null) {
            Div pieceDiv = createCircle(piece);
            cellDiv.add(pieceDiv);
        }
    }

    private Div createCircle(Piece piece) {
        Div circle = new Div();

        String circleColor = piece.getPieceColor().getColorCode();

        circle.getStyle().set("width", "40px");
        circle.getStyle().set("height", "40px");
        circle.getStyle().set("border-radius", "50%");
        circle.getStyle().set("background-color", circleColor);
        circle.getStyle().set("transition", "all 0.3s ease");
        circle.getStyle().set("box-shadow", "0 2px 4px rgba(0,0,0,0.2)");

        String borderColor;
        switch (piece.getType()) {
            case WHITE -> {
                borderColor = "#FFFFFF";
                circle.getStyle().set("box-shadow", "0 0 0 1px rgba(0,0,0,0.1), 0 2px 4px rgba(0,0,0,0.2)");
            }
            case BLACK -> {
                borderColor = "#000000";
                circle.getStyle().set("box-shadow", "0 2px 4px rgba(0,0,0,0.3)");
            }
            default -> borderColor = "#CCCCCC";
        }

        circle.getStyle().set("border", "3px" + " solid " + borderColor);
        // Эффект при наведении
        circle.addClassName("circle-hover");

        pieceDivMap.put(piece, circle);
        return circle;
    }

    public static Board initializeBoard() {
        Board board = new Board();
        colorToCellMap = initCorolsToCellMap();
        setupInitialCells(board);
        setupInitialPieces(board);
        return board;
    }

    private static void setupInitialCells(Board board){
        for (int row = 0; row < BOARD_SIZE; row++) {
            List<Color> coloredRow = colorToCellMap.get(row);
            for (int col = 0; col < BOARD_SIZE; col++) {
                Cell cell = board.getGrid()[row][col];
                if (cell == null) {
                    cell = new Cell(row, col, coloredRow.get(col));
                    board.getGrid()[row][col] = cell;
                }
            }
        }
    }

    private static void setupInitialPieces(Board board) {
        List<Piece> whitePieces = new ArrayList<>();
        List<Piece> blackPieces = new ArrayList<>();
        Player black = new Player(PieceType.BLACK, 0, PlayerType.AI);

        List<Color> upColors = colorToCellMap.get(0);
        for (int col = 0; col < BOARD_SIZE; col++) {
            Piece piece = new Piece("Black"+upColors.get(col).name(), upColors.get(col), 0, col, 1, PieceType.BLACK);
            piece.setOwner(black);
            blackPieces.add(piece);
            board.getGrid()[0][col].setPiece(piece);
            board.setBlack(black);
        }


        Player white = new Player(PieceType.WHITE,7, PlayerType.HUMAN);
        List<Color> downColors = colorToCellMap.get(7);
        for (int col = 0; col < BOARD_SIZE; col++) {
            Piece piece = new Piece("White"+downColors.get(col).name(), downColors.get(col), 7, col, -1, PieceType.WHITE);
            piece.setOwner(white);
            whitePieces.add(piece);
            board.getGrid()[7][col].setPiece(piece);
            board.setWhite(white);
        }
        board.setWhitePieces(whitePieces);
        board.setBlackPieces(blackPieces);
    }

    private static Map<Integer, List<Color>> initCorolsToCellMap() {
        Map<Integer, List<Color>> colorToCellMap = new HashMap<>();
        colorToCellMap.put(0, Arrays.asList(
                Color.ORANGE,
                Color.BLUE,
                Color.PURPLE,
                Color.PINK,
                Color.YELLOW,
                Color.RED,
                Color.GREEN,
                Color.BROWN));
        colorToCellMap.put(1, Arrays.asList(
                Color.RED,
                Color.ORANGE,
                Color.PINK,
                Color.GREEN,
                Color.BLUE,
                Color.YELLOW,
                Color.BROWN,
                Color.PURPLE));
        colorToCellMap.put(2, Arrays.asList(
                Color.GREEN,
                Color.PINK,
                Color.ORANGE,
                Color.RED,
                Color.PURPLE,
                Color.BROWN,
                Color.YELLOW,
                Color.BLUE));
        colorToCellMap.put(3, Arrays.asList(
                Color.PINK,
                Color.PURPLE,
                Color.BLUE,
                Color.ORANGE,
                Color.BROWN,
                Color.GREEN,
                Color.RED,
                Color.YELLOW));
        colorToCellMap.put(4, Arrays.asList(
                Color.YELLOW,
                Color.RED,
                Color.GREEN,
                Color.BROWN,
                Color.ORANGE,
                Color.BLUE,
                Color.PURPLE,
                Color.PINK));
        colorToCellMap.put(5, Arrays.asList(
                Color.BLUE,
                Color.YELLOW,
                Color.BROWN,
                Color.PURPLE,
                Color.RED,
                Color.ORANGE,
                Color.PINK,
                Color.GREEN));
        colorToCellMap.put(6, Arrays.asList(
                Color.PURPLE,
                Color.BROWN,
                Color.YELLOW,
                Color.BLUE,
                Color.GREEN,
                Color.PINK,
                Color.ORANGE,
                Color.RED));
        colorToCellMap.put(7, Arrays.asList(
                Color.BROWN,
                Color.GREEN,
                Color.RED,
                Color.YELLOW,
                Color.PINK,
                Color.PURPLE,
                Color.BLUE,
                Color.ORANGE));
        return colorToCellMap;
    }


}
