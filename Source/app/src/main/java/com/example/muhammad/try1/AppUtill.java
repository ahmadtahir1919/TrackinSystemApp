package com.example.muhammad.try1;

import android.provider.Settings;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;

import java.text.DecimalFormat;

/**
 * Created by muhammad on 29/3/2016.
 */
public class AppUtill extends MainActivity {
    //static Context context;

    /*private static AppUtill instance = null;
    private AppUtill() {
        // Exists only to defeat instantiation.
    }
    public static AppUtill getInstance() {
        if(instance == null) {
            instance = new AppUtill();
        }
        return instance;
    }*/
    public static void convertDataIntoDouble(double a, double b, String c, String d, String e, String f) {
        // CalculationByDistance(HAMBURG1, HAMBURG);

       /* List<String> longitude = Arrays.asList(longitudeServer);
        logitudeLast = longitude.get(longitude.size() - 1);

        System.out.println(logitudeLast + " logitude ");


        List<String> latitude = Arrays.asList(latitudeServer);
        latitudeLast = latitude.get(latitude.size() - 1);

        System.out.println(latitudeLast + " latitude ");


        List<String> uniqueid = Arrays.asList(uniqueidSserver);
        uniqueidlast = uniqueid.get(uniqueid.size() - 1);

        System.out.println(uniqueidlast + " unique id ");
        latilasdoublet = Double.parseDouble(latitudeLast);
        longilastdouble = Double.parseDouble(logitudeLast);
*/

    }

    public static double CalculationByDistance(LatLng StartP, LatLng EndP) {
        int Radius = 6371;//radius of earth in Km
        double lat1 = StartP.latitude;
        double lat2 = EndP.latitude;
        double lon1 = StartP.longitude;
        double lon2 = EndP.longitude;
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
                        Math.sin(dLon / 2) * Math.sin(dLon / 2);
        double c = 2 * Math.asin(Math.sqrt(a));
        double valueResult = Radius * c;
        double km = valueResult / 1;
        DecimalFormat newFormat = new DecimalFormat("####");
        int kmInDec = Integer.valueOf(newFormat.format(km));
        double meter = valueResult % 1000;
        int meterInDec = Integer.valueOf(newFormat.format(meter));
        Log.i("Radius Value", "" + valueResult + "   KM  " + kmInDec + " Meter   " + meterInDec);
        double dist = Radius * c;
        System.out.println(dist + " distance ");
        return Radius * c;
    }
    public static String UniqueId(){
        String android_id = Settings.Secure.getString(getContext().getContentResolver(), Settings.Secure.ANDROID_ID);
        Toast.makeText(getContext(), android_id, Toast.LENGTH_SHORT).show();

        return android_id;
    }
}
