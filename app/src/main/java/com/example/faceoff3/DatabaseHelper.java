package com.example.faceoff3;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.Toast;

import androidx.annotation.Nullable;

/*This class builds our initial database and keeps using the same one once it is built. User data is persistent on drive. If we want online capabilities, someone will need to code that in PHP and run a server*/
public class DatabaseHelper extends SQLiteOpenHelper
{
    /*The database, named faceoff3 for now*/
    public static final String DATABASE_NAME = "faceoff3.db";

    /*Tables. Currently just user and informative tips*/
    public static final String USER_TABLE = "user";
    public static final String INFORMATIVE_TIPS = "informativeTips";


    //public static final String COL_ID = "id";

    /*Here are our columns. If you want to add a column, copy this format and then update the number of columns in the create table in onCreate. Other functions will be affected, so check to make sure other functions aren't affected before adding*/

    /*Table user: columns*/
    public static final String COL_USERNAME = "userName";
    public static final String COL_PASSWORD = "password";
    public static final String COL_FIRSTNAME = "firstName";
    public static final String COL_LASTNAME = "lastName";
    public static final String COL_FTOUCHES = "fTouches";
    public static final String COL_WASHEDHANDS_DAILYCOUNT = "washedHandsDailyCount";
    public static final String COL_WASHEDHANDS_MONTHLYCOUNT = "washedHandsMonthlyCount";
    public static final String COL_SALT = "salt";
    //public static final String;

    /*Table informativeTips: columns*/
    public static final String COL_IT_ID = "id";
    public static final String COL_IT_TIP = "tip";

    /*Constructor, it builds the database*/
    public DatabaseHelper(@Nullable Context context)
    {
        super(context, DATABASE_NAME, null, 2);

    }

    /*Builds the table. I assume if we want more tables, this would be the best place. The format is pretty straight forward. It looks like execSQL is used for DDL.*/
    @Override
    public void onCreate(SQLiteDatabase db)
    {

        /*Informative Tab: tip table creation*/
        db.execSQL("create table "+ INFORMATIVE_TIPS + " (id primary key AUTOINCREMENT, tip text)");

        db.execSQL("create table " + USER_TABLE + " (userName text primary key, password text, firstName text, lastName text, fTouches integer, salt blob)");
        /* This doesn't work, I'm not sure why yet */
        //fillTipTable(INFORMATIVE_TIPS);
    }

    /*Still not sure about this one. I suppose it would delete all the data and recreate the tables.*/
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
    {
        db.execSQL("drop table if exists " + USER_TABLE);
        onCreate(db);
    }



    /*
     *
     * Here are the important functions so far. InsertData() is used to create new users. DO NOT USE for updating fTouches, use the updateData()!!! Thank you :)
     *
     * */
    public boolean insertData(String userName, String password, String firstName, String lastName, Integer fTouches, byte[] salt)
    {
        SQLiteDatabase db = this.getWritableDatabase(); // or we could use .getReadableDatabase(); which is used in case the database isn't currently writeable.
        ContentValues contentValues = new ContentValues(); // We use the content values to .put() the data we want to insert into the right columns.
        //-------------------------------------------------------------------------------
        contentValues.put(COL_USERNAME, userName); // .put(column_name, input_variable)
        contentValues.put(COL_PASSWORD, password);
        //-------------------------------------------------------------------------------
        contentValues.put(COL_FIRSTNAME, firstName);
        contentValues.put(COL_LASTNAME, lastName);
        contentValues.put(COL_FTOUCHES, fTouches);
        contentValues.put(COL_SALT, salt);

        long result = db.insert(USER_TABLE, null, contentValues); // Either the data is insert or it is not, db.insert() returns -1 if it did not insert successfully, otherwise it returns how many rows were affected.

        if(result == -1)
        {
            return false;
        }
        else
        {
            return true;
        }

    }




    /*This is just used to fill the tip table with default values to pull from randomly. I figure the tips will be a randomly chosen tip popping up on the user's tip of the day or something.*/

    public boolean fillTipTable(String tableName)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();


        /* Here we hard code the tips to pull from */

        contentValues.put(COL_IT_TIP, "Wear a mask. Save lives.");
        db.insert(tableName, null, contentValues);


        contentValues.put(COL_IT_TIP, "Wash your hands with soap and water for at least 20 seconds");
        db.insert(tableName, null, contentValues);


        contentValues.put(COL_IT_TIP, "Water alone is not enough! To prevent spread of the virus, you must use soap as well!");
        db.insert(tableName, null, contentValues);


