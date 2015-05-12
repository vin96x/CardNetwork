package vincently.cardnetwork;

public enum Value {
    JOKER1(0),
    JOKER2(1),
    TWO(2),
    THREE(3),
    FOUR(4),
    FIVE(5),
    SIX(6),
    SEVEN(7),
    EIGHT(8),
    NINE(9),
    TEN(10),
    JACK(11),
    QUEEN(12),
    KING(13),
    ACE(14);

    private int cardValue;

    private Value (int value){
        this.cardValue = value;
    }

    public int getValue() {
        return cardValue;
    }

    public static Value lookup(int c){
        switch(c){
            case 0: return JOKER1;
            case 1: return JOKER2;
            case 2: return TWO;
            case 3: return THREE;
            case 4: return FOUR;
            case 5: return FIVE;
            case 6: return SIX;
            case 7: return SEVEN;
            case 8: return EIGHT;
            case 9: return NINE;
            case 10: return TEN;
            case 11: return JACK;
            case 12: return QUEEN;
            case 13: return KING;
            case 14: return ACE;
            default: return null;
        }
    }

    public static Value lookup2(String rank){
        switch(rank){
            case "Joker1": return JOKER1;
            case "Joker2": return JOKER2;
            case "2": return TWO;
            case "3": return THREE;
            case "4": return FOUR;
            case "5": return FIVE;
            case "6": return SIX;
            case "7": return SEVEN;
            case "8": return EIGHT;
            case "9": return NINE;
            case "10": return TEN;
            case "J": return JACK;
            case "Q": return QUEEN;
            case "K": return KING;
            case "A": return ACE;
            default: return null;
        }
    }

    public String toString(){
        switch(this){
            case JOKER1: return "Joker1";
            case JOKER2: return "Joker2";
            case TWO: return "2";
            case THREE: return "3";
            case FOUR: return "4";
            case FIVE: return "5";
            case SIX: return "6";
            case SEVEN: return "7";
            case EIGHT: return "8";
            case NINE: return "9";
            case TEN: return "10";
            case JACK: return "J";
            case QUEEN: return "Q";
            case KING: return "K";
            case ACE: return "A";
            default: return "";
        }
    }
}
