package sapper;

public class BotMap {

    private Matrix botMap;
    private int countOfClosedBoxes;

    //инициализация поля бота
    void init() {
        botMap = new Matrix(Cells.CLOSED);
        countOfClosedBoxes = Ranges.getSize().x * Ranges.getSize().y;
    }

    //получение клетки поля бота по координатам
    public Cells get (Coord coord) {
        return  botMap.get(coord);
    }

    //установка(открытие) клетки на поле бота по координатам
    public void setCell(Coord coord, Cells cell) {
        botMap.set(coord, cell);
        countOfClosedBoxes--;
    }

    //установка флага на поле бота по координатам
    public void setFlaggedCell(Coord coord) {
        botMap.set(coord, Cells.FLAGED);
    }

    //получение кол-ва всех закрытых клеток
    int getCountOfClosedCells() {
        return countOfClosedBoxes;
    }

    //открытие бомбы на поле бота по координатам
    void setBombedCell(Coord coord) {
        botMap.set (coord, Cells.BOMBED);
    }
}
