package com.example.faceoff3;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.Toast;

import androidx.annotation.Nullable;

public class DatabaseHelper extends SQLiteOpenHelper
{
    public static final String DATABASE_NAME = "faceoff3.db";
    public static final String TABLE_NAME = "user";
    //public static final String COL_ID = "id";
    public static final String COL_USERNAME = "userName";
    public static final String COL_PASSWORD = "password";
    public static final String COL_FIRSTNAME = "firstName";
    public static final String COL_LASTNAME = "lastName";
    public static final String COL_FTOUCHES = "fTouches";


    public DatabaseHelper(@Nullable Context context)
    {
        super(context, DATABASE_NAME, null, 1);

    }

    @Override
    public void onCreate(SQLiteDatabase db)
    {
        db.execSQL("create table " + TABLE_NAME + " (userName text primary key, password text, firstName text, lastName text, fTouches integer)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
    {
        db.execSQL("drop table if exists " + TABLE_NAME);
        onCreate(db);
    }

    public boolean insertData(String userName, String password, String firstName, String lastName, Integer fTouches)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        //-------------------------------------------------------------------------------
        contentValues.put(COL_USERNAME, userName);
        contentValues.put(COL_PASSWORD, password);
        //-------------------------------------------------------------------------------
        contentValues.put(COL_FIRSTNAME, firstName);
        contentValues.put(COL_LASTNAME, lastName);
        contentValues.put(COL_FTOUCHES, fTouches);

        long result = db.insert(TABLE_NAME, null, contentValues);

        if(result == -1)
        {
            return false;
        }
        else
        {
            return true;
        }

    }


    public Cursor findUser(String userName, String password)
    {

        SQLiteDatabase db = this.getWritableDatabase();

        String[] columns = {COL_USERNAME, COL_PASSWORD};

        String[] arguments = {userName};

        Cursor res = db.query(TABLE_NAME, columns, COL_USERNAME+" LIKE ?", arguments, null, null, null);

        return res;
    }





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

    public Integer deleteData(String userName)
    {
        SQLiteDatabase db = this.getWritableDatabase();

        return db.delete(TABLE_NAME, "userName = ?", new String[] { userName });
    }


     */
}
