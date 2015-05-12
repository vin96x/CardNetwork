package vincently.cardnetwork;

import android.app.Activity;
import android.graphics.Typeface;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.ArrayList;

public class GoFish extends Activity {

    GoFishGame go_fish;
    static Handler mHandler;

    int player;
    String rank;
    int asked;
    int startCards;
    CardHand tmp_cd_hand;
    CardHand tmp_cd_hand2;
    CardHand tmp_pile;
    int recBytes;

    final int CARD_MSG = 5738;
    final int DATA_MSG = 2613;

    TextView go_fish_msg;
    TextView go_fish_hand;
    TextView go_fish_points;
    TextView go_fish_turn;
    TextView go_fish_points_o;
    TextView pile_size;
    ArrayAdapter<String> rank_adapter;
    ArrayList<String> rank_list;
    Button fish_button;

    Thread runner;
    Handler updater;
    Runnable updates;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_go_fish);
        player = ConnectionData.player;
        ConnectionData.message_type=CARD_MSG;
        go_fish = new GoFishGame(ConnectionData.players);
        mHandler = new Handler(Looper.getMainLooper()) {
            public void handleMessage(Message inputMessage) {
                byte[] data = (byte[]) inputMessage.obj;
                System.out.println("Bytes: " + inputMessage.arg1);
                switch(inputMessage.what){
                    case DATA_MSG:
                        asked = data[0];
                        rank = Value.lookup(data[1]).toString();
                        if(rank!=null && asked!=-1){
                            go_fish.input(Value.lookup2(rank),asked);
                            rank=null;
                            asked=-1;
                        }
                        break;
                    case CARD_MSG:
                        for(int i=0;i<inputMessage.arg1;i++) {
                            if(recBytes<startCards){
                                tmp_cd_hand.add(new Card((int) (((byte[]) inputMessage.obj)[i])));
                            }
                            else if(recBytes<startCards*2){
                                tmp_cd_hand2.add(new Card((int) (((byte[]) inputMessage.obj)[i])));
                            }
                            else
                                tmp_pile.add(new Card((int) (((byte[]) inputMessage.obj)[i])));
                            recBytes++;
                        }
                        if(recBytes==52) {
                            go_fish.setHand(tmp_cd_hand, 0);
                            go_fish.setHand(tmp_cd_hand2, 1);
                            go_fish.setPile(tmp_pile);
                            ConnectionData.message_type = DATA_MSG;
                        }
                        break;
                    default: System.out.println(inputMessage.what);
                }
            }
        };
        ConnectionData.set_handler(mHandler);
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {}
        recBytes=0;
        startCards = go_fish.getStartCards();
        tmp_cd_hand = new CardHand();
        tmp_cd_hand2 = new CardHand();
        tmp_pile = new CardHand();
        rank=null;
        asked=-1;
        if(ConnectionData.isHost){
            CardHand ch1 = go_fish.getHand(0);
            CardHand ch2 = go_fish.getHand(1);
            CardHand ch4 = go_fish.getPile();
            byte[] data = new byte[52];
            int count = 0;
            for(Card c:ch1) {
                data[count] = (Integer.valueOf(c.getNumber())).byteValue();
                count++;
            }
            for(Card c2:ch2) {
                data[count] = (Integer.valueOf(c2.getNumber())).byteValue();
                count++;
            }
            for(Card c4:ch4){
                data[count] = (Integer.valueOf(c4.getNumber())).byteValue();
                count++;
            }
            ConnectionData.write(data);
            ConnectionData.message_type=DATA_MSG;
        }
        Typeface myTypeface = Typeface.createFromAsset(getAssets(), "font/DejaVuSans.ttf");
        TextView go_fish_title = (TextView) findViewById(R.id.go_fish_title);
        go_fish_title.setText("Go Fish (Player " + (player+1) + ")");
        final TextView game_over = (TextView) findViewById(R.id.go_fish_gameover);
        game_over.setVisibility(View.GONE);
        pile_size = (TextView) findViewById(R.id.pile_size);
        go_fish_msg = (TextView) findViewById(R.id.go_fish_msg);
        go_fish_msg.setTypeface(myTypeface);
        go_fish_points_o = (TextView) findViewById(R.id.go_fish_points_o);
        go_fish_hand = (TextView) findViewById(R.id.go_fish_hand);
        go_fish_hand.setTypeface(myTypeface);
        go_fish_points = (TextView) findViewById(R.id.go_fish_points);
        go_fish_turn = (TextView) findViewById(R.id.go_fish_turn);
        final Spinner spinner_player = (Spinner) findViewById(R.id.player_go_fish);
        spinner_player.setGravity(Gravity.CENTER);
        ArrayList<Integer> players_list = new ArrayList<Integer>();
        for(int i=0;i<ConnectionData.players;i++)
            players_list.add(i + 1);
        players_list.remove(new Integer(player+1));
        ArrayAdapter<Integer> players_adapter =
                new ArrayAdapter<Integer>(this,android.R.layout.simple_spinner_item, players_list);
        players_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner_player.setAdapter(players_adapter);

        final Spinner spinner_rank = (Spinner) findViewById(R.id.rank_go_fish);
        spinner_rank.setGravity(Gravity.CENTER);
        rank_list = new ArrayList<String>();
        rank_adapter = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item, rank_list);
        rank_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner_rank.setAdapter(rank_adapter);

        fish_button = (Button) findViewById(R.id.fish_button);
        fish_button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                asked = (int) spinner_player.getSelectedItem();
                rank = (String) spinner_rank.getSelectedItem();
                byte[] data = new byte[2];
                data[0] = (Integer.valueOf(asked-1)).byteValue();
                data[1] = (Integer.valueOf(Value.lookup2(rank).getValue())).byteValue();
                ConnectionData.write(data);
                go_fish.input(Value.lookup2(rank), asked-1);
            }
        });
        runner = new Thread(){
            public void run(){
                go_fish.run(player);
            }
        };
        runner.start();
        updater = new Handler();
        updates = new Runnable(){
            public void run() {
                pile_size.setText("Pile: " + go_fish.getPile().size());
                String s = "";
                for(Card c:go_fish.getHand(player))
                    s+=Card.unicode_to(c.toString()) +" ";
                go_fish_hand.setText(s);
                go_fish_points.setText("Your Books: " + go_fish.getBooks(player).toString());
                if(player==0)
                    go_fish_points_o.setText("Their Books: " + go_fish.getBooks(1).toString());
                else
                    go_fish_points_o.setText("Their Books: " + go_fish.getBooks(0).toString());
                int turn = go_fish.getTurn();
                go_fish_turn.setText("Turn: Player " + (turn+1) + ((player)==turn?" (You)":""));
                go_fish.updateMoves(rank_list, player);
                rank_adapter.notifyDataSetChanged();
                go_fish_msg.setText(go_fish.getMsg());
                if(player==turn)
                    fish_button.setEnabled(true);
                else
                    fish_button.setEnabled(false);
                if(go_fish.isDone()){
                    go_fish_msg.setVisibility(View.GONE);
                    fish_button.setEnabled(false);
                    int my_points = go_fish.getBooks(player).size();
                    int other_points = (player==1)?go_fish.getBooks(0).size():go_fish.getBooks(1).size();
                    if(my_points>other_points)
                        game_over.setText("You are a winner! (" + my_points + ":" + other_points + ")");
                    else if(my_points<other_points)
                        game_over.setText("You are a loser! (" + my_points + ":" + other_points + ")");
                    else
                        game_over.setText("Tie! (" + my_points + ":" + other_points + ")");
                    game_over.setVisibility(View.VISIBLE);
                }
            }
        };
        Thread tUpdate = new Thread(){
            public void run(){
                while(true){
                    updater.post(updates);
                    try {
                        sleep(500);
                    } catch (InterruptedException e) {}
                }
            }
        };
        tUpdate.start();
    }
}
