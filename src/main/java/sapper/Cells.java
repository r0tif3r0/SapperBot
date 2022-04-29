package sapper;

//возможные значения клеток на поле
public enum Cells {
    ZERO,
    NUM1,
    NUM2,
    NUM3,
    NUM4,
    NUM5,
    NUM6,
    NUM7,
    NUM8,
    BOMB,
    OPENED,
    CLOSED,
    FLAGED,
    BOMBED,
    NOBOMB;

    //получить следующий номер из списка значений клеток
    Cells getNextNum () {
        return Cells.values() [this.ordinal() + 1];
    }

}
