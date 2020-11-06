package com.example.faceoff3;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.Manifest;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

import static com.example.faceoff3.Constants.ERROR_DIALOG_REQUEST;
import static com.example.faceoff3.Constants.PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION;
import static com.example.faceoff3.Constants.PERMISSIONS_REQUEST_ENABLE_GPS;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    public static String currentActiveUser; // Track the current active user
    public static Integer fTouches = 0;
    public static Integer washedHands = 0;
    private boolean mLocationPermissionGranted = false;
    private FusedLocationProviderClient mFusedLocationClient;

    private View.OnClickListener signIn = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            try {
                if (myDB.getHashedPass(edit_AM_userName.getText().toString()).equals(  // if the hashed password in the db equals
                        // what you get when you compute the hash again using the input password and the salt stored in the db
                        computeSaltedHash(edit_AM_password.getText().toString(), "SHA-256", myDB.getSalt(edit_AM_userName.getText().toString())))) {
                    // then sign the user in
                    Toast.makeText(MainActivity.this, "Signed in", Toast.LENGTH_LONG).show();
                    /*This bit is used to set the Logged In As text and set the Activity variable currentActiveUser*/
                    textView_AM_activeUser.setText(edit_AM_userName.getText().toString());
                    currentActiveUser = edit_AM_userName.getText().toString();
                    fTouches = myDB.getfTouches(currentActiveUser);
                    //Intent intent = new Intent(MainActivity.this, informativeTabActivity.class);
                    Intent intent = new Intent(MainActivity.this, HomeScreenActivity.class);
                    startActivity(intent);
                } else {
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
    public void generateSaltedHash(String password) throws Exception {
        String algorithm = "SHA-256"; // the hashing algorithm to be used for user passwords

        salt = createSalt(); // a salt to be stored in the database for each user and appended to user passwords
        // before hashing to defend against dictionary & rainbow table attacks

        // call computeSaltedHash to get a password that is now safer to store in the db
        saltedHashedPass = computeSaltedHash(password, algorithm, salt);
    }

    /* for authenticating users whose accounts have already been created/are already in the db */
    private static String computeSaltedHash(String password, String algorithm, byte[] salt) throws NoSuchAlgorithmException {
        MessageDigest digest = MessageDigest.getInstance(algorithm);
        digest.reset();
        digest.update(salt); // have the digest/hash function update itself with the salt
        byte[] hash = digest.digest(password.getBytes());
        ;
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
        for (int j = 0; j < bytes.length; j++) {
            int v = bytes[j] & 0xFF;              // convert each byte to an int
            hexChars[j * 2] = hexArray[v >>> 4];    // even indices will be the hex char corresponding 4 most-significant bits
            hexChars[j * 2 + 1] = hexArray[v & 0x0F]; // odd indices will be the hex char corresponding to the 4 least-significant bits
        }
        return new String(hexChars);
    }

    /* only used the first time the user creates their account, called by function generateSaltedHash */
    public static byte[] createSalt() {
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
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        myDB = new DatabaseHelper(this);  // Get the new instance of the DB, this may need to be taken out of onCreate if it poses a problem.
        //myDB.insertOne();
        myDB.fillTipTable("informativeTips");

        /* Here we link the variables above to their activity_main (AM) items */
        textView_AM_activeUser = (TextView) findViewById(R.id.textView_AM_activeUser);

        edit_AM_userName = (EditText) findViewById(R.id.edit_AM_userName);
        edit_AM_password = (EditText) findViewById(R.id.edit_AM_password);

        edit_AM_passwordConfirm = (EditText) findViewById(R.id.edit_AM_passwordConfirm);
        edit_AM_firstName = (EditText) findViewById(R.id.edit_AM_firstName);
        edit_AM_lastName = (EditText) findViewById(R.id.edit_AM_lastName);

        button_AM_signIn = (Button) findViewById(R.id.button_AM_signIn);
        button_AM_createAccount = (Button) findViewById(R.id.button_AM_createAccount);







        /* Here we have to call the functions we create below.*/
        AM_createAccount();
        AM_signIn();
//        getLastKnownLocation();


    }

    private void getLastKnownLocation() {
        Log.d(TAG, "getLastKnownLocation: called");
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        mFusedLocationClient.getLastLocation().addOnCompleteListener(new OnCompleteListener<Location>() {
            @Override
            public void onComplete(@NonNull Task<Location> task) {
                if(task.isSuccessful()){
                    Location location = task.getResult();
        //            GeoPoint geoPoint = new GeoPoint(location.getLatitude(), location.getLongitude()); // might not use this later, so can remove later
                    Log.d(TAG, "onComplete: latitude: " + location.getLatitude()); // this could just be changed to location.getLatitude() and location.getLongitude if not using GeoPoint later
                    Log.d(TAG, "onComplete: longitude: " + location.getLongitude()); // ( this was geoPoint.getLatitude() )
                }
            }
        });
    }

    private boolean checkMapServices(){
        if(isServicesOK()){
            if(isMapsEnabled()){
                return true;
            }
        }
        return false;
    }

    /* if isMapsEnabled finds that GPS is not enabled on the device, this function will prompt user w/a dialog
    * that asks if they want to enable it */
    private void buildAlertMessageNoGps() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("This application requires GPS to work properly, do you want to enable it?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(@SuppressWarnings("unused") final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                        Intent enableGpsIntent = new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        startActivityForResult(enableGpsIntent, PERMISSIONS_REQUEST_ENABLE_GPS);
                    }
                });
        final AlertDialog alert = builder.create();
        alert.show();
    }
