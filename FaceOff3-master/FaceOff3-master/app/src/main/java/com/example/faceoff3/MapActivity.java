package com.example.faceoff3;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;

public class MapActivity extends AppCompatActivity
{

    DatabaseHelper myDB;


    MapView mapView_AMap_map;

    OnMapReadyCallback mapView_AMap_map_CallBack;



    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);


        myDB = new DatabaseHelper(this);


        mapView_AMap_map = (MapView)findViewById(R.id.mapView_AMap_map);

        mapView_AMap_map.getMapAsync(mapView_AMap_map_CallBack);

    }

}
