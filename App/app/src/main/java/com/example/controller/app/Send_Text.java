package com.example.controller.app;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import com.example.controller.app.SimpleGestureFilter.SimpleGestureListener;

import java.util.ArrayList;
import java.util.Locale;

public class Send_Text extends AppCompatActivity implements SimpleGestureListener{
    final int SEND_SMS_PERMISSION_REQUEST_CODE=1;
    Button buttonSend;
    TextView textPhoneNo;
    TextView textSMS;

    private TextToSpeech tts;
    private TextView textView;
    private  TextView ph,msg;

    private SimpleGestureFilter detector;

    private final int REQ_CODE_SPEECH_INPUT1 = 101;
    private final int REQ_CODE_SPEECH_INPUT2 = 102;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send__text);

        // Detect touched area
        detector = new SimpleGestureFilter(Send_Text.this, this);

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

        ph = (TextView) findViewById(R.id.textViewPhoneNo);
        msg = (TextView) findViewById(R.id.textViewSMS);

        buttonSend = (Button) findViewById(R.id.buttonSend);
        textPhoneNo = (TextView) findViewById(R.id.editTextPhoneNo);
        textSMS = (TextView) findViewById(R.id.editTextSMS);

        textPhoneNo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getSpeechInput(REQ_CODE_SPEECH_INPUT1);
            }
        });

        textSMS.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getSpeechInput(REQ_CODE_SPEECH_INPUT2);
            }
        });

        buttonSend.setEnabled(false);
        if(checkPermission(Manifest.permission.SEND_SMS)){
            buttonSend.setEnabled(true);
            buttonSend.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    send();
                }
            });
        }
        else {
            ActivityCompat.requestPermissions(this,
                    new String[] {Manifest.permission.SEND_SMS}, SEND_SMS_PERMISSION_REQUEST_CODE);
        }
    }
    //Text to Speech
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
                getSpeechInput(REQ_CODE_SPEECH_INPUT1);
                showToastMessage = "Enter Phone Number";
                break;
            case SimpleGestureFilter.SWIPE_LEFT:
                getSpeechInput(REQ_CODE_SPEECH_INPUT2);
                showToastMessage = "Enter Your Message";
                break;
        }
        Toast.makeText(this, showToastMessage, Toast.LENGTH_SHORT).show();
    }

    //Toast shown when double tapped on screen
    @Override
    public void onDoubleTap() {
        Toast.makeText(this, "You have Double Tapped To Send Message", Toast.LENGTH_SHORT)
                .show();
        send();
    }

    //sending message
    public void send() {
        String phone=textPhoneNo.getText().toString();
        String sms=textSMS.getText().toString();

        if ( phone==null||phone.length()==0 || sms==null||sms.length()==0) {
            return;
        }
        else {
            if(checkPermission(Manifest.permission.SEND_SMS)){
                SmsManager smsManager=SmsManager.getDefault();
                smsManager.sendTextMessage(phone,null,sms,null,null);
                Toast.makeText(this, "Message Sent", Toast.LENGTH_SHORT).show();
            }
            else {
                Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public boolean checkPermission(String permission){
        int check= ContextCompat.checkSelfPermission(this,permission);
        return (check== PackageManager.PERMISSION_GRANTED);
    }

    //Speech to Text
    public void getSpeechInput(int req) {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());

        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(intent, req);
        } else {
            Toast.makeText(this, "Your Device Don't Support Speech Input", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case REQ_CODE_SPEECH_INPUT1: {
                if (resultCode == RESULT_OK && data != null) {
                    ArrayList<String> result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    textPhoneNo.setText(result.get(0));
                }
            }
            break;
            case REQ_CODE_SPEECH_INPUT2: {
                if (resultCode == RESULT_OK && data != null) {
                    ArrayList<String> result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    textSMS.setText(result.get(0));
                }
            }
            break;
        }
    }
}