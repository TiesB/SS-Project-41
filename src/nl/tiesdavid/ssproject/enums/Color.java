package nl.tiesdavid.ssproject.enums;

/**
 * Created by Ties on 19-12-2015.
 */
public enum Color {
    BLUE,
    GREEN,
    ORANGE,
    PURPLE,
    RED,
    YELLOW;

    @Override
    public String toString() {
        switch (this)  {
            case BLUE:
                return "B";
            case GREEN:
                return "G";
            case ORANGE:
                return "O";
            case PURPLE:
                return "P";
            case RED:
                return "R";
            case YELLOW:
                return "Y";
            default:
                return ".";
        }
    }
}
