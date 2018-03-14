package com.example.bhavya.datacollector;

/**
 * Created by bhavya on 12/3/18.
 */

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
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

//
//        if (android.os.Build.VERSION.SDK_INT > 9) {
//            StrictMode.ThreadPolicy policy =
//                    new StrictMode.ThreadPolicy.Builder().permitAll().build();
//            StrictMode.setThreadPolicy(policy);}
//          Thread myThread = null;


    }
    @RequiresApi(api = Build.VERSION_CODES.M)

    @Override
    public int onStartCommand (Intent intent,int flags, int startId) {

        Toast toast=null;
        toast.makeText(this,"service running "+startId,Toast.LENGTH_LONG).show();
//        try {
//            toast.makeText(this, Camera.getNumberOfCameras(), Toast.LENGTH_LONG).show();
//        }catch (Exception e)
//        {
//            toast.makeText(this,"err in no of cameras "+ e.toString(), Toast.LENGTH_LONG).show();
//        }
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
            Toast toast=null;
            toast.makeText(CapPhoto.this,data.length,Toast.LENGTH_LONG).show();
            FileOutputStream outStream = null;
            try{

                File sd = new File(Environment.getExternalStorageDirectory(), "A");
                if(!sd.exists()) {
                    sd.mkdirs();
                    Log.i("FO", "folder" + Environment.getExternalStorageDirectory());
                }

                Calendar cal = Calendar.getInstance();
                SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmss");
                String tar = (sdf.format(cal.getTime()));

                outStream = new FileOutputStream(sd+tar+".jpg");
                outStream.write(data);
                outStream.close();

                Log.i("CAM", data.length + " byte written to:"+sd+tar+".jpg");
                camkapa(sHolder);


            } catch (FileNotFoundException e){
                Log.d("CAM", e.getMessage());
            } catch (Exception e){
                Log.d("CAM", e.getMessage());
            }}
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


