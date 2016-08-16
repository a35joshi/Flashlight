package flashlightapp.flashlight;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class Flashlight extends AppCompatActivity {

    Button onoff;
    boolean clicked=false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_flashlight);
        InitialiseViews();
        onoff.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //click detected!
                clicked = true;
                FlashlightLogic();
            }
        });
    }
    void InitialiseViews() {
        try {
       onoff=(Button)findViewById(R.id.ONOFFBUTTON);
        }
catch (Exception ex){
    throw ex;
}

    }
    void FlashlightLogic(){
        
    }
}

