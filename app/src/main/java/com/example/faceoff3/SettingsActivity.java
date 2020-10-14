package com.example.faceoff3;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;

public class SettingsActivity extends AppCompatActivity
{

    DatabaseHelper myDB;

    ImageButton imageButton_AHS_informativeTab;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        myDB = new DatabaseHelper(this);



        imageButton_AHS_informativeTab = (ImageButton)findViewById(R.id.imageButton_AHS_informativeTab);





        goToInformativeTab();


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