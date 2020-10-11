package com.example.faceoff3;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

public class MainActivity extends AppCompatActivity
{
    String currentActiveUser = "";

    //GlobalVariables globalVariables = (GlobalVariables) this.getApplication();


    DatabaseHelper myDB;

    TextView textView_AM_activeUser;

    EditText edit_AM_userName, edit_AM_password, edit_AM_passwordConfirm, edit_AM_firstName, edit_AM_lastName;

    Button button_AM_signIn, button_AM_createAccount;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        myDB = new DatabaseHelper(this);

        textView_AM_activeUser = (TextView)findViewById(R.id.textView_AM_activeUser);

        edit_AM_userName = (EditText)findViewById(R.id.edit_AM_userName);
        edit_AM_password = (EditText)findViewById(R.id.edit_AM_password);
        edit_AM_passwordConfirm = (EditText)findViewById(R.id.edit_AM_passwordConfirm);
        edit_AM_firstName = (EditText)findViewById(R.id.edit_AM_firstName);
        edit_AM_lastName = (EditText)findViewById(R.id.edit_AM_lastName);

        button_AM_signIn = (Button)findViewById(R.id.button_AM_signIn);
        button_AM_createAccount = (Button) findViewById(R.id.button_AM_createAccount);



        AM_createAccount();
        AM_signIn();
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
                                if(edit_AM_firstName.getText().toString().isEmpty() || edit_AM_lastName.getText().toString().isEmpty() || edit_AM_userName.getText().toString().isEmpty() || edit_AM_password.getText().toString().isEmpty() || edit_AM_passwordConfirm.getText().toString().isEmpty())
                                {
                                    Toast.makeText(MainActivity.this, "Please fill out all fields.", Toast.LENGTH_LONG).show();
                                }
                                else if (!(edit_AM_password.getText().toString().equals(edit_AM_passwordConfirm.getText().toString())))
                                {
                                    Toast.makeText(MainActivity.this, "Passwords do not match.", Toast.LENGTH_LONG).show();
                                }
                                else
                                {
                                    boolean isInserted = myDB.insertData(edit_AM_userName.getText().toString(), edit_AM_password.getText().toString(), edit_AM_firstName.getText().toString(), edit_AM_lastName.getText().toString(), 0);

                                    if(isInserted == true)
                                    {
                                        Toast.makeText(MainActivity.this, "Account Created", Toast.LENGTH_LONG).show();
                                    }
                                    else
                                    {
                                        Toast.makeText(MainActivity.this, "Account NOT Created", Toast.LENGTH_LONG).show();
                                    }
                                }


                            }
                        }
                );
    }



    public void AM_signIn()
    {
        button_AM_signIn.setOnClickListener
                (
                        new View.OnClickListener()
                        {
                            @Override
                            public void onClick(View v)
                            {


                                Cursor userFound = myDB.findUser(edit_AM_userName.getText().toString(), edit_AM_password.getText().toString());
                                if(userFound.getCount() == 0)
                                {
                                    Toast.makeText(MainActivity.this, "Account NOT Found", Toast.LENGTH_LONG).show();
                                }
                                else
                                {
                                    Toast.makeText(MainActivity.this, "Account Found", Toast.LENGTH_LONG).show();
                                    textView_AM_activeUser.setText(edit_AM_userName.getText().toString());
                                    currentActiveUser = edit_AM_userName.getText().toString();


                                    //globalVariables.setCurrentActiveUser(currentActiveUser.toString());



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