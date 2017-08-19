package com.example.roman.graffiti.activities;

import android.content.Context;
import android.graphics.Bitmap;
import android.location.Location;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;

import com.example.roman.graffiti.drawing.DrawView;
import com.example.roman.graffiti.R;

import java.util.Timer;
import java.util.TimerTask;

public class DrawActivity extends AppCompatActivity {

    private DrawView myDrawView;

    private SeekBar mRudeSeekbar;
    private ImagesManager imagesManager;
    private Context mContext;

    @Override

    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pain);
        myDrawView = (DrawView) findViewById(R.id.draw);
        mRudeSeekbar = (SeekBar) findViewById(R.id.rude_seekbar);
        imagesManager = new ImagesManager();

        Button btnCapture = (Button) findViewById(R.id.saveImageBtn);
        btnCapture.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Location location = new Location("");
                location.setLatitude(32.0629238);
                location.setLongitude(34.7719123);
                location.setAltitude(0);

                Bitmap b = myDrawView.mBitmap;

                imagesManager.uploadImage(b, location);
                Timer timer = new Timer();
                timer.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        finish();

                    }
                }, 2*60*2000);


            }
        });

        mContext = this;
        myDrawView.mPaint.setXfermode(null);


        int seekBarVisibility = mRudeSeekbar.getVisibility();
        mRudeSeekbar.setVisibility(View.VISIBLE);


        mRudeSeekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

                int myColor = -1;
                switch (progress) {

                    case 1:
                        myColor = mContext.getResources().getColor(R.color.slider_gradient_bottom);
                        break;

                    case 2:
                        myColor = mContext.getResources().getColor(R.color.slider_gradient_3);
                        break;

                    case 3:
                        myColor = mContext.getResources().getColor(R.color.slider_gradient_3);
                        break;

                    case 4:
                        myColor = mContext.getResources().getColor(R.color.slider_gradient_4);
                        break;

                    case 5:
                        myColor = mContext.getResources().getColor(R.color.slider_gradient_bottom);
                        break;

                    case 6:
                        myColor = mContext.getResources().getColor(R.color.slider_gradient_6);
                        break;

                    case 7:
                        myColor = mContext.getResources().getColor(R.color.slider_gradient_7);
                        break;

                    case 8:
                        myColor = mContext.getResources().getColor(R.color.slider_gradient_8);
                        break;

                    case 9:
                        myColor = mContext.getResources().getColor(R.color.slider_gradient_9);
                        break;

                    case 10:
                        myColor = mContext.getResources().getColor(R.color.slider_gradient_top);
                        break;

                    default:
                        myColor = mContext.getResources().getColor(R.color.slider_gradient_bottom);
                }

                myDrawView.mPaint.setColor(myColor);


            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }

}
