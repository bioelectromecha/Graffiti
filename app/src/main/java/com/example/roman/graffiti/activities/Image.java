package com.example.roman.graffiti.activities;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.util.Log;

import com.google.firebase.storage.StorageReference;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by stav on 8/17/2017.
 */
public class Image {
    private Location location;
    private Bitmap image;

    public Image(Bitmap img, Location l){
        this.image = img;
        this.location = l;
    }

    public Location location(){
        return this.location;
    }
    public Bitmap getImage(){
        return this.image;
    }
}
