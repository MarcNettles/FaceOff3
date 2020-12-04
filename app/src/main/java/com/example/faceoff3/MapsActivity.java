package com.example.faceoff3;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.common.api.Status;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Dash;
import com.google.android.gms.maps.model.Dot;
import com.google.android.gms.maps.model.Gap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.PatternItem;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.model.RectangularBounds;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;
import com.google.maps.android.collections.GroundOverlayManager;
import com.google.maps.android.collections.MarkerManager;
import com.google.maps.android.collections.PolygonManager;
import com.google.maps.android.collections.PolylineManager;
import com.google.maps.android.data.Feature;
import com.google.maps.android.data.geojson.GeoJsonFeature;
import com.google.maps.android.data.geojson.GeoJsonLayer;
import com.google.maps.android.data.geojson.GeoJsonPointStyle;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Objects;
import java.util.List;
import java.util.regex.Pattern;

import androidx.annotation.DrawableRes;
import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;


import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.android.data.geojson.GeoJsonPolygonStyle;


public class MapsActivity extends FragmentActivity {
    private static final String TAG = "MapsActivity";
    private GoogleMap mMap;
    private final static String mLogTag = "GeoJsonLayers";
    public GeoJsonLayer layer2;
    public HashMap<String, String> countyCodeMap = new HashMap<>();
    private LatLngBounds mMapBoundary;
    private Location mUserPosition;
    public MarkerManager markerManager;
    public GroundOverlayManager groundOverlayManager;
    public PolygonManager polygonManager;
    public PolylineManager polylineManager;


    /* this function pulls the county polygons from the 2010 Census GeoJSON on-device resource */
    private void retrieveFileFromResource() {
        try {
            layer2 = new GeoJsonLayer(mMap, R.raw.census2010counties20m, Objects.requireNonNull(this));

        } catch (IOException e) {
            Log.e(mLogTag, "GeoJSON file could not be read");
        } catch (JSONException e) {
            Log.e(mLogTag, "GeoJSON file could not be converted to a JSONObject");
        }
    }


    /* these next two functions pull live COVID-19 data from URL stored in /res/values/strings.xml */
    private void retrieveCovidFileFromUrl() {
        new DownloadCovidGeoJsonFile().execute(getString(R.string.covid_geojson_url));
    }

    private void retrieveCountyFileFromUrl() {
        new DownloadCountyGeoJsonFile().execute(getString(R.string.county_geojson_url));
    }

    private class DownloadCovidGeoJsonFile extends AsyncTask<String, Void, GeoJsonLayer> {


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

                return new GeoJsonLayer(mMap, new JSONObject(result.toString()), markerManager, polygonManager, polylineManager, groundOverlayManager);
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

    private class DownloadCountyGeoJsonFile extends AsyncTask<String, Void, GeoJsonLayer> {
        @Override
        protected GeoJsonLayer doInBackground(String... params) {
            try {
                // Open a stream from the URL (which is hosted at https://eric.clst.org/tech/usgeojson/)
                InputStream stream = new URL(params[0]).openStream();

                String line;
                String keyString;

                // StringBuilder result = new StringBuilder();
                BufferedReader reader = new BufferedReader(new InputStreamReader(stream));

                while ((line = reader.readLine()) != null) {
                    // Read and save each line of the stream
                    if (line.contains("0500000US")) { // find the county code in the county geojson
                        keyString = line.substring((line.indexOf("0500000US") + 9), (line.indexOf("0500000US") + 14));  // this should grab the 5 numbers right after the indexed "0500000US" portion of the string
                        countyCodeMap.put(keyString, line.substring(0, line.length() - 1)); // put this key value pair in the hashMap countyCodeMap, chopping the last comma off each line beforehand
                    }
                }
                // line = line.substring(0, line.length() - 1);  // chop off the last character (a comma)

                // Close the stream
                reader.close();
                stream.close();

            } catch (IOException e) {
                Log.e(mLogTag, "GeoJSON file could not be read");
            }
            return null;
        }

//        @Override
//        protected void onPostExecute(GeoJsonLayer layer) {
//            if (layer != null) {
//                layer.addLayerToMap();
//            }
//        }
    }

    private void drawAndFillCountyBoundary(GeoJsonPolygonStyle geoJsonPolygonStyle, String countyCode) throws JSONException {
        String line = countyCodeMap.get(countyCode);
        line = "{\n" +
                "\"type\": \"FeatureCollection\",\n" +
                "\"features\": [\n" + line + "}]\n" +
                "}\n";                                // make the single line match a valid GeoJSON file
        GeoJsonLayer geoJsonLayer = new GeoJsonLayer(mMap, new JSONObject(line), markerManager, polygonManager, polylineManager, groundOverlayManager);
        for (GeoJsonFeature feature : geoJsonLayer.getFeatures()){
            feature.setPolygonStyle(geoJsonPolygonStyle);
        }
        geoJsonLayer.addLayerToMap();
    }

//    line = countyCodeMap.get(countyCode);
//    line = "{\n" +
//            "\"type\": \"FeatureCollection\",\n" +
//            "\"features\": [\n" + line + "]\n" +
//            "}\n";                                // make the single line match a valid GeoJSON file
//
//    // Close the stream
//                reader.close();
//                stream.close();
//
//                layer = new GeoJsonLayer(mMap, new JSONObject(line));
//                layer.addLayerToMap();

