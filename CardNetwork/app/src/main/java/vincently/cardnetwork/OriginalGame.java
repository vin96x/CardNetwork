package vincently.cardnetwork;

import java.util.ArrayList;

public class OriginalGame extends CardGame{

    String msg;
    int startCards;
    boolean hands_made;
    int[] diamonds;
    int round;
    int played;

    public OriginalGame(){
        super(2);
        startCards = 3;
        makeHands(players,startCards,false);
        turn = 1;
        hands_made = false;
        played = -1;
        diamonds = new int[2];
        round = 1;
        msg="";
    }

    public void input(int n){
        played = n;
    }

    public boolean over(){
        if(round >= 4)
            return true;
        return false;
    }

    public void run(int player){
        if(ConnectionData.isHost){
            makeHands(players, startCards, false);
            sort();
            byte[] data = new byte[52];
            int count = 0;
            for(Card c:hands.get(0)) {
                data[count] = (Integer.valueOf(c.getNumber())).byteValue();
                count++;
            }
            for(Card c2:hands.get(1)) {
                data[count] = (Integer.valueOf(c2.getNumber())).byteValue();
                count++;
            }
            for(Card c3:pile){
                data[count] = (Integer.valueOf(c3.getNumber())).byteValue();
                count++;
            }
            ConnectionData.write(data);
            hands_made = true;
        }
        while(!hands_made){
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {}
        }
        msg="";
        while(!over()) {
            played = -1;
            while(played==-1){
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {}
            }
            msg ="";
            move();
            sort();
            turn = 0;
            played = -1;
            while(played==-1){
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {}
            }
            move();
            sort();
            turn = 1;
            round++;
        }
        msg = "";
        score();
        done = true;
    }

    public void move(){
        int tmp;
        int other = (turn+1)%2;
        CardHand player = hands.get(turn);
        CardHand opponent = hands.get(other);
        switch(played){
            case 0:
                msg+="Player " + (turn+1) + " traded in a diamond!\n";
                msg+="Player " + (turn+1) + " picked up 3 cards!\n";
                for(tmp = 0;tmp < player.size();tmp++){
                    if(player.get(tmp).getSuit()==Suit.DIAMONDS){
                        break;
                    }
                }
                player.remove(tmp);
                player.add(pile.remove(0));
                player.add(pile.remove(0));
                player.add(pile.remove(0));
                break;
            case 1:
                msg+="Player " + (turn+1) + " attacks with a club!\n";
                for(tmp = 0;tmp < player.size();tmp++){
                    if(player.get(tmp).getSuit()==Suit.CLUBS){
                        break;
                    }
                }
                player.remove(tmp);
                boolean defend = false;
                for(tmp = 0;tmp < opponent.size();tmp++){
                    if(opponent.get(tmp).getSuit()==Suit.HEARTS){
                        defend = true;
                        break;
                    }
                }
                if(defend){
                    msg+="Player " + (other+1) + " defended with a heart!\n";
                    opponent.remove(tmp);
                }
                else{
                    boolean loot = false;
                    for(tmp = 0;tmp < opponent.size();tmp++){
                        if(opponent.get(tmp).getSuit()==Suit.DIAMONDS){
                            loot = true;
                            break;
                        }
                    }
                    if(loot){
                        msg+="Player " + (turn+1) + " looted a diamond from Player " + (other+1) + "!\n";
                        player.add(opponent.remove(tmp));
                    }
                    else{
                        msg+="Player " + (other+1) + " had no diamonds for Player " + (other+1) + " to take!\n";
                    }
                }
                break;
            case 2:
                msg+="Player " + (turn+1) + " traded in a club!\n";
                msg+="Player " + (turn+1) + " picked up 1 card!\n";
                for(tmp = 0;tmp < player.size();tmp++){
                    if(player.get(tmp).getSuit()==Suit.CLUBS){
                        break;
                    }
                }
                player.remove(tmp);
                player.add(pile.remove(0));
                break;
            case 3:
                msg+="Player " + (turn+1) + " shoveled with the spade!\n";
                msg+="Player " + (turn+1) + " picked up 2 cards!\n";
                for(tmp = 0;tmp < player.size();tmp++){
                    if(player.get(tmp).getSuit()==Suit.SPADES){
                        break;
                    }
                }
                player.remove(tmp);
                player.add(pile.remove(0));
                player.add(pile.remove(0));
                break;
            case 4:
                break;
            default: System.out.println(played);
        }
    }

    public void sort(){
        for(CardHand ch:hands) {
            ch.sort2();
        }
    }

    public void updateMoves(ArrayList<String> l, int p){
        l.clear();
        CardHand ch = hands.get(p);
        for(Card c:ch){
            if(c.getSuit()==Suit.DIAMONDS){
                l.add("Trade a Diamond (Get 3 Cards)");
                break;
            }
        }
        for(Card c:ch){
            if(c.getSuit()==Suit.CLUBS){
                l.add("Attack with Club");
                l.add("Trade in Club (Get 1 Card)");
                break;
            }
        }
        for(Card c:ch){
            if(c.getSuit()==Suit.SPADES){
                l.add("Shovel with Spade (Get 2 Cards)");
                break;
            }
        }
        l.add("Pass");
    }

    public void score(){
        for(Card c1:hands.get(0)){
            if(c1.getSuit()==Suit.DIAMONDS)
                diamonds[0]++;
        }
        for(Card c2:hands.get(1)){
            if(c2.getSuit()==Suit.DIAMONDS)
                diamonds[1]++;
        }
    }

    public int getPoints(int player){
        return diamonds[player];
    }

    public void appendMsg(String s){
        msg+=s;
        System.out.println(msg);
    }

    public void madeHands(){
        hands_made = true;
    }

    public String getMsg(){
        return msg;
    }

    public int getStartCards(){
        return startCards;
    }

    public int getRound(){
        return round;
    }
}
