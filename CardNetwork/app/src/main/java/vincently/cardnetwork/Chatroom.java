package vincently.cardnetwork;

import android.app.Activity;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.TextView;

import java.util.Arrays;


public class Chatroom extends Activity {

    static Handler mHandler;
    TextView chat;
    String msg;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chatroom);
        mHandler = new Handler(Looper.getMainLooper()) {
            public void handleMessage(Message inputMessage) {
                if(ConnectionData.isHost){
                    ConnectionData.write((byte[]) inputMessage.obj);
                }
                msg+=new String((byte [])inputMessage.obj);
                chat.setText(msg);
            }
        };
        ConnectionData.set_handler(mHandler);
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {}
        chat = (TextView) findViewById(R.id.chat);
        final EditText input = (EditText) findViewById(R.id.input);
        input.addTextChangedListener(new TextWatcher() {
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                ConnectionData.write(s.subSequence(start,s.length()).toString().getBytes());
                msg+=s.subSequence(start,s.length());
                chat.setText(msg);
            }
            public void afterTextChanged(Editable s) {

            }
        });
    }
}
