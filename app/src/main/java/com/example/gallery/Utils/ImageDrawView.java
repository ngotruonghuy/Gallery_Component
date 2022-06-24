package com.example.gallery.Utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;

public class ImageDrawView extends androidx.appcompat.widget.AppCompatImageView {

    List<Point> points = new ArrayList<>();
    float ScaleX = 1;
    float ScaleY = 1;
    float height;

    public ImageDrawView(@NonNull Context context, AttributeSet attributeSet) {
        super(context,attributeSet);
    }
    public void drawBitmapWithBoundingBox(Bitmap bm, List<Point> points, Paint paint) {
        this.points.clear();
        Bitmap bm1 = bm.copy(bm.getConfig(),true);
        Canvas canvas = new Canvas(bm1);
        Path path = new Path();
        path.moveTo(points.get(0).x,points.get(0).y);
        for (int i = 1; i < points.size() ; i++) {
            path.lineTo(points.get(i).x, points.get(i).y);
        }
        path.lineTo(points.get(0).x, points.get(0).y);
        canvas.drawPath(path,paint);
        this.setImageBitmap(bm1);
        height = this.getHeight();
        if (this.getWidth() != bm1.getWidth())
            ScaleX = (float)1.0*this.getWidth()/bm1.getWidth();
        if (this.getHeight() != bm1.getHeight())
            ScaleY = (float)1.0*this.getHeight()/bm1.getHeight();
        if (ScaleX == 0)
            ScaleX = 1;
        if (ScaleY == 0) {
            ScaleY = 1;
            height = bm1.getHeight();
        }
        for (int i = 0;i<points.size();i++) {
            this.points.add(new Point((float)ScaleX*points.get(i).x,(float)ScaleY*points.get(i).y));
        }
    }

    public void setOnTouchBoundingBox(BoundingBox boundingBox) {
        this.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (motionEvent.getAction() == MotionEvent.ACTION_DOWN)
                    if (boundingBox.isPointInPoly(new Point(motionEvent.getX(), height - motionEvent.getY()), points,ScaleX,ScaleY))
                        boundingBox.DoSomeThings(view);
                return false;
            }
        });
    }
    public interface BoundingBox {
        boolean isPointInPoly(Point pointInView, List<Point> polygonInRaw, float scaleX, float scaleY);
        void DoSomeThings(View view);
    }
}
