package com.example.faceoff3;

import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
/* Our home screen once the user has logged in. */
public class HomeScreenActivity extends AppCompatActivity
{





    DatabaseHelper myDB;


    TextView textView_HS_randomTip;




    @Override
    protected void onCreate(Bundle savedInstanceState)
    {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_screen);


        myDB = new DatabaseHelper(this);


        textView_HS_randomTip = (TextView)findViewById(R.id.textView_HS_randomTip);

        textView_HS_randomTip.setText(myDB.getRandomTip());





    }









}
