package com.example.faceoff3;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
/* Our home screen once the user has logged in. */
public class HomeScreenActivity extends AppCompatActivity
{





    DatabaseHelper myDB;


    TextView textView_HS_randomTip;

    Button button_HS_informativeTab;




    @Override
    protected void onCreate(Bundle savedInstanceState)
    {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_screen);


        myDB = new DatabaseHelper(this);


        textView_HS_randomTip = (TextView)findViewById(R.id.textView_HS_randomTip);

        button_HS_informativeTab = (Button)findViewById(R.id.button_HS_informativeTab);


        textView_HS_randomTip.setText(myDB.getRandomTip());


        goToInformativeTab();


    }


public void goToInformativeTab()
{
    button_HS_informativeTab.setOnClickListener
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
