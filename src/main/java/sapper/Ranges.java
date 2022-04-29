package sapper;

import java.util.ArrayList;
import java.util.Random;

public class Ranges {

    private static Coord size;
    private static ArrayList<Coord> allCoords;
    private static final Random rand = new Random();

    //установка размеров поля по x и y
    public static void setSize (Coord _size) {
        size = _size;
        allCoords = new ArrayList<>();
        for (int y = 0; y < size.y; y++)
            for (int x = 0; x < size.x; x++)
                allCoords.add(new Coord(x,y));
    }

    //получение размера поля
    public static Coord getSize() {
        return size;
    }

    //получение всех координат поля
    public static ArrayList<Coord> getAllCoords () {
        return allCoords;
    }

    //проверка нахождения координаты в пределах поля
    static boolean inRange (Coord coord) {
        return coord.x >= 0 && coord.x < size.x && coord.y >= 0 && coord.y < size.y;
    }

    //получаения случайной координаты
    static Coord getRandCoord() {
        return new Coord(rand.nextInt(size.x),rand.nextInt(size.y));
    }

    //получение всех соседних координат вокруг заданной
    static ArrayList<Coord> getCoordsAround(Coord coord) {
        Coord around;
        ArrayList<Coord> list = new ArrayList<>();
        for (int x = coord.x - 1; x <= coord.x + 1; x++)
            for (int y = coord.y - 1; y <= coord.y + 1; y++)
                if (inRange(around = new Coord(x,y)))
                    if (!around.equals(coord))
                        list.add(around);
        return list;
    }

}
