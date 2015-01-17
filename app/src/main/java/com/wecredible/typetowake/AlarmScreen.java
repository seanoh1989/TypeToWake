package com.wecredible.typetowake;

/**
 * Created by Sean on 1/16/15.
 */
import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.content.Context;
import android.graphics.Color;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.text.Editable;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextWatcher;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;

public class AlarmScreen extends Activity {

    public final String TAG = this.getClass().getSimpleName();

    private WakeLock mWakeLock;
    private MediaPlayer mPlayer;

    private static final int WAKELOCK_TIMEOUT = 60 * 1000;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_NOSENSOR);


        //Setup layout
        this.setContentView(R.layout.activity_alarm_screen);

        String name = getIntent().getStringExtra(AlarmManagerHelper.NAME);
        String saved_phrase = getIntent().getStringExtra(AlarmManagerHelper.PHRASE);

        int timeHour = getIntent().getIntExtra(AlarmManagerHelper.TIME_HOUR, 0);
        int timeMinute = getIntent().getIntExtra(AlarmManagerHelper.TIME_MINUTE, 0);
        String tone = getIntent().getStringExtra(AlarmManagerHelper.TONE);

        TextView tvName = (TextView) findViewById(R.id.alarm_screen_name);
        tvName.setText(name);

        TextView tvTime = (TextView) findViewById(R.id.alarm_screen_time);
        tvTime.setText(String.format("%02d : %02d", timeHour, timeMinute));

        final TextView phrase = (TextView) findViewById(R.id.phrase);
        phrase.setText(saved_phrase);

        final EditText editText = (EditText) findViewById(R.id.editText);
        editText.setText("");
        final SpannableString sb = new SpannableString(saved_phrase);

        editText.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_DEL && editText.getText().toString().length() < phrase.getText().toString().length()) {
                    final ForegroundColorSpan black = new ForegroundColorSpan(Color.BLACK);
                    sb.setSpan(black, editText.getText().toString().length(), editText.getText().toString().length() + 1, Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
                    phrase.setText(sb);
                }
                return false;
            }
        });
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                // Log.v(TAG, "Phrase: " + phrase.getText().toString());
                Log.v(TAG, "Sting to Check: " + editText.getText().toString());
                int curPos = editText.getText().toString().length();
                //Log.v(TAG, "String: " + a);
                Log.v(TAG, "length: " + editText.getText().toString().length());
                //  Log.v(TAG, "char: " + a.toString().substring(curPos - 1, curPos));
                //  Log.v(TAG, "char1: " + phrase.getText().toString().substring(curPos - 1, curPos));
                int i;

                final ForegroundColorSpan blue = new ForegroundColorSpan(Color.BLUE);
                final ForegroundColorSpan red = new ForegroundColorSpan(Color.RED);
                if (editText.getText().toString().length() <= phrase.getText().toString().length()) {
                    for (i = 0; i < curPos; i++) {
                        Log.v(TAG, "char1: " + editText.getText().toString().charAt(i));
                        Log.v(TAG, "char2: " + phrase.getText().toString().charAt(i));
                        if (editText.getText().toString().charAt(i) == phrase.getText().toString().charAt(i)) {
                            sb.setSpan(blue, i, i + 1, Spannable.SPAN_INCLUSIVE_EXCLUSIVE);


                        } else {
                            sb.setSpan(red, i, i + 1, Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
                        }
                    }

                    phrase.setText(sb);
                    if (phrase.getText().toString().equals(editText.getText().toString())) {

                        mPlayer.stop();
                        finish();
                    }
                }
            }

        });

        //Play alarm tone
        mPlayer = new MediaPlayer();
        try {
            if (tone != null && !tone.equals("")) {
                Uri toneUri = Uri.parse(tone);
                if (toneUri != null) {
                    mPlayer.setDataSource(this, toneUri);
                    mPlayer.setAudioStreamType(AudioManager.STREAM_ALARM);
                    mPlayer.setLooping(true);
                    mPlayer.prepare();
                    mPlayer.start();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        //Ensure wakelock release
        Runnable releaseWakelock = new Runnable() {

            @Override
            public void run() {
                getWindow().clearFlags(WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);
                getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
                getWindow().clearFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED);
                getWindow().clearFlags(WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD);

                if (mWakeLock != null && mWakeLock.isHeld()) {
                    mWakeLock.release();
                }
            }
        };

        new Handler().postDelayed(releaseWakelock, WAKELOCK_TIMEOUT);
    }

    @SuppressWarnings("deprecation")
    @Override
    protected void onResume() {
        super.onResume();

        // Set the window to keep screen on
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD);

        // Acquire wakelock
        PowerManager pm = (PowerManager) getApplicationContext().getSystemService(Context.POWER_SERVICE);
        if (mWakeLock == null) {
            mWakeLock = pm.newWakeLock((PowerManager.FULL_WAKE_LOCK | PowerManager.SCREEN_BRIGHT_WAKE_LOCK | PowerManager.ACQUIRE_CAUSES_WAKEUP), TAG);
        }

        if (!mWakeLock.isHeld()) {
            mWakeLock.acquire();
            Log.i(TAG, "Wakelock aquired!!");
        }

    }

    @Override
    protected void onPause() {
        super.onPause();

        if (mWakeLock != null && mWakeLock.isHeld()) {
            mWakeLock.release();
        }
    }
}
