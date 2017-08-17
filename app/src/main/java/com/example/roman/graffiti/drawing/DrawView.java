package com.example.roman.graffiti.drawing;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.example.roman.graffiti.R;

/**
 * Created by roman on 8/17/17.
 */

public class DrawView extends View {

    public Bitmap mBitmap, bitmapBackup;
    public Canvas mCanvas, bitmapBackupCanvas;
    private Path mPath ;
    private Paint mBitmapPaint;
    public Paint mPaint,tPaint,qPaint;
    public String shape;
    boolean undofresh;

    public DrawView(Context c, AttributeSet attrs) {
        super(c, attrs);

        shape = "normal";
        mPath = new Path();
        mBitmapPaint = new Paint(Paint.DITHER_FLAG);

        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setDither(true);

        mPaint.setColor(getContext().getResources().getColor(R.color.colorPrimary));
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeJoin(Paint.Join.ROUND);
        mPaint.setStrokeCap(Paint.Cap.ROUND);
        mPaint.setStrokeWidth(50);

//        tPaint = new Paint();
//        tPaint.setStrokeCap(Paint.Cap.ROUND);
//
//        qPaint = new Paint();
//        qPaint.setStrokeCap(Paint.Cap.SQUARE);

    }

    public void changeColor(int color){

        //validate color
        mBitmapPaint.setColor(color);
        // mCanvas.drawPa

    }


    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {

        super.onSizeChanged(w, h, oldw, oldh);
        mBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        mCanvas = new Canvas(mBitmap);

//        bitmapBackup = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
//        bitmapBackupCanvas = new Canvas(bitmapBackup);
    }


    public void undo (){
        mBitmap.eraseColor(getDrawingCacheBackgroundColor());
        mCanvas.drawBitmap(bitmapBackup, 0, 0, null);
        invalidate();

        mPath.reset();
        undofresh=true;
    }


    @Override
    protected void onDraw(Canvas canvas) {

        canvas.drawBitmap(mBitmap, 0, 0, mBitmapPaint);
        canvas.drawPath(mPath, mPaint);

    }


    private float mX, mY;
    private static final float TOUCH_TOLERANCE = 4;

    private void touch_start(float x, float y) {

        View v1 = this;
        v1.setDrawingCacheEnabled(true);
//        this.bitmapBackup = Bitmap.createBitmap(v1.getDrawingCache());
        v1.setDrawingCacheEnabled(false);


        mPath.reset();
        mPath.moveTo(x, y);
        mX = x;
        mY = y;
    }

    private void touch_move(float x, float y) {
        float dx = Math.abs(x - mX);
        float dy = Math.abs(y - mY);
        if (dx >= TOUCH_TOLERANCE || dy >= TOUCH_TOLERANCE) {
            mPath.quadTo(mX, mY, (x + mX)/2, (y + mY)/2);
            mX = x;
            mY = y;
        }
    }

    private void touch_up() {

        float ss = mPaint.getStrokeWidth();

        mPath.lineTo(mX, mY);
        // commit the path to our offscreen
        if (shape.equals("normal"))
            mCanvas.drawPath(mPath, mPaint);
//        else  if (shape.equals("Circle"))
//            mCanvas.drawCircle(mX, mY,ss,mPaint);
//        else  if (shape.equals("square"))
//            mCanvas.drawRect(mX-ss,mY-ss,mX+ss,mY+ss,mPaint);
//        else  if (shape.equals("triangleup")) {
//            float[] pts = {mX,mY,mX+1*ss,mY+2*ss   ,mX+1*ss,mY+2*ss,mX-1*ss,mY+2*ss   ,mX-1*ss,mY+2*ss,mX,mY  };
//            tPaint.setColor(mPaint.getColor());
//            tPaint.setStrokeWidth(mPaint.getStrokeWidth()/2);
//            mCanvas.drawLines(pts, tPaint);
//        }else  if (shape.equals("triangle")) {
//            float[] pts = {mX-1*ss,mY-2*ss,mX+1*ss,mY-2*ss  ,mX+1*ss,mY-2*ss,mX,mY  ,mX,mY,mX-1*ss,mY-2*ss  };
//            tPaint.setColor(mPaint.getColor());
//            tPaint.setStrokeWidth(mPaint.getStrokeWidth()/2);
//            mCanvas.drawLines(pts, tPaint);
//        }else  if (shape.equals("dot")) {
//            tPaint.setColor(mPaint.getColor());
//            tPaint.setStrokeWidth(mPaint.getStrokeWidth());
//            mCanvas.drawPoint(mX, mY, tPaint);
//        }else  if (shape.equals("dotsq")) {
//            qPaint.setColor(mPaint.getColor());
//            qPaint.setStrokeWidth(mPaint.getStrokeWidth());
//            mCanvas.drawPoint(mX, mY, qPaint);
//        }


        // kill this so we don't double draw
        mPath.reset();
        undofresh=false;

    }



    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float x = event.getX();
        float y = event.getY();

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                touch_start(x, y);
                invalidate();
                break;
            case MotionEvent.ACTION_MOVE:
                touch_move(x, y);
                invalidate();
                break;
            case MotionEvent.ACTION_UP:
                touch_up();
                invalidate();
                break;
        }
        return true;
    }




    public void clear(){


        mBitmap.eraseColor(getDrawingCacheBackgroundColor());
        invalidate();
        System.gc();

        DrawView.this.setBackgroundColor(Color.parseColor("#ffffff"));
    }
}
