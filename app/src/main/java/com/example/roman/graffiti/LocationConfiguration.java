package com.example.roman.graffiti;
import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.PixelFormat;
import android.graphics.drawable.Drawable;
import android.hardware.Camera;
import android.location.Location;
import android.net.Uri;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import static java.lang.Math.abs;
import static java.lang.Math.asin;
import static java.lang.Math.tan;


/**
 * Created by amit nissan on 8/17/2017.
 */

public class LocationConfiguration {
    // returns the vertical angle between the camera and the graffity image
    protected static double getVerticalAngle (Location camL, Location grafL){
        double distance = camL.distanceTo(grafL);
        double heightDif = abs(camL.getAltitude() - grafL.getAltitude());
        double verticalAngle = asin(heightDif/distance);
        return verticalAngle;
    }

    protected static double getHorizontalAngle (Location camL, Location grafL){
        // returns the horizontal angle between the camera and the graffity image
        return abs(camL.getBearing()-grafL.getBearing());
    }

    protected static boolean isOnScreen (Location camL, Location grafL, Camera.Parameters camParameters){
        // checks if the graffity needs to be displayed on the screen
        double verticalAngle = getVerticalAngle(camL,grafL);
        double horizontalAngle = getHorizontalAngle(camL,grafL);
        if (verticalAngle <= camParameters.getVerticalViewAngle()/2 && horizontalAngle <= camParameters.getHorizontalViewAngle()/2)
            return true;
        return false;
    }

    protected static double convertWidthToPixels (Location camL, Location grafL, Camera.Parameters camParameters, float xScreenSize){
        // returns the amounts of pixels in order to correct the size...
        double horizontalAngle = getHorizontalAngle(camL,grafL);
        double viewAngle = camParameters.getHorizontalViewAngle();
        double y = (xScreenSize/2)/tan(viewAngle);
        double z = y * tan(horizontalAngle);
        return z;
    }

    protected static double convertHeightToPixels (Location camL, Location grafL, Camera.Parameters camParameters, float yScreenSize){
        // // returns the amounts of pixels in order to correct the size...
        double verticalAngle = getVerticalAngle(camL,grafL);
        double viewAngle = camParameters.getVerticalViewAngle();
        double y = (yScreenSize/2)/tan(viewAngle);
        double z = y * tan(verticalAngle);
        return z;
    }


}









