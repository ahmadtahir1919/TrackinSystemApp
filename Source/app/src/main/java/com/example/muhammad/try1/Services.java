package com.example.muhammad.try1;

import android.app.ProgressDialog;
import android.app.Service;
import android.content.Intent;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

/**
 * Created by muhammad on 31/3/2016.
 */
public class Services extends Service implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {
    private com.google.android.gms.location.LocationListener mListener;
    public MainActivity main;
    private GoogleApiClient mLocationClient;

    double latitude = 0;
    double longitude = 0;
    protected static String longitudeServer;
    protected static String latitudeServer;
    protected static String uniqueidSserver;
    protected static String latitudeLast;
    protected static String logitudeLast;
    protected static String uniqueidlast;
    protected static double latilasdoublet;
    protected static double longilastdouble;
    protected Handler handler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            if (msg.what == 1) {
               // main.LocationDetector(getApplicationContext());

            }
        }
    };

    @Override
    public void onCreate() {

        super.onCreate();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Toast.makeText(getApplicationContext(), "Serviceee OUTTT", Toast.LENGTH_LONG).show();

        //handler.sendEmptyMessage(1);

        mLocationClient = new GoogleApiClient.Builder(this)
                .addApi(LocationServices.API)
                .addConnectionCallbacks((GoogleApiClient.ConnectionCallbacks) this)
                .addOnConnectionFailedListener((GoogleApiClient.OnConnectionFailedListener) this)
                .build();

        mLocationClient.connect();


        return START_STICKY;

    }

    protected void LocationFinder() {
        mListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                latitude = location.getLatitude();
                longitude = location.getLongitude();
                LatLng latLng = new LatLng(latitude, longitude);
                Toast.makeText(getApplicationContext(), "Location serviceeeeeeeee : " + location.getLatitude() + ", " + location.getLongitude(), Toast.LENGTH_LONG).show();

             //   AppUtill.UniqueId();
                if (AppStatus.getInstance(getApplication()).isOnline()) {
                    new JSONAsyncTask().execute("http://103.8.113.7/hajjapi/api/GPSLocator/GetLocations");
                } else {

                    Toast.makeText(getApplicationContext(), "Turn On your WIFI ", Toast.LENGTH_LONG).show();

                }
            }
        };
        LocationRequest request = LocationRequest.create();
        request.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        request.setInterval(1000);
        request.setFastestInterval(1000);
        LocationServices.FusedLocationApi.requestLocationUpdates(mLocationClient, request, mListener);
    }



    @Override
    public void onConnected(Bundle bundle) {
        LocationFinder();

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }

   private class JSONAsyncTask extends AsyncTask<String, Void, Boolean> {

        ProgressDialog dialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
          /*  dialog = new ProgressDialog(MainActivity.this);
            dialog.setMessage("Loading, please wait");
            dialog.setTitle("Connecting server");
            dialog.show();
            dialog.setCancelable(false);*/
            /////// Toast.makeText(getApplicationContext(), "fetch data from server", Toast.LENGTH_LONG).show();

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


                    for (int i = 0; i < jsonarray.length(); i++) {
                        JSONObject obj = jsonarray.getJSONObject(i);

                        longitudeServer = obj.getString("longi");
                        latitudeServer = obj.getString("lati");
                        uniqueidSserver = obj.getString("uniqueid");
                        //   System.out.println(longitudeServer);
                        // System.out.println(latitudeServer);
                        //System.out.println(uniqueidSserver);


                    }

                    AppUtill.convertDataIntoDouble(latilasdoublet, longilastdouble, latitudeServer, longitudeServer, latitudeLast, logitudeLast);

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
            // Toast.makeText(getApplicationContext(), "Receicve data from server", Toast.LENGTH_LONG).show();

            if (result == false)
                Toast.makeText(getApplicationContext(), "Unable to fetch data from server", Toast.LENGTH_LONG).show();

        }

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

}
