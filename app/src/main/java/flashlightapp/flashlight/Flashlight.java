package flashlightapp.flashlight;
//Basic application to create a torch.
//BY ANURAG JOSHI
//UNIVERSITY OF WATERLOO.
/*
To fix issue of torch being turned off when:
1)Phone screen locked
2)make it run in the background
*/
import android.app.AlertDialog;
import android.app.KeyguardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.hardware.Camera;
import android.hardware.Camera.Parameters;
import android.os.PowerManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.Toast;

import java.util.Timer;
import java.util.TimerTask;

public class Flashlight extends AppCompatActivity {
    Button onoff;
    private Camera myCamera;
    private boolean FlashOn;
    private boolean ScreenOn;
    private boolean FlashSupport;
    Parameters myParameters;
    PowerManager pm;
    Timer mTimer;
    TimerTask mTimerTask;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_flashlight);
        getWindow().getDecorView().setBackgroundColor(Color.BLACK);
        startService(new Intent(this, FlashLightService.class));
        onoff=(Button)findViewById(R.id.onoffbutton);
        //checking if hardware supports torchlight or not!
        FlashSupport = getApplicationContext().getPackageManager()
                .hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH);
        //if not then throw error
        if (!FlashSupport) {
            // device doesn't support flash
            // Show alert message and close the application
            AlertDialog alert = new AlertDialog.Builder(Flashlight.this)
                    .create();
            alert.setTitle("Error");
            alert.setMessage("YOUR DEVICE HARDWARE DOESN'T SUPPORT FLASHLIGHT");
            alert.setButton("OK", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    // closing the application
                    finish();
                }
            });
            alert.show();
            return;
        }

        //Get Camera features
        getCamera();
        onoff.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (FlashOn) {
                    // turn off flash
                    TurnOffFlash();
                } else {
                    // turn on flash
                    TurnOnFlash();
                }
            }
        });
    }

    // Get the camera
    public void getCamera() {
        if (myCamera == null) {
            try {
                myCamera = Camera.open();
                myParameters = myCamera.getParameters();
            } catch (RuntimeException e) {
                //need to give app permission explicitly for Android 6.0 and upwards.
                throw e;
            }
        }
    }

    public void TurnOnFlash() {
        if (!FlashOn) {
            if (myCamera == null || myParameters == null) {
                return;
            }
            myParameters = myCamera.getParameters();
            myParameters.setFlashMode(Parameters.FLASH_MODE_TORCH);
            myCamera.setParameters(myParameters);
            myCamera.startPreview();
            FlashOn = true;
        }

    }
    public void TurnOffFlash() {
        if (FlashOn) {
            if (myCamera == null || myParameters == null) {
                return;
            }
            myParameters = myCamera.getParameters();
            myParameters.setFlashMode(Parameters.FLASH_MODE_OFF);
            myCamera.setParameters(myParameters);
            myCamera.stopPreview();
            FlashOn = false;
     }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

   /* @Override
    protected void onPause() {
        super.onPause();

        // on pause turn off the flash
        TurnOffFlash();
    }*/

    @Override
    protected void onRestart() {
        FlashOn=false;
        super.onRestart();
    }

    @Override
    protected void onResume() {
        super.onResume();

        // on resume turn on the flash
       // if(FlashSupport)
         //   TurnOnFlash();
    }

    @Override
    protected void onStart() {
        super.onStart();

        // on starting the app get the camera params
        getCamera();
    }

    /*@Override
    protected void onStop() {
        super.onStop();

        // on stop release the camera
        if (myCamera != null) {
            myCamera.release();
            myCamera = null;
        }
    }*/
    @Override
    public void onBackPressed() {
        super.onBackPressed();

        myParameters = myCamera.getParameters();
        myParameters.setFlashMode(Parameters.FLASH_MODE_OFF);
        myCamera.setParameters(myParameters);
        myCamera.stopPreview();
        FlashOn = false;

        if (myCamera != null) {
            myCamera.release();
            myCamera = null;
        }
    }

}
