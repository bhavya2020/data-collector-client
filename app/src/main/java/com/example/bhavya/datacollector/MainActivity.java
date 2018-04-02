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
    TextView AValuesX,lAValuesX,MValuesX,GValuesX;
    TextView AValuesY,lAValuesY,MValuesY,GValuesY;;
    TextView AValuesZ,lAValuesZ,MValuesZ,GValuesZ;;

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
                Sensor accelerometer=null,linearAcceleration=null,gyroscope=null,magnetometer=null;

                mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);

                AValuesX=findViewById(R.id.AValuesX);
                AValuesY=findViewById(R.id.AValuesY);
                AValuesZ=findViewById(R.id.AValuesZ);
                lAValuesX=findViewById(R.id.lAValuesX);
                lAValuesY=findViewById(R.id.lAValuesY);
                lAValuesZ=findViewById(R.id.lAValuesZ);
                MValuesX=findViewById(R.id.MValuesX);
                MValuesY=findViewById(R.id.MValuesY);
                MValuesZ=findViewById(R.id.MValuesZ);
                GValuesX=findViewById(R.id.GValuesX);
                GValuesY=findViewById(R.id.GValuesY);
                GValuesZ=findViewById(R.id.GValuesZ);

                accelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
                linearAcceleration = mSensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION);
                gyroscope = mSensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
                magnetometer = mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);

                if(accelerometer!=null)
                    mSensorManager.registerListener(MainActivity.this,accelerometer,SensorManager.SENSOR_DELAY_NORMAL);
                if(linearAcceleration!=null)
                    mSensorManager.registerListener(MainActivity.this,linearAcceleration,SensorManager.SENSOR_DELAY_NORMAL);
                if(gyroscope!=null)
                    mSensorManager.registerListener(MainActivity.this,gyroscope,SensorManager.SENSOR_DELAY_NORMAL);
                if(magnetometer!=null)
                    mSensorManager.registerListener(MainActivity.this,magnetometer,SensorManager.SENSOR_DELAY_NORMAL);
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
                AValuesX=findViewById(R.id.AValuesX);
                AValuesY=findViewById(R.id.AValuesY);
                AValuesZ=findViewById(R.id.AValuesZ);
                lAValuesX=findViewById(R.id.lAValuesX);
                lAValuesY=findViewById(R.id.lAValuesY);
                lAValuesZ=findViewById(R.id.lAValuesZ);
                MValuesX=findViewById(R.id.MValuesX);
                MValuesY=findViewById(R.id.MValuesY);
                MValuesZ=findViewById(R.id.MValuesZ);
                GValuesX=findViewById(R.id.GValuesX);
                GValuesY=findViewById(R.id.GValuesY);
                GValuesZ=findViewById(R.id.GValuesZ);
                String json="{\"ax\":\""+AValuesX.getText()+"\",\"ay\":\""+AValuesY.getText()+"\",\"az\":\""+AValuesZ.getText()+"\",\"lx\":\""+lAValuesX.getText()+"\",\"ly\":\""+lAValuesY.getText()+"\",\"lz\":\""+lAValuesZ.getText()+"\",\"gx\":\""+GValuesX.getText()+"\",\"gy\":\""+GValuesY.getText()+"\",\"gz\":\""+GValuesZ.getText()+"\",\"mx\":\"" +MValuesX.getText()+"\",\"my\":\""+MValuesY.getText()+"\",\"mz\":\""+MValuesZ.getText()+"\",\"class\":\"";
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
                            URL url = new URL("http://192.168.43.170:4444/sensor");
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
        if(event.sensor.getType()==Sensor.TYPE_ACCELEROMETER) {
            AValuesX.setText(String.valueOf(event.values[0]));
            AValuesY.setText(String.valueOf(event.values[1]));
            AValuesZ.setText(String.valueOf(event.values[2]));
        }
        if(event.sensor.getType()==Sensor.TYPE_LINEAR_ACCELERATION) {
            lAValuesX.setText(String.valueOf(event.values[0]));
            lAValuesY.setText(String.valueOf(event.values[1]));
            lAValuesZ.setText(String.valueOf(event.values[2]));
        }
        if(event.sensor.getType()==Sensor.TYPE_MAGNETIC_FIELD) {
            MValuesX.setText(String.valueOf(event.values[0]));
            MValuesY.setText(String.valueOf(event.values[1]));
            MValuesZ.setText(String.valueOf(event.values[2]));
        }
        if(event.sensor.getType()==Sensor.TYPE_GYROSCOPE) {
            GValuesX.setText(String.valueOf(event.values[0]));
            GValuesY.setText(String.valueOf(event.values[1]));
            GValuesZ.setText(String.valueOf(event.values[2]));
        }

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }


}



