package com.example.muhammad.try1;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.gson.Gson;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity
        implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {
    // static final LatLng HAMBURG1 = new LatLng(74.3226214, 31.5003567);
    // static final LatLng HAMBURG = new LatLng(74.3229122, 31.5003193);
    private static MainActivity instance;
    private ArrayList<LatLng> latLngList;
    private static final int ERROR_DIALOG_REQUEST = 9001;
    GoogleMap mMap;
    int FirstTimeMapIniciate = 0;
    double latitude = 0;
    double longitude = 0;
    private GoogleApiClient mLocationClient;
    private com.google.android.gms.location.LocationListener mListener;
    private Marker marker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        latLngList = new ArrayList<>();

        if (servicesOK()) {
            setContentView(R.layout.activity_map);

            if (initMap()) {
                //  gotoLocation(SEATTLE_LAT, SEATTLE_LNG, 15);

                mLocationClient = new GoogleApiClient.Builder(this)
                        .addApi(LocationServices.API)
                        .addConnectionCallbacks(this)
                        .addOnConnectionFailedListener(this)
                        .build();

                mLocationClient.connect();


                mMap.setMyLocationEnabled(true);
            } else {
                Toast.makeText(this, "Map Connected!", Toast.LENGTH_SHORT).show();
            }

        } else {
            setContentView(R.layout.activity_main);
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.mapTypeNone:
                mMap.setMapType(GoogleMap.MAP_TYPE_NONE);
                break;
            case R.id.mapTypeNormal:
                mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                break;
            case R.id.mapTypeSatellite:
                mMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
                break;
            case R.id.mapTypeTerrain:
                mMap.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
                break;
            case R.id.mapTypeHybrid:
                mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
                break;
        }


        return super.onOptionsItemSelected(item);
    }

    public boolean servicesOK() {

        int isAvailable = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);

        if (isAvailable == ConnectionResult.SUCCESS) {
            return true;
        } else if (GooglePlayServicesUtil.isUserRecoverableError(isAvailable)) {
            Dialog dialog = GooglePlayServicesUtil.getErrorDialog(isAvailable, this, ERROR_DIALOG_REQUEST);
            dialog.show();
        } else {
            Toast.makeText(this, "Can't connect to mapping service", Toast.LENGTH_SHORT).show();
        }

        return false;
    }

    private boolean initMap() {
        if (mMap == null && FirstTimeMapIniciate == 0) {
            SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
            mMap = mapFragment.getMap();
            mMap.setMyLocationEnabled(true);
            mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);

        }
        return (mMap != null);
    }

    private void gotoLocation(double lat, double lng, float zoom) {
        LatLng latLng = new LatLng(lat, lng);
        CameraUpdate update = CameraUpdateFactory.newLatLngZoom(latLng, zoom);
        mMap.moveCamera(update);
    }


    public void showCurrentLocation(MenuItem item) {
        Location currentLocation = LocationServices.FusedLocationApi
                .getLastLocation(mLocationClient);
        if (currentLocation == null) {
            Toast.makeText(this, "Couldn't connect!", Toast.LENGTH_SHORT).show();
        } else {
            LatLng latLng = new LatLng(
                    currentLocation.getLatitude(),
                    currentLocation.getLongitude()
            );
            CameraUpdate update = CameraUpdateFactory.newLatLngZoom(
                    latLng, 10
            );
            mMap.moveCamera(update);
        }

    }

    @Override
    public void onConnected(Bundle bundle) {
        Toast.makeText(this, "Ready to map!", Toast.LENGTH_SHORT).show();

        mListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                latitude = location.getLatitude();
                longitude = location.getLongitude();
                LatLng latLng1 = new LatLng(latitude, longitude);
                MarkerOptions mp = new MarkerOptions();
                mp = new MarkerOptions();
                mp.position(new LatLng(location.getLatitude(),
                        location.getLongitude()));

                //  mp.draggable(true);
                mp.icon(BitmapDescriptorFactory
                        .defaultMarker(BitmapDescriptorFactory.HUE_ROSE));
                mMap.addMarker(mp);

                // mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
                Toast.makeText(MainActivity.this, "Location : " + location.getLatitude() + ", " + location.getLongitude(), Toast.LENGTH_LONG).show();
                if (FirstTimeMapIniciate == 0) {
                    gotoLocation(location.getLatitude(), location.getLongitude(), 15);

                    FirstTimeMapIniciate = 1;

                }
                AppUtill.UniqueId();
                new JSONAsyncTask().execute("http://103.8.113.7/hajjapi/api/GPSLocator/GetLocations");

                if (AppStatus.getInstance(getContext()).isOnline()) {
                    //    new JSONAsyncTaskDistance().execute("https://maps.googleapis.com/maps/api/distancematrix/json?units=imperial&origins=31.5003567,74.3226214&destinations=31.4821587%2C74.3104778&key=AIzaSyA4j7PYgp9oP5wVclG0IDtYhowNzJr871M");

                } else {

                    Toast.makeText(MainActivity.this, "Turn On your WIFI ", Toast.LENGTH_LONG).show();

                }

             /*   if (marker != null) {
                    marker.remove();

                }*/


            }
        };
        LocationRequest request = LocationRequest.create();
        request.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        request.setInterval(1000);
        request.setFastestInterval(1000);
        LocationServices.FusedLocationApi.requestLocationUpdates(mLocationClient, request, mListener);
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }

    public void showcurrentLocation() {

        mMap.animateCamera(CameraUpdateFactory.zoomTo(15));


        FirstTimeMapIniciate = 1;

    }

    class JSONAsyncTask extends AsyncTask<String, Void, Boolean> {

        ProgressDialog dialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
          /*  dialog = new ProgressDialog(MainActivity.this);
            dialog.setMessage("Loading, please wait");
            dialog.setTitle("Connecting server");
            dialog.show();
            dialog.setCancelable(false);*/
            Toast.makeText(getApplicationContext(), "fetch data from server", Toast.LENGTH_LONG).show();

        }

        @Override
        protected Boolean doInBackground(String... urls) {
            try {

                //------------------>>

                HttpGet httpGet = new HttpGet(urls[0]);
                HttpClient httpclient = new DefaultHttpClient();
                HttpResponse response = httpclient.execute(httpGet);
                int status = response.getStatusLine().getStatusCode();

                if (status == 200) {
                    HttpEntity entity = response.getEntity();
                    String data = EntityUtils.toString(entity);


                    JSONArray jsonarray = new JSONArray(data);

                    latLngList.clear();

                    /*for (int i = 0; i < jsonarray.length(); i++) {
                        JSONObject obj = jsonarray.getJSONObject(i);

                        ModelClass s = new ModelClass(obj);
                        LatLng latLng = new LatLng(Double.parseDouble(s.getLatitude()), Double.parseDouble(s.getLongitude())); // Use your server's methods
                        latLngList.add(latLng);

                    }*/
                    for (int i = 0; i < jsonarray.length(); i++) {
                       // ModelClass s = LoganSquare.parse(jsonarray.getJSONObject(i).toString(), ModelClass.class);
                        ModelClass modelClass = new Gson().fromJson(jsonarray.getJSONObject(i).toString(), ModelClass.class);


                        LatLng latLng = new LatLng(Double.parseDouble(modelClass.getLatitude()), Double.parseDouble(modelClass.getLongitude())); // Use your server's methods
                        latLngList.add(latLng);

                    }


                    return true;

                }

                //------------------>>

            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return false;


        }

        protected void onPostExecute(Boolean result) {
            // dialog.cancel();
            // adapter.notifyDataSetChanged();
            Toast.makeText(getApplicationContext(), "Receicve data from server", Toast.LENGTH_LONG).show();

            if (result == false) {
                Toast.makeText(getApplicationContext(), "Unable to fetch data from server", Toast.LENGTH_LONG).show();

            }
            try {
                //ArrayList list = new ArrayList<>();
                //TODO: remove previus markers
                if (marker != null) {
                    mMap.clear();
                    Toast.makeText(getApplicationContext(), "Remove", Toast.LENGTH_LONG).show();

                }
                for (LatLng object : latLngList)

                    marker = mMap.addMarker(new MarkerOptions().title("User Name").position(object).icon(BitmapDescriptorFactory.fromResource(R.drawable.female4)));

                System.out.println(marker.getPosition() + "  Marker position.......");
            } catch (Exception e) {
                Toast.makeText(MainActivity.this, "Error ", Toast.LENGTH_LONG).show();
                // mMap.clear();
            }

        }

    }

    class JSONAsyncTaskDistance extends AsyncTask<String, Void, Boolean> {

        ProgressDialog dialog;

        @Override
        protected void onPreExecute() {
          /*  super.onPreExecute();
           dialog = new ProgressDialog(MainActivity.this);
            dialog.setMessage("Loading, please wait");
            dialog.setTitle("Connecting server");
            dialog.show();
            dialog.setCancelable(false);*/
            // Toast.makeText(getApplicationContext(), "fetch data from server", Toast.LENGTH_LONG).show();

        }

        @Override
        protected Boolean doInBackground(String... param) {


            try {
                URL url = new URL(param[0]);
                HttpURLConnection con = (HttpURLConnection) url.openConnection();
                con.setRequestMethod("GET");
                con.connect();
                int statuscode = con.getResponseCode();
                if (statuscode == HttpURLConnection.HTTP_OK) {
                    BufferedReader br = new BufferedReader(new InputStreamReader(con.getInputStream()));
                    StringBuilder sb = new StringBuilder();
                    String line = br.readLine();
                    while (line != null) {
                        sb.append(line);
                        line = br.readLine();
                    }
                    String json = sb.toString();
                    Log.d("JSON", json);
                    JSONObject root = new JSONObject(json);
                    JSONArray array_rows = root.getJSONArray("rows");
                    Log.d("JSON", "array_rows:" + array_rows);
                    JSONObject object_rows = array_rows.getJSONObject(0);
                    Log.d("JSON", "object_rows:" + object_rows);
                    JSONArray array_elements = object_rows.getJSONArray("elements");
                    Log.d("JSON", "array_elements:" + array_elements);
                    JSONObject object_elements = array_elements.getJSONObject(0);
                    Log.d("JSON", "object_elements:" + object_elements);
                    JSONObject object_duration = object_elements.getJSONObject("duration");
                    JSONObject object_distance = object_elements.getJSONObject("distance");

                    Log.d("JSON", "object_duration:" + object_duration);
                    System.out.println(object_duration.getString("value") + "," + object_distance.getString("value"));
                    System.out.println(object_duration.getString("value") + "," + object_distance.getString("value"));

                    return true;

                }
            } catch (MalformedURLException e) {
                Log.d("error", "error1");
            } catch (IOException e) {
                Log.d("error", "error2");
            } catch (JSONException e) {
                Log.d("error", "error3");
            }


            return false;
        }

        protected void onPostExecute(Boolean result) {
            // dialog.cancel();
            // adapter.notifyDataSetChanged();
            Toast.makeText(getApplicationContext(), "Receicve data from server", Toast.LENGTH_LONG).show();

            if (result == false) {
                Toast.makeText(getApplicationContext(), "Unable to fetch data from server", Toast.LENGTH_LONG).show();
            }
            //    Toast.makeText(getApplicationContext(), "fetch data from server", Toast.LENGTH_LONG).show();

        }


    }

    @Override
    protected void onStop() {
        super.onStop();

        //    startService(new Intent(getContext(), Services.class));


    }

    public MainActivity() {
        instance = this;
    }

    public static Context getContext() {
        return instance;
    }

}




