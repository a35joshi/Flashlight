package flashlightapp.flashlight;
//Basic application to create a torch.
//BY ANURAG JOSHI
//UNIVERSITY OF WATERLOO.
/*
To fix issue of torch being turned off when:
1)Using a handler.
*/
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.hardware.Camera;
import android.hardware.Camera.Parameters;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import java.util.logging.Level;
import java.util.logging.Logger;

public class Flashlight extends AppCompatActivity {
    Button on, off, SOSbutton;
    private Camera myCamera;
    private boolean FlashOn;
    private boolean ScreenOn;
    private boolean FlashSupport;
    private boolean SOSon;
    private boolean FlashThreadStop=false;
    Parameters myParameters;
    Thread Flashthread = new Thread();
    Logger logger = Logger.getAnonymousLogger();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_flashlight);
        getWindow().getDecorView().setBackgroundColor(Color.BLACK);
        startService(new Intent(this, FlashLightService.class));
        on = (Button) findViewById(R.id.ONbutton);
        off = (Button) findViewById(R.id.OFFbutton);
        SOSbutton = (Button) findViewById(R.id.SOSButton1);
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
        on.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (FlashOn) {
                    // turn off flash
                    //TurnOffFlash();
                } else {
                    // turn on flash
                    TurnOnFlash();
                }
            }
        });
        off.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (FlashOn) {
                    // turn off flash
                    TurnOffFlash();
                } else {
                    // turn on flash
                    // TurnOffFlash();
                }
            }
        });

        SOSbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    SOSon = true;
                    startSOS();
                    onSOSPress();
                } catch (Exception ex) {
                    throw ex;
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

    private void TurnOnFlash() {
        if (!FlashOn) {
            if (myCamera == null || myParameters == null) {
                return;
            }
            myParameters = myCamera.getParameters();
            try {
                myParameters.setFlashMode(Parameters.FLASH_MODE_TORCH);
            } catch (Exception ex) {
                throw ex;
            }
            myCamera.setParameters(myParameters);
            myCamera.startPreview();
            FlashOn = true;
        }

    }

    private void TurnOffFlash() {
        if (FlashOn) {
            if (myCamera == null || myParameters == null) {
                return;
            }
            myParameters = myCamera.getParameters();
            myParameters.setFlashMode(Parameters.FLASH_MODE_OFF);
            myCamera.setParameters(myParameters);
            myCamera.stopPreview();
            try {
                if (SOSon)
                {
                endSOS();
                    SOSon = false;
                }
            } catch (Exception ex) {
                throw ex;
            }
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
        FlashOn = false;
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

    void onSOSPress() {
            try {
              Flashlight flashlight=new Flashlight();
                SOSon = true;
                   flashlight.Flashthread = new Thread(new Runnable() {
                            @Override
                            public void run() {
                                for (int i = 0; i < System.currentTimeMillis(); i++) {
                                    while (!FlashThreadStop) {
                                        if (FlashOn) {
                                           myParameters.setFlashMode(Parameters.FLASH_MODE_OFF);
                                            try {
                                                myCamera.setParameters(myParameters);
                                            }
                                            catch (Exception ex)
                                            {
                                                logger.log(Level.SEVERE, "an exception was thrown", ex);
                                            }
                                           myCamera.stopPreview();
                                           FlashOn = false;
                                        } else {
                                            TurnOnFlash();
                                        }
                                        try {
                                            Thread.sleep(1000);
                                        } catch (InterruptedException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                /*if (!SOSon) {
                                    flashlight.Flashthread.stop();
                                    break;
                                }*/
                                }
                            }
                        });
                       flashlight.Flashthread.start();
            } catch (Exception ex) {
                throw ex;
            }
        }
    void endSOS(){
        FlashThreadStop=true;
    }
    void startSOS(){
        FlashThreadStop=false;
    }
}
