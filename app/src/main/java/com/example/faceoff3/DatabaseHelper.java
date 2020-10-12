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
    public static final String DATABASE_NAME = "faceoff3.db";
    public static final String TABLE_NAME = "user";
    //public static final String COL_ID = "id";

    /*Here are our columns. If you want to add a column, copy this format and then update the number of columns in the create table in onCreate. Other functions will be affected, so check to make sure other functions aren't affected before adding*/
    public static final String COL_USERNAME = "userName";
    public static final String COL_PASSWORD = "password";
    public static final String COL_FIRSTNAME = "firstName";
    public static final String COL_LASTNAME = "lastName";
    public static final String COL_FTOUCHES = "fTouches";


    /*Constructor, it builds the database*/
    public DatabaseHelper(@Nullable Context context)
    {
        super(context, DATABASE_NAME, null, 1);

    }

    /*Builds the table. I assume if we want more tables, this would be the best place. The format is pretty straight forward. It looks like execSQL is used for DDL.*/
    @Override
    public void onCreate(SQLiteDatabase db)
    {
        db.execSQL("create table " + TABLE_NAME + " (userName text primary key, password text, firstName text, lastName text, fTouches integer)");
    }

    /*Still not sure about this one. I suppose it would delete all the data and recreate the tables.*/
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
    {
        db.execSQL("drop table if exists " + TABLE_NAME);
        onCreate(db);
    }



    /*
    *
    * Here are the important functions so far. InsertData() is used to create new users. DO NOT USE for updating fTouches, use the updateData()!!! Thank you :)
    *
    * */
    public boolean insertData(String userName, String password, String firstName, String lastName, Integer fTouches)
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

        long result = db.insert(TABLE_NAME, null, contentValues); // Either the data is insert or it is not, db.insert() returns -1 if it did not insert successfully, otherwise it returns how many rows were affected.

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

        String selectQuery = "select "+COL_USERNAME+","+COL_PASSWORD+" from " +TABLE_NAME+" where "+COL_USERNAME+ " = ? and "+COL_PASSWORD+" = ?";

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
            return

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

        db.update(TABLE_NAME, contentValues, "userName = ?", new String[]{ userName });

        return true;

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

        Cursor res = db.rawQuery("select * from " + TABLE_NAME, null);
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

        db.update(TABLE_NAME, contentValues, "userName = ?", new String[]{ userName });

        return true;

    }


/* deletes a user in the user table

    NEEDS TO BE UPDATED to take in Table name for extra tables
     */


     /*
    public Integer deleteData(String userName)
    {
        SQLiteDatabase db = this.getWritableDatabase();

        return db.delete(TABLE_NAME, "userName = ?", new String[] { userName });
    }


     */
}