        contentValues.put(COL_IT_TIP, "Sanitizer should be at least 60% alcohol.");
        db.insert(tableName, null, contentValues);


        contentValues.put(COL_IT_TIP, "Cough into your elbow to reduce the spray of germs.");
        db.insert(tableName, null, contentValues);

        contentValues.put(COL_IT_TIP, "Wear masks with two or more layers.");
        db.insert(tableName, null, contentValues);


        contentValues.put(COL_IT_TIP, "Masks need to cover your nose and mouth, secured under your chin.");
        db.insert(tableName, null, contentValues);


        contentValues.put(COL_IT_TIP, "Masks with breathing valves are a no go!");
        db.insert(tableName, null, contentValues);


        contentValues.put(COL_IT_TIP, "Do not put masks on children younger than 2 years old.");
        db.insert(tableName, null, contentValues);

        contentValues.put(COL_IT_TIP, "Avoid touching your face with unwashed hands!");
        db.insert(tableName, null, contentValues);


        contentValues.put(COL_IT_TIP, "Clean and disinfect frequently touched surfaces daily.");
        db.insert(tableName, null, contentValues);


        contentValues.put(COL_IT_TIP, "Wear gloves when disinfecting. Chemicals can enter your bloodstream through the skin.");
        db.insert(tableName, null, contentValues);


        contentValues.put(COL_IT_TIP, "Maintain 6 feet of distance from others at all times.");
        db.insert(tableName, null, contentValues);


        contentValues.put(COL_IT_TIP, "Constant handwashing can lead to dry skin. Try using a soap with a moisturizer, such as Dove.");
        db.insert(tableName, null, contentValues);

        contentValues.put(COL_IT_TIP, "If someone in the house is sick, make sure they use a separate bathroom.");
        db.insert(tableName, null, contentValues);


        contentValues.put(COL_IT_TIP, "Replace your HVAC filter regularly.");
        db.insert(tableName, null, contentValues);


        contentValues.put(COL_IT_TIP, "HVAC systems only filter when on, so turn on the system fan without heating or cooling for longer times.");
        db.insert(tableName, null, contentValues);


        contentValues.put(COL_IT_TIP, "Stay up to date by visiting www.cdc.gov/coronavirus");
        db.insert(tableName, null, contentValues);


        contentValues.put(COL_IT_TIP, "If you are sick, stay home!");
        db.insert(tableName, null, contentValues);


        contentValues.put(COL_IT_TIP, "If you are sick, stay hydrated!");
        db.insert(tableName, null, contentValues);


        contentValues.put(COL_IT_TIP, "You don’t have to go to the hospital to be seen. Many doctors are using online consultations to keep people safely away from hospitals filled with coronavirus patients.");
        db.insert(tableName, null, contentValues);


        contentValues.put(COL_IT_TIP, "Coronavirus symptoms include fever, chills, cough, shortness of breath, and many more. Visit the CDC’s website for a full list.");
        db.insert(tableName, null, contentValues);


        contentValues.put(COL_IT_TIP, "Seek IMMEDIATE medical attention if you experience: Trouble breathing, persistent chest pain, confusion, inability to stay awake, or bluish lips/face.");

        long result = db.insert(tableName, null, contentValues);

