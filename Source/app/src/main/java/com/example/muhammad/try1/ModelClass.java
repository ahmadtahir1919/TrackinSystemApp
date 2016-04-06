package com.example.muhammad.try1;

import com.google.gson.annotations.SerializedName;

/**
 * Created by muhammad on 5/4/2016.
 */

public class ModelClass {

    @SerializedName("longi")
    public String longitudeServer;

    @SerializedName("lati")
    public String latitudeServer;

    @SerializedName("uniqueid")
    public String uniqueidSserver;

    public ModelClass(){
        // blank constructor is required
    }

    public String getLongitude(){
        return longitudeServer;
    }


    public String getLatitude(){
        return latitudeServer;
    }


    public String getUniqueId(){
        return uniqueidSserver;
    }


    //... More setter and getter here
}