/* determines whether the app has GPS enabled on the device */
    public boolean isMapsEnabled(){
        final LocationManager manager = (LocationManager) getSystemService( Context.LOCATION_SERVICE );

        if ( !manager.isProviderEnabled( LocationManager.GPS_PROVIDER ) ) {
            buildAlertMessageNoGps();
            return false;
        }
        return true;
    }

    private void getLocationPermission() {
        /*
         * Request location permission, so that we can get the location of the
         * device. The result of the permission request is handled by a callback,
         * onRequestPermissionsResult.
         */
        if (ContextCompat.checkSelfPermission(this.getApplicationContext(),
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            mLocationPermissionGranted = true;
            AM_createAccount();  // continue on to the app
//            getLastKnownLocation();
        } else { // this will actually ask the user for permissions
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        }
    }

    /* determines whether the device can use Google Services, which are necessary for the map functionality */
    public boolean isServicesOK(){
        Log.d(TAG, "isServicesOK: checking google services version");

        int available = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(MainActivity.this);

        if(available == ConnectionResult.SUCCESS){
            //everything is fine and the user can make map requests
            Log.d(TAG, "isServicesOK: Google Play Services is working");
            return true;
        }
        else if(GoogleApiAvailability.getInstance().isUserResolvableError(available)){
            //an error occured but we can resolve it
            Log.d(TAG, "isServicesOK: an error occured but we can fix it");
            Dialog dialog = GoogleApiAvailability.getInstance().getErrorDialog(MainActivity.this, available, ERROR_DIALOG_REQUEST);
            dialog.show();
        }else{
            Toast.makeText(this, "You can't make map requests", Toast.LENGTH_SHORT).show();
        }
        return false;
    }

    /* called last in the get permissions procedure, by getLocationPermission() above */
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String permissions[],
                                           @NonNull int[] grantResults) {
        mLocationPermissionGranted = false;
        switch (requestCode) {
            case PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    mLocationPermissionGranted = true;
                }
            }
        }
    }


    /* this function is called after user either accepts or denies GPS permissions */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d(TAG, "onActivityResult: called.");
        switch (requestCode) {
            case PERMISSIONS_REQUEST_ENABLE_GPS: { // this case is a const in the Constants.java file
                if(mLocationPermissionGranted){ // then we go on to use the application as intended
                    AM_createAccount();
                    getLastKnownLocation();
                }
                else{
                    getLocationPermission();
                }
            }
        }

    }

    @Override
    protected void onResume(){
        super.onResume();
        AM_createAccount();
        if (checkMapServices()){
            if(mLocationPermissionGranted){
                AM_createAccount();
            }
            else {
                getLocationPermission();
            }
        }
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
//                                getLastKnownLocation();
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
//        getLastKnownLocation();
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
