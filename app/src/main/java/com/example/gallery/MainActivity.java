package com.example.gallery;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

import android.os.ParcelFileDescriptor;
import android.provider.MediaStore;
import android.util.Pair;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import java.io.File;
import java.io.FileDescriptor;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import com.example.gallery.Utils.ImageDrawView;
import com.example.gallery.Utils.Point;
import com.hbisoft.pickit.PickiT;
import com.hbisoft.pickit.PickiTCallbacks;

public class MainActivity extends AppCompatActivity{

    ArrayList<Uri> images = new ArrayList<>();
    RecyclerView imageCollection;
    AppCompatButton pickImageButton;
    RecyclerAdapter recyclerAdapter;
    ImageDrawView imageView;

    private static final int READ_PERMISSION = 101;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        imageCollection = findViewById(R.id.imageCollection);
        pickImageButton = findViewById(R.id.pickUpButton);
        recyclerAdapter = new RecyclerAdapter(images,this);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        imageCollection.setAdapter(recyclerAdapter);
        imageCollection.setLayoutManager(linearLayoutManager);
        imageView = findViewById(R.id.image);

        if (ContextCompat.checkSelfPermission
                (MainActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) !=
                PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MainActivity.this,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, READ_PERMISSION);
        }

        pickImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent,"Select images"),1);
            }
        });
        imageView.setOnTouchBoundingBox(new ImageDrawView.BoundingBox() {
            @Override
            public boolean isPointInPoly(Point pointInView, List<Point> polygonInRaw, float scaleX, float scaleY) {
                return true;
            }

            @Override
            public void DoSomeThings(View view) {
                int h = 9;
            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.Q)
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1 && resultCode == RESULT_OK) {
            if (data.getClipData() != null) {
                int x = data.getClipData().getItemCount();
                for (int i=0; i<x; i++) {
                    Uri uri = data.getClipData().getItemAt(i).getUri();
                    images.add(uri);
                    recyclerAdapter.notifyDataSetChanged();
                }

            }
            else if (data.getData() != null) {;
                images.add(data.getData());
                ParcelFileDescriptor parcelFileDescriptor = null;
                try {
                    parcelFileDescriptor = getContentResolver().openFileDescriptor(data.getData(), "r");
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
                FileDescriptor fileDescriptor = parcelFileDescriptor.getFileDescriptor();
                Bitmap myImg = BitmapFactory.decodeFileDescriptor(fileDescriptor);

                List<Point> pts = new ArrayList<>();
                pts.add(new Point(178,156));
                pts.add(new Point(284,195));
                pts.add(new Point(371,230));
                pts.add(new Point(281,287));
                pts.add(new Point(207,318));
                pts.add(new Point(167,246));

                Paint mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
                mPaint.setColor(Color.BLUE);
                mPaint.setStrokeWidth(30);
                mPaint.setStyle(Paint.Style.STROKE);

                imageView.drawBitmapWithBoundingBox(myImg,pts,mPaint);
                recyclerAdapter.notifyDataSetChanged();
            }
        }
    }
    private Bitmap drawRectOnBitMap(Bitmap bm, float left, float top, float right, float bottom){
        Bitmap bm1 = bm.copy(bm.getConfig(),true);
        Paint mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setColor(Color.BLUE);
        mPaint.setStrokeWidth(30);
        mPaint.setStyle(Paint.Style.STROKE);
        Canvas canvas = new Canvas(bm1);
        canvas.drawRect(left,top,right,bottom,mPaint);

        return bm1;
    }
    private Bitmap drawPoly(Bitmap bm) {
        Bitmap bm1 = bm.copy(bm.getConfig(),true);
        Paint mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setColor(Color.BLUE);
        mPaint.setStrokeWidth(20);
        mPaint.setStyle(Paint.Style.STROKE);
        Canvas canvas = new Canvas(bm1);
        Path path = new Path();
        path.moveTo(178,156);
        path.lineTo(284,195);
        path.lineTo(371,230);
        path.lineTo(281,287);
        path.lineTo(207,318);
        path.lineTo(167,246);
        path.lineTo(178,156);
        canvas.drawPath(path,mPaint);
        return bm1;
    }
}