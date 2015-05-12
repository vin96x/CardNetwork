package vincently.cardnetwork;

import java.util.ArrayList;

public class GoFishGame extends CardGame {

    ArrayList<ArrayList<Value>> books;
    int[] points;
    Value rank;
    int asked;
    int startCards;
    String msg;

    public GoFishGame(int p){
        super(p);
        if(players==2)
            startCards = 7;
        else
            startCards = 5;
        makeHands(players, startCards, false);
        turn = 1;
        points = new int[p];
        for(int i=0;i<points.length;i++)
            points[i]=0;
        rank = null;
        asked = -1;
        books = new ArrayList<ArrayList<Value>>();
        for(int i=0;i<players;i++){
            books.add(new ArrayList<Value>());
        }
        sort();
        msg="";
    }

    public void input(Value r, int a){
        rank = r;
        asked = a;
    }

    public boolean over(){
        boolean over = false;
        for(CardHand ch:hands)
            if(ch.isEmpty())
                over = true;
        if(pile.isEmpty())
            over = true;
        return over;
    }

    public CardHand fish(CardHand ch, Value v){
        CardHand tmp = new CardHand();
        for(Card c:ch) {
            if(c.getValue()==v){
                tmp.add(c);
            }
        }
        for(Card cd:tmp){
            ch.remove(cd);
        }
        return tmp;
    }

    public void bookCheck(int p,int player){
        int[] tally = new int[13];
        CardHand ch = hands.get(p);
        CardHand rem = new CardHand();
        for(Card c:ch){
            tally[c.getValue().getValue()-2] +=1;
        }
        for(int i=0;i<tally.length;i++){
            if(tally[i] == 4){
                Value tmp = Value.lookup(i+2);
                for(Suit s:Suit.values())
                    rem.add(new Card(tmp, s));
                books.get(p).add(tmp);
                if(player==p)
                    msg+="You";
                else
                    msg+="Player " + (p+1);
                msg+=" completed book " + tmp.name() + ".\n";
            }
        }
        for(Card cd:rem){
            ch.remove(cd);
        }
    }

    public void run(int player){
        for(int i = 0;i<hands.size();i++)
            bookCheck(i,player);
        sort();
        while(!over()){
            asked=-1;
            rank=null;
            while(rank==null || asked==-1){
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {}
            }
            msg="";
            CardHand ch = fish(hands.get(asked), rank);
            if(player==turn)
                msg+="You";
            else
                msg+="Player " + (turn+1);
            msg+=" asked for " + rank.name() + ".\n";
            if(ch.isEmpty()){
                msg+="Found no " + rank.name() + ".\n";
                Card c = pile.remove(0);
                hands.get(turn).add(c);
                if(c.getValue()!=rank) {
                    if(player==turn)
                        msg+="Picked up " + Card.unicode_to(c.toString()) + ".\n";
                    turn = asked;
                }
                else{
                    if(player==turn)
                        msg+="But you";
                    else
                        msg+="But Player " + (turn+1);
                    msg+=" picked up " + Card.unicode_to(c.toString()) + ".\n";
                }
            }
            else{
                hands.get(turn).addAll(ch);
                if(player==turn)
                    msg+="You retrieved ";
                else
                    msg+="You gave up ";
                for(Card tmp:ch)
                    msg+=Card.unicode_to(tmp.toString());
                msg+=".\n";
            }
            for(int i = 0;i<hands.size();i++)
                bookCheck(i,player);
            sort();
        }
        done = true;
    }

    public ArrayList<Value> getBooks(int p){
        return books.get(p);
    }

    public void updateMoves(ArrayList<String> l, int p){
        l.clear();
        CardHand ch = hands.get(p);
        for(Card c:ch){
            if(!l.contains(c.getValue().toString()))
                l.add(c.getValue().toString());
        }
    }

    public int getStartCards(){
        return startCards;
    }

    public void sort(){
        for(CardHand ch:hands)
            ch.sort2();
        int index;
        Value tmp;
        Value small;
        for(ArrayList<Value> s:books){
            for(int i = 0;i<s.size()-1;i++){
                index = i;
                small = s.get(index);
                for(int j = i+1;j<s.size();j++){
                    if(s.get(j).getValue()<small.getValue()){
                        index = j;
                        small = s.get(j);
                    }
                }
                tmp = s.get(i);
                s.set(i, small);
                s.set(index, tmp);
            }
        }
    }

    public String getMsg(){
        return msg;
    }
}