        if(result == -1)
        {
            return false;
        }
        else
        {
            return true;
        }
    }


    /*So, there's a lot of ways to do this, this is just what worked for me. db.rawQuery() takes in a string for the query with ?'s replacing the variables. Then you make a String[] for the arguments, which have to match the number of ?'s in the String before it.*/
    public Cursor findUser(String userName, String password)
    {

        SQLiteDatabase db = this.getWritableDatabase();

        String selectQuery = "select "+COL_USERNAME+","+COL_PASSWORD+" from " +USER_TABLE+" where "+COL_USERNAME+ " = ? and "+COL_PASSWORD+" = ?";

        String[] arguments = {userName,password};

        Cursor res = db.rawQuery(selectQuery, arguments);





        return res;
    }


    /*The way I wrote this, it should prevent needing to input all the arguments.
     * If you don't put in an argument, it just isn't added to the list of contentValues.
     *
     *  It NEEDS a userName
     *
     * userName should eventually be replaced with the global variable for the user name, since the user will be signed in.
     *
     * */
    public boolean updateData(String userName, String password, String firstName, String lastName, Integer fTouches)
    {

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        if(userName.isEmpty())
            return false; // We at least need a user name

        if(!userName.isEmpty())
            contentValues.put(COL_USERNAME, userName);

        if(!password.isEmpty())
            contentValues.put(COL_PASSWORD, password);

        if(!firstName.isEmpty())
            contentValues.put(COL_FIRSTNAME, firstName);

        if(!lastName.isEmpty())
            contentValues.put(COL_LASTNAME, lastName);

        if(fTouches > 0)
            contentValues.put(COL_FTOUCHES, fTouches);

        db.update(USER_TABLE, contentValues, "userName = ?", new String[]{ userName });

        return true;

    }


    public String getRandomTip()
    {
        SQLiteDatabase db = this.getWritableDatabase();

        //String res;

        //res = db.rawQuery();

        String returnString;



        String selectQuery = "select "+COL_IT_TIP+" from " +INFORMATIVE_TIPS+" order by random() limit 1";



        Cursor cursor = db.rawQuery(selectQuery, null);

        if(cursor.getCount() == 1)
        {
            cursor.moveToFirst();
            returnString = cursor.getString(cursor.getColumnIndex(COL_IT_TIP));
            return returnString;


        }
        else
        {
            return "Failed";
        }


    }


    public void incrementfTouches(String userName, Integer touches)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        Integer fTouches = 0;


        String selectQuery = "select "+COL_FTOUCHES+" from " +USER_TABLE+" where userName = '"+userName+"'";
        Cursor cursor = db.rawQuery(selectQuery, null);

        if(cursor.getCount() == 1)
        {
            cursor.moveToFirst();
            fTouches = cursor.getInt(cursor.getColumnIndex(COL_FTOUCHES));
        }
        else
        {

        }

        fTouches = fTouches + touches;


        // Put new value into the table
        ContentValues contentValues = new ContentValues();

        contentValues.put(COL_FTOUCHES, fTouches);

        db.update(USER_TABLE, contentValues, "userName = ?", new String[]{ userName });




    }

    public Integer getfTouches(String userID)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        Integer returnInt = 9999;

        String selectQuery = "select "+COL_FTOUCHES+" from " +USER_TABLE+" where userName = '"+userID+"'";


        Cursor cursor = db.rawQuery(selectQuery, null);

        if(cursor.getCount() == 1)
        {
            cursor.moveToFirst();
            returnInt = cursor.getInt(cursor.getColumnIndex(COL_FTOUCHES));
            return returnInt;


        }
        else
        {
            return returnInt;
        }

    }

    public byte[] getSalt(String userID)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        byte[] returnBytes = new byte[20];

        String selectQuery = "select "+COL_SALT+" from " +USER_TABLE+" where userName = '"+userID+"'";


        Cursor cursor = db.rawQuery(selectQuery, null);

        if(cursor.getCount() == 1)
        {
            cursor.moveToFirst();
            returnBytes = cursor.getBlob(cursor.getColumnIndex(COL_SALT));
            return returnBytes;
        }
        else
        {
            return returnBytes;
        }

    }

    public String getHashedPass(String userID)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        String returnString = "";

        String selectQuery = "select "+COL_PASSWORD+" from " +USER_TABLE+" where userName = '"+userID+"'";


        Cursor cursor = db.rawQuery(selectQuery, null);

        if(cursor.getCount() == 1)
        {
            cursor.moveToFirst();
            returnString = cursor.getString(cursor.getColumnIndex(COL_PASSWORD));
            return returnString;
        }
        else
        {
            return returnString;
        }

    }





    /*These are just some extra helper functions to be used as needed
     *
     *
     *
     *
     *
     *
     * In case we need to pull a whole table*/
    /*
    public Cursor getAllData()
    {
        SQLiteDatabase db = this.getWritableDatabase();

        Cursor res = db.rawQuery("select * from " + USER_TABLE, null);
        return res;

    }



    public boolean updateData(String userName, String firstName, String lastName, String fTouches)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();

        contentValues.put(COL_USERNAME, userName);
        contentValues.put(COL_FIRSTNAME, firstName);
        contentValues.put(COL_LASTNAME, lastName);
        contentValues.put(COL_FTOUCHES, fTouches);

        db.update(USER_TABLE, contentValues, "userName = ?", new String[]{ userName });

        return true;

    }


/* deletes a user in the user table

    NEEDS TO BE UPDATED to take in Table name for extra tables
     */


     /*
    public Integer deleteData(String userName)
    {
        SQLiteDatabase db = this.getWritableDatabase();

        return db.delete(USER_TABLE, "userName = ?", new String[] { userName });
    }


     */
}
