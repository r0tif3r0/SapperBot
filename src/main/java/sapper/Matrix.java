package sapper;

//матрица полей игры
class Matrix {
    private final Cells [][] matrix;

    //инициализация матрицы
    Matrix (Cells defCells)
    {
        matrix = new Cells[Ranges.getSize().x][Ranges.getSize().y];
        for (Coord coord : Ranges.getAllCoords())
            matrix [coord.x][coord.y] = defCells;
    }

    //получение клетки поля по координатам матрицы
    Cells get (Coord coord) {
        if (Ranges.inRange (coord))
            return matrix [coord.x][coord.y];
        return null;
    }

    //установка клетки на поле по координатам матрицы
    void set (Coord coord, Cells cell) {
        if (Ranges.inRange (coord))
            matrix [coord.x][coord.y] = cell;
    }
}
