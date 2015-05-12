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
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;


public class Hearts extends Activity {

    HeartsGame hearts;
    static Handler mHandler;

    int player;
    int startCards;
    CardHand tmp_cd_hand;
    CardHand tmp_cd_hand2;
    CardHand tmp_cd_hand3;
    CardHand tmp_cd_hand4;
    CardHand tmp_pass_hand;
    CardHand tmp_pass_hand2;
    CardHand tmp_pass_hand3;
    CardHand tmp_pass_hand4;
    ArrayList<Integer> tmp_pass;
    ArrayList<Integer> tmp_pass2;
    ArrayList<Integer> tmp_pass3;
    ArrayList<Integer> tmp_pass4;
    ArrayList<Integer> locpass;
    int recBytes;
    int choice;

    final int CARD_MSG = 413;
    final int DATA_MSG = 13;
    final int PASS_MSG = 34;

    ArrayAdapter<String> card_adapter;
    ArrayList<String> card_list;
    ArrayAdapter<String> pass_adapter;
    ArrayList<String> pass_list;
    TextView hearts_p1_pts;
    TextView hearts_p2_pts;
    TextView hearts_p3_pts;
    TextView hearts_p4_pts;
    TextView hearts_turn;
    TextView hearts_hand;
    TextView hearts_gameover;
    TextView hearts_msg;
    Button hearts_play_button;
    Button hearts_pass_button;

