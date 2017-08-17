package com.example.roman.graffiti.location;
import android.hardware.Camera;
import android.location.Location;

import com.apkfuns.logutils.LogUtils;

import static java.lang.Math.abs;
import static java.lang.Math.asin;
import static java.lang.Math.tan;


/**
 * Created by amit nissan on 8/17/2017.
 */


public final class LocationConfiguration {
    private static double minDistanceFromGraffiti = 5; // initial distance between the camera and the grffiity
    private static double maxDistanceFromGraffity = 70;


    private LocationConfiguration() {
        // private constrcutor to avoid initializing
    }

    // returns the vertical angle between the camera and the graffity image
    public static double getVerticalAngle (Location camL, Location grafL){
        double distance = camL.distanceTo(grafL);
        double heightDif = camL.getAltitude() - grafL.getAltitude();
        double verticalAngle = 0;
        if (heightDif >= 0)
                verticalAngle = asin(heightDif/distance);
        else
                verticalAngle = -asin(-heightDif/distance);
        return verticalAngle;
    }

    public static double getHorizontalAngle (Location cameraLocation, Location graffitiLocation){
        // returns the horizontal angle between the camera and the graffity image
        LogUtils.d("camera bearing: " + cameraLocation.getBearing() + " graffiti bearing: "+ graffitiLocation.getBearing());
        return cameraLocation.bearingTo(graffitiLocation);
    }

    public static boolean isOnScreen (Location camL, Location grafL, Camera.Parameters camParameters){
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

    public static double convertHeightToPixels (Location camL, Location grafL, Camera.Parameters camParameters, double yScreenSize){
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
        return 1/(minDistanceFromGraffiti - maxDistanceFromGraffity);
    }

    public static double graffitySizeRatio (Location camLocation, Location grafLocation){
        // returns the screen-graffity ratio
        double ratio = 0;
        double distance = camLocation.distanceTo(grafLocation);
        LogUtils.d("distance = " + distance);
        if (distance <= minDistanceFromGraffiti) {
            ratio = 1;
        } else if (distance >= maxDistanceFromGraffity) {
            ratio = 0;
        }else {
            ratio = (getIncline() * distance) - (getIncline() * maxDistanceFromGraffity);
        }

        return ratio;
    }


}









