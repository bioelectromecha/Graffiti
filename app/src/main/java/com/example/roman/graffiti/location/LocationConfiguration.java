package com.example.roman.graffiti.location;
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
    private static double defaultDistanceFromGraffity = 2.97; // initial distance between the camera and the grffiity
    private static double maxDistanceFromGraffity = 10.0;

    // returns the vertical angle between the camera and the graffity image
    protected static double getVerticalAngle (Location camL, Location grafL){
        double distance = camL.distanceTo(grafL);
        double heightDif = camL.getAltitude() - grafL.getAltitude();
        double verticalAngle = 0;
        if (heightDif >= 0)
                verticalAngle = asin(heightDif/distance);
        else
                verticalAngle = -asin(-heightDif/distance);
        return verticalAngle;
    }

    protected static double getHorizontalAngle (Location camL, Location grafL){
        // returns the horizontal angle between the camera and the graffity image
        return camL.getBearing()-grafL.getBearing();
    }

    protected static boolean isOnScreen (Location camL, Location grafL, Camera.Parameters camParameters){
        // checks if the graffity needs to be displayed on the screen
        double verticalAngle = getVerticalAngle(camL,grafL);
        double horizontalAngle = abs(getHorizontalAngle(camL,grafL));
        if (verticalAngle <= camParameters.getVerticalViewAngle()/2 && horizontalAngle <= camParameters.getHorizontalViewAngle()/2)
            return true;
        return false;
    }

    protected static double convertWidthToPixels (Location camL, Location grafL, Camera.Parameters camParameters, double xScreenSize){
        // returns the amounts of pixels in order to correct the size...
        double horizontalAngle = getHorizontalAngle(camL,grafL);
        double viewAngle = camParameters.getHorizontalViewAngle();
        double y = (xScreenSize/2)/tan(viewAngle);
        double z = 0;
        if (horizontalAngle >=0)
            z = y * tan(horizontalAngle);
        else
            z = -(y * tan(-horizontalAngle));
        return z;
    }

    protected static double convertHeightToPixels (Location camL, Location grafL, Camera.Parameters camParameters, double yScreenSize){
        // returns the amounts of pixels in order to correct the size...
        double verticalAngle = getVerticalAngle(camL,grafL);
        double viewAngle = camParameters.getVerticalViewAngle();
        double y = (yScreenSize/2)/tan(viewAngle);
        double z = 0;
        if (verticalAngle >=0)
            z = y * tan(verticalAngle);
        else
            z = -(y * tan(-verticalAngle));
        return z;
    }

    private static double getIncline (){
        // returns the distance graph incline
        return 1/(defaultDistanceFromGraffity - maxDistanceFromGraffity);
    }

    protected static double graffitySizeRatio (Location camL, Location grafL, double xScreenSize, double yScreenSize, double xGrafSize, double yGrafSize){
        // returns the screen-graffity ratio
        double ratio = 0;
        double distance = camL.distanceTo(grafL);
        if (distance <= defaultDistanceFromGraffity)
            ratio = 1;
        else if (distance >= maxDistanceFromGraffity)
            ratio = 0;
        else
            ratio = getIncline() * distance - (getIncline() * maxDistanceFromGraffity);
        return ratio;
    }


}