    Thread runner;
    Handler updater;
    Runnable updates;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hearts);
        player = ConnectionData.player;
        ConnectionData.message_type=CARD_MSG;
        hearts = new HeartsGame();
        mHandler = new Handler(Looper.getMainLooper()) {
            public void handleMessage(Message inputMessage) {
                byte[] data = (byte[]) inputMessage.obj;
                System.out.println("Bytes: " + inputMessage.arg1);
                switch(inputMessage.what){
                    case PASS_MSG:
                        if(ConnectionData.isHost){
                            int i = 0;
                            int b = inputMessage.arg1;
                            while(b>0){
                                switch(data[i++]){
                                    case 0:
                                        tmp_pass.add(Integer.valueOf(data[i++]));
                                        tmp_pass.add(Integer.valueOf(data[i++]));
                                        tmp_pass.add(Integer.valueOf(data[i++]));
                                        break;
                                    case 1:
                                        tmp_pass2.add(Integer.valueOf(data[i++]));
                                        tmp_pass2.add(Integer.valueOf(data[i++]));
                                        tmp_pass2.add(Integer.valueOf(data[i++]));
                                        break;
                                    case 2:
                                        tmp_pass3.add(Integer.valueOf(data[i++]));
                                        tmp_pass3.add(Integer.valueOf(data[i++]));
                                        tmp_pass3.add(Integer.valueOf(data[i++]));
                                        break;
                                    case 3:
                                        tmp_pass4.add(Integer.valueOf(data[i++]));
                                        tmp_pass4.add(Integer.valueOf(data[i++]));
                                        tmp_pass4.add(Integer.valueOf(data[i++]));
                                        break;
                                    default: System.out.println(data[i-1]);
                                }
                                b-=4;
                                recBytes+=4;
                            }
                            if(recBytes==16){
                                recBytes=0;
                                byte[] passdata = new byte[16];
                                int j = 0;
                                for(Integer in:tmp_pass)
                                    passdata[j++] = in.byteValue();
                                for(Integer in:tmp_pass2)
                                    passdata[j++] = in.byteValue();
                                for(Integer in:tmp_pass3)
                                    passdata[j++] = in.byteValue();
                                for(Integer in:tmp_pass4)
                                    passdata[j++] = in.byteValue();
                                for(Integer p1:tmp_pass)
                                    tmp_pass_hand.add(new Card(p1));
                                for(Integer p2:tmp_pass2)
                                    tmp_pass_hand2.add(new Card(p2));
                                for(Integer p3:tmp_pass3)
                                    tmp_pass_hand3.add(new Card(p3));
                                for(Integer p4:tmp_pass4)
                                    tmp_pass_hand4.add(new Card(p4));
                                tmp_pass.clear();
                                tmp_pass2.clear();
                                tmp_pass3.clear();
                                tmp_pass4.clear();
                                for(Card c2:tmp_pass_hand2)
                                    hearts.getHand(1).remove(c2);
                                for(Card c3:tmp_pass_hand3)
                                    hearts.getHand(2).remove(c3);
                                for(Card c4:tmp_pass_hand4)
                                    hearts.getHand(3).remove(c4);
                                switch(hearts.cycle){
                                    case 0:
                                        tmp_pass_hand.addAll(hearts.getHand(1));
                                        tmp_pass_hand2.addAll(hearts.getHand(2));
                                        tmp_pass_hand3.addAll(hearts.getHand(3));
                                        tmp_pass_hand4.addAll(hearts.getHand(0));
                                        hearts.setHand(tmp_pass_hand4,0);
                                        hearts.setHand(tmp_pass_hand,1);
                                        hearts.setHand(tmp_pass_hand2,2);
                                        hearts.setHand(tmp_pass_hand3,3);
                                        break;
                                    case 1:
                                        tmp_pass_hand.addAll(hearts.getHand(3));
                                        tmp_pass_hand2.addAll(hearts.getHand(0));
                                        tmp_pass_hand3.addAll(hearts.getHand(1));
                                        tmp_pass_hand4.addAll(hearts.getHand(2));
                                        hearts.setHand(tmp_pass_hand2,0);
                                        hearts.setHand(tmp_pass_hand3,1);
                                        hearts.setHand(tmp_pass_hand4,2);
                                        hearts.setHand(tmp_pass_hand,3);
                                        break;
                                    case 2:
                                        tmp_pass_hand.addAll(hearts.getHand(2));
                                        tmp_pass_hand2.addAll(hearts.getHand(3));
                                        tmp_pass_hand3.addAll(hearts.getHand(0));
                                        tmp_pass_hand4.addAll(hearts.getHand(1));
                                        hearts.setHand(tmp_pass_hand3,0);
                                        hearts.setHand(tmp_pass_hand4,1);
                                        hearts.setHand(tmp_pass_hand,2);
                                        hearts.setHand(tmp_pass_hand2,3);
                                        break;
                                    default: System.out.println(hearts.cycle);
                                }
                                hearts.sort();
                                byte[] sendnewpass = new byte[52];
                                int count = 0;
                                for(Card cp1:hearts.getHand(0))
                                    sendnewpass[count++] = Integer.valueOf(cp1.getNumber()).byteValue();
                                for(Card cp2:hearts.getHand(1))
                                    sendnewpass[count++] = Integer.valueOf(cp2.getNumber()).byteValue();
                                for(Card cp3:hearts.getHand(2))
                                    sendnewpass[count++] = Integer.valueOf(cp3.getNumber()).byteValue();
                                for(Card cp4:hearts.getHand(3))
                                    sendnewpass[count++] = Integer.valueOf(cp4.getNumber()).byteValue();
                                ConnectionData.write(sendnewpass);
                                ConnectionData.message_type=DATA_MSG;
                                locpass.clear();
                                hearts.finishPass();
                            }
                        }
                        else {
                            ConnectionData.message_type = DATA_MSG;
                            for (int i = 0; i < inputMessage.arg1; i++) {
                                if (recBytes < startCards) {
                                    tmp_pass_hand.add(new Card((int) (((byte[]) inputMessage.obj)[i])));
                                } else if (recBytes < startCards * 2) {
                                    tmp_pass_hand2.add(new Card((int) (((byte[]) inputMessage.obj)[i])));
                                } else if (recBytes < startCards * 3) {
                                    tmp_pass_hand3.add(new Card((int) (((byte[]) inputMessage.obj)[i])));
                                } else
                                    tmp_pass_hand4.add(new Card((int) (((byte[]) inputMessage.obj)[i])));
                                recBytes++;
                            }
                            if (recBytes == 52) {
                                hearts.setHand((CardHand)tmp_pass_hand.clone(), 0);
                                hearts.setHand((CardHand)tmp_pass_hand2.clone(), 1);
                                hearts.setHand((CardHand)tmp_pass_hand3.clone(), 2);
                                hearts.setHand((CardHand)tmp_pass_hand4.clone(), 3);
                                ConnectionData.message_type = DATA_MSG;
                                recBytes = 0;
                                tmp_pass_hand.clear();
                                tmp_pass_hand2.clear();
                                tmp_pass_hand3.clear();
                                tmp_pass_hand4.clear();
                                locpass.clear();
                                hearts.finishPass();
                            }
                        }
                        break;
                    case DATA_MSG:
                        if(ConnectionData.isHost) {
                            ConnectionData.write(data);
                        }
                        hearts.input(data[0]);
                        break;
                    case CARD_MSG:
                        for(int i=0;i<inputMessage.arg1;i++) {
                            if(recBytes<startCards){
                                tmp_cd_hand.add(new Card((int) (((byte[]) inputMessage.obj)[i])));
                            }
                            else if(recBytes<startCards*2){
                                tmp_cd_hand2.add(new Card((int) (((byte[]) inputMessage.obj)[i])));
                            }
                            else if(recBytes<startCards*3){
                                tmp_cd_hand3.add(new Card((int) (((byte[]) inputMessage.obj)[i])));
                            }
                            else
                                tmp_cd_hand4.add(new Card((int) (((byte[]) inputMessage.obj)[i])));
                            recBytes++;
                        }
                        if(recBytes==52) {
                            hearts.setHand((CardHand)tmp_cd_hand.clone(), 0);
                            hearts.setHand((CardHand)tmp_cd_hand2.clone(), 1);
                            hearts.setHand((CardHand)tmp_cd_hand3.clone(), 2);
                            hearts.setHand((CardHand)tmp_cd_hand4.clone(), 3);
                            if(hearts.cycle!=3)
                                ConnectionData.message_type = PASS_MSG;
                            else
                                ConnectionData.message_type = DATA_MSG;
                            recBytes=0;
                            tmp_cd_hand.clear();
                            tmp_cd_hand2.clear();
                            tmp_cd_hand3.clear();
                            tmp_cd_hand4.clear();
                            hearts.madeHands();
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
        startCards = hearts.getStartCards();
        tmp_cd_hand = new CardHand();
        tmp_cd_hand2 = new CardHand();
        tmp_cd_hand3 = new CardHand();
        tmp_cd_hand4 = new CardHand();
        tmp_pass_hand = new CardHand();
        tmp_pass_hand2 = new CardHand();
        tmp_pass_hand3 = new CardHand();
        tmp_pass_hand4 = new CardHand();
        tmp_pass = new ArrayList<Integer>();
        tmp_pass2 = new ArrayList<Integer>();
        tmp_pass3 = new ArrayList<Integer>();
        tmp_pass4 = new ArrayList<Integer>();
        locpass = new ArrayList<Integer>();
        Typeface myTypeface = Typeface.createFromAsset(getAssets(), "font/DejaVuSans.ttf");
        TextView hearts_title = (TextView) findViewById(R.id.hearts_title);
        hearts_title.setText("Hearts (Player " + (player+1) + ")");
        hearts_gameover = (TextView) findViewById(R.id.hearts_gameover);
        hearts_gameover.setVisibility(View.GONE);
        hearts_msg = (TextView) findViewById(R.id.hearts_msg);
        hearts_msg.setTypeface(myTypeface);
        hearts_p1_pts = (TextView) findViewById(R.id.hearts_p1_pts);
        hearts_p2_pts = (TextView) findViewById(R.id.hearts_p2_pts);
        hearts_p3_pts = (TextView) findViewById(R.id.hearts_p3_pts);
        hearts_p4_pts = (TextView) findViewById(R.id.hearts_p4_pts);
        hearts_turn = (TextView) findViewById(R.id.hearts_turn);
        hearts_hand = (TextView) findViewById(R.id.hearts_hand);
        hearts_hand.setTypeface(myTypeface);
        final Spinner spinner_pass = (Spinner) findViewById(R.id.hearts_pass);
        spinner_pass.setGravity(Gravity.CENTER);
        pass_list = new ArrayList<String>();
        pass_adapter = new CardArrayAdapter(this,android.R.layout.simple_spinner_item, pass_list,myTypeface);
        pass_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner_pass.setAdapter(pass_adapter);

        final Spinner spinner_card = (Spinner) findViewById(R.id.hearts_card);
        spinner_card.setGravity(Gravity.CENTER);
        card_list = new ArrayList<String>();
        card_adapter = new CardArrayAdapter(this,android.R.layout.simple_spinner_item, card_list,myTypeface);
        card_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner_card.setAdapter(card_adapter);

        hearts_play_button = (Button) findViewById(R.id.hearts_play_button);
        hearts_play_button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                choice = Card.convert(Card.unicode_from((String) spinner_card.getSelectedItem()));
                byte[] data = new byte[1];
                data[0] = Integer.valueOf(choice).byteValue();
                ConnectionData.write(data);
                if(ConnectionData.isHost)
                    hearts.input(choice);
            }
        });

        hearts_pass_button = (Button) findViewById(R.id.hearts_pass_button);
        hearts_pass_button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                int tmp = Card.convert(Card.unicode_from((String) spinner_pass.getSelectedItem()));
                locpass.add(tmp);
                Card cd_pass = new Card(tmp);
                hearts.getHand(player).remove(cd_pass);
                hearts.appendMsg("Passed " + Card.unicode_to(cd_pass.toString()) +".\n");
                if(locpass.size()==3){
                    hearts.appendMsg("Finished passing!\n");
                    hearts_pass_button.setEnabled(false);
                    byte[] data = new byte[4];
                    data[0] = Integer.valueOf(player).byteValue();
                    data[1] = Integer.valueOf(locpass.get(0)).byteValue();
                    data[2] = Integer.valueOf(locpass.get(1)).byteValue();
                    data[3] = Integer.valueOf(locpass.get(2)).byteValue();
                    if(ConnectionData.isHost){
                        mHandler.obtainMessage(PASS_MSG, 4, -1, data)
                                .sendToTarget();
                    }
                    else
                        ConnectionData.write(data);
                    pass_adapter.clear();
                    pass_adapter.notifyDataSetChanged();
                }
            }
        });

        runner = new Thread(){
            public void run(){
                hearts.run(player);
            }
        };
        runner.start();
        updater = new Handler();
        updates = new Runnable(){
            public void run() {
                if(!hearts.pass){
                    spinner_pass.setVisibility(View.VISIBLE);
                    hearts_pass_button.setVisibility(View.VISIBLE);
                    if(locpass.size()<3)
                        hearts_pass_button.setEnabled(true);
                    pass_list.clear();
                    for(Card c:hearts.getHand(player))
                        pass_list.add(c.toString());
                    pass_adapter.notifyDataSetChanged();
                }
                else{
                    spinner_pass.setVisibility(View.INVISIBLE);
                    hearts_pass_button.setVisibility(View.INVISIBLE);
                }
                String s = "";
                for(Card c:hearts.getHand(player))
                    s+=Card.unicode_to(c.toString())+" ";
                hearts_hand.setText(s);
                hearts_p1_pts.setText("Player 1: (" + hearts.getPoints(0) + ") " + hearts.getTaken(0));
                hearts_p2_pts.setText("Player 2: (" + hearts.getPoints(1) + ") " + hearts.getTaken(1));
                hearts_p3_pts.setText("Player 3: (" + hearts.getPoints(2) + ") " + hearts.getTaken(2));
                hearts_p4_pts.setText("Player 4: (" + hearts.getPoints(3) + ") " + hearts.getTaken(3));
                int turn = hearts.getTurn();
                hearts_turn.setText("Turn: Player " + (turn+1) + ((player)==turn?" (You)":""));
                hearts.updateMoves(card_list, player);
                card_adapter.notifyDataSetChanged();
                hearts_msg.setText(hearts.getMsg());
                if(player==turn)
                    hearts_play_button.setEnabled(true);
                else
                    hearts_play_button.setEnabled(false);
                if(hearts.isDone()){
                    hearts_msg.setVisibility(View.GONE);
                    hearts_play_button.setEnabled(false);
                    hearts_pass_button.setEnabled(false);
                    int[] final_points = hearts.points;
                    int min = 0;
                    boolean tie = false;
                    int min_value = final_points[min];
                    for(int t = 1;t<4;t++){
                        if(final_points[t]==min_value){
                            tie = true;
                            min = -1;
                        }
                        else if(final_points[t]<min_value){
                            tie = false;
                            min = t;
                            min_value = final_points[t];
                        }
                    }
                    if(tie){
                        hearts_gameover.setText("Tie! (" + final_points[0] + ":" + final_points[1] + ":" + final_points[2] + ":" + final_points[3] + ")");
                    }
                    else
                        hearts_gameover.setText("Player " + (min+1) + " wins! (" + final_points[0] + ":" + final_points[1] + ":" + final_points[2] + ":" + final_points[3] + ")");
                    hearts_gameover.setVisibility(View.VISIBLE);
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
