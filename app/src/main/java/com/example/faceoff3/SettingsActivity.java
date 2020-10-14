package com.example.faceoff3;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class SettingsActivity extends AppCompatActivity
{

    DatabaseHelper myDB;



    TextView textView_AS_helloMessage;

    Button button_AS_logOut;
    ImageButton imageButton_AHS_informativeTab;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        myDB = new DatabaseHelper(this);



        textView_AS_helloMessage = (TextView)findViewById(R.id.textView_AS_helloMessage);

        button_AS_logOut = (Button)findViewById(R.id.button_AS_logOut);
        imageButton_AHS_informativeTab = (ImageButton)findViewById(R.id.imageButton_AS_informativeTab);





        textView_AS_helloMessage.setText("Hello, "+MainActivity.currentActiveUser+"!");



        goToInformativeTab();
        logOut();

    }




    public void logOut()
    {
        button_AS_logOut.setOnClickListener
                (
                        new View.OnClickListener()
                        {
                            @Override
                            public void onClick(View v)
                            {


                                /* Clear out the variables and reset the info, just in case they get back to this screen after logging out */
                                MainActivity.currentActiveUser = "";
                                MainActivity.fTouches = 0;
                                MainActivity.washedHands = 0;

                                textView_AS_helloMessage.setText("Hello, User!");


                                /* This clears the activity stack and goes to the MainActivity class (sign in screen) */
                                finishAffinity();
                                Intent intent = new Intent(SettingsActivity.this, MainActivity.class);
                                startActivity(intent);
                            }
                        }
                );
    }



    public void goToInformativeTab()
    {
        imageButton_AHS_informativeTab.setOnClickListener
                (
                        new View.OnClickListener()
                        {
                            @Override
                            public void onClick(View v)
                            {
                                Intent intent = new Intent(SettingsActivity.this, InformativeTabActivity.class);
                                startActivity(intent);
                            }
                        }
                );
    }
}