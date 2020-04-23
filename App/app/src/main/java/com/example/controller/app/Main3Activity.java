package com.example.controller.app;

import android.content.Intent;
import android.speech.tts.TextToSpeech;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import com.example.controller.app.ocr.OcrCaptureActivity;

import java.util.Locale;

public class Main3Activity extends AppCompatActivity implements TextToSpeech.OnInitListener {

    private TextToSpeech myTTS;
    // status check code
    private int MY_DATA_CHECK_CODE = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main3);
        Intent checkTTSIntent = new Intent();
        checkTTSIntent
                .setAction(TextToSpeech.Engine.ACTION_CHECK_TTS_DATA);
        startActivityForResult(checkTTSIntent, MY_DATA_CHECK_CODE);
        Thread logoTimer = new Thread() {
            public void run() {
                try {
                    try {
                        sleep(3000);
                        speakWords("Welcome!  To detect Currency Notes swipe left, to Read Documents swipe right, and to send a Text swipe up.");
                    } catch (InterruptedException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }

                    Intent menuIntent = new Intent(Main3Activity.this, Main2Activity.class);
                    startActivity(menuIntent);

                }

                finally {
                    finish();
                }
            }

        };
        logoTimer.start();
    }

    // speak the user text
    private void speakWords(String speech) {

        // speak straight away
        if(myTTS != null)
        {
            myTTS.speak(speech, TextToSpeech.QUEUE_FLUSH, null);
        }
    }

    // act on result of TTS data check
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == MY_DATA_CHECK_CODE) {
            if (resultCode == TextToSpeech.Engine.CHECK_VOICE_DATA_PASS) {
                // the user has the necessary data - create the TTS
                myTTS = new TextToSpeech(this, this);
            } else {
                // no data - install it now
                Intent installTTSIntent = new Intent();
                installTTSIntent
                        .setAction(TextToSpeech.Engine.ACTION_INSTALL_TTS_DATA);
                startActivity(installTTSIntent);
            }
        }
    }

    // setup TTS
    public void onInit(int initStatus) {

        // check for successful instantiation
        if (initStatus == TextToSpeech.SUCCESS) {
            if (myTTS.isLanguageAvailable(Locale.getDefault()) == TextToSpeech.LANG_AVAILABLE)
                myTTS.setLanguage(Locale.getDefault());
        } else if (initStatus == TextToSpeech.ERROR) {
            Toast.makeText(this, "Sorry! Text To Speech failed...",
                    Toast.LENGTH_LONG).show();
        }
    }
}