    private void addGeoJsonLayerToMap(GeoJsonLayer layer) {

        addColorsToMarkers(layer);
        layer.addLayerToMap();

        // receiving features via GeoJsonLayer clicks.
        layer.setOnFeatureClickListener(new GeoJsonLayer.GeoJsonOnFeatureClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onFeatureClick(Feature feature) {   // TODO: this is apparently where a problem is that keeps the map from registering more than 1 county-boundary drawing click. What should be expected from this function onFeatureClick?
                String countyCode = feature.getProperty("FIPS");

                GeoJsonPolygonStyle geoJsonPolygonStyle = new GeoJsonPolygonStyle();
                List<PatternItem> dotList =  Arrays.asList(new Dash(30), new Gap(20));
                Color colorTransparentYellow = Color.valueOf(0x80DEDA7A);
                Color colorTransparentOrange = Color.valueOf(0x80DEA600);
                Color colorTransparentDarkOrange = Color.valueOf(0x80DE7400);
                Color colorTransparentRed = Color.valueOf(0x80BF1B00);
                geoJsonPolygonStyle.setStrokePattern(dotList);
                geoJsonPolygonStyle.setClickable(true);

                String recommendation = "";
                /* right now this is the same calculation as below in the magnitudeToColor function,
                 * but in the future we can change it to incorporate the user's current face-touching
                 * to hand-washing ratio, to give a more tailored recommendation to the user */
                if (Double.parseDouble(feature.getProperty("Incident_Rate")) < 500) {
                    recommendation = "exercise caution in this area";
                    geoJsonPolygonStyle.setFillColor(colorTransparentYellow.toArgb());
                } else if (Double.parseDouble(feature.getProperty("Incident_Rate")) < 1000) {
                    recommendation = "exercise exaggerated caution in this area";
                    geoJsonPolygonStyle.setFillColor(colorTransparentOrange.toArgb());
                } else if (Double.parseDouble(feature.getProperty("Incident_Rate")) < 3333) {
                    recommendation = "exercise extreme caution in this area";
                    geoJsonPolygonStyle.setFillColor(colorTransparentDarkOrange.toArgb());
                } else {
                    recommendation = "avoid unnecessary travel to or within this area";
                    geoJsonPolygonStyle.setFillColor(colorTransparentRed.toArgb());
                }

                try {
                    drawAndFillCountyBoundary(geoJsonPolygonStyle, countyCode);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                Toast.makeText(getApplicationContext(),
                        "Based on your habits, FaceOff recommends that you " + recommendation,
                        Toast.LENGTH_LONG).show();// TODO: seems like this only gets called once and done

                /* this is where we'll add and remove each county's GeoJSON layer */
             /*   if ((feature.getProperty("STATE") + feature.getProperty("COUNTY")).equals(feature.getProperty("FIPS"))) {

                }*/
            }

        });
    }

    /**
     * Puts a COVID pointIcon (marker) in every US county based on each county's latest/current incidence rate
     */
    private void addColorsToMarkers(GeoJsonLayer layer) {
        BitmapDescriptor pointIcon;
        BitmapDescriptor pointIconYellow = BitmapDescriptorFactory.fromBitmap(getMarkerBitmapFromView(R.drawable.ic_coronavirus_yellow));
        BitmapDescriptor pointIconOrange = BitmapDescriptorFactory.fromBitmap(getMarkerBitmapFromView(R.drawable.ic_coronavirus_orange));
        BitmapDescriptor pointIconDarkOrange = BitmapDescriptorFactory.fromBitmap(getMarkerBitmapFromView(R.drawable.ic_coronavirus_dark_orange));
        BitmapDescriptor pointIconRed = BitmapDescriptorFactory.fromBitmap(getMarkerBitmapFromView(R.drawable.ic_coronavirus_red));

        // Iterate over all the features stored in the layer
        for (GeoJsonFeature feature : layer.getFeatures()) {
            // Check if the magnitude property exists
            if (feature.getProperty("Incident_Rate") != null && feature.hasProperty("Admin2")) {
                double magnitude = Double.parseDouble(feature.getProperty("Incident_Rate"));

                /**
                 * Now assign a color based on the given magnitude
                 */
                if (magnitude < 500) {         // meaning less than 500 in 100,000 people, or fewer than 1 in 200 people or up to 0.5% of people
                    pointIcon = pointIconYellow;
                } else if (magnitude < 1000) { // meaning less than 1,000 in 100,000 people, or up to 1 in 100 people or up to 1% of people
                    pointIcon = pointIconOrange;
                } else if (magnitude < 3333) { // meaning less than 3,333 in 100,000 people, or up to 1 in 30 people or up to 3.3% of people
                    pointIcon = pointIconDarkOrange;
                } else {                       //meaning anything greater than 3,333 in 100,000 people, or at least 1 in 30 people or >=3.3% of people
                    pointIcon = pointIconRed;
                }

                // Create a new point style
                GeoJsonPointStyle pointStyle = new GeoJsonPointStyle();

                // Set options for the point style
                pointStyle.setIcon(pointIcon);
                pointStyle.setTitle("1 in " + Math.round(100000 / magnitude) + " people are known positive");
                pointStyle.setSnippet("COVID-19 incidence rate in " + feature.getProperty("Admin2") + " County, " + feature.getProperty("Province_State"));
//                pointStyle.setAlpha((float) 0.6); // pointIcon transparency

                // Assign the point style to the feature
                feature.setPointStyle(pointStyle);
            }
        }
    }


