package com.example.faceoff3;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity
{
    DatabaseHelper myDB;

    EditText edit_AM_userName, edit_AM_password, edit_AM_passwordConfirm, edit_AM_firstName, edit_AM_lastName;

    Button button_AM_signIn, button_AM_createAccount;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        myDB = new DatabaseHelper(this);

        edit_AM_userName = (EditText)findViewById(R.id.edit_AM_userName);
        edit_AM_password = (EditText)findViewById(R.id.edit_AM_password);
        edit_AM_passwordConfirm = (EditText)findViewById(R.id.edit_AM_passwordConfirm);
        edit_AM_firstName = (EditText)findViewById(R.id.edit_AM_firstName);
        edit_AM_lastName = (EditText)findViewById(R.id.edit_AM_lastName);

        button_AM_signIn = (Button)findViewById(R.id.button_AM_signIn);
        button_AM_createAccount = (Button) findViewById(R.id.button_AM_createAccount);



        AM_createAccount();
    }



    public void AM_createAccount()
    {
        button_AM_createAccount.setOnClickListener
                (
                        new View.OnClickListener()
                        {
                            @Override
                            public void onClick(View v)
                            {
                                if(edit_AM_password == edit_AM_passwordConfirm)
                                {
                                    boolean isInserted = myDB.insertData(edit_AM_userName.getText().toString(), edit_AM_password.getText().toString(), edit_AM_firstName.getText().toString(), edit_AM_lastName.getText().toString());

                                    if(isInserted == true)
                                    {
                                        Toast.makeText(MainActivity.this, "Account Created", Toast.LENGTH_LONG).show();
                                    }
                                    else
                                    {
                                        Toast.makeText(MainActivity.this, "Account NOT Created", Toast.LENGTH_LONG).show();
                                    }
                                }
                               else
                                {
                                    Toast.makeText(MainActivity.this, "Passwords do not match.", Toast.LENGTH_LONG).show();
                                }
                            }
                        }
                );
    }





    public void showMessage(String title, String message)
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(true);
        builder.setTitle(title);
        builder.setMessage(message);
        builder.show();
    }
}