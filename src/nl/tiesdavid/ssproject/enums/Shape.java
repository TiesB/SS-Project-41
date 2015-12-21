package nl.tiesdavid.ssproject.enums;

/**
 * Created by Ties on 19-12-2015.
 */
public enum Shape {
    CIRCLE,
    DIAMOND,
    PLUS,
    STAR,
    SQUARE,
    X;

    @Override
    public String toString() {
        switch (this) {
            case CIRCLE:
                return "O";
            case DIAMOND:
                return "#";
            case PLUS:
                return "+";
            case STAR:
                return "*";
            case SQUARE:
                return "@";
            case X:
                return "X";
            default:
                return ".";
        }
    }
}
