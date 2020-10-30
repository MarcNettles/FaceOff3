package com.example.faceoff3;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;

import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.data.Feature;
import com.google.maps.android.data.geojson.GeoJsonFeature;
import com.google.maps.android.data.geojson.GeoJsonLayer;
import com.google.maps.android.data.geojson.GeoJsonPointStyle;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.android.data.geojson.GeoJsonPolygonStyle;


public class MapFragment extends Fragment {
    private GoogleMap mMap;

    private void retrieveFileFromUrl() {
        new DownloadGeoJsonFile().execute(getString(R.string.geojson_url));
    }

    private class DownloadGeoJsonFile extends AsyncTask<String, Void, GeoJsonLayer> {
        private final static String mLogTag = "GeoJsonDemo";

        @Override
        protected GeoJsonLayer doInBackground(String... params) {
            try {
                // Open a stream from the URL
                InputStream stream = new URL(params[0]).openStream();

                String line;
                StringBuilder result = new StringBuilder();
                BufferedReader reader = new BufferedReader(new InputStreamReader(stream));

                while ((line = reader.readLine()) != null) {
                    // Read and save each line of the stream
                    result.append(line);
                }

                // Close the stream
                reader.close();
                stream.close();

                return new GeoJsonLayer(mMap, new JSONObject(result.toString()));
            } catch (IOException e) {
                Log.e(mLogTag, "GeoJSON file could not be read");
            } catch (JSONException e) {
                Log.e(mLogTag, "GeoJSON file could not be converted to a JSONObject");
            }
            return null;
        }

        @Override
        protected void onPostExecute(GeoJsonLayer layer) {
            if (layer != null) {
                addGeoJsonLayerToMap(layer);
            }
        }
    }

    private void addGeoJsonLayerToMap(GeoJsonLayer layer) {

        addColorsToMarkers(layer);
        layer.addLayerToMap();
        // Demonstrate receiving features via GeoJsonLayer clicks.
        layer.setOnFeatureClickListener(new GeoJsonLayer.GeoJsonOnFeatureClickListener() {
            @Override
            public void onFeatureClick(Feature feature) {
                String recommendation = "";
                /* right now this is just the calculation as below in the magnitudeToColor function,
                * but in the future we can change it to incorporate the user's current face-touching
                * to hand-washing ratio, to give a more tailored recommendation to the user */
                if (Double.parseDouble(feature.getProperty("Incident_Rate")) < 2000) {
                    recommendation = "exercise caution";
                } else if (Double.parseDouble(feature.getProperty("Incident_Rate")) < 4000) {
                    recommendation = "exercise great caution";
                } else if (Double.parseDouble(feature.getProperty("Incident_Rate")) < 6000) {
                    recommendation = "exercise extreme caution";
                } else {
                    recommendation = "avoid travel to this county";
                }
                Toast.makeText(getActivity().getApplicationContext(),
                        "Recommendation: " + recommendation,
                        Toast.LENGTH_SHORT).show();
            }

        });
    }

    /**
     * Adds a point style to all features to change the color of the marker based on its magnitude
     * property
     */
    private void addColorsToMarkers(GeoJsonLayer layer) {
        // Iterate over all the features stored in the layer
        for (GeoJsonFeature feature : layer.getFeatures()) {
            // Check if the magnitude property exists
            if (feature.getProperty("Incident_Rate") != null && feature.hasProperty("Admin2")) {
                double magnitude = Double.parseDouble(feature.getProperty("Incident_Rate"));

                // Get the icon for the feature
                BitmapDescriptor pointIcon = BitmapDescriptorFactory
                        .defaultMarker(magnitudeToColor(magnitude));

                // Create a new point style
                GeoJsonPointStyle pointStyle = new GeoJsonPointStyle();

                // Set options for the point style
                pointStyle.setIcon(pointIcon);
                pointStyle.setTitle("1 in " + Math.round(100000 / magnitude) + " people");
                pointStyle.setSnippet("COVID-19 incidence rate in " + feature.getProperty("Admin2") + " County, " + feature.getProperty("Province_State"));
                pointStyle.setAlpha((float) 0.6);

                // Assign the point style to the feature
                feature.setPointStyle(pointStyle);
            }
        }
    }

    /**
     * Assigns a color based on the given magnitude
     */
    private static float magnitudeToColor(double magnitude) {
        if (magnitude < 2000) {
            return BitmapDescriptorFactory.HUE_YELLOW;
        } else if (magnitude < 4000) {
            return BitmapDescriptorFactory.HUE_ORANGE;
        } else if (magnitude < 6000) {
            return BitmapDescriptorFactory.HUE_RED;
        } else {
            return BitmapDescriptorFactory.HUE_VIOLET;
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        /* get latest COVID-19 data */
        retrieveFileFromUrl();

        /* Initialize view */
        View view = inflater.inflate(R.layout.fragment_map, container, false);

        /* Initialize map fragment */
        SupportMapFragment supportMapFragment = (SupportMapFragment)
                getChildFragmentManager().findFragmentById(R.id.google_map);

        /* Async map */
        supportMapFragment.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(final GoogleMap googleMap) {
                /* when map is loaded */
                mMap = googleMap;
                if (ActivityCompat.checkSelfPermission(getActivity().getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity().getApplicationContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    return;
                }
                mMap.setMyLocationEnabled(true);
/*                try {
                    GeoJsonLayer layer = new GeoJsonLayer(mMap, R.raw.covid19cases, getContext());
                    GeoJsonPolygonStyle polyStyle = layer.getDefaultPolygonStyle();

                    GeoJsonPointStyle pointStyle = layer.getDefaultPointStyle();
                    pointStyle.setAlpha((float)0.2);
                    // String geoJsonFeature = GeoJsonFeature.;

                    polyStyle.setStrokeColor(Color.CYAN);
                    polyStyle.setStrokeWidth(2);
                    layer.addLayerToMap();
                } catch (IOException e) {
                } catch (JSONException e) {
                }*/
                googleMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
                    @Override
                    public void onMapClick(LatLng latLng) {
                        /* when clicked on map */
                        /* initialize marker options */
                        MarkerOptions markerOptions = new MarkerOptions();
                        /* set position of marker */
                        markerOptions.position(latLng);
                        /* set title of marker */
                        markerOptions.title(latLng.latitude + " : " + latLng.longitude);

                        /* remove all markers */
                        // googleMap.clear();

                        /* animating to zoom the marker */
                        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(
                                latLng, 10

                        ));
                        /* add marker on map */
                        mMap.addMarker(markerOptions);

                    }
                });
            }
        });

        /* return view */
        return view;
    }

    //protected GoogleMap getMap() {
      //  return mMap;
    //}

}