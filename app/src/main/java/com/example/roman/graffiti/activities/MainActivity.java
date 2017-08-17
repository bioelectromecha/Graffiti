package com.example.roman.graffiti.activities;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.PixelFormat;
import android.graphics.drawable.Drawable;
import android.hardware.Camera;
import android.location.Location;
import android.net.Uri;
import android.os.Environment;
import android.os.ParcelFileDescriptor;
import android.provider.MediaStore;
import android.os.Bundle;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.apkfuns.logutils.LogUtils;
import com.example.roman.graffiti.location.LocationConfiguration;
import com.example.roman.graffiti.location.LocationHelper;
import com.example.roman.graffiti.R;

import java.io.File;
import java.io.FileDescriptor;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class MainActivity extends Activity implements SurfaceHolder.Callback {
    private static final int RESULT_LOAD_IMAGE = 403;

    private Camera camera = null;
    private SurfaceView mCameraSurfaceView = null;
    private SurfaceHolder cameraSurfaceHolder = null;
    private ImageView mImageView = null;
    private boolean previewing = false;
    private RelativeLayout relativeLayout;
    private Button btnCapture = null;
    private LocationHelper mLocationHelper;
    private Location mGraffitiLocation = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        mGraffitiLocation = new Location("");
        mGraffitiLocation.setLatitude(32.0629238);
        mGraffitiLocation.setLongitude(34.7719123);
        mGraffitiLocation.setAltitude(0);

        mLocationHelper = new LocationHelper(this);
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);

        getWindow().setFormat(PixelFormat.TRANSLUCENT);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_main);

        relativeLayout = (RelativeLayout) findViewById(R.id.containerImg);
        relativeLayout.setDrawingCacheEnabled(true);
        mCameraSurfaceView = (SurfaceView)
                findViewById(R.id.surfaceView1);
        //  mCameraSurfaceView.setLayoutParams(new FrameLayout.LayoutParams(640, 480));
        cameraSurfaceHolder = mCameraSurfaceView.getHolder();
        cameraSurfaceHolder.addCallback(this);
        //    cameraSurfaceHolder.setType(SurfaceHolder.
        //                                               SURFACE_TYPE_PUSH_BUFFERS);

        mImageView = (ImageView) findViewById(R.id.imageView1);

        btnCapture = (Button) findViewById(R.id.button1);
        btnCapture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Random random = new Random();
//                int x = random.nextInt(mCameraSurfaceView.getWidth());
//                int y = random.nextInt(mCameraSurfaceView.getHeight());
//                int size = random.nextInt(Math.min(x, y));
//                mImageView.setMinimumHeight(size);
//                mImageView.setMinimumWidth(size);

//                ViewGroup.MarginLayoutParams marginParams = new ViewGroup.MarginLayoutParams(mImageView.getLayoutParams());
//                marginParams.setMargins(x, y, 0, 0);
//                RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(marginParams);
//                mImageView.setLayoutParams(layoutParams);

//                Intent i = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
//                startActivityForResult(i, RESULT_LOAD_IMAGE);

