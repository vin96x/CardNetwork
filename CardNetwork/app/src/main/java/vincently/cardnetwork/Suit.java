package vincently.cardnetwork;

/**
 * Created by VincentLy on 3/28/2015.
 */
public enum Suit {
    CLUBS(0),
    DIAMONDS(1),
    HEARTS(2),
    SPADES(3);

    private int suitValue;

    private Suit (int suit){
        suitValue = suit;
    }

    public int getSuit() {
        return suitValue;
    }

    public static Suit lookup(int c){
        switch(c){
            case 0: return CLUBS;
            case 1: return DIAMONDS;
            case 2: return HEARTS;
            case 3: return SPADES;
            default: return null;
        }
    }

    public static Suit lookup2(String suit){
        switch(suit){
            case "C": return CLUBS;
            case "D": return DIAMONDS;
            case "H": return HEARTS;
            case "S": return SPADES;
            default: return null;
        }
    }

    public String toString(){
        switch(this){
            case CLUBS: return "C";
            case DIAMONDS: return "D";
            case HEARTS: return "H";
            case SPADES: return "S";
            default: return "";
        }
    }
}
