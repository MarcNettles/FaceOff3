package com.example.faceoff3;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class InformativeTabActivity extends AppCompatActivity
{


    // Here will be all the code just like MainActivity to get information. This is why we'll need a global variable for the user name at some point.

    DatabaseHelper myDB;


    TextView textView_AIT_informativeTips,textView_AIT_fTouches, textView_AIT_head_fTouches, textView_AIT_loggedInAs, textView_AIT_activeUser;

    Button button_nextTip, button_logOut;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_informative_tab);


        // Access the database
        myDB = new DatabaseHelper(this);


        // Initialize the on screen items to their variables
        textView_AIT_informativeTips = (TextView)findViewById(R.id.textView_AIT_informativeTips);
        textView_AIT_head_fTouches = (TextView)findViewById(R.id.textView_AIT_head_fTouches);
        textView_AIT_fTouches = (TextView)findViewById(R.id.textView_AIT_fTouches);
        textView_AIT_loggedInAs = (TextView)findViewById(R.id.textView_AIT_loggedInAs);
        textView_AIT_activeUser = (TextView)findViewById(R.id.textView_AIT_activeUser);


        button_nextTip = (Button)findViewById(R.id.button_nextTip);
        button_logOut = (Button)findViewById(R.id.button_logOut);


        //Show active user and how many times they have touched their face.
        textView_AIT_activeUser.setText(MainActivity.currentActiveUser);

        textView_AIT_fTouches.setText(MainActivity.fTouches.toString());


        textView_AIT_informativeTips.setText(myDB.getRandomTip());


        //textView_AIT_fTouches



        nextTip();
        logOut();

    }


    /* Needs to disable the back button for the next screen. */
    public void logOut()
    {
        button_logOut.setOnClickListener
                (
                        new View.OnClickListener()
                        {
                            @Override
                            public void onClick(View v)
                            {
                                /* This goes to the MainActivity class */
                                Intent intent = new Intent(InformativeTabActivity.this, MainActivity.class);
                                startActivity(intent);

                                /* Clear out the variables and reset the info, just in case they get back to this screen after logging out */
                                MainActivity.currentActiveUser = "";
                                MainActivity.fTouches = 0;
                                textView_AIT_activeUser.setText("");
                                textView_AIT_fTouches.setText("0");
                            }
                        }
                );
    }


    /* Function to increment fTouches by 1. Call when the user touches their face */
    public void incrementfTouches()
    {
        myDB.incrementfTouches(MainActivity.currentActiveUser, 1);

    }


    /* Used to get a new random tip. Currently bound to the Next Tip button, but can be used elsewhere. */
    public void nextTip()
    {
        button_nextTip.setOnClickListener
                (
                        new View.OnClickListener()
                        {
                            @Override
                            public void onClick(View v)
                            {

                                textView_AIT_informativeTips.setText(myDB.getRandomTip());



                            }
                        }
                );
    }


}
