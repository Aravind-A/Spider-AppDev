package com.example.aravind.task;

import android.Manifest;
import android.app.DownloadManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MapActivity extends FragmentActivity implements OnMapReadyCallback, LocationListener {

    private GoogleMap mMap;
    Intent mapIntent;
    RequestQueue queue;
    String url = "https://spider.nitt.edu/lateral/appdev/coordinates?category=";
    static public ArrayList<JSONObject> objects;
    JSONObject object;
    Button dirButton;
    LocationManager locationManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.test);
        dirButton = (Button) findViewById(R.id.dirButton);
        dirButton.setVisibility(View.INVISIBLE);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        final JSONObject[] object = new JSONObject[1];
        final double[] lat = new double[1];
        final double[] lng = new double[1];
        final String[] title = new String[1];
        TextView categoryName = (TextView) findViewById(R.id.categoryName);

        objects = new ArrayList<JSONObject>();
        String category = getIntent().getStringExtra("CATEGORY");
        url += category;
        categoryName.setText(category);

        queue = FetchLocations.getInstance().getRequestQueue();
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, url, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                for (int i = 0; i < response.length(); i++) {
                    try {
                        object[0] = response.getJSONObject(i);
                        objects.add(i, object[0]);
                        lat[0] = Double.parseDouble(object[0].getString("latitude"));
                        lng[0] = Double.parseDouble(object[0].getString("longitude"));
                        title[0] = object[0].getString("name");

                        LatLng place = new LatLng(lat[0], lng[0]);
                        mMap.addMarker(new MarkerOptions().position(place).title(title[0]));
                        mMap.moveCamera(CameraUpdateFactory.newLatLng(place));

                    } catch (JSONException e) {
                        e.printStackTrace();
                        Log.i("Error", "while adding JSON objects from response");
                    }
                }
                mMap.animateCamera(CameraUpdateFactory.zoomTo((float) 15.0));

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                displayError();
            }
        });
        queue.add(jsonArrayRequest);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (locationManager != null)
            locationManager.removeUpdates(this);
    }
    public void displayError() {
        Toast.makeText(this, "Please check your internet connection", Toast.LENGTH_SHORT).show();
    }

    public void searchClicked(View v) {
        EditText searchText = (EditText) findViewById(R.id.search);
        int flag = 0;
        object = new JSONObject();
        String query = searchText.getText().toString();
        for (JSONObject obj : objects) {
            try {
                if (obj.getString("name").equalsIgnoreCase(query)) {
                    object = obj;
                    flag = 1;
                } else {
                    LatLng dummy = new LatLng(Double.parseDouble(object.getString("latitude")), Double.parseDouble(object.getString("longitude")));
                    mMap.addMarker(new MarkerOptions().position(dummy).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)));
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        if (flag == 1) {
            try {
                LatLng place = new LatLng(Double.parseDouble(object.getString("latitude")), Double.parseDouble(object.getString("longitude")));
                Marker marker = mMap.addMarker(new MarkerOptions().position(place).title(object.getString("name")).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));
                mMap.moveCamera(CameraUpdateFactory.newLatLng(place));
                marker.showInfoWindow();
                dirButton.setVisibility(View.VISIBLE);
            } catch (JSONException e) {
                Log.i("error", "in new code");
                e.printStackTrace();
            }
        } else{
            Toast.makeText(this, "Sorry! Location not found", Toast.LENGTH_SHORT).show();
            dirButton.setVisibility(View.INVISIBLE);
        }
    }

    public void getDirections(View v) {
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        Criteria criteria = new Criteria();
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        String provider = locationManager.getBestProvider(criteria, false);
        Location location = locationManager.getLastKnownLocation(provider);
        if (location != null)
            onLocationChanged(location);
        locationManager.requestLocationUpdates(provider, 500, 1, this);
    }

    private String getMapsApiDirectionsUrl(double lat1,double lng1, double lat2,double lng2) {
        String waypoints = "waypoints=optimize:true|" + lat1 + "," + lng1 + "|" + "|"
                                                      + lat2 + "," + lng2;
        String sensor = "sensor=false";
        String params = waypoints + "&" + sensor;
        String output = "json";
        String url = "https://maps.googleapis.com/maps/api/directions/"
                + output + "?" + params;
        return url;
    }

    @Override
    public void onLocationChanged(Location location) {
        double lat = location.getLatitude();
        double lng = location.getLongitude();
        LatLng user_pos = new LatLng(lat,lng);
        Marker user = mMap.addMarker(new MarkerOptions().position(user_pos).title("You are here").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));
        user.showInfoWindow();
        try {
            String url = getMapsApiDirectionsUrl(lat,lng,Double.parseDouble(object.getString("latitude")),Double.parseDouble(object.getString("longitude")));
            ReadTask downloadTask = new ReadTask();
            downloadTask.execute(url);
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {
        Toast.makeText(this,"Sorry! Provider got disabled",Toast.LENGTH_SHORT).show();

    }

    private class ReadTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... url) {
            String data = "";
            try {
                HttpConnection http = new HttpConnection();
                data = http.readUrl(url[0]);
            } catch (Exception e) {
                Log.d("Background Task", e.toString());
            }
            return data;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            new ParserTask().execute(result);
        }
    }

    private class ParserTask extends
            AsyncTask<String, Integer, List<List<HashMap<String, String>>>> {

        @Override
        protected List<List<HashMap<String, String>>> doInBackground(
                String... jsonData) {

            JSONObject jObject;
            List<List<HashMap<String, String>>> routes = null;

            try {
                jObject = new JSONObject(jsonData[0]);
                PathJSONParser parser = new PathJSONParser();
                routes = parser.parse(jObject);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return routes;
        }

        @Override
        protected void onPostExecute(List<List<HashMap<String, String>>> routes) {
            ArrayList<LatLng> points = null;
            PolylineOptions polyLineOptions = null;

            // traversing through routes
            if (routes != null) {
                for (int i = 0; i < routes.size(); i++) {
                    points = new ArrayList<LatLng>();
                    polyLineOptions = new PolylineOptions();
                    List<HashMap<String, String>> path = routes.get(i);

                    for (int j = 0; j < path.size(); j++) {
                        HashMap<String, String> point = path.get(j);
                        double lat = Double.parseDouble(point.get("lat"));
                        double lng = Double.parseDouble(point.get("lng"));
                        LatLng position = new LatLng(lat, lng);
                        points.add(position);
                    }
                    polyLineOptions.addAll(points);
                    polyLineOptions.width(2);
                    polyLineOptions.color(Color.BLUE);
                }
                mMap.addPolyline(polyLineOptions);

            }
        }
    }
}
