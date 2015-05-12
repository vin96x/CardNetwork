package vincently.cardnetwork;

import java.util.ArrayList;

public class CardGame {

    ArrayList<CardHand> hands;
    CardHand pile;
    int turn;
    int players;
    boolean done;

    public CardGame(int p){
        hands = new ArrayList<CardHand>();
        players = p;
        done = false;
    }

    public void run(){

    }

    public void sort(){

    }

    public void makeHands(int players, int size, boolean jokers){
        ArrayList<CardHand> tmp = new ArrayList<CardHand>();
        CardHand fd = fullDeck(jokers);
        for(int i = 0;i<players;i++) {
            CardHand tmp_hand = new CardHand();
            for(int j = 0;j<size;j++){
                tmp_hand.add(fd.remove((int) (Math.random()*fd.size())));
            }
            tmp.add(tmp_hand);
        }
        hands = tmp;
        pile = shuffle(fd);
    }

    public CardHand shuffle(CardHand ch){
        CardHand shffl = new CardHand();
        int t = ch.size();
        for(int i = 0;i<t;i++){
            Card tmp = ch.remove((int) (Math.random()*ch.size()));
            shffl.add(tmp);
        }
        return shffl;
    }

    public CardHand fullDeck(boolean jokers){
        CardHand fd = new CardHand();
        if(jokers){
            fd.add(new Card(Value.JOKER1,null));
            fd.add(new Card(Value.JOKER2,null));
        }
        for(Value v:Value.values()) {
            if(v!=Value.JOKER1 && v!=Value.JOKER2)
                for (Suit s : Suit.values()){
                    fd.add(new Card(v,s));
                }
        }
        return fd;
    }

    public void setPile(CardHand ch) {
        pile = ch;
    }

    public CardHand getPile(){
        return pile;
    }

    public int getTurn(){
        return turn;
    }

    public CardHand getHand(int p){
        return hands.get(p);
    }

    public void setHand(CardHand ch, int p) {
        hands.set(p, ch);
    }

    public boolean isDone(){
        return done;
    }
}
