package vincently.cardnetwork;

import android.bluetooth.BluetoothSocket;
import android.os.Handler;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;

public class ConnectionData {

    static ArrayList<ConnectedThread> host;
    static ConnectedThread client;
    static Handler mHandler;
    static boolean isHost;
    static int player = -1;
    static int players = 2;
    static int message_type;
    static boolean gameStart = false;
    static String gameName = "";

    public static void init(String gn){
        gameName = gn;
        player = 0;
        int i = 1;
        int game_num = -1;
        switch(gn){
            case "Go Fish": game_num = 0; break;
            case "Hearts": game_num = 1; break;
            case "DDDD": game_num = 2; break;
            default: game_num = -1;
        }
        for(ConnectedThread ct:host){
            ct.write(new byte[]{new Integer(game_num).byteValue(),new Integer(i).byteValue()});
            i++;
        }
    }

    public static void write(byte[] bytes){
        if(isHost) {
            for(ConnectedThread ct:host)
                ct.write(bytes);
        }
        else
            client.write(bytes);
    }

    public static void set_host(ArrayList<BluetoothSocket> h){
        if(host==null)
            host = new ArrayList<ConnectedThread>();
        if(h!=null) {
            for(BluetoothSocket bs:h){
                ConnectedThread tmp = new ConnectedThread(bs);
                tmp.start();
                host.add(tmp);
            }
        }
    }

    public static void set_client(BluetoothSocket c){
        if(c!=null) {
            client = new ConnectedThread(c);
            client.start();
        }
    }

    public static void set_handler(Handler h){
        mHandler = h;
    }

    private static class ConnectedThread extends Thread {
        private final BluetoothSocket mmSocket;
        private final InputStream mmInStream;
        private final OutputStream mmOutStream;

        public ConnectedThread(BluetoothSocket socket) {
            mmSocket = socket;
            InputStream tmpIn = null;
            OutputStream tmpOut = null;
            try {
                tmpIn = socket.getInputStream();
                tmpOut = socket.getOutputStream();
            } catch (IOException e) { }

            mmInStream = tmpIn;
            mmOutStream = tmpOut;
        }

        public void run() {
            byte[] buffer = new byte[1024];
            int bytes;

            while (true) {
                try {
                    bytes = mmInStream.read(buffer);
                    if(!gameStart) {
                        gameStart = true;
                        switch(buffer[0]){
                            case 0: gameName = "Go Fish"; break;
                            case 1: gameName = "Hearts"; break;
                            case 2: gameName = "DDDD"; break;
                            default: System.out.println("Crap!");
                        }
                        player = buffer[1];
                    }
                    else
                        mHandler.obtainMessage(message_type, bytes, -1, Arrays.copyOfRange(buffer,0,bytes))
                            .sendToTarget();
                } catch (IOException e) {
                    break;
                }
            }
        }

        public void write(byte[] bytes) {
            try {
                mmOutStream.write(bytes);
            } catch (IOException e) { }
        }

        public void cancel() {
            try {
                mmSocket.close();
            } catch (IOException e) { }
        }
    }
}
