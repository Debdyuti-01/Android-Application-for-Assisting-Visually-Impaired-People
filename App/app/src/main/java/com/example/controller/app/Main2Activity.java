package com.example.controller.app;

import android.content.Intent;
import android.speech.tts.TextToSpeech;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.controller.app.currency.ClassifierActivity;
import com.example.controller.app.ocr.OcrCaptureActivity;

import com.example.controller.app.SimpleGestureFilter.SimpleGestureListener;

import java.util.Locale;
import java.util.Queue;

public class Main2Activity extends AppCompatActivity implements SimpleGestureListener{

    private TextToSpeech tts;
    private TextView textView;
    private SeekBar s1,s2;

    private SimpleGestureFilter detector;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        // Detect touched area
        detector = new SimpleGestureFilter(Main2Activity.this, this);

        tts = new TextToSpeech(this, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {

                if (status == TextToSpeech.SUCCESS) {
                    int result = tts.setLanguage(Locale.getDefault());

                    if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                        Log.e("TTS", "Language Not Supported");
                    } else {

                    }
                } else{
                    Log.e("TTS","Initialization failed");
                }
            }
        });
    }
    @Override
    public boolean dispatchTouchEvent(MotionEvent me) {
        // Call onTouchEvent of SimpleGestureFilter class
        this.detector.onTouchEvent(me);
        return super.dispatchTouchEvent(me);
    }

    @Override
    public void onSwipe(int direction) {
        //Detect the swipe gestures and display toast
        String showToastMessage = "";
        switch (direction) {

            case SimpleGestureFilter.SWIPE_RIGHT:
                                                    Intent i = new Intent(Main2Activity.this, OcrCaptureActivity.class);
                                                    tts.speak("Read Documents",TextToSpeech.QUEUE_FLUSH,null);
                                                    startActivity(i);
                break;
            case SimpleGestureFilter.SWIPE_LEFT:
                                                    Intent i1 = new Intent(Main2Activity.this, ClassifierActivity.class);
                                                    tts.speak("Detect Currency",TextToSpeech.QUEUE_FLUSH,null);
                                                    startActivity(i1);
                break;
            case SimpleGestureFilter.SWIPE_UP:
                                                    Intent i2 = new Intent(Main2Activity.this, Send_Text.class);
                                                    tts.speak("Send Message.... Swipe right to enter phone number, Swipe Left to enter message, and Double Tap to send message",TextToSpeech.QUEUE_FLUSH,null);
                                                    startActivity(i2);
                break;

        }
    }


    //Toast shown when double tapped on screen
    @Override
    public void onDoubleTap() {
        textView=findViewById(R.id.text);
        String text =textView.getText().toString();
        s1=findViewById(R.id.s1);
        s2=findViewById(R.id.s2);

        float pitch=(float) s1.getProgress()/50;
        if(pitch<0.1) pitch=0.1f;
        tts.setPitch(pitch);
        float speed=(float) s2.getProgress()/50;
        if(speed<0.1) speed=0.1f;
        tts.setSpeechRate(speed);

        tts.speak(text,TextToSpeech.QUEUE_FLUSH,null);
        Toast.makeText(this, "You have Double Tapped.", Toast.LENGTH_SHORT)
                .show();
    }
}

