package com.example.bhavya.datacollector;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Calendar;
import java.util.List;
import java.util.logging.Handler;

import android.Manifest;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.RadioGroup;
import android.widget.TextView;


public class MainActivity extends Activity implements SensorEventListener{

    private static final int MY_REQUEST_CODE = 3;
    Intent service;
    String selectedSensor;
    AlarmManager alarm;
    PendingIntent pintent;
    SensorManager mSensorManager;
    RadioGroup SENSORS;
    RadioGroup storage;
    TextView ValuesX;
    TextView ValuesY;
    TextView ValuesZ;

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == MY_REQUEST_CODE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Now user should be able to use camera
                startService(service);
                Calendar cal = Calendar.getInstance();
                cal.add(Calendar.SECOND, 60);
                 pintent = PendingIntent.getService(this, 0, service, 0);
                 alarm = (AlarmManager) getSystemService(Context.ALARM_SERVICE);

                alarm.setRepeating(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(),
                        60 * 1000, pintent);
            }
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        if (ContextCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.CAMERA}, 50);
        }
        service = new Intent(getBaseContext(), CapPhoto.class);


        Button btn = findViewById(R.id.start);
        btn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                if (checkSelfPermission(Manifest.permission.CAMERA)
                        != PackageManager.PERMISSION_GRANTED) {

                    requestPermissions(new String[]{Manifest.permission.CAMERA}, MY_REQUEST_CODE);
                } else {
                    startService(service);
                    Calendar cal = Calendar.getInstance();
                    cal.add(Calendar.SECOND, 60);
                     pintent = PendingIntent.getService(MainActivity.this, 0, service, 0);
                     alarm = (AlarmManager) getSystemService(Context.ALARM_SERVICE);

                    alarm.setRepeating(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(),
                            60 * 1000, pintent);
                }
            }

        });
        Button btn2 = findViewById(R.id.stop);
        btn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                stopService(service);
                if(alarm !=null)
                {
                    alarm.cancel(pintent);
                }

            }
        });
        Button btn3 = findViewById(R.id.sensor);
        btn3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Sensor mSensor=null;
                mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);

                SENSORS=findViewById(R.id.SENSORS);
                ValuesX=findViewById(R.id.ValuesX);
                ValuesY=findViewById(R.id.ValuesY);
                ValuesZ=findViewById(R.id.ValuesZ);

                int sensorID=SENSORS.getCheckedRadioButtonId();

                if(sensorID == R.id.Accelerometer)
                {
                    selectedSensor="Accelerometer";
                    mSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
                }
                else  if(sensorID == R.id.lAcc)
                {
                    selectedSensor="linear Acceleration";
                    mSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION);
                }else  if(sensorID == R.id.gyroscope)
                {
                    selectedSensor="gyroscope";
                    mSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
                }else  if(sensorID == R.id.magnetometer)
                {
                    selectedSensor="magnetometer";
                    mSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
                }


                if(mSensor!=null)
                    mSensorManager.registerListener(MainActivity.this,mSensor,SensorManager.SENSOR_DELAY_NORMAL);
            }
        });
        Button btn4 = findViewById(R.id.sensorStop);
        btn4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mSensorManager.unregisterListener(MainActivity.this);
            }
        });
        Button btn5 = findViewById(R.id.store);
        btn5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                storage=findViewById(R.id.storage);
                ValuesX=findViewById(R.id.ValuesX);
                ValuesY=findViewById(R.id.ValuesY);
                ValuesZ=findViewById(R.id.ValuesZ);

                String json="{\"sensor\":\""+selectedSensor+"\",\"x\":\""+ValuesX.getText()+"\",\"y\":\""+ValuesY.getText()+"\",\"z\":\""+ValuesZ.getText()+"\",\"class\":\"";
                int storageID=storage.getCheckedRadioButtonId();

                if(storageID == R.id.aggressiveAcceleration)
                {
                    json=json.concat("aggressiveAcceleration");
                }else  if(storageID == R.id.aggressiveBreaking)
                {
                    json=json.concat("aggressiveBreaking");
                }else  if(storageID == R.id.aggressiveLeft)
                {
                    json=json.concat("aggressiveLeft");
                }else  if(storageID == R.id.aggressiveRight)
                {
                    json=json.concat("aggressiveRight");
                }else  if(storageID == R.id.aggressiveLeftLane)
                {
                    json=json.concat("aggressiveLeftLane");
                }else  if(storageID == R.id.aggressiveRightLane)
                {
                    json=json.concat("aggressiveRightLane");
                }else  if(storageID == R.id.nonAggressive)
                {
                    json=json.concat("nonAggressive");
                }

                json=json.concat("\"}");
                final String finalJson = json;
                final StringBuffer response=new StringBuffer();
                final Thread thread = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            URL url = new URL("http://192.168.1.11:4444/sensor");
                            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                            conn.setDoOutput(true);
                            conn.setRequestMethod("POST");
                            conn.setRequestProperty("Content-Type", "application/json");
                            OutputStream os=null;
                            os = conn.getOutputStream();
                            os.write(finalJson.getBytes());
                            os.flush();
                            os.close();
                            conn.getResponseCode();
                            BufferedReader in = new BufferedReader(
                                    new InputStreamReader(conn.getInputStream()));
                            String inputLine;
                            while ((inputLine = in.readLine()) != null) {
                                response.append(inputLine);
                            }
                            in.close();
                            conn.disconnect();

                        }catch (Exception e){
                            //error occured
                            Log.d("error",e.toString());
                        }
                    }
                });
                thread.start();
                try {
                    thread.join();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                if(response.toString().equals("got"))
                {
                    Log.d("success","sensor data sent");
                }


            }
        });

    }
    public void onSensorChanged(SensorEvent event){
        ValuesX.setText( String.valueOf(event.values[0]));
        ValuesY.setText( String.valueOf(event.values[1]));
        ValuesZ.setText( String.valueOf(event.values[2]));
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }


}



