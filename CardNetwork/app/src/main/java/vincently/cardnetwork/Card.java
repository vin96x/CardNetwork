package vincently.cardnetwork;

public class Card
{
    private Suit suit;
    private Value value;

    public Card (Value v, Suit s){
        value = v;
        if(value!=Value.JOKER1 && value!=Value.JOKER2)
            suit = s;
    }

    public Card (int c){
        if(c==52)
            value = Value.JOKER1;
        else if(c==53)
            value = Value.JOKER2;
        else {
            value = Value.lookup(c/4+2);
            suit = Suit.lookup(c%4);
        }
    }

    public int getNumber(){
        if(value == Value.JOKER1)
            return 52;
        else if(value == Value.JOKER2)
            return 53;
        else
            return (value.getValue()-2)*4 + suit.getSuit();
    }

    public Suit getSuit()
    {
        return suit;
    }

    public Value getValue()
    {
        return value;
    }

    public String toString(){
        if(value==Value.JOKER1 || value==Value.JOKER2)
            return value.toString();
        else
            return value.toString() + suit.toString();
    }

    public boolean equals(Object o){
        Card other = (Card) o;
        if(value == other.value && suit == other.suit)
            return true;
        else
            return false;
    }

    public static int convert(String s){
        int val = -1;
        int suit = -1;
        val = Value.lookup2(s.substring(0,s.length()-1)).getValue() - 2;
        suit = Suit.lookup2(s.substring(s.length()-1)).getSuit();
        return val*4+suit;
    }

    public static String unicode_to(String card){
        switch(card){
            case "AS": return "\uD83C\uDCA1";
            case "2S": return "\uD83C\uDCA2";
            case "3S": return "\uD83C\uDCA3";
            case "4S": return "\uD83C\uDCA4";
            case "5S": return "\uD83C\uDCA5";
            case "6S": return "\uD83C\uDCA6";
            case "7S": return "\uD83C\uDCA7";
            case "8S": return "\uD83C\uDCA8";
            case "9S": return "\uD83C\uDCA9";
            case "10S": return "\uD83C\uDCAA";
            case "JS": return "\uD83C\uDCAB";
            case "QS": return "\uD83C\uDCAD";
            case "KS": return "\uD83C\uDCAE";

            case "AH": return "\uD83C\uDCB1";
            case "2H": return "\uD83C\uDCB2";
            case "3H": return "\uD83C\uDCB3";
            case "4H": return "\uD83C\uDCB4";
            case "5H": return "\uD83C\uDCB5";
            case "6H": return "\uD83C\uDCB6";
            case "7H": return "\uD83C\uDCB7";
            case "8H": return "\uD83C\uDCB8";
            case "9H": return "\uD83C\uDCB9";
            case "10H": return "\uD83C\uDCBA";
            case "JH": return "\uD83C\uDCBB";
            case "QH": return "\uD83C\uDCBD";
            case "KH": return "\uD83C\uDCBE";

            case "AD": return "\uD83C\uDCC1";
            case "2D": return "\uD83C\uDCC2";
            case "3D": return "\uD83C\uDCC3";
            case "4D": return "\uD83C\uDCC4";
            case "5D": return "\uD83C\uDCC5";
            case "6D": return "\uD83C\uDCC6";
            case "7D": return "\uD83C\uDCC7";
            case "8D": return "\uD83C\uDCC8";
            case "9D": return "\uD83C\uDCC9";
            case "10D": return "\uD83C\uDCCA";
            case "JD": return "\uD83C\uDCCB";
            case "QD": return "\uD83C\uDCCD";
            case "KD": return "\uD83C\uDCCE";

            case "AC": return "\uD83C\uDCD1";
            case "2C": return "\uD83C\uDCD2";
            case "3C": return "\uD83C\uDCD3";
            case "4C": return "\uD83C\uDCD4";
            case "5C": return "\uD83C\uDCD5";
            case "6C": return "\uD83C\uDCD6";
            case "7C": return "\uD83C\uDCD7";
            case "8C": return "\uD83C\uDCD8";
            case "9C": return "\uD83C\uDCD9";
            case "10C": return "\uD83C\uDCDA";
            case "JC": return "\uD83C\uDCDB";
            case "QC": return "\uD83C\uDCDD";
            case "KC": return "\uD83C\uDCDE";

            case "JOKER1" : return "\uD83C\uDCCF";
            case "JOKER2" : return "\uD83C\uDCDF";
            default: return null;
        }
    }

    public static String unicode_from(String unicode){
        switch(unicode){
            case "\uD83C\uDCA1": return "AS";
            case "\uD83C\uDCA2": return "2S";
            case "\uD83C\uDCA3": return "3S";
            case "\uD83C\uDCA4": return "4S";
            case "\uD83C\uDCA5": return "5S";
            case "\uD83C\uDCA6": return "6S";
            case "\uD83C\uDCA7": return "7S";
            case "\uD83C\uDCA8": return "8S";
            case "\uD83C\uDCA9": return "9S";
            case "\uD83C\uDCAA": return "10S";
            case "\uD83C\uDCAB": return "JS";
            case "\uD83C\uDCAD": return "QS";
            case "\uD83C\uDCAE": return "KS";

            case "\uD83C\uDCB1": return "AH";
            case "\uD83C\uDCB2": return "2H";
            case "\uD83C\uDCB3": return "3H";
            case "\uD83C\uDCB4": return "4H";
            case "\uD83C\uDCB5": return "5H";
            case "\uD83C\uDCB6": return "6H";
            case "\uD83C\uDCB7": return "7H";
            case "\uD83C\uDCB8": return "8H";
            case "\uD83C\uDCB9": return "9H";
            case "\uD83C\uDCBA": return "10H";
            case "\uD83C\uDCBB": return "JH";
            case "\uD83C\uDCBD": return "QH";
            case "\uD83C\uDCBE": return "KH";

            case "\uD83C\uDCC1": return "AD";
            case "\uD83C\uDCC2": return "2D";
            case "\uD83C\uDCC3": return "3D";
            case "\uD83C\uDCC4": return "4D";
            case "\uD83C\uDCC5": return "5D";
            case "\uD83C\uDCC6": return "6D";
            case "\uD83C\uDCC7": return "7D";
            case "\uD83C\uDCC8": return "8D";
            case "\uD83C\uDCC9": return "9D";
            case "\uD83C\uDCCA": return "10D";
            case "\uD83C\uDCCB": return "JD";
            case "\uD83C\uDCCD": return "QD";
            case "\uD83C\uDCCE": return "KD";

            case "\uD83C\uDCD1": return "AC";
            case "\uD83C\uDCD2": return "2C";
            case "\uD83C\uDCD3": return "3C";
            case "\uD83C\uDCD4": return "4C";
            case "\uD83C\uDCD5": return "5C";
            case "\uD83C\uDCD6": return "6C";
            case "\uD83C\uDCD7": return "7C";
            case "\uD83C\uDCD8": return "8C";
            case "\uD83C\uDCD9": return "9C";
            case "\uD83C\uDCDA": return "10C";
            case "\uD83C\uDCDB": return "JC";
            case "\uD83C\uDCDD": return "QC";
            case "\uD83C\uDCDE": return "KC";

            case "\uD83C\uDCCF" : return "JOKER1";
            case "\uD83C\uDCDF" : return "JOKER2";
            default: return null;
        }
    }
}
