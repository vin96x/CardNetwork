package vincently.cardnetwork;

import android.app.Activity;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.Gravity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.ArrayList;

public class Original extends Activity {

    OriginalGame og;
    static Handler mHandler;

    int player;
    int recBytes = 0;
    int startCards;
    final int CARD_MSG = 3346;
    final int DATA_MSG = 14;
    CardHand tmp_cd_hand;
    CardHand tmp_cd_hand2;
    CardHand tmp_pile;
    ArrayList<String> action_list;
    ArrayAdapter<String> action_adapter;

    TextView og_gameover;
    TextView og_hand;
    TextView og_turn;
    TextView og_msg;
    TextView og_round;
    Button og_action_button;

    Thread runner;
    Handler updater;
    Runnable updates;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_original);
        if(ConnectionData.isHost)
            ConnectionData.message_type = DATA_MSG;
        else
            ConnectionData.message_type = CARD_MSG;
        player = ConnectionData.player;
        og = new OriginalGame();
        mHandler = new Handler(Looper.getMainLooper()) {
            public void handleMessage(Message inputMessage) {
                byte[] data = (byte[]) inputMessage.obj;
                System.out.println("Bytes: " + inputMessage.arg1);
                switch(inputMessage.what){
                    case DATA_MSG:
                        og.input(Integer.valueOf(data[0]));
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
                            og.setHand((CardHand)tmp_cd_hand.clone(), 0);
                            og.setHand((CardHand)tmp_cd_hand2.clone(), 1);
                            og.setPile((CardHand) tmp_pile.clone());
                            ConnectionData.message_type = DATA_MSG;
                            recBytes=0;
                            tmp_cd_hand.clear();
                            tmp_cd_hand2.clear();
                            tmp_pile.clear();
                            og.madeHands();
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
        tmp_cd_hand = new CardHand();
        tmp_cd_hand2 = new CardHand();
        tmp_pile = new CardHand();
        startCards = og.getStartCards();
        og_gameover = (TextView)findViewById(R.id.og_gameover);
        og_hand = (TextView)findViewById(R.id.og_hand);
        Typeface myTypeface = Typeface.createFromAsset(getAssets(), "font/DejaVuSans.ttf");
        og_hand.setTypeface(myTypeface);
        og_turn = (TextView)findViewById(R.id.og_turn);
        og_msg = (TextView)findViewById(R.id.og_msg);
        og_msg.setTypeface(myTypeface);
        og_round = (TextView)findViewById(R.id.og_round);
        TextView og_title = (TextView)findViewById(R.id.og_title);
        og_title.setText("Original Game (Player " + (player+1) + ")");

        final Spinner og_action_picker = (Spinner) findViewById(R.id.og_action_picker);
        og_action_picker.setGravity(Gravity.CENTER);
        action_list = new ArrayList<String>();
        action_adapter = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item, action_list);
        action_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        og_action_picker.setAdapter(action_adapter);

        og_action_button = (Button) findViewById(R.id.og_action_button);
        og_action_button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                int i = -1;
                switch((String)og_action_picker.getSelectedItem()){
                    case "Trade a Diamond (Get 3 Cards)":
                        i=0;
                        break;
                    case "Attack with Club":
                        i=1;
                        break;
                    case "Trade in Club (Get 1 Card)":
                        i=2;
                        break;
                    case "Shovel with Spade (Get 2 Cards)":
                        i=3;
                        break;
                    case "Pass":
                        i=4;
                        break;
                    default: System.out.println("Wat");
                }
                ConnectionData.write(new byte[]{Integer.valueOf(i).byteValue()});
                og.input(i);
            }
        });

        runner = new Thread(){
            public void run(){
                og.run(player);
            }
        };
        runner.start();
        updater = new Handler();
        updates = new Runnable(){
            public void run() {
                og_round.setText("Round: " + og.getRound());
                String s = "";
                for(Card c:og.getHand(player))
                    s+=Card.unicode_to(c.toString())+" ";
                og_hand.setText(s);
                int turn = og.getTurn();
                og_turn.setText("Turn: Player " + (turn+1) + ((player)==turn?" (You)":""));
                og.updateMoves(action_list, player);
                action_adapter.notifyDataSetChanged();
                og_msg.setText(og.getMsg());
                if(player==turn)
                    og_action_button.setEnabled(true);
                else
                    og_action_button.setEnabled(false);
                if(og.isDone()){
                    og_action_button.setEnabled(false);
                    int your_points = og.getPoints(player);
                    int opponent_points = og.getPoints((player+1)%2);
                    if(your_points>opponent_points){
                        og_gameover.setText("You are the winner! (" + your_points + ":" + opponent_points + ")");
                    }
                    else if(opponent_points>your_points){
                        og_gameover.setText("You are the loser! (" + your_points + ":" + opponent_points + ")");
                    }
                    else{
                        og_gameover.setText("Tie! (" + your_points + ":" + opponent_points + ")");
                    }
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
