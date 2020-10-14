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


    TextView textView_HS_randomTip, textView_HS_touchedFace, textView_HS_washedHands;

    Button button_HS_informativeTab, button_HS_increaseWashedHands;




    @Override
    protected void onCreate(Bundle savedInstanceState)
    {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_screen);


        myDB = new DatabaseHelper(this);


        textView_HS_randomTip = (TextView)findViewById(R.id.textView_HS_randomTip);
        textView_HS_touchedFace = (TextView)findViewById(R.id.textView_HS_touchedFace);
        textView_HS_washedHands = (TextView)findViewById(R.id.textView_HS_washedHands);

        button_HS_informativeTab = (Button)findViewById(R.id.button_HS_informativeTab);

        button_HS_increaseWashedHands = (Button)findViewById(R.id.button_HS_increaseWashedHands);


        textView_HS_randomTip.setText(myDB.getRandomTip());
        textView_HS_touchedFace.setText("You have touched your face "+myDB.getfTouches(MainActivity.currentActiveUser).toString()+" times today.");
        textView_HS_washedHands.setText("You have washed your hands "+MainActivity.washedHands + " times today.");


        goToInformativeTab();
        increaseWashedHands();

    }




    public void increaseWashedHands()
    {
        button_HS_increaseWashedHands.setOnClickListener
                (
                        new View.OnClickListener()
                        {
                            @Override
                            public void onClick(View v)
                            {
                                MainActivity.washedHands = MainActivity.washedHands + 1;
                                textView_HS_washedHands.setText("You have washed your hands "+MainActivity.washedHands + " times today.");
                            }
                        }
                );
    }



    // For now this is tied to a button, but we might want to make it look nicer.
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
