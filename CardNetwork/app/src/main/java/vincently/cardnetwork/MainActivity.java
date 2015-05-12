package vincently.cardnetwork;
import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends Activity {

    Music m;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Drawable loginActivityBackground = findViewById(R.id.main_layout).getBackground();
        loginActivityBackground.setAlpha(100);
        final Intent game_intent = new Intent(this, Game.class);
        final Intent settings_intent = new Intent(this, Settings.class);
        final Intent about_intent = new Intent(this, About.class);
        Button about_button = (Button) findViewById(R.id.about);
        Button settings_button = (Button) findViewById(R.id.settings);
        Button play_button = (Button) findViewById(R.id.play);
        Button exit_button = (Button) findViewById(R.id.exit);

        play_button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                startActivity(game_intent);
            }
        });
        settings_button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                startActivity(settings_intent);
            }
        });
        about_button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                startActivity(about_intent);
            }
        });
        exit_button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                finish();
                System.exit(0);
            }
        });
    }

    protected void onResume(){
        super.onResume();
        m = new Music();
        m.execute((Void[]) null);
    }

    public void onPause() {
        super.onPause();
        m.mp.stop();
        m.mp.release();
        m.cancel(true);
    }

    public class Music extends AsyncTask<Void, Void, Void> {

        MediaPlayer mp;

        protected void onPreExecute() {
            mp = MediaPlayer.create(MainActivity.this, R.raw.music);
        }
        protected Void doInBackground(Void... params) {
            mp.setLooping(true);
            mp.setVolume(100,100);
            mp.start();
            return null;
        }
    }
}


