package entities;

public class Pointer {
    public final int index;
    public final Dir direction;

    public Pointer(int index, Dir direction) {
        this.index = index;
        this.direction = direction;
    }

    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null)
            return false;
        if (!(o instanceof Pointer))
            return false;
        Pointer p = (Pointer) o;
        return p.index == index && p.direction == direction;
    }

    public String toString() {
        return direction + " " + String.format("%02d", index);
    }
}
