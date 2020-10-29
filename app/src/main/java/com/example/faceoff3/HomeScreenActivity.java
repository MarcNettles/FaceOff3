package com.example.faceoff3;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
/* Our home screen once the user has logged in. */
public class HomeScreenActivity extends AppCompatActivity
{





    DatabaseHelper myDB;


    TextView textView_AHS_randomTip, textView_AHS_touchedFace, textView_AHS_washedHands, textView_AHS_warnBehaviorBottom;



    ImageButton imageButton_AHS_informativeTab, imageButton_AHS_settings, imageButton_AHS_increaseWashedHands, imageButton_AHS_maps, imageButton_AHS_increaseFaceTouches;



    ImageView imageView_AHS_imgFaceOff, imageView_AHS_tamagochi;






    @Override
    protected void onCreate(Bundle savedInstanceState)
    {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_screen);

        /*String riskAssessment = "";

        if(((double)MainActivity.fTouches.intValue() / (double)MainActivity.washedHands.intValue()) > 2 )
        {
            riskAssessment = "Risky";
        }
        else if(((double)MainActivity.fTouches.intValue() /(double) MainActivity.washedHands.intValue()) > 0 && (((double)MainActivity.fTouches.intValue() / (double)MainActivity.washedHands.intValue()) < 2 ) )
        {
            riskAssessment = "Moderately Risky";
        }
        else
        {
            riskAssessment = "Safe";
        }*/


        myDB = new DatabaseHelper(this);


        textView_AHS_randomTip = (TextView)findViewById(R.id.textView_AHS_randomTip);
        textView_AHS_touchedFace = (TextView)findViewById(R.id.textView_AHS_touchedFace);
        textView_AHS_washedHands = (TextView)findViewById(R.id.textView_AHS_washedHands);
        textView_AHS_warnBehaviorBottom = (TextView)findViewById(R.id.textView_AHS_warnBehaviorBottom);

        imageButton_AHS_informativeTab = (ImageButton)findViewById(R.id.imageButton_AS_informativeTab);
        imageButton_AHS_settings = (ImageButton)findViewById(R.id.imageButton_AHS_settings);
        imageButton_AHS_increaseWashedHands = (ImageButton)findViewById(R.id.button_AHS_increaseWashedHands);
        imageButton_AHS_maps = (ImageButton)findViewById(R.id.imageButton_AHS_maps);
        imageButton_AHS_increaseFaceTouches = (ImageButton)findViewById(R.id.imageButton_AHS_increaseFaceTouches);



        imageView_AHS_imgFaceOff = (ImageView)findViewById(R.id.imageView_AHS_imgFaceOff);
        imageView_AHS_tamagochi = (ImageView)findViewById(R.id.imageView_AHS_tamagochi);






        textView_AHS_randomTip.setText(myDB.getRandomTip());
        textView_AHS_touchedFace.setText("You have touched your face "+myDB.getfTouches(MainActivity.currentActiveUser).toString()+" times today.");
        textView_AHS_washedHands.setText("You have washed your hands "+MainActivity.washedHands + " times today.");
        //textView_AHS_warnBehaviorBottom.setText("Behavior is " +riskAssessment+".");

        updateRisk();


        goToInformativeTab();
        goToSettings();
        goToMaps();


        increaseWashedHands();
        increaseFaceTouches();



    }


    public void goToMaps()
    {
        imageButton_AHS_maps.setOnClickListener
                (
                        new View.OnClickListener()
                        {
                            @Override
                            public void onClick(View v)
                            {
                                Intent intent = new Intent(HomeScreenActivity.this, MapActivity.class);
                                startActivity(intent);
                            }
                        }
                );
    }


    public void updateRisk()
    {

        String riskAssessment = "";

        if(((double)MainActivity.fTouches.intValue() / (double)MainActivity.washedHands.intValue()) > 2 )
        {
            riskAssessment = "Risky";
            imageView_AHS_tamagochi.setImageResource(R.drawable.sad_cell);
        }
        else if(((double)MainActivity.fTouches.intValue() /(double) MainActivity.washedHands.intValue()) > 1 && (((double)MainActivity.fTouches.intValue() / (double)MainActivity.washedHands.intValue()) <= 2 ) )
        {
            riskAssessment = "Moderately Risky";
            imageView_AHS_tamagochi.setImageResource(R.drawable.indif_cell);
        }
        else
        {
            riskAssessment = "Safe";
            imageView_AHS_tamagochi.setImageResource(R.drawable.happy_cell);
        }

        textView_AHS_warnBehaviorBottom.setText("Behavior is " +riskAssessment+".");
    }
    public void goToSettings()
    {
        imageButton_AHS_settings.setOnClickListener
                (
                        new View.OnClickListener()
                        {
                            @Override
                            public void onClick(View v)
                            {
                                Intent intent = new Intent(HomeScreenActivity.this, SettingsActivity.class);
                                startActivity(intent);

                            }
                        }
                );
    }


    public void increaseWashedHands()
    {
        imageButton_AHS_increaseWashedHands.setOnClickListener
                (
                        new View.OnClickListener()
                        {
                            @Override
                            public void onClick(View v)
                            {
                                MainActivity.washedHands = MainActivity.washedHands + 1;
                                textView_AHS_washedHands.setText("You have washed your hands "+MainActivity.washedHands + " times today.");
                                updateRisk();
                            }
                        }
                );
    }

    public void increaseFaceTouches()
    {
        imageButton_AHS_increaseFaceTouches.setOnClickListener
                (
                        new View.OnClickListener()
                        {
                            @Override
                            public void onClick(View v)
                            {
                            MainActivity.fTouches = MainActivity.fTouches + 1;
                            textView_AHS_touchedFace.setText("You have touched your face "+MainActivity.fTouches+" times today.");
                            updateRisk();
                            }
                        }

                );
    }



    // For now this is tied to a button, but we might want to make it look nicer.
public void goToInformativeTab()
{
    imageButton_AHS_informativeTab.setOnClickListener
            (
                    new View.OnClickListener()
                    {
                        @Override
                        public void onClick(View v)
                        {
                            Intent intent = new Intent(HomeScreenActivity.this, InformativeTabActivity.class);
                            startActivity(intent);

                        }
                    }
            );
}






}
