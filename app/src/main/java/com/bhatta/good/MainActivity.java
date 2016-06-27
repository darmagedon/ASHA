package com.bhatta.good;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.WindowManager;
import android.widget.TextView;

import java.text.Format;
import java.text.NumberFormat;


public class MainActivity extends Activity implements SensorEventListener {

    private float lastX, lastY, lastZ;  // Store Sensor Current Value which is the ACCELERATION/DEACCELERATION in X,Y,Z axis.

    private SensorManager sensorManager;
    private Sensor accelerometer;

    // To show maximum ACCELERATION VALUE
    private float deltaXMax = 0;
    private float deltaYMax = 0;
    private float deltaZMax = 0;

//     Current value - last value
    private float deltaX = 0;
    private float deltaY = 0;
    private float deltaZ = 0;

    private TextView currentX, currentY, currentZ, maxX, maxY, maxZ,accident;

    // For Position
    public static double latitude, longitude;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        // FOR GETTING LATTITUDE AND LONGITUDE FROM GPS
        GetLocation.LocationResult locationResult = new GetLocation.LocationResult(){
            @Override
            public void gotLocation(Location location){
                //Got the location!
                Log.d("location",Double.toString(location.getLatitude()) + ", " + Double.toString(location.getLongitude()));
                latitude = location.getLatitude();
                longitude = location.getLongitude();
            }
        };
        GetLocation myLocation = new GetLocation();
        myLocation.getLocation(this, locationResult);


        // Log.d("","Work1");
        initializeViews();
        //Log.d("","Work2");

        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        if (sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER) != null) {
            // success! we have an accelerometer

            accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
            sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);

        } else {
            // fail ! we dont have an accelerometer!
        }

    }

    public void initializeViews() {
        currentX = (TextView) findViewById(R.id.currentX);
        currentY = (TextView) findViewById(R.id.currentY);
        currentZ = (TextView) findViewById(R.id.currentZ);
        accident = (TextView) findViewById(R.id.accident);

        maxX = (TextView) findViewById(R.id.maxX);
        maxY = (TextView) findViewById(R.id.maxY);
        maxZ = (TextView) findViewById(R.id.maxZ);
    }

    //onResume() register the accelerometer for listening the events
    protected void onResume() {
        super.onResume();
        sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);
    }

    //onPause() unregister the accelerometer for stop listening the events
    protected void onPause() {
        super.onPause();
        sensorManager.unregisterListener(this);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    @Override
    public void onSensorChanged(SensorEvent event) {

        // clean current values
        //displayCleanValues();
        // display the current x,y,z accelerometer values
        displayCurrentValues();
        // display the max x,y,z accelerometer values
        displayMaxValues();

        // get the change of the x,y,z values of the accelerometer
        deltaX = Math.abs(lastX - event.values[0]);
        deltaY = Math.abs(lastY - event.values[1]);
        deltaZ = Math.abs(lastZ - event.values[2]);

        // if the change is below 2, it is just plain noise
        if (deltaX < 2)
            deltaX = 0;
        if (deltaY < 2)
            deltaY = 0;
        if (deltaZ < 2)
            deltaZ = 0;

        // set the last know values of x,y,z
        lastX = event.values[0];
        lastY = event.values[1];
        lastZ = event.values[2];



    }
    public void displayCleanValues() {
        currentX.setText("0.0");
        currentY.setText("0.0");
        currentZ.setText("0.0");
    }

    // display the current x,y,z accelerometer values
    public void displayCurrentValues() {

        NumberFormat formatter = NumberFormat.getNumberInstance();
        formatter.setMinimumFractionDigits(2);
        formatter.setMaximumFractionDigits(2);

        currentX.setText(formatter.format(lastX));
        currentY.setText(formatter.format(lastY));
        currentZ.setText(formatter.format(lastZ));

        if(lastY<=-10.5 || lastY >=10.5){
            accident.setText("Accident:CRASH TYPE: "+ formatter.format(lastY));
            Intent intent = new Intent(this, RescueActivity.class);
            startActivity(intent);
        }
        if (lastX>=7.5|| lastX<=-7.5){
            accident.setText("Accident:TILT TYPE: "+ formatter.format(lastX));
            Intent intent = new Intent(this, RescueActivity.class);
            startActivity(intent);
        }

    }

    // display the max x,y,z accelerometer values
    public void displayMaxValues() {
        if (deltaX > deltaXMax) {
            deltaXMax = deltaX;
            maxX.setText(Float.toString(deltaXMax));
        }
        if (deltaY > deltaYMax) {
            deltaYMax = deltaY;
            maxY.setText(Float.toString(deltaYMax));
        }
        if (deltaZ > deltaZMax) {
            deltaZMax = deltaZ;
            maxZ.setText(Float.toString(deltaZMax));
        }
    }
}

