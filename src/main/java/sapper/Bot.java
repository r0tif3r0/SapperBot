package sapper;

import javafx.application.Platform;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class Bot extends Thread {
    Game game;
    GridPane gridPane;
    private final BotMap botMap;
    BorderPane borderPane;

    //инициализация бота
    public Bot (BorderPane borderPane, GridPane grid, Game game) {
        gridPane = grid;
        this.game = game;
        botMap = new BotMap();
        this.borderPane = borderPane;
    }

    //сервис, позволяющий перерисовывать поле в режиме реального времени
    Service<Void> service = new Service<Void>() {
        @Override
        protected Task createTask() {
            return new Task() {
                @Override
                protected Void call() throws Exception {
                    Platform.runLater(Bot.this::repaint);
                    return null;
                }
            };
        }
    };

    //сервис, позволяющий анализировать поле в режиме реального времени
    Service<Void> service1 = new Service<Void>() {
        @Override
        protected Task createTask() {
            return new Task() {
                @Override
                protected Void call() throws Exception {
                    Platform.runLater(Bot.this::analyze);
                    return null;
                }
            };
        }
    };

    //перерисовка поля
    public void repaint() {
        for (Coord coord : Ranges.getAllCoords()) {
                gridPane.add(getImage(botMap, coord), coord.x, coord.y);
        }
        System.out.println("repainting");
    }

    //получение картинки по координате поля для вывода на экран
    private ImageView getImage (BotMap botMap, Coord coord){
        String img = botMap.get(coord).toString().toLowerCase();
        Image image = new Image("/images/" + img + ".png");
        return new ImageView(image);
    }


    //начало работы бота, инициализация поля бота, запуск сервисов, отвечающий за анализ и перерисовку,
    // открытие первой случайной клетки
    public void startBot() {
        botMap.init();
        service.start();
        openRandCell();
        service1.start();
    }

    //поиск случайной клетки поля, при условии, что она была закрыта
    private void openRandCell() {
        while (true) {
            Coord randCoord = Ranges.getRandCoord();
            System.out.println(botMap.get(randCoord));
            if (botMap.get(randCoord) != Cells.CLOSED)
                continue;
            System.out.println("OPENING RANDOM CELL: " + randCoord);
            openCell(randCoord);
            break;
        }
    }

    //открытие клетки поля бота по координате
    public void openCell (Coord coord) {
        if (game.getState() ==  GameState.BOMBED)
            return;
        game.open(coord, botMap, gridPane);
        service.restart();
        System.out.println("OPENING CELL: " + coord);
    }

    //установка флага на поле бота по координате
    public void setFlag (Coord coord) {
        game.setFlag(coord, botMap, gridPane);
        service.restart();
        System.out.println("FLAGGING CELL: " + coord);
    }

    //анализ имеющегося поля бота, если состояние не игровое, то остановка сервисов перерисовки и анализа
    public void analyze() {
        if (game.getState() !=  GameState.PLAYED) {
            service.cancel();
            service1.cancel();
            return;
        }
        // первый алгоритм
        first();
        // второй алгоритм
        second();
        // третий алгоритм
        third();
    }

    //переменная, показывающая изменение поля бота, во время работы алгоритмов
    boolean isChanged = false;

    /*
    Первый алгоритм

    Этот алгоритм пробегает по всем открытым клеткам поля бота с цифрами и проверяет:
    - если кол-во неоткрытых соседних клеток равняется номеру этой клетки, то бот ставит флаги
    на все эти неоткрытые клетки
    - если кол-во флагов на соседних клетках совпадает с номером этой клетки, то бот открывает
    все эти неоткрытые клетки

    После каждого такого действия первый алгоритм заканчивается, и переменная, отвечающая за
    изменения поля бота устанавливается в true

    Также в начале добавлена искусственная задержка 0.4 с для наглядности работы бота
     */
    private void first () {
        isChanged = false;
        System.out.println("DOING FIRST");
        try {
            sleep(400);
        } catch (InterruptedException ex){}
        for (Coord coord : Ranges.getAllCoords()) {
            switch (botMap.get(coord)) {
                case NUM1, NUM2, NUM3, NUM4, NUM5, NUM6, NUM7, NUM8 -> {
                    ArrayList<Coord> closedInd = new ArrayList<>();
                    int flagsNum = 0;
                    for (Coord around : Ranges.getCoordsAround(coord)) {
                        if (botMap.get(around) == Cells.CLOSED || botMap.get(around) == Cells.FLAGED) {
                            closedInd.add(around);
                            if (botMap.get(around) == Cells.FLAGED) {
                                flagsNum++;
                            }
                        }
                    }
                    switch (botMap.get(coord)) {
                        case NUM1 -> {
                            System.out.println("Fist Num1" + " " + coord + " " + closedInd.size() + " " + flagsNum);
                            if (closedInd.size() == 1 && flagsNum == 0) {
                                for (Coord closed : closedInd) {
                                    setFlag(closed);
                                    isChanged = true;
                                }
                                return;
                            } else if (flagsNum == 1) {
                                for (Coord closed : closedInd) {
                                    if (botMap.get(closed) == Cells.CLOSED) {
                                        openCell(closed);
                                        isChanged = true;
                                        return;
                                    }
                                }
                            }
                        }
                        case NUM2 -> {
                            System.out.println("Fist Num2");
                            if (closedInd.size() == 2 && flagsNum < 2) {
                                for (Coord closed : closedInd) {
                                    setFlag(closed);
                                    isChanged = true;
                                }
                                return;
                            }
                            else {
                                if (flagsNum == 2) {
                                    for (Coord closed : closedInd) {
                                        if (botMap.get(closed) == Cells.CLOSED) {
                                            openCell(closed);
                                            isChanged = true;
                                            return;
                                        }
                                    }
                                }
                            }
                        }
                        case NUM3 -> {
                            System.out.println("Fist Num3");
                            if (closedInd.size() == 3 && flagsNum < 3) {
                                for (Coord closed : closedInd) {
                                    setFlag(closed);
                                    isChanged = true;
                                }
                                return;
                            }
                            else {
                                if (flagsNum == 3) {
                                    for (Coord closed : closedInd) {
                                        if (botMap.get(closed) == Cells.CLOSED) {
                                            openCell(closed);
                                            isChanged = true;
                                            return;
                                        }
                                    }
                                }
                            }
                        }
                        case NUM4 -> {
                            if (closedInd.size() == 4 && flagsNum < 4) {
                                for (Coord closed : closedInd) {
                                    setFlag(closed);
                                    isChanged = true;
                                }
                                return;
                            }
                            else {
                                if (flagsNum == 4)
                                    for (Coord closed : closedInd) {
                                        if (botMap.get(closed) == Cells.CLOSED) {
                                            openCell(closed);
                                            isChanged = true;
                                            return;
                                        }
                                    }
                            }
                        }
                        case NUM5 -> {
                            if (closedInd.size() == 5 && flagsNum < 5) {
                                for (Coord closed : closedInd) {
                                    setFlag(closed);
                                    isChanged = true;
                                }
                            return;
                        }
                            else {
                                if (flagsNum == 5)
                                    for (Coord closed : closedInd) {
                                        if (botMap.get(closed) == Cells.CLOSED) {
                                            openCell(closed);
                                            isChanged = true;
                                            return;
                                        }
                                    }
                            }
                        }
                        case NUM6 -> {
                            if (closedInd.size() == 6 && flagsNum < 6) {
                                for (Coord closed : closedInd) {
                                    setFlag(closed);
                                    isChanged = true;
                                }
                                return;
                            }
                            else {
                                if (flagsNum == 6)
                                    for (Coord closed : closedInd) {
                                        if (botMap.get(closed) == Cells.CLOSED) {
                                            openCell(closed);
                                            isChanged = true;
                                            return;
                                        }
                                    }
                            }
                        }
                        case NUM7 -> {
                            if (closedInd.size() == 7 && flagsNum < 7) {
                                for (Coord closed : closedInd) {
                                    setFlag(closed);
                                    isChanged = true;
                                }
                            return;
                        }
                            else {
                                if (flagsNum == 7)
                                    for (Coord closed : closedInd) {
                                        if (botMap.get(closed) == Cells.CLOSED) {
                                            openCell(closed);
                                            isChanged = true;
                                            return;
                                        }
                                    }
                            }
                        }
                        case NUM8 -> {
                            if (closedInd.size() == 8 && flagsNum < 8) {
                                for (Coord closed : closedInd) {
                                    setFlag(closed);
                                    isChanged = true;
                                }
                                return;
                            }
                        }
                    }
                }
            }
        }
    }

    /*
    Второй алгоритм

    Переход ко второду алгоритму осуществляется только в том случае, если после первого алгоритма
    поле бота осталось неизменным.

    Второй алгоритм делает следущее:
    - если множество неоткрытых соседних клеток клетки А является подмножеством неоткрытых
    соседних клеток клетки В, и клетка В не должна касаться большего чила мин, чем клетка А,
    то тогда бот открывает всех соседей клетки В, не являющихся соседями клетки А.
    И аналогичным образом идет поиск клеток, где точно есть мина и бот ставит флаг.

    После каждого такого действия первый алгоритм заканчивается, и переменная, отвечающая за
    изменения поля бота устанавливается в true
     */

    private void second() {
        System.out.println("DOING SECOND");
        if (isChanged)
            return;
        ArrayList<Coord> solvableCells = new ArrayList<>();
        //добавляем в список те номерные клетки, у которых неоткрытые клетки расположены только с одной стороны
        //(возможно попадание клеток не выполняющих данное условие, они будут отсеяны далее)
        for (Coord coord : Ranges.getAllCoords()) {
            int closedNeighbors = getCountOfClosedNeighbors(coord);
            if (closedNeighbors > 1 && closedNeighbors < 4 && coordIsNum(coord)){
                System.out.println(coord + " " + botMap.get(coord));
                solvableCells.add(coord);
            }
        }
        //поиск в списке клеток, которые стоят на поле рядом и запуск для них анализа по поиску
        //возможных решений
        for (int i = 0; i < solvableCells.size(); i++) {
            for (int j = i + 1; j < solvableCells.size(); j++) {
                System.out.println(solvableCells.get(i) + " - " + solvableCells.get(j));
                if ((Math.abs(solvableCells.get(i).x - solvableCells.get(j).x) == 1 && (solvableCells.get(i).y == solvableCells.get(j).y))
                || ((Math.abs(solvableCells.get(i).y - solvableCells.get(j).y) == 1 && (solvableCells.get(i).x == solvableCells.get(j).x)))) {
                    bombDefine(solvableCells.get(i),solvableCells.get(j));
                }
            }
        }
    }

    //поиск возможностей для открытия клетки или установки флага по второму алгоритму
    private void bombDefine(Coord coord1, Coord coord2) {
        ArrayList<Coord> list1 = new ArrayList<>();
        int cellNum1 = botMap.get(coord1).ordinal();
        int cellFlags1 = 0;
        ArrayList<Coord> list2 = new ArrayList<>();
        int cellNum2 = botMap.get(coord2).ordinal();
        int cellFlags2 = 0;

        //составление списков соседей и кол-ва флагов для двух клеток
        for (Coord around : Ranges.getCoordsAround(coord1)){
            if (botMap.get(around) == Cells.CLOSED)
                list1.add(around);
            if (botMap.get(around) == Cells.FLAGED)
                cellFlags1++;
        }
        for (Coord around : Ranges.getCoordsAround(coord2)){
            if (botMap.get(around) == Cells.CLOSED)
                list2.add(around);
            if (botMap.get(around) == Cells.FLAGED)
                cellFlags2++;
        }

        ArrayList<Coord> tmpList1 = new ArrayList<>(list1);
        ArrayList<Coord> tmpList2 = new ArrayList<>(list2);
        System.out.println();
        System.out.println(list1.size() + " " + list2.size());
        System.out.println();

        //проверка условий второго алгоритма
        if (list1.size() > list2.size()) {
            for (Coord coord : list2) {
                if (tmpList1.contains(coord))
                    tmpList1.remove(coord);
                else return;
                tmpList2.remove(coord);
            }
            //условие, при котором слудует открыть клетки
            if (tmpList2.isEmpty() && !tmpList1.isEmpty()) {
                if (cellNum1 - cellFlags1 <= cellNum2 - cellFlags2) {
                    for (Coord coord : tmpList1){
                        System.out.println("CELL_OPENED_BY_2 - " + coord);
                        openCell(coord);
                        isChanged = true;
                        return;
                    }
                }
                //условие, при котором слудует установить флаги на клетки
                else {
                    for (Coord coord : tmpList1){
                        System.out.println("CELL_FLAGGED_BY_2 - " + coord);
                        setFlag(coord);
                        isChanged = true;
                        return;
                    }
                }
            }
            //та же проверка условий второго алгоритма, только множества А и В поменяны местами
        } else if (list1.size() < list2.size()) {
            for (Coord coord : list1) {
                if (tmpList2.contains(coord))
                    tmpList2.remove(coord);
                else return;
                tmpList1.remove(coord);
            }
            //условие, при котором слудует открыть клетки
            if (tmpList1.isEmpty() && !tmpList2.isEmpty()) {
                if (cellNum2 - cellFlags2 <= cellNum1-cellFlags1) {
                    for (Coord coord : tmpList2) {
                        System.out.println("CELL_OPENED_BY_2 - " + coord);
                        openCell(coord);
                        isChanged = true;
                        return;
                    }
                }
                //условие, при котором слудует установить флаги на клетки
                else {
                    for (Coord coord : tmpList2) {
                        System.out.println("CELL_FLAGGED_BY_2 - " + coord);
                        setFlag(coord);
                        isChanged = true;
                        return;
                    }
                }
            }
        }
    }

    //проверка клекти на наличие в ней значения
    private boolean coordIsNum (Coord coord) {
        Cells compCell = botMap.get(coord);
        switch (compCell) {
            case NUM1, NUM2, NUM3, NUM4, NUM5, NUM6, NUM7, NUM8 -> {
                return true;
            }
            default -> {
                return false;
            }
        }
    }

    // получение числа всех закрытых соседних клеток данной клетки
    private int getCountOfClosedNeighbors (Coord coord) {
        int num = 0;
        for (Coord neighbor : Ranges.getCoordsAround(coord)) {
            if (botMap.get(neighbor) == Cells.CLOSED)
                num++;
        }
        return num;
    }

    // получение числа всех соседних клеток с флагом данной клетки
    private int getCountOfFlaggedNeighbors (Coord coord) {
        int num = 0;
        for (Coord neighbor : Ranges.getCoordsAround(coord)) {
            if (botMap.get(neighbor) == Cells.FLAGED)
                num++;
        }
        return num;
    }

    /*
    Третий алгоритм

    Переход к третьему алгоритму осуществляется только в том случае, если после первого и второго алгоритмов
    поле бота осталось неизменным.

    Сначала проверяется сколько бомб уже помечено флагом, и если это кол-во уже совпадает с общим кол-вом бомб на
    поле, то бот открывает все оставшиеся закрытые клетки и игра заканчивается

    Далее если у нас больше половины поля закрыто, то бот открывает случайную клетку и анализ поля начинается заново
    с первого алгоритма

    Если закрытых клеток у нас меньше половины всех клеток поля, то бот оценивает вероятность нахождения
    бомбы в закрытых клетках и открывает, то, где эта вероятность минимальна. После этого анализ начинается заново
    с первого алгоритма
     */
    private void third() {
        System.out.println("DOING THIRD");
        if (isChanged) {
            service1.restart();
            return;
        }
        int minesRemained = 10;

        //подсчет кол-ва оставшихся мин
        ArrayList<Coord> closedCells = new ArrayList<>();
        ArrayList<Coord> numCells = new ArrayList<>();
        for (Coord coord : Ranges.getAllCoords()) {
            if (coordIsNum(coord))
                numCells.add(coord);
            switch (botMap.get(coord)) {
                case FLAGED -> minesRemained--;
                case CLOSED -> closedCells.add(coord);
            }
        }

        //если мин не осталось, то открываются все закрытые клетки
        if (minesRemained == 0) {
            for (Coord closed : closedCells) {
                openCell(closed);
            }
        }

        //если закрытых клеток больше половины всего поля, то открывается случайная закрытая клетка
        if (closedCells.size() > Ranges.getSize().x*Ranges.getSize().y/2) {
            openRandCell();
            service1.restart();
        }
        //построение соответствий клеток и вероятновстей нахождения бомб в них и сортировка по возрастанию
        else {
            Map<Coord, Integer> probMap = getProb(numCells).entrySet().stream().sorted(Map.Entry.comparingByValue())
                    .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e1, LinkedHashMap::new));
            for (Coord cell : closedCells)
                if (!probMap.containsKey(cell)) {
                    probMap.put(cell, 0);
                }

            //открытие клетки с меньшей вероятность нахождения в ней бомбы и перезапуск сервиса аналитики
            for (Coord coord : probMap.keySet()) {
                openCell(coord);
                service1.restart();
                return;
            }
        }
    }

    //возвращает мапу с всеми закрытыми бомбами и числом для каждой. Чем больше число,
    // тем больше вероятность нахождения бомбы в клетке
    private Map<Coord, Integer> getProb(ArrayList<Coord> numberedCells) {
        ArrayList<Coord> cellsWithoutAllFlags = new ArrayList<>();
        //составление списка нумерованых клеток, в которых кол-во флагов не совпадают с номером
        for (Coord coord : numberedCells) {
            int cellNum = botMap.get(coord).ordinal();
            int flags = getCountOfFlaggedNeighbors(coord);
            if (cellNum != flags) {
                cellsWithoutAllFlags.add(coord);
                System.out.println("Without Flag: " + coord);
            }
        }
        Map<Coord, Integer> closedCells = new HashMap<>();
        //добавление в мапу закрытых клетов, в которых может быть бомба
        // чем больше закрытую клетму окружают клетки из списка нумерованых клеток,
        // в которых кол-во флагов не совпадают с номером,
        // тем больше число соответствует данной клетке
        for (Coord cell : cellsWithoutAllFlags) {
            for (Coord neighbor : Ranges.getCoordsAround(cell)) {
                if (botMap.get(neighbor) == Cells.CLOSED) {
                    int value = 1;
                    if (closedCells.containsKey(neighbor)) {
                        value = closedCells.get(neighbor);
                        value++;
                    }
                    closedCells.put(neighbor, value);
                }
            }
        }
        return closedCells;
    }
}