    /* turn the coronavirus png that Android Studio has in image assets into a bitmap (as required
    for later use as a color-coded point marker for each US county) */
    private Bitmap getMarkerBitmapFromView(@DrawableRes int resId) {

        View customMarkerView = ((LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.view_custom_marker, null);
        ImageView markerImageView = (ImageView) customMarkerView.findViewById(R.id.profile_image);
        markerImageView.setImageResource(resId);
        customMarkerView.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
        customMarkerView.layout(0, 0, customMarkerView.getMeasuredWidth(), customMarkerView.getMeasuredHeight());
        customMarkerView.buildDrawingCache();
        Bitmap returnedBitmap = Bitmap.createBitmap(customMarkerView.getMeasuredWidth(), customMarkerView.getMeasuredHeight(),
                Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(returnedBitmap);
        canvas.drawColor(Color.WHITE, PorterDuff.Mode.SRC_IN);
        Drawable drawable = customMarkerView.getBackground();
        if (drawable != null)
            drawable.draw(canvas);
        customMarkerView.draw(canvas);
        return returnedBitmap;
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the supportMapFragment and get notified when the map is ready to be used

        /* Initialize map fragment */
        SupportMapFragment mapFragment = (SupportMapFragment)
                getSupportFragmentManager().findFragmentById(R.id.google_map);
        mapFragment.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                /* when map is loaded */
                mMap = googleMap;
                mMap.setPadding(0,220, 0, 0);
                mMap.getUiSettings().setZoomControlsEnabled(true);
           //     mMap.setOnCameraIdleListener();  TODO: not sure if we need this but it's in the demo multilayer activity


                markerManager = new MarkerManager(mMap);
                groundOverlayManager = new GroundOverlayManager(mMap);
                polygonManager = new PolygonManager(mMap);
                polylineManager = new PolylineManager(mMap);

                /* get latest COVID-19 data */
                retrieveCovidFileFromUrl();

                /* get the county polygons from the 2010 Census GeoJSON */
                //     retrieveFileFromResource();
                retrieveCountyFileFromUrl();

                if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    return;
                }
                mMap.setMyLocationEnabled(true);
                //setCameraView();
//                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(myLocation ));


                LatLngBounds coloradoBounds = new LatLngBounds(
                        new LatLng(37.17, -109.10), // SW bounds
                        new LatLng(41.70, -101.09)  // NE bounds
                );
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(coloradoBounds.getCenter(), 8));



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

                mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
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
                                latLng, 8

                        ));
                        /* add marker on map */
                        MarkerManager.Collection markerCollection = markerManager.newCollection();
                        markerCollection.addMarker(markerOptions);

//                                mMap.addMarker(markerOptions);

                    }
                });
            }
        });

        String apiKey = getString(R.string.api_key);

        if(!Places.isInitialized()){
            Places.initialize(getApplicationContext(), apiKey);
        }

        PlacesClient placesClient = Places.createClient(getApplicationContext());

        // Initialize the AutocompleteSupportFragment.
        AutocompleteSupportFragment autocompleteFragment = (AutocompleteSupportFragment)
                getSupportFragmentManager().findFragmentById(R.id.autocomplete_fragment);


        autocompleteFragment.setLocationBias(RectangularBounds.newInstance(
                new LatLng(37.20, -109.00), // SW bounds
                new LatLng(41.73, -100.99)  // NE bounds
        ));

        autocompleteFragment.setCountries("US");

        // Specify the types of place data to return.
        autocompleteFragment.setPlaceFields(Arrays.asList(Place.Field.ID, Place.Field.NAME));

        // Set up a PlaceSelectionListener to handle the response.
        autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(@NotNull Place place) {
                // TODO: Get info about the selected place.
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(place.getLatLng().latitude, place.getLatLng().longitude), 10));    // TODO: why doesn't this work?
                Log.i(TAG, "Place: " + place.getName() + ", " + place.getId());
            }


            @Override
            public void onError(@NotNull Status status) {
                // TODO: Handle the error.
                Log.i(TAG, "An error occurred: " + status);
            }
        });
    }
}

    //protected GoogleMap getMap() {
      //  return mMap;
    //}

