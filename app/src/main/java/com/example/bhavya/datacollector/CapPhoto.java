package com.example.bhavya.datacollector;

/**
 * Created by bhavya on 12/3/18.
 */


import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.Calendar;


import android.Manifest;
import android.app.Service;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.hardware.Camera.CameraInfo;
import android.hardware.Camera.Parameters;
import android.os.Build;
import android.os.Environment;
import android.os.IBinder;
import android.os.StrictMode;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Base64;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.Toast;


public class CapPhoto extends Service
{


    private SurfaceHolder sHolder;
    private Camera mCamera;
    private Parameters parameters;

    @Override
    public void onCreate()
    {
        super.onCreate();
        Log.d("CAM", "start");
    }
    @RequiresApi(api = Build.VERSION_CODES.M)

    @Override
    public int onStartCommand (Intent intent,int flags, int startId) {

        Toast toast=null;
        toast.makeText(this,"service running "+startId,Toast.LENGTH_LONG).show();
        int cameras=Camera.getNumberOfCameras();
        if (cameras >= 2) {


            try {
                int id=CameraInfo.CAMERA_FACING_FRONT;
               mCamera = Camera.open(id);
               // mCamera=null;
            } catch (Exception e)
            {
                toast.makeText(this,"err in open "+e.toString(), Toast.LENGTH_LONG).show();
                Log.d("err",e.toString());
            }
        }

        if (Camera.getNumberOfCameras() < 2) {

            mCamera = Camera.open(); }

  if(mCamera !=null) {
      toast.makeText(this,"yoyo",Toast.LENGTH_LONG).show();
      SurfaceView sv = new SurfaceView(getApplicationContext());
      try {
          mCamera.setPreviewDisplay(sv.getHolder());
          parameters = mCamera.getParameters();
          mCamera.setParameters(parameters);
          mCamera.startPreview();
          Log.d("START","STARTED");
          mCamera.takePicture(null,null,null,mCall);

      } catch (Exception e) {

          e.printStackTrace();
      }

      sHolder = sv.getHolder();
      sHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
  }
        return  START_NOT_STICKY;
    }


    Camera.PictureCallback mCall = new Camera.PictureCallback()
    {
        public void onPictureTaken(final byte[] data, Camera camera)
        {
            final StringBuffer response=new StringBuffer();
            Thread thread = new Thread(new Runnable() {

                @Override
                public void run() {
                    byte[] postData=null;
                    try  {
                        URL url = null;
                        try {
                            url = new URL("http://192.168.1.11:4444/click");
                        } catch (MalformedURLException e) {
                            e.printStackTrace();
                        }
                        HttpURLConnection conn = null;
                        try {
                            assert url != null;
                            conn = (HttpURLConnection) url.openConnection();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                        assert conn != null;
                        conn.setDoOutput(true);
                        try {
                            conn.setRequestMethod("POST");
                            conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                            String json =java.net.URLEncoder.encode(Base64.encodeToString(data,Base64.DEFAULT), "ISO-8859-1");
                            String urlParameters  = "img="+json;
                             postData = urlParameters.getBytes( StandardCharsets.UTF_8 );
                            int postDataLength = postData.length;
                            conn.setRequestProperty("Content-Length", Integer.toString(postDataLength ));
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        OutputStream os = null;
                        try {
                            os = conn.getOutputStream();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        try {
                            assert os != null;
                            os.write(postData);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        try {
                            os.flush();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
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


                        conn.disconnect();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    camkapa(sHolder);
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
                Log.d("success","image sent");
            }
        }
    };


    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    public void camkapa(SurfaceHolder sHolder) {

        if (null == mCamera)
            return;
        mCamera.stopPreview();
        mCamera.release();
        mCamera = null;
        Log.i("CAM", " closed");
    }

}


