package sapper;

class Bomb {

    private Matrix bombMap;
    private int totalBombs;

    //установка кол-ва бомб
    Bomb (int totalBombs) {
        this.totalBombs = totalBombs;
        fixBombsCount();
    }

    //инициализация поля с бомбами
    void init () {
        bombMap = new Matrix(Cells.ZERO);
        for (int i = 0; i < totalBombs; i++)
            placeBomb();
    }

    //возвращает то, что хранится в клетке поля по координате
    Cells get (Coord coord) {
        return bombMap.get(coord);
    }

    //фиксация количества бомб, если задано слишком много бомб
    private void fixBombsCount () {
        int maxBombs = Ranges.getSize().x * Ranges.getSize().y / 2;
        if (totalBombs > maxBombs)
            totalBombs = maxBombs;
    }

    //расположение бомб по полю и установка цифр вокруг
    private void placeBomb () {
        while (true) {
            Coord coord = Ranges.getRandCoord();
            if (Cells.BOMB == bombMap.get(coord))
                continue;
            bombMap.set(coord ,Cells.BOMB);
            incNumAroundBomb(coord);
            break;
        }
    }

    //вычисление цифр, которые должны расположиться вокруг бомб
    private void incNumAroundBomb (Coord coord) {
        for (Coord around : Ranges.getCoordsAround(coord))
            if (Cells.BOMB != bombMap.get(around))
                bombMap.set(around, bombMap.get(around).getNextNum());
    }

    //получение общего числа бомб
    int getTotalBombs() {
        return totalBombs;
    }
}
