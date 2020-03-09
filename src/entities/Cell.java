package entities;

public enum Cell {
    FULL, EMPTY, UNKOWN;

    public String toString() {
        if (this == FULL)
            return "\u2588";
        else if (this == EMPTY)
            return " ";
        else
            return "?";
    }

    public Cell rev() {
        if (this == FULL)
            return EMPTY;
        else if (this == EMPTY)
            return FULL;
        return this;
    }
}
