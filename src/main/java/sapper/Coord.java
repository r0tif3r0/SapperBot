package sapper;

public class Coord {
    public int x;
    public int y;

    //координаты поля
    public Coord (int x, int y) {
        this.x = x;
        this.y = y;
    }

    //переопределения функции сравнения координат
    @Override
    public boolean equals(Object obj) {
        if (obj.getClass().equals(this.getClass())) {
            Coord coord = (Coord) obj;
            return coord.x == this.x && coord.y == this.y;
        }
        return false;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 97 * hash + this.x;
        hash = 97 * hash + this.y;
        return hash;
    }

    //переопределение функции преобразования в строку для координат
    @Override
    public String toString(){
        return "x: " + x + " | y: " + y;
    }
}
