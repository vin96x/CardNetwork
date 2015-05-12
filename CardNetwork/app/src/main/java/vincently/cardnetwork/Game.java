package vincently.cardnetwork;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.ParcelUuid;
import android.os.Parcelable;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.Collections;
import java.util.UUID;


public class Game extends Activity {

    BroadcastReceiver mReceiver;
    BluetoothAdapter mBluetoothAdapter;
    int REQUEST_ENABLE_BT=1337;
    int MESSAGE_READ=420;
    UUID id = UUID.fromString("5c5ca8a8-d25b-11e4-b9d6-1681e6b88ec1");
    boolean host;
    BluetoothSocket host_device;
    ArrayList<BluetoothSocket> host_devices;
    BluetoothSocket client_device;
    TextView bluetooth_text;
    String game;
    int players_needed;
    Game self;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);
        host_devices = new ArrayList<BluetoothSocket>();
        players_needed=1;
        self=this;
        bluetooth_text = (TextView) findViewById(R.id.bluetooth);
        ArrayList <String> devices = new ArrayList<String>();
        final ArrayAdapter<String> mArrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1,devices);
        final ListView lv = (ListView) (findViewById(R.id.devices));
        lv.setAdapter(mArrayAdapter);

        final Spinner game_choice = (Spinner) findViewById(R.id.game_choice);
        game_choice.setGravity(Gravity.CENTER);
        ArrayList<String> game_list = new ArrayList<String>();
        game_list.add("Go Fish");
        game_list.add("Hearts");
        game_list.add("DDDD");
        ArrayAdapter<String> game_adapter =
                new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item, game_list);
        game_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        game_choice.setAdapter(game_adapter);

        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (mBluetoothAdapter != null) {
            if (!mBluetoothAdapter.isEnabled()) {
                bluetooth_text.setText("Bluetooth not operational!");
                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
            }
            else
                bluetooth_text.setText("Bluetooth operational!");
        }
        else
            bluetooth_text.setText("Bluetooth not supported!");
        final Button create_room_button = (Button) findViewById(R.id.createroom);
        create_room_button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent discoverableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
                discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 300);
                startActivity(discoverableIntent);
                ((TextView) findViewById(R.id.bluetooth)).setText("Accepting!");
                game = (String) game_choice.getSelectedItem();
                switch(game){
                    case "Go Fish":
                        players_needed = 1;
                        break;
                    case "Hearts":
                        players_needed = 3;
                        break;
                    case "DDDD":
                        players_needed = 1;
                        break;
                    default: players_needed = 0;
                }
                (new AcceptThread()).start();
            }
        });
        final Button join_room_button = (Button) findViewById(R.id.joinroom);
        join_room_button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                mBluetoothAdapter.startDiscovery();
                mReceiver = new BroadcastReceiver() {
                    public void onReceive(Context context, Intent intent) {
                        String action = intent.getAction();
                        if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                            BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                            if(!device.fetchUuidsWithSdp())
                                System.out.println("Failure.");
                        }
                        else if(BluetoothDevice.ACTION_UUID.equals(action)){
                            BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                            Parcelable[] uuidExtra = intent.getParcelableArrayExtra(BluetoothDevice.EXTRA_UUID);
                            boolean app_user = false;
                            if(uuidExtra!= null)
                                for(Parcelable p:uuidExtra) {
                                    System.out.println(p);
                                    if (p.toString().equals(id.toString()))
                                        app_user = true;
                                }
                            if(app_user)
                                mArrayAdapter.add(device.getName() + "\n" + device.getAddress());
                        }
                    }
                };
                IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
                IntentFilter filter2 = new IntentFilter(BluetoothDevice.ACTION_UUID);
                registerReceiver(mReceiver, filter);
                registerReceiver(mReceiver, filter2);
                bluetooth_text.setText("Looking!");
            }
        });
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                bluetooth_text.setText("Connecting to " + ((String)(parent.getItemAtPosition(position))).split("\n")[0] + "!");
                lv.setEnabled(false);
                lv.setVisibility(View.INVISIBLE);
                (new ConnectThread(mBluetoothAdapter.getRemoteDevice(((String)(parent.getItemAtPosition(position))).split("\n")[1]))).start();
            }
        });
        (new CheckThread()).start();
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_ENABLE_BT) {
            if (resultCode == RESULT_OK) {
                ((TextView) findViewById(R.id.bluetooth)).setText("Bluetooth operational!");
            }
        }
    }

    protected void onDestroy(){
        if(mReceiver != null)
            unregisterReceiver(mReceiver);
        super.onDestroy();
    }

    private class AcceptThread extends Thread {
        private final BluetoothServerSocket mmServerSocket;

        public AcceptThread() {
            BluetoothServerSocket tmp = null;
            try {
                tmp = mBluetoothAdapter.listenUsingRfcommWithServiceRecord("CardNetwork", id);
            } catch (IOException e) { }
            mmServerSocket = tmp;
        }

        public void run() {
            BluetoothSocket socket = null;
            while (true) {
                try {
                    socket = mmServerSocket.accept();
                } catch (IOException e) {
                    break;
                }
                if (socket != null) {
                    host_devices.add(socket);
                    host = true;
                    players_needed--;
                }
                if (players_needed == 0){
                    try {
                        mmServerSocket.close();
                    } catch (IOException e) {}
                    players_needed--;
                    ConnectionData.gameStart = true;
                    ConnectionData.set_host(host_devices);
                    ConnectionData.set_client(client_device);
                    ConnectionData.isHost = host;
                    break;
                }
            }
        }

        public void cancel() {
            try {
                mmServerSocket.close();
            } catch (IOException e) { }
        }
    }

    private class ConnectThread extends Thread {
        private final BluetoothSocket mmSocket;

        public ConnectThread(BluetoothDevice device) {
            BluetoothSocket tmp = null;
            try {
                tmp = device.createRfcommSocketToServiceRecord(id);
            } catch (IOException e) { }
            mmSocket = tmp;
        }

        public void run() {
            mBluetoothAdapter.cancelDiscovery();
            try {
                mmSocket.connect();
            } catch (IOException connectException) {
                try {
                    mmSocket.close();
                } catch (IOException closeException) { }
                return;
            }
            client_device = mmSocket;
            host = false;
            players_needed = -1;
            ConnectionData.set_host(host_devices);
            ConnectionData.set_client(client_device);
            ConnectionData.isHost = host;
        }

        public void cancel() {
            try {
                mmSocket.close();
            } catch (IOException e) { }
        }
    }

    private class CheckThread extends Thread {
        public CheckThread() {

        }

        public void run(){
            while(true) {
                if(players_needed==-1 && ConnectionData.gameStart){
                    if(host)
                        ConnectionData.init(game);
                    else {
                        self.unregisterReceiver(mReceiver);
                        mReceiver = null;
                    }
                    switch(ConnectionData.gameName){
                        case "Go Fish":
                            ConnectionData.players = 2;
                            startActivity(new Intent(self, GoFish.class));
                            break;
                        case "Hearts":
                            ConnectionData.players = 4;
                            startActivity(new Intent(self, Hearts.class));
                            break;
                        case "DDDD":
                            ConnectionData.players = 2;
                            startActivity(new Intent(self, Original.class));
                        default:
                            System.out.println("Lol!");
                    }
                    break;
                }
                else
                    try {
                        sleep(1000);
                    } catch (InterruptedException e) {}
            }
        }
    }
}
