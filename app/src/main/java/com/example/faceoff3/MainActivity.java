package com.example.faceoff3;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

public class MainActivity extends AppCompatActivity
{
    public static String currentActiveUser; // Track the current active user
    public static Integer fTouches = 0;
    public static Integer washedHands = 0;
    private View.OnClickListener signIn = new View.OnClickListener()
    {
        @Override
        public void onClick(View v)
        {
            try {
                if (myDB.getHashedPass(edit_AM_userName.getText().toString()).equals(  // if the hashed password in the db equals
                        // what you get when you compute the hash again using the input password and the salt stored in the db
                        computeSaltedHash(edit_AM_password.getText().toString(),"SHA-256", myDB.getSalt(edit_AM_userName.getText().toString()))))
                {
                    // then sign the user in
                    Toast.makeText(MainActivity.this, "Signed in", Toast.LENGTH_LONG).show();
                    /*This bit is used to set the Logged In As text and set the Activity variable currentActiveUser*/
                    textView_AM_activeUser.setText(edit_AM_userName.getText().toString());
                    currentActiveUser = edit_AM_userName.getText().toString();
                    fTouches = myDB.getfTouches(currentActiveUser);
                    //Intent intent = new Intent(MainActivity.this, informativeTabActivity.class);
                    Intent intent = new Intent(MainActivity.this, HomeScreenActivity.class);
                    startActivity(intent);
                }
                else
                {
                    Toast.makeText(MainActivity.this, "Username and password do not match", Toast.LENGTH_LONG).show();
                }
            } catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
            }
        }
    };

    public static String saltedHashedPass;  // to be inserted into the db on new account creation
    byte[] salt = new byte[20];

    /* for new users, generates a salt and salted hash of users' passwords to be saved in the db */
    public void generateSaltedHash(String password) throws Exception
    {
        String algorithm = "SHA-256"; // the hashing algorithm to be used for user passwords

        salt = createSalt(); // a salt to be stored in the database for each user and appended to user passwords
        // before hashing to defend against dictionary & rainbow table attacks

        // call computeSaltedHash to get a password that is now safer to store in the db
        saltedHashedPass = computeSaltedHash(password, algorithm, salt);
    }

    /* for authenticating users whose accounts have already been created/are already in the db */
    private static String computeSaltedHash(String password, String algorithm, byte[] salt) throws NoSuchAlgorithmException
    {
        MessageDigest digest = MessageDigest.getInstance(algorithm);
        digest.reset();
        digest.update(salt); // have the digest/hash function update itself with the salt
        byte[] hash = digest.digest(password.getBytes());;
        String hashAsString;
        for (int i = 0; i < 4999; i++) { // going off what ECEN 4133 textbook Security Engineering (2020) says, that "in modern Linux distributions,
            hashAsString = bytesToStringHex(hash); // passwords are salted, hashed using 5000 rounds of SHA-512, and stored in a file
            hash = digest.digest(hashAsString.getBytes()); // that only the root user can read." SHA-512 gives me errors so just using SHA-256
        }
        return bytesToStringHex(hash);
    }

    private final static char[] hexArray = "0123456789ABCDEF".toCharArray();

    /* takes in hash as byte array and turns it into a string of hex chars */
    public static String bytesToStringHex(byte[] bytes) {
        char[] hexChars = new char[bytes.length * 2];
        for (int j = 0; j < bytes.length; j++)
        {
            int v = bytes[j] & 0xFF;              // convert each byte to an int
            hexChars[j*2] = hexArray[v >>> 4];    // even indices will be the hex char corresponding 4 most-significant bits
            hexChars[j*2+1] = hexArray[v & 0x0F]; // odd indices will be the hex char corresponding to the 4 least-significant bits
        }
        return new String(hexChars);
    }

    /* only used the first time the user creates their account, called by function generateSaltedHash */
    public static byte[] createSalt()
    {
        byte[] bytes = new byte[20]; // we'll have a salt that is 20 bytes of pseudo-randomness
        SecureRandom random = new SecureRandom();
        random.nextBytes(bytes); // fill the bytes variable up with the next generated random bytes
        return bytes;
    }


    DatabaseHelper myDB; // Instance of the database


    /* These are the TextView (text box), EditText (input fields), and Button creation, below in onCreate I link them to the activity_main's instances of these items */
    TextView textView_AM_activeUser;

    EditText edit_AM_userName, edit_AM_password, edit_AM_passwordConfirm, edit_AM_firstName, edit_AM_lastName;




    Button button_AM_signIn, button_AM_createAccount;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        myDB = new DatabaseHelper(this);  // Get the new instance of the DB, this may need to be taken out of onCreate if it poses a problem.
        //myDB.insertOne();
        myDB.fillTipTable("informativeTips");

        /* Here we link the variables above to their activity_main (AM) items */
        textView_AM_activeUser = (TextView)findViewById(R.id.textView_AM_activeUser);

        edit_AM_userName = (EditText)findViewById(R.id.edit_AM_userName);
        edit_AM_password = (EditText)findViewById(R.id.edit_AM_password);

        edit_AM_passwordConfirm = (EditText)findViewById(R.id.edit_AM_passwordConfirm);
        edit_AM_firstName = (EditText)findViewById(R.id.edit_AM_firstName);
        edit_AM_lastName = (EditText)findViewById(R.id.edit_AM_lastName);

        button_AM_signIn = (Button)findViewById(R.id.button_AM_signIn);
        button_AM_createAccount = (Button) findViewById(R.id.button_AM_createAccount);







        /* Here we have to call the functions we create below.*/
        AM_createAccount();
        AM_signIn();


    }

    /* Temporary fix. If user logs out, they can't go back to previous screen. This prevents them from putting funky data in the DB*/
    @Override
    public void onBackPressed() {
        //super.onBackPressed();
    }



    /* These functions are mostly just the button name with a ClickListener to do things inside which I'll annotate anyhow so I won't repeat myself here.*/
    public void AM_createAccount()
    {
        button_AM_createAccount.setOnClickListener // The Create Account button is listening
            (
                new View.OnClickListener() // Just type "new On" (with a capital O) and the rest auto-completes
                {
                    @Override
                    public void onClick(View v) // Autogenerated
                    {
                        /* Checking for empty input fields*/
                        if(edit_AM_firstName.getText().toString().isEmpty() || edit_AM_lastName.getText().toString().isEmpty() || edit_AM_userName.getText().toString().isEmpty() || edit_AM_password.getText().toString().isEmpty() || edit_AM_passwordConfirm.getText().toString().isEmpty())
                        {
                            Toast.makeText(MainActivity.this, "Please fill out all fields.", Toast.LENGTH_LONG).show(); //This is how you give a pop-up notification
                        }
                        /*Checking for password and confirm password to be equal. Note == does not seem to work, use .equal() instead*/
                        else if (!(edit_AM_password.getText().toString().equals(edit_AM_passwordConfirm.getText().toString())))
                        {
                            Toast.makeText(MainActivity.this, "Passwords do not match.", Toast.LENGTH_LONG).show();
                        }
                        /* If all is good, insert the data */
                        else
                        {
                            /* here we will generate a salted hash of the user's chosen password and prepare for it and the salt to be stored in the database */
                            try {
                                generateSaltedHash(edit_AM_password.getText().toString());// pw is now salted and hashed
                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                            /*insertData() returns a bool, so you can test if it was inserted or not*/
                            boolean isInserted = myDB.insertData(edit_AM_userName.getText().toString(), saltedHashedPass, edit_AM_firstName.getText().toString(), edit_AM_lastName.getText().toString(), 0, salt); // Number of fields must match the table's columns

                            if(isInserted == true)
                            {
                                Toast.makeText(MainActivity.this, "Account Created", Toast.LENGTH_LONG).show();
                                signIn.onClick(v);
                            }                                               // This code block also signs you in
                            else
                            {
                                Toast.makeText(MainActivity.this, "Username unavailable", Toast.LENGTH_LONG).show();
                            }
                        }
                    }
                }
             );

    }


    /* Creating the Sign In Button */
    public void AM_signIn()
    {
        button_AM_signIn.setOnClickListener(signIn); // Sign In is listening for a click
    }





    /* Function to reset all variables storing user information.*/
    public static void clearUserData()
    {
        currentActiveUser = "";
        fTouches = 0;
        washedHands = 0;
    }

    /*Could be useful*/

    public void showMessage(String title, String message)
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(true);
        builder.setTitle(title);
        builder.setMessage(message);
        builder.show();
    }
}
