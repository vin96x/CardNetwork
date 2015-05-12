package vincently.cardnetwork;

import java.util.ArrayList;

public class CardHand extends ArrayList<Card>{

    public CardHand(){
        super();
    }

    public boolean containsSuit(Suit s){
        for(Card c:this){
            if(c.getSuit()==s)
                return true;
        }
        return false;
    }

    public void sort(){
        int index;
        Card tmp;
        Card small;
        for(int i = 0;i<size()-1;i++){
            index = i;
            small = get(index);
            for(int j = i+1;j<size();j++){
                if(get(j).getNumber()<small.getNumber()){
                    index = j;
                    small = get(j);
                }
            }
            tmp = get(i);
            set(i, small);
            set(index, tmp);
        }
    }

    public void sort2(){
        int index;
        Card tmp;
        Card small;
        for(int i = 0;i<size()-1;i++){
            index = i;
            small = get(index);
            for(int j = i+1;j<size();j++){
                if(get(j).getSuit().getSuit()<small.getSuit().getSuit() || (get(j).getSuit().getSuit()==small.getSuit().getSuit() && get(j).getValue().getValue()<small.getValue().getValue())){
                    index = j;
                    small = get(j);
                }
            }
            tmp = get(i);
            set(i, small);
            set(index, tmp);
        }
    }
}
