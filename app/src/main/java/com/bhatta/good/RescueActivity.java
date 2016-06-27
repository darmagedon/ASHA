package com.bhatta.good;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.renderscript.Double2;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;


public class RescueActivity extends Activity {

    public static Boolean isTouch = true;
    final static int INTERVAL = 100; // 1 second
    private static View myView = null;
    boolean redColour = true;
    TextView counter ;
    Thread t;
    CountDownTimer cDT;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.rescue_activity);
        //Log.d("istouch",Boolean.toString(isTouch));
        myView = (View) findViewById(R.id.my_view);
        counter = (TextView) findViewById(R.id.counter);

        myView.setBackgroundColor(Color.RED);

        cDT = new CountDownTimer(15000, 1000) {

            public void onTick(long millisUntilFinished) {
                counter.setText(Long.toString(millisUntilFinished / 1000));
            }

            public void onFinish() {
                String phoneNumber="9851197617",smsText="Accident occured at";
                Toast.makeText(getApplicationContext(), "Rescue Message Sent", Toast.LENGTH_LONG).show();
                SmsManager smsManager = SmsManager.getDefault();
                //MainActivity m = new MainActivity();
                Log.d("Location",Double.toString(MainActivity.latitude) + Double.toString(MainActivity.longitude));
                smsManager.sendTextMessage(phoneNumber,null, smsText+ Double.toString(MainActivity.latitude) + Double.toString(MainActivity.longitude),null,null);

                //System.exit(0);
                //onDestroy();
            }
        };
        cDT.start();

        t= new Thread(new Runnable() {
            public void run() {
                while (true){
                    try {
                        Thread.sleep(INTERVAL);
                    }
                    catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    if(isTouch) {
                        updateColor();
                        redColour = !redColour;
                    }
                }
            }


        });
        t.start();
    }
    private void updateColor() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (redColour)
                    myView.setBackgroundColor(Color.RED);
                else
                    myView.setBackgroundColor(Color.GREEN);
            }
        });
    }

    public void screenTapped(View view) {
        Toast.makeText(getApplicationContext(), "Rescue operation aborted", Toast.LENGTH_LONG).show();
        super.onBackPressed();
    }
}