//                Intent intent = new Intent(getApplicationContext(),DrawActivity.class);
//                startActivity(intent);
//                finish();

            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        mLocationHelper.startLocationUpdates();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mLocationHelper.stopLocationUpdates();
    }

    Camera.ShutterCallback cameraShutterCallback = new Camera.ShutterCallback() {
        @Override
        public void onShutter() {
            // TODO Auto-generated method stub
        }
    };

    Camera.PictureCallback cameraPictureCallbackRaw = new Camera.PictureCallback() {
        @Override
        public void onPictureTaken(byte[] data, Camera camera) {
            // TODO Auto-generated method stub
        }
    };

    Camera.PictureCallback cameraPictureCallbackJpeg = new Camera.PictureCallback() {
        @Override
        public void onPictureTaken(byte[] data, Camera camera) {
            // TODO Auto-generated method stub
            Bitmap cameraBitmap = BitmapFactory.decodeByteArray
                    (data, 0, data.length);

            int wid = cameraBitmap.getWidth();
            int hgt = cameraBitmap.getHeight();

            //  Toast.makeText(getApplicationContext(), wid+""+hgt, Toast.LENGTH_SHORT).show();
            Bitmap newImage = Bitmap.createBitmap
                    (wid, hgt, Bitmap.Config.ARGB_8888);

            Canvas canvas = new Canvas(newImage);

            canvas.drawBitmap(cameraBitmap, 0f, 0f, null);

            Drawable drawable = getResources().getDrawable
                    (R.mipmap.ic_launcher);
            drawable.setBounds(20, 30, drawable.getIntrinsicWidth() + 20, drawable.getIntrinsicHeight() + 30);
            drawable.draw(canvas);


            File storagePath = new File(Environment.
                    getExternalStorageDirectory() + "/PhotoAR/");
            storagePath.mkdirs();

            File myImage = new File(storagePath,
                    Long.toString(System.currentTimeMillis()) + ".jpg");

            try {
                FileOutputStream out = new FileOutputStream(myImage);
                newImage.compress(Bitmap.CompressFormat.JPEG, 80, out);


                out.flush();
                out.close();
            } catch (FileNotFoundException e) {
                Log.d("In Saving File", e + "");
            } catch (IOException e) {
                Log.d("In Saving File", e + "");
            }

            camera.startPreview();


            newImage.recycle();
            newImage = null;

            Intent intent = new Intent();
            intent.setAction(Intent.ACTION_VIEW);

            intent.setDataAndType(Uri.parse("file://" + myImage.getAbsolutePath()), "image/*");
            startActivity(intent);

        }
    };

    @Override
    public void surfaceChanged(SurfaceHolder holder,
                               int format, int width, int height) {
        // TODO Auto-generated method stub

        if (previewing) {
            camera.stopPreview();
            previewing = false;
        }
        try {
            Camera.Parameters parameters = camera.getParameters();
//            TODO: give it to amit
//            LogUtils.d("horizontal view angle: " + camera.getParameters().getHorizontalViewAngle());
//            LogUtils.d("vertical view angle: " + camera.getParameters().getVerticalViewAngle());

            parameters.setPreviewSize(640, 480);
            parameters.setPictureSize(640, 480);
            if (this.getResources().getConfiguration().orientation != Configuration.ORIENTATION_LANDSCAPE) {
                camera.setDisplayOrientation(90);

            }

            // parameters.setRotation(90);
            camera.setParameters(parameters);

            camera.setPreviewDisplay(cameraSurfaceHolder);
            camera.startPreview();
            previewing = true;
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        // TODO Auto-generated method stub
        try {
            camera = Camera.open();
        } catch (RuntimeException e) {
            Toast.makeText(getApplicationContext(), "Device camera  is not working properly, please try after sometime.", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        // TODO Auto-generated method stub
        camera.stopPreview();
        camera.release();
        camera = null;
        previewing = false;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RESULT_LOAD_IMAGE && resultCode == RESULT_OK && null != data) {
            Uri selectedImage = data.getData();
            String[] filePathColumn = {MediaStore.Images.Media.DATA};

            Cursor cursor = getContentResolver().query(selectedImage,
                    filePathColumn, null, null, null);
            cursor.moveToFirst();

            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            String picturePath = cursor.getString(columnIndex);
            cursor.close();

            Bitmap bmp = null;
            try {
                bmp = getBitmapFromUri(selectedImage);
            } catch (IOException e) {
                e.printStackTrace();
            }
            mImageView.setImageBitmap(bmp);
        }
    }

    private Bitmap getBitmapFromUri(Uri uri) throws IOException {
        ParcelFileDescriptor parcelFileDescriptor = getContentResolver().openFileDescriptor(uri, "r");
        FileDescriptor fileDescriptor = parcelFileDescriptor.getFileDescriptor();
        Bitmap image = BitmapFactory.decodeFileDescriptor(fileDescriptor);
        parcelFileDescriptor.close();
        return image;
    }

    public void updateGraffitiSize(Location cameraLocation) {
        if (mGraffitiLocation != null) {
            double ratio = LocationConfiguration.graffitySizeRatio(cameraLocation, mGraffitiLocation);
            int width = mCameraSurfaceView.getWidth();
            int height = mCameraSurfaceView.getHeight();

            double bearingDif = LocationConfiguration.getHorizontalAngle(cameraLocation, mGraffitiLocation);

            if (ratio != 0) {
                mImageView.setVisibility(View.VISIBLE);
                width = (int) (width * ratio);
                height = (int) (height * ratio);
            }else{
                mImageView.setVisibility(View.INVISIBLE);
            }
            mImageView.setMinimumHeight(height);
            mImageView.setMaxHeight(height);
            mImageView.setMinimumWidth(width);
            mImageView.setMaxWidth(width);
            LogUtils.d("image size changed " + width+ " , " + height+" , " + ratio + " , "+ bearingDif);
        }
    }

}
