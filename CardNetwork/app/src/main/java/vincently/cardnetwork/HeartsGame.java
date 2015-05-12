package vincently.cardnetwork;

import java.util.ArrayList;

public class HeartsGame extends CardGame {

    ArrayList<CardHand> taken;
    int[] points;
    Card[] played;
    Card lead;
    int cards_played;
    boolean pass;
    boolean hearts_broken;
    boolean hands_made;
    String msg;
    int startCards;
    int cycle;
    final int CARD_MSG = 413;
    final int DATA_MSG = 13;
    final int PASS_MSG = 34;

    public HeartsGame(){
        super(4);
        startCards=13;
        makeHands(players, startCards, false);
        turn = -1;
        points = new int[4];
        for(int i=0;i<points.length;i++)
            points[i]=0;
        played = new Card[4];
        taken = new ArrayList<CardHand>();
        for(int i=0;i<players;i++)
            taken.add(new CardHand());
        cycle = 0;
        pass = false;
        hands_made = false;
        hearts_broken = false;
        cards_played = 0;
        msg="";
    }

    public void input(int n){
        played[turn] = new Card(n);
        if(lead==null) {
            msg="";
            lead = new Card(n);
            msg+="Player " + (turn+1) + " has led with " + Card.unicode_to(lead.toString()) + ".\n";
        }
        else
            msg+="Player " + (turn+1) + " has played " + Card.unicode_to(played[turn].toString()) + ".\n";
    }

    public boolean over(){
        for(int i = 0;i<points.length;i++){
            if(points[i]>=100)
                return true;
        }
        return false;
    }

    public void run(int player){
        while(!over()){
            hands_made = false;
            if(ConnectionData.isHost){
                makeHands(players,startCards,false);
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
                for(Card c3:hands.get(2)) {
                    data[count] = (Integer.valueOf(c3.getNumber())).byteValue();
                    count++;
                }
                for(Card c4:hands.get(3)){
                    data[count] = (Integer.valueOf(c4.getNumber())).byteValue();
                    count++;
                }
                ConnectionData.write(data);
                if(cycle!=3)
                    ConnectionData.message_type = PASS_MSG;
                else
                    ConnectionData.message_type = DATA_MSG;
                hands_made = true;
            }
            msg+="Cards have been dealt.\n";
            while(!hands_made){
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {}
            }
            msg="";
            if(cycle!=3)
                pass = false;
            else
                pass = true;
            while(!pass){
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {}
            }
            msg+="Passing is finished.\n";
            turn = findTwoOfClubs();
            lead = new Card(0);
            msg+="Player " + (turn+1) + " has led with " + Card.unicode_to(lead.toString()) + ".\n";
            played[turn] = new Card(0);
            hands.get(turn).remove(new Card(0));
            cards_played++;
            turn=(turn+1)%4;
            for(int i = 51;i>0;i--){
                while(played[turn]==null){
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {}
                }
                if(!hearts_broken && played[turn].getSuit()==Suit.HEARTS) {
                    hearts_broken = true;
                    msg+="Hearts has been broken!\n";
                }
                hands.get(turn).remove(played[turn]);
                cards_played++;
                if(cards_played==4)
                    take();
                else
                    turn=(turn+1)%4;
            }
            ConnectionData.message_type=CARD_MSG;
            tally();
            if(ConnectionData.isHost){
                try {
                    Thread.sleep(5000);
                } catch (InterruptedException e) {}
            }

        }
        done = true;
    }

    public int findTwoOfClubs(){
        int i = 0;
        for(CardHand ch:hands){
            for(Card c:ch){
                if(c.getNumber()==0)
                    return i;
            }
            i++;
        }
        return -1;
    }

    public void take(){
        int taker = (turn+1)%4;
        int taker_value = lead.getValue().getValue();
        for(int a = 0;a<4;a++) {
            if (played[a].getSuit() == lead.getSuit() && played[a].getValue().getValue() > taker_value) {
                taker = a;
                taker_value = played[a].getValue().getValue();
            }
        }
        msg+="Player " + (taker+1) + " takes this trick with " + Card.unicode_to(played[taker].toString()) + ".\n";
        for(int b = 0;b<4;b++){
            if(played[b].getSuit()==Suit.HEARTS || played[b].getNumber()==43) {
                taken.get(taker).add(played[b]);
                msg+="Player " + (taker+1) + " picked up " + Card.unicode_to(played[b].toString()) + ".\n";
            }
        }
        for(int t = 0;t<4;t++)
            played[t] = null;
        cards_played = 0;
        lead = null;
        turn = taker;
        sort();
    }

    public void tally(){
        int sum;
        int i = 0;
        for(CardHand ch:taken){
            sum = 0;
            for(Card c:ch){
                if(c.getNumber()==43)
                    sum+=13;
                else
                    sum++;
            }
            if(sum==26)
                points[i]-=sum;//replace with check
            else
                points[i]+=sum;
            i++;
        }
        turn=-1;
        for(CardHand ch:taken)
            ch.clear();
        hearts_broken = false;
        cycle=(cycle+1)%4;
    }

    public void shootTheMoon(int i){
        boolean game = false;
        for(int t = 0;t<points.length;t++)
            if(points[t]>=74)
                game = true;
        if(game) {
            int min_index = i;
            int min_value = points[i]-26;
            for(int m = 0;m<4;m++){
                if(m!=i && points[m]<=min_value){
                    min_index = m;
                    min_value = points[m];
                }
            }
            if(min_index == i) {
                for (int a = 0; a < 4; a++)
                    if (a != i)
                        points[a] += 26;
            }
            else
                points[i]-=26;
        }
        else
            for(int a = 0;a<4;a++)
                if(a!=i)
                    points[a]+=26;
    }

    public void function_doStuf(){
        System.out.println("ay lmao");
    }

    public void updateMoves(ArrayList<String> l, int p){
        l.clear();
        CardHand ch = hands.get(p);
        if(cards_played==0){
            for(Card c:ch){
                if(c.getSuit()!=Suit.HEARTS || (c.getSuit()==Suit.HEARTS && hearts_broken))
                    l.add(Card.unicode_to(c.toString()));
            }
        }
        else{
            if(ch.containsSuit(lead.getSuit())){
                for(Card c:ch){
                    if(c.getSuit()==lead.getSuit())
                        l.add(Card.unicode_to(c.toString()));
                }
            }
            else{
                for(Card c:ch){
                    l.add(Card.unicode_to(c.toString()));
                }
            }
        }
    }

    public int getStartCards(){
        return startCards;
    }

    public void sort(){
        for(CardHand ch:hands) {
            ch.sort2();
        }
        for(CardHand ch2:taken)
            ch2.sort2();
    }

    public String getMsg(){
        return msg;
    }

    public void finishPass(){
        pass = true;
    }

    public void madeHands(){
        hands_made = true;
    }

    public int getPoints(int p){
        return points[p];
    }

    public CardHand getTaken(int p){
        return taken.get(p);
    }

    public void appendMsg(String s){
        msg+=s;
        System.out.println(msg);
    }
}
