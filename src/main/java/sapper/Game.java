package sapper;

import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;


public class Game {

    private final Bomb bomb;
    private GameState state;

    //возвращает состояние игры
    public GameState getState() {
        return state;
    }

    BorderPane borderPane;

    //инициализация игры
    public Game (int cols, int rows, int bombs, BorderPane border) {
        Ranges.setSize(new Coord(cols, rows));
        bomb = new Bomb(bombs);
        borderPane = border;
    }

    //запуск инициализации поля, установка состояния игры
    public void start () {
        bomb.init();
        state = GameState.PLAYED;
    }

    //открытие клетки и последующая проверка состояния игры пока игра не закончена
    public void open(Coord coord, BotMap botMap, GridPane grid){
        if (gameOver()) {
            return;
        }
        openCell(botMap, coord);
        checkWin(botMap);
    }

    //возврат значения того, что хранится в закрытой клетке
    public Cells getCell (Coord coord) {
        return bomb.get(coord);
    }

    //открытие клетки и обработка, в зависимости от того, что хранилось в закрытой клетке. Проверка состояния игры
    private void openCell(BotMap botMap, Coord coord) {
        switch (botMap.get(coord))
        {
            case OPENED : return;
            case FLAGED : return;
            case CLOSED :
                switch (bomb.get(coord)) {
                    case ZERO : {
                        openCellsAround(botMap, coord);
                    } return;
                    case BOMB : openBombs(coord, botMap); return;
                    default : botMap.setCell(coord, getCell(coord)); return;
                }
        }
        checkWin(botMap);
    }

    //обработка открытия клетки, если там оказалась бомба
    private void openBombs(Coord bombed, BotMap botMap) {
        state = GameState.BOMBED;
        botMap.setBombedCell(bombed);
    }

    //обработка открытия клетки, если там оказалась пустота
    private void openCellsAround(BotMap botMap, Coord coord) {
        botMap.setCell(coord, getCell(coord));
        for (Coord around : Ranges.getCoordsAround(coord))
            openCell(botMap, around);
    }

    //установка флага
    public void setFlag (Coord coord, BotMap botMap, GridPane grid) {
        botMap.setFlaggedCell (coord);
    }

    //проверка выигрыша (если кол-во закрытых клеток совпадает с общим количеством бомб)
    private void checkWin (BotMap botMap) {
        if (state == GameState.PLAYED)
            if (botMap.getCountOfClosedCells() == bomb.getTotalBombs())
                state = GameState.WINNER;
    }

    //возвращает true, если игра окончена
    private  boolean gameOver() {
        return state != GameState.PLAYED;
    }
